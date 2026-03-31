package com.telusko.jobApp.repository;

import com.mongodb.client.MongoClient;
import com.telusko.jobApp.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import java.util.Arrays;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import java.util.concurrent.TimeUnit;

import com.mongodb.client.AggregateIterable;



@Component
public class SearchRepositoryImplementation implements SearchRepository
{

    @Autowired
    MongoClient client;

    @Autowired
    MongoConverter convert;


    @Override
    public List<Post> findByText(String text) {

        final List<Post> posts = new ArrayList<>();

        MongoDatabase database = client.getDatabase("postlisting");
        MongoCollection<Document> collection = database.getCollection("PostingInfo");
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
                                        new Document("$search",
                                        new Document("text", new Document("query", text)
                                        .append("path", Arrays.asList("techs", "desc", "profile"))
                                        .append("matchCriteria", "any"))),
                                        new Document("$limit", 5L),
                                        new Document("$sort",
                                        new Document("exp", 1L))));


        result.forEach((doc) -> posts.add(convert.read(Post.class,doc)));


        return posts ;

    }
}
