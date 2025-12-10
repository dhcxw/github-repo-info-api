package com.example.mapper;

import com.example.entity.GithubRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GithubRepositoryMapper {

    GithubRepository findByOwnerAndRepositoryName(@Param("owner") String owner, 
                                                    @Param("repositoryName") String repositoryName);

    int insert(GithubRepository repository);
}
