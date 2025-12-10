package com.example;

import com.example.dto.RepositoryResponse;
import com.example.entity.GithubRepository;
import com.example.mapper.GithubRepositoryMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private GithubRepositoryMapper repositoryMapper;

	@Test
	void contextLoads() {
		// Test that the application context loads successfully
	}

	@Test
	void testGetRepository_Success() {
		// Test with a known public repository
		String owner = "spring-projects";
		String repoName = "spring-boot";
		String url = String.format("http://localhost:%d/repositories/%s/%s", port, owner, repoName);

		ResponseEntity<RepositoryResponse> response = restTemplate.getForEntity(
				url,
				RepositoryResponse.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getFullName()).isEqualTo("spring-projects/spring-boot");
		assertThat(response.getBody().getCloneUrl()).isNotNull();
		assertThat(response.getBody().getStars()).isGreaterThan(0);
		assertThat(response.getBody().getCreatedAt()).isNotNull();
	}

	@Test
	void testGetRepository_CachedResponse() {
		// Test that the second request is served from cache
		String owner = "octocat";
		String repoName = "Hello-World";
		String url = String.format("http://localhost:%d/repositories/%s/%s", port, owner, repoName);

		// First request - should fetch from GitHub API
		ResponseEntity<RepositoryResponse> firstResponse = restTemplate.getForEntity(
				url,
				RepositoryResponse.class
		);

		assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(firstResponse.getBody()).isNotNull();

		// Verify it's saved in database
		GithubRepository cached = repositoryMapper.findByOwnerAndRepositoryName(owner, repoName);
		assertThat(cached).isNotNull();
		assertThat(cached.getFullName()).isEqualTo(firstResponse.getBody().getFullName());

		// Second request - should be served from cache
		ResponseEntity<RepositoryResponse> secondResponse = restTemplate.getForEntity(
				url,
				RepositoryResponse.class
		);

		assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(secondResponse.getBody()).isNotNull();
		assertThat(secondResponse.getBody().getFullName()).isEqualTo(firstResponse.getBody().getFullName());
	}

	@Test
	void testGetRepository_NotFound() {
		// Test with a non-existent repository
		String owner = "user123";
		String repoName = "repo123";
		String url = String.format("http://localhost:%d/repositories/%s/%s", port, owner, repoName);

		ResponseEntity<String> response = restTemplate.getForEntity(
				url,
				String.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

}
