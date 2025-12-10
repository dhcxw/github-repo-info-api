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
 * GitHub Repository Service
 * Handles repository information querying, caching, and data transformation
 */
@Service
public class RepositoryService {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryService.class);

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    private final GithubRepositoryMapper repositoryMapper;
    /** GitHub API client */
    private final GithubApiClient githubApiClient;

    @Autowired
    public RepositoryService(GithubRepositoryMapper repositoryMapper, GithubApiClient githubApiClient) {
        this.repositoryMapper = repositoryMapper;
        this.githubApiClient = githubApiClient;
    }

    /**
     * Get GitHub repository information
     * @param owner Repository owner
     * @param repositoryName Repository name
     * @return Response object with repository details
     */
    public RepositoryResponse getRepository(String owner, String repositoryName) {
        logger.info("Getting repository: {}/{}", owner, repositoryName);

        // Query database cache first
        GithubRepository cachedRepo = repositoryMapper.findByOwnerAndRepositoryName(owner, repositoryName);

        if (cachedRepo != null) {
            // Cache hit, return directly
            logger.info("Repository found in cache: {}/{}", owner, repositoryName);
            return mapToResponse(cachedRepo);
        }

        // Cache miss, fetch latest data from GitHub
        logger.info("Repository not in cache, fetching from GitHub API: {}/{}", owner, repositoryName);
        GithubApiResponse apiResponse = githubApiClient.fetchRepository(owner, repositoryName);

        // Convert to entity and save to database
        GithubRepository entity = mapToEntity(owner, repositoryName, apiResponse);
        repositoryMapper.insert(entity);
        logger.info("Repository saved to cache: {}/{}", owner, repositoryName);

        return mapToResponse(entity);
    }

    /**
     * Convert GitHub API response to database entity
     * 
     * @param owner Repository owner
     * @param repositoryName Repository name
     * @param apiResponse GitHub API response data
     * @return Database entity object
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
                .lastUpdated(LocalDateTime.now())  // Record cache update time
                .build();
    }

    /**
     * Convert database entity to API response object
     * 
     * @param entity Database entity object
     * @return Response object conforming to specifications
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
