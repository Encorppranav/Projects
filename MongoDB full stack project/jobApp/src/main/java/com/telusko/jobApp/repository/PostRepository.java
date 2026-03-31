package com.telusko.jobApp.repository;

import com.telusko.jobApp.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post,String>
{
}
