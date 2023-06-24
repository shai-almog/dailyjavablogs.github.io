package com.github.dailyjavablogs.ghpagesbuilder.data.repository;

import com.github.dailyjavablogs.ghpagesbuilder.data.entity.BlogItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface BlogItemRepository extends MongoRepository<BlogItem, UUID> {

	Optional<BlogItem> findOneByBlogAndUrl(String blog, String url);
}
