package com.telusko.jobApp.controller;

import com.telusko.jobApp.repository.PostRepository;
import com.telusko.jobApp.model.Post;
import com.telusko.jobApp.repository.SearchRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class PostController {



        @Autowired
        PostRepository repo;

        @Autowired
        SearchRepository srepo;


        @RequestMapping("/value")

        public void redirect(HttpServletResponse response) throws IOException
        {
            response.sendRedirect("/swagger-ui.html");
        }

        @GetMapping("/allPosts")

        public List<Post> getAllPost()
        {
            return  repo.findAll();
        }

        @GetMapping("/posts/{text}")

        public List<Post> search(@PathVariable String text)
        {
            return srepo.findByText(text);
        }

        @PostMapping("/post")

        public Post addPost(@RequestBody Post post)
        {
            return repo.save(post);
        }

    }


