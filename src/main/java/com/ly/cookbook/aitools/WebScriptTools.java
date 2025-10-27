package com.ly.cookbook.aitools;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * @author 刘燚
 * @version v1.0.0
 * @Description 网页抓取工具的作用是根据网址解析到网页的内容。
 * @createDate：2025/10/3 21:18
 * @email liuyia2022@163.com
 */
@Slf4j
public class WebScriptTools {

    private static final int TIMEOUT = 10000; // 10秒超时

    /**
     * 抓取指定 URL 的网页文本内容（不包含 HTML 标签）。
     *
     * @param url 目标网页 URL
     * @return 网页文本内容
     */
    public static String doFetchText(String url) {
        try {
            log.info("开始抓取网页文本: {}", url);
            Document doc = Jsoup.connect(url)
                    .timeout(TIMEOUT)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();
            
            String text = doc.body().text();
            log.info("抓取网页文本成功: {}, 长度: {} 字符", url, text.length());
            return text;
        } catch (IOException e) {
            log.error("抓取网页文本失败: {}", url, e);
            return null;
        }
    }

    /**
     * 抓取指定 URL 的网页 HTML 内容。
     *
     * @param url 目标网页 URL
     * @return 网页 HTML 内容
     */
    public static String doFetchHtml(String url) {
        try {
            log.info("开始抓取网页 HTML: {}", url);
            Document doc = Jsoup.connect(url)
                    .timeout(TIMEOUT)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();
            
            String html = doc.html();
            log.info("抓取网页 HTML 成功: {}, 长度: {} 字符", url, html.length());
            return html;
        } catch (IOException e) {
            log.error("抓取网页 HTML 失败: {}", url, e);
            return null;
        }
    }

    /**
     * 抓取指定 URL 网页中符合 CSS 选择器的元素内容。
     *
     * @param url      目标网页 URL
     * @param selector CSS 选择器
     * @return 元素内容
     */
    public static String doFetchElementsBySelector(String url, String selector) {
        try {
            log.info("开始抓取网页元素: {}, 选择器: {}", url, selector);
            Document doc = Jsoup.connect(url)
                    .timeout(TIMEOUT)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();
            
            Elements elements = doc.select(selector);
            if (elements.isEmpty()) {
                log.warn("未找到匹配的元素: {}, 选择器: {}", url, selector);
                return null;
            }
            
            StringBuilder result = new StringBuilder();
            for (Element element : elements) {
                result.append(element.text()).append("\n");
            }
            
            log.info("抓取网页元素成功: {}, 找到 {} 个元素", url, elements.size());
            return result.toString().trim();
        } catch (IOException e) {
            log.error("抓取网页元素失败: {}, 选择器: {}", url, selector, e);
            return null;
        }
    }

    /**
     * 抓取网页的纯文本内容（移除所有 HTML 标签）。
     */
    @Tool(description = "抓取指定网页的纯文本内容，自动移除所有 HTML 标签。")
    public String fetchWebPageText(@ToolParam(description = "目标网页的完整 URL 地址") String url) {
        String text = doFetchText(url);
        if (text != null) {
            return String.format("抓取网页成功！\nURL: %s\n内容长度: %d 字符\n\n内容:\n%s", 
                    url, text.length(), text.length() > 2000 ? text.substring(0, 2000) + "...(内容过长已截断)" : text);
        }
        return String.format("抓取网页失败！URL: %s", url);
    }

    /**
     * 抓取网页的 HTML 源代码。
     */
    @Tool(description = "抓取指定网页的 HTML 源代码。")
    public String fetchWebPageHtml(@ToolParam(description = "目标网页的完整 URL 地址") String url) {
        String html = doFetchHtml(url);
        if (html != null) {
            return String.format("抓取网页 HTML 成功！\nURL: %s\n内容长度: %d 字符\n\nHTML:\n%s", 
                    url, html.length(), html.length() > 1000 ? html.substring(0, 1000) + "...(内容过长已截断)" : html);
        }
        return String.format("抓取网页 HTML 失败！URL: %s", url);
    }

    /**
     * 抓取网页中特定的元素内容（使用 CSS 选择器）。
     */
    @Tool(description = "抓取网页中符合 CSS 选择器的特定元素内容。例如：'h1' 抓取标题，'.classname' 抓取特定类的元素。")
    public String fetchWebPageElements(
            @ToolParam(description = "目标网页的完整 URL 地址") String url,
            @ToolParam(description = "CSS 选择器，例如: 'h1', '.title', '#content', 'div.article p'") String selector) {
        String content = doFetchElementsBySelector(url, selector);
        if (content != null && !content.isEmpty()) {
            return String.format("抓取网页元素成功！\nURL: %s\n选择器: %s\n\n元素内容:\n%s", url, selector, content);
        }
        return String.format("抓取网页元素失败或未找到匹配元素！\nURL: %s\n选择器: %s", url, selector);
    }

    /**
     * 获取网页的标题。
     */
    @Tool(description = "获取指定网页的标题（title 标签内容）。")
    public String fetchWebPageTitle(@ToolParam(description = "目标网页的完整 URL 地址") String url) {
        try {
            log.info("开始获取网页标题: {}", url);
            Document doc = Jsoup.connect(url)
                    .timeout(TIMEOUT)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();
            
            String title = doc.title();
            log.info("获取网页标题成功: {}, 标题: {}", url, title);
            return String.format("获取网页标题成功！\nURL: %s\n标题: %s", url, title);
        } catch (IOException e) {
            log.error("获取网页标题失败: {}", url, e);
            return String.format("获取网页标题失败！URL: %s, 错误: %s", url, e.getMessage());
        }
    }

}
