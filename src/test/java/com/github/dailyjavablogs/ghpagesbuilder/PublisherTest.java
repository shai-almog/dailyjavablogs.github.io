package com.github.dailyjavablogs.ghpagesbuilder;

import com.github.dailyjavablogs.ghpagesbuilder.service.BlogItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PublisherTest {

    @Autowired
    private BlogItemService blogItemService;

    @Test
    void publish() {
        blogItemService.processNewBlogItems();
    }
}
