package com.ly.cookbook.aitools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class WebSearchToolsTest {
    WebSearchTools webSearchTools = new WebSearchTools();
    @Test
    void search() {
        String search = webSearchTools.webSearch("如何快速熟悉业务", 10);
        System.out.println(search);
    }
}