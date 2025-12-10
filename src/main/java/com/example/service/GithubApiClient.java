package com.example.service;

import com.example.dto.GithubApiResponse;
import com.example.exception.GithubApiException;
import com.example.exception.RepositoryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * GitHub API客户端服务
 * 与GitHub REST API通信，获取仓库信息
 */
@Service
public class GithubApiClient {

    private static final Logger logger = LoggerFactory.getLogger(GithubApiClient.class);
    /** GitHub API的RESTful接口地址 */
    private static final String GITHUB_API_URL = "https://api.github.com/repos/{owner}/{repo}";

    private final RestTemplate restTemplate;

    public GithubApiClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 从GitHub API获取仓库信息
     * 
     * @param owner 仓库所有者
     * @param repositoryName 仓库名称
     * @return GitHub API返回的仓库信息
     * @throws RepositoryNotFoundException 仓库不存在时404
     * @throws GithubApiException 调用失败时抛出
     */
    public GithubApiResponse fetchRepository(String owner, String repositoryName) {
        logger.info("Fetching repository from GitHub API: {}/{}", owner, repositoryName);
        
        try {
            ResponseEntity<GithubApiResponse> response = restTemplate.getForEntity(
                GITHUB_API_URL,
                GithubApiResponse.class,
                owner,
                repositoryName
            );

            // 检查响应状态码和响应体
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Successfully fetched repository from GitHub: {}/{}", owner, repositoryName);
                return response.getBody();
            } else {
                throw new GithubApiException("Unexpected response from GitHub API");
            }
        } catch (HttpClientErrorException.NotFound e) {
            // 404
            logger.error("Repository not found on GitHub: {}/{}", owner, repositoryName);
            throw new RepositoryNotFoundException(
                String.format("Repository %s/%s not found on GitHub", owner, repositoryName)
            );
        } catch (HttpClientErrorException e) {
            // 4xx
            logger.error("GitHub API client error: {}", e.getMessage());
            throw new GithubApiException("Error calling GitHub API: " + e.getMessage(), e);
        } catch (Exception e) {
            // 5xx服务器错误或其他异常
            logger.error("Unexpected error calling GitHub API: {}", e.getMessage());
            throw new GithubApiException("Unexpected error calling GitHub API", e);
        }
    }
}
