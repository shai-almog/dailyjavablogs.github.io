package com.github.dailyjavablogs.ghpagesbuilder.service;

import com.github.dailyjavablogs.ghpagesbuilder.data.entity.BlogItem;
import com.github.dailyjavablogs.ghpagesbuilder.data.model.Blog;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class BlogItemCrawler {
	private final WebClient webClient;

	public BlogItemCrawler(final WebClient webClient) {
		this.webClient = webClient;
	}

	private static Optional<SyndFeed> bodyToFeed(InputStream body) {
		try (body) {
			return Optional.of(new SyndFeedInput().build(new XmlReader(body)));
		} catch (FeedException | IOException e) {
			return Optional.empty();
		}
	}

	public List<BlogItem> loadNewBlogItemsOfBlog(final Blog blog) {
		final List<SyndEntry> blogEntries = webClient.get()
			.uri(blog.getFeedUrl())
			.retrieve()
			.toEntity(DataBuffer.class)
			.mapNotNull(HttpEntity::getBody)
			.map(DataBuffer::asInputStream)
			.map(BlogItemCrawler::bodyToFeed)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.map(SyndFeed::getEntries)
			.block();

		if (blogEntries == null) {
			return Collections.emptyList();
		}

		return blogEntries.stream()
			.map(se -> {
				final BlogItem b = new BlogItem();
				b.setBlog(blog.getName());
				b.setCreatedAt(Instant.now());
				b.setDescription(getDescription(se));
				b.setImage(blog.getImage());
				b.setPublishedAt(se.getPublishedDate() != null ? se.getPublishedDate()
					.toInstant() : null);
				b.setTitle(se.getTitle());
				b.setUrl(se.getLink());
				return b;
			})
			.toList();
	}

	private String getDescription(final SyndEntry blogItem) {
		return blogItem.getDescription() != null ? blogItem.getDescription().getValue() : null;
	}
}
