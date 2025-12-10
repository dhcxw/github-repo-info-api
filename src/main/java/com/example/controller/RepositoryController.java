package com.example.controller;

import com.example.dto.RepositoryResponse;
import com.example.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GitHub Repository RESTful API Controller
 */
@RestController
@RequestMapping("/repositories")
public class RepositoryController {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryController.class);

    /** Repository service layer, handles business logic */
    private final RepositoryService repositoryService;

    /**
     * Constructor injection
     */
    @Autowired
    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    /**
     * Get detailed information of specified GitHub repository
     * @param owner Repository owner
     * @param repositoryName Repository name
     * @return Repository details response
     */
    @GetMapping("/{owner}/{repository-name}")
    public ResponseEntity<RepositoryResponse> getRepository(
            @PathVariable("owner") String owner,
            @PathVariable("repository-name") String repositoryName) {
        
        logger.info("Received request for repository: {}/{}", owner, repositoryName);
        // Call service layer to get repository information
        RepositoryResponse response = repositoryService.getRepository(owner, repositoryName);
        return ResponseEntity.ok(response);
    }
}
