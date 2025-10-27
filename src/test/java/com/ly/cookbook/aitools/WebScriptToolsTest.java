package com.ly.cookbook.aitools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebScriptToolsTest {
    WebScriptTools webScriptTools = new WebScriptTools();
    @Test
    void doFetchText() {
    }

    @Test
    void doFetchHtml() {
    }

    @Test
    void doFetchElementsBySelector() {
    }

    @Test
    void fetchWebPageText() {
    }

    @Test
    void fetchWebPageHtml() {
        String html = webScriptTools.fetchWebPageHtml("https://www.baidu.com");
        System.out.println(html);
    }

    @Test
    void fetchWebPageElements() {
    }

    @Test
    void fetchWebPageTitle() {
    }
}