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
 * GitHub仓库信息Restful API控制器
 */
@RestController
@RequestMapping("/repositories")
public class RepositoryController {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryController.class);

    /** 仓库服务层，处理具体的业务逻辑 */
    private final RepositoryService repositoryService;

    /**
     * 构造器注入依赖
     */
    @Autowired
    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    /**
     * 获取指定GitHub仓库的详细信息
     * @param owner 仓库所有者
     * @param repositoryName 仓库名称
     * @return 仓库详细信息响应
     */
    @GetMapping("/{owner}/{repository-name}")
    public ResponseEntity<RepositoryResponse> getRepository(
            @PathVariable("owner") String owner,
            @PathVariable("repository-name") String repositoryName) {
        
        logger.info("Received request for repository: {}/{}", owner, repositoryName);
        // 调用服务层获取仓库信息
        RepositoryResponse response = repositoryService.getRepository(owner, repositoryName);
        return ResponseEntity.ok(response);
    }
}
