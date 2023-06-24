package com.github.dailyjavablogs.ghpagesbuilder.service;

import com.github.dailyjavablogs.ghpagesbuilder.config.DailyJavaBlogConfigurationProperties;
import com.github.dailyjavablogs.ghpagesbuilder.data.entity.BlogItem;
import com.github.dailyjavablogs.ghpagesbuilder.data.repository.BlogItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


@Service
public class BlogItemService {

	private static final Logger logger = LoggerFactory.getLogger(BlogItemService.class);
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
		.withZone(ZoneId.systemDefault());
	private static final String NEW_LINE = "\n";


	private final BlogItemRepository blogItemRepository;
	private final DailyJavaBlogConfigurationProperties configurationProperties;
	private final BlogItemCrawler blogItemCrawler;

	public BlogItemService(
		BlogItemRepository blogItemRepository,
		DailyJavaBlogConfigurationProperties configurationProperties,
		BlogItemCrawler blogItemCrawler) {
		this.blogItemRepository = blogItemRepository;
		this.configurationProperties = configurationProperties;
		this.blogItemCrawler = blogItemCrawler;
	}

	public void processNewBlogItems() {
		logger.info("Starting blog items update");
		configurationProperties.getSources().forEach(blog -> {
			logger.debug("Updating blog {} with feed {}", blog.getName(), blog.getFeedUrl());
			try {
				blogItemCrawler.loadNewBlogItemsOfBlog(blog)
					.forEach(blogItem -> {
						final Optional<BlogItem> existingBlogItem =
							blogItemRepository.findOneByBlogAndUrl(blogItem.getBlog(), blogItem.getUrl());
						if (existingBlogItem.isEmpty()) {
							try {
								createBlogFrontMatterFile(blogItem);
								blogItemRepository.save(blogItem);
							} catch (final Exception e) {
								logger.error("Error while processing blog item {}", blogItem);
							}
						}
					});
			} catch (final Exception e) {
				logger.error("Error while processing blog {}", blog.getName(), e);
			}
		});

		logger.info("Blog items update finished");
	}

	/**
	 * Creates a new Front Matter file for given blog item.
	 *
	 * @param blogItem the blog item to be processed
	 */
	void createBlogFrontMatterFile(final BlogItem blogItem) {
		final String title = blogItem.getTitle().replaceAll("[^a-zA-Z0-9]", "_");
		final String blogDateFolder = formatter.format(blogItem.getPublishedAt()).replaceAll("-", "/");
		final String blogFileName = title.concat(".md");
		final Path blogFilePath = Paths.get(configurationProperties.getFrontMatter().getPostsDir(),
			blogDateFolder, blogFileName);

		blogFilePath.getParent().toFile().mkdirs();

		final List<String> fileLines = new LinkedList<>();
		fileLines.add("---");
		fileLines.add("layout: post");
		fileLines.add("blog: \"" + blogItem.getBlog() + "\"");
		fileLines.add("title: \"" + blogItem.getTitle() + "\"");
		fileLines.add("date: " + blogItem.getPublishedAt());
		fileLines.add("image: " + blogItem.getImage());
		fileLines.add("remote_url: \"" + blogItem.getUrl() + "\"");
		fileLines.add("---");

		try {
			logger.debug("Writing blog {} file {}", blogItem.getBlog(), blogFilePath);
			Files.write(blogFilePath, fileLines);
		} catch (IOException e) {
			logger.error("Unable to write file {}", blogFilePath, e);
		}
	}
}
