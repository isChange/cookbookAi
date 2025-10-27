package com.ly.cookbook.aitools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description 使用 SerpAPI 实现从 Google 等搜索引擎搜索内容。返回结构化的搜索结果。
 * @createDate：2025/10/3 21:16
 * @email liuyia2022@163.com
 */
@Slf4j
public class WebSearchTools {

    private final String serpApiKey;
    private final String searchEngine;

    private static final String SERPAPI_BASE_URL = "https://serpapi.com/search.json";
    private static final int TIMEOUT = 15000; // 15秒超时

    /**
     * 构造函数，允许注入配置
     */
    public WebSearchTools(String serpApiKey, String searchEngine) {
        this.serpApiKey = serpApiKey;
        this.searchEngine = searchEngine;
    }

    /**
     * 默认构造函数，使用默认配置
     */
    public WebSearchTools() {
        this.serpApiKey = "adc33aa598fcca613161f2c0100eb0683fb67791824abc0478ae9c2f2ccb39b1";
        this.searchEngine = "google";
    }

    /**
     * 执行实际的搜索请求，调用 SerpAPI 并返回结构化结果
     *
     * @param query      搜索关键词
     * @param numResults 返回结果数量，默认为 10
     * @return 格式化的搜索结果字符串
     */
    private String doSearch(String query, Integer numResults) {
        if (serpApiKey == null || serpApiKey.isEmpty()) {
            log.error("SerpAPI key 未配置，请在配置文件中设置 serpapi.key");
            return "搜索失败：SerpAPI key 未配置";
        }

        if (query == null || query.trim().isEmpty()) {
            log.warn("搜索关键词为空");
            return "搜索失败：搜索关键词不能为空";
        }

        try {
            // 构建请求 URL
            int num = (numResults != null && numResults > 0) ? numResults : 10;
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String urlString = String.format("%s?engine=%s&q=%s&api_key=%s&num=%d",
                    SERPAPI_BASE_URL, searchEngine, encodedQuery, serpApiKey, num);

            // 发起 HTTP 请求
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT);
            conn.setReadTimeout(TIMEOUT);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                log.error("SerpAPI 请求失败，响应码: {}", responseCode);
                return String.format("搜索失败：API 响应码 %d", responseCode);
            }

            // 读取响应
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            // 解析 JSON 响应并格式化输出
            return parseSearchResults(response.toString(), query);

        } catch (Exception e) {
            log.error("执行搜索时发生异常: {}", e.getMessage(), e);
            return String.format("搜索失败：%s", e.getMessage());
        }
    }

    /**
     * 解析 SerpAPI 返回的 JSON 数据，提取关键信息并格式化
     *
     * @param jsonResponse SerpAPI 返回的 JSON 字符串
     * @param query        原始搜索关键词
     * @return 格式化的搜索结果
     */
    private String parseSearchResults(String jsonResponse, String query) {
        try {
            JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
            StringBuilder result = new StringBuilder();
            result.append(String.format("搜索关键词: %s\n", query));
            result.append("=" .repeat(50)).append("\n\n");

            // 提取 Answer Box（直接答案）
            if (root.has("answer_box")) {
                JsonObject answerBox = root.getAsJsonObject("answer_box");
                result.append("【直接答案】\n");
                if (answerBox.has("answer")) {
                    result.append(answerBox.get("answer").getAsString()).append("\n");
                }
                if (answerBox.has("snippet")) {
                    result.append(answerBox.get("snippet").getAsString()).append("\n");
                }
                result.append("\n");
            }

            // 提取 Knowledge Graph（知识图谱）
            if (root.has("knowledge_graph")) {
                JsonObject kg = root.getAsJsonObject("knowledge_graph");
                result.append("【知识图谱】\n");
                if (kg.has("title")) {
                    result.append("标题: ").append(kg.get("title").getAsString()).append("\n");
                }
                if (kg.has("description")) {
                    result.append("描述: ").append(kg.get("description").getAsString()).append("\n");
                }
                result.append("\n");
            }

            // 提取 Organic Results（有机搜索结果）
            if (root.has("organic_results")) {
                JsonArray organicResults = root.getAsJsonArray("organic_results");
                result.append("【搜索结果】\n");

                int count = 1;
                for (JsonElement element : organicResults) {
                    JsonObject item = element.getAsJsonObject();
                    result.append(String.format("%d. ", count++));

                    if (item.has("title")) {
                        result.append(item.get("title").getAsString()).append("\n");
                    }

                    if (item.has("link")) {
                        result.append("   链接: ").append(item.get("link").getAsString()).append("\n");
                    }

                    if (item.has("snippet")) {
                        result.append("   摘要: ").append(item.get("snippet").getAsString()).append("\n");
                    }

                    result.append("\n");
                }
            }

            // 提取 Related Questions（相关问题）
            if (root.has("related_questions")) {
                JsonArray relatedQuestions = root.getAsJsonArray("related_questions");
                result.append("【相关问题】\n");

                for (JsonElement element : relatedQuestions) {
                    JsonObject item = element.getAsJsonObject();
                    if (item.has("question")) {
                        result.append("• ").append(item.get("question").getAsString()).append("\n");
                        if (item.has("snippet")) {
                            result.append("  ").append(item.get("snippet").getAsString()).append("\n");
                        }
                    }
                }
                result.append("\n");
            }

            return result.toString();

        } catch (Exception e) {
            log.error("解析搜索结果时发生异常: {}", e.getMessage(), e);
            return "搜索结果解析失败：" + e.getMessage();
        }
    }

    /**
     * 使用搜索引擎搜索指定的关键词，返回结构化的搜索结果。
     *
     * @param query      搜索关键词
     * @param numResults 返回结果数量（可选，默认 10）
     * @return 格式化的搜索结果
     */
    @Tool(description = "使用搜索引擎搜索指定的关键词，返回网页搜索结果、相关问题等信息。")
    public String webSearch(
            @ToolParam(description = "搜索关键词") String query,
            @ToolParam(description = "返回结果数量，默认为 10") Integer numResults) {
        return doSearch(query, numResults);
    }

    /**
     * 简化版搜索方法，只需要搜索关键词
     *
     * @param query 搜索关键词
     * @return 格式化的搜索结果
     */
    @Tool(description = "使用搜索引擎搜索指定的关键词，返回默认数量的搜索结果。")
    public String searchSimple(@ToolParam(description = "搜索关键词") String query) {
        return doSearch(query, 10);
    }

}