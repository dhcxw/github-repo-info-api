package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GithubRepository {

    private Long id;

    private String owner;

    private String repositoryName;

    private String fullName;

    private String description;

    private String cloneUrl;

    private Integer stars;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdated;
}
