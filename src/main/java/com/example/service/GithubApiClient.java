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
 * GitHub API Client Service
 * Communicates with GitHub REST API to fetch repository information
 */
@Service
public class GithubApiClient {

    private static final Logger logger = LoggerFactory.getLogger(GithubApiClient.class);
    /** GitHub API RESTful endpoint address */
    private static final String GITHUB_API_URL = "https://api.github.com/repos/{owner}/{repo}";

    private final RestTemplate restTemplate;

    public GithubApiClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Fetch repository information from GitHub API
     * 
     * @param owner Repository owner
     * @param repositoryName Repository name
     * @return Repository information returned by GitHub API
     * @throws RepositoryNotFoundException Thrown when repository not found (404)
     * @throws GithubApiException Thrown when API call fails
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

            // Check response status code and body
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
            // 5xx server errors or other exceptions
            logger.error("Unexpected error calling GitHub API: {}", e.getMessage());
            throw new GithubApiException("Unexpected error calling GitHub API", e);
        }
    }
}
