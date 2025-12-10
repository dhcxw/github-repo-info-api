package com.example.service;

import com.example.dto.GithubApiResponse;
import com.example.dto.RepositoryResponse;
import com.example.entity.GithubRepository;
import com.example.mapper.GithubRepositoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * GitHub仓库信息服务类
 * 负责处理仓库信息的查询、缓存和数据转换逻辑
 */
@Service
public class RepositoryService {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryService.class);

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    private final GithubRepositoryMapper repositoryMapper;
    /** GitHub API客户端*/
    private final GithubApiClient githubApiClient;

    @Autowired
    public RepositoryService(GithubRepositoryMapper repositoryMapper, GithubApiClient githubApiClient) {
        this.repositoryMapper = repositoryMapper;
        this.githubApiClient = githubApiClient;
    }

    /**
     * 获取GitHub仓库信息
     * @param owner 仓库所有者
     * @param repositoryName 仓库名称
     * @return 仓库详细信息的响应对象
     */
    public RepositoryResponse getRepository(String owner, String repositoryName) {
        logger.info("Getting repository: {}/{}", owner, repositoryName);

        //优先从数据库缓存中查询
        GithubRepository cachedRepo = repositoryMapper.findByOwnerAndRepositoryName(owner, repositoryName);

        if (cachedRepo != null) {
            // 缓存命中，直接返回
            logger.info("Repository found in cache: {}/{}", owner, repositoryName);
            return mapToResponse(cachedRepo);
        }

        //缓存未命中，从GitHub获取最新数据
        logger.info("Repository not in cache, fetching from GitHub API: {}/{}", owner, repositoryName);
        GithubApiResponse apiResponse = githubApiClient.fetchRepository(owner, repositoryName);

        //转换为实体对象并保存到数据库
        GithubRepository entity = mapToEntity(owner, repositoryName, apiResponse);
        repositoryMapper.insert(entity);
        logger.info("Repository saved to cache: {}/{}", owner, repositoryName);

        return mapToResponse(entity);
    }

    /**
     * 将GitHub API响应转换为数据库实体对象
     * 
     * @param owner 仓库所有者
     * @param repositoryName 仓库名称
     * @param apiResponse GitHub API的响应数据
     * @return 数据库实体对象
     */
    private GithubRepository mapToEntity(String owner, String repositoryName, GithubApiResponse apiResponse) {
        LocalDateTime createdAt = LocalDateTime.parse(apiResponse.getCreatedAt(), ISO_FORMATTER);

        return GithubRepository.builder()
                .owner(owner)
                .repositoryName(repositoryName)
                .fullName(apiResponse.getFullName())
                .description(apiResponse.getDescription())
                .cloneUrl(apiResponse.getCloneUrl())
                .stars(apiResponse.getStargazersCount())
                .createdAt(createdAt)
                .lastUpdated(LocalDateTime.now())  // 记录缓存更新时间
                .build();
    }

    /**
     * 将数据库实体对象转换为API响应对象
     * 
     * @param entity 数据库实体对象
     * @return 符合规范的响应对象
     */
    private RepositoryResponse mapToResponse(GithubRepository entity) {
        return RepositoryResponse.builder()
                .fullName(entity.getFullName())
                .description(entity.getDescription())
                .cloneUrl(entity.getCloneUrl())
                .stars(entity.getStars())
                .createdAt(entity.getCreatedAt().format(ISO_FORMATTER))
                .build();
    }
}
