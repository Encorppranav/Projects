package com.telusko.jobApp.repository;

import com.telusko.jobApp.model.Post;

import java.util.List;

public interface SearchRepository
{
    public List<Post> findByText(String text);
}
