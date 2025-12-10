DROP TABLE IF EXISTS github_repositories;

CREATE TABLE github_repositories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner VARCHAR(255) NOT NULL,
    repository_name VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    clone_url VARCHAR(255) NOT NULL,
    stars INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    UNIQUE KEY uk_owner_repo (owner, repository_name)
);
