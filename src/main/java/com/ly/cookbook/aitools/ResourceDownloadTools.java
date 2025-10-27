package com.ly.cookbook.aitools;


import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description 资源下载工具的作用是通过链接下载文件到本地
 * @createDate：2025/10/3 21:19
 * @email liuyia2022@163.com
 */
@Slf4j
public class ResourceDownloadTools {

    private static final Path DEFAULT_DIR = Paths.get(System.getProperty("user.dir"), "temp", "download");

    /**
     * 从指定 URL 下载文件到项目根目录下 temp/download 目录。
     *
     * @param url      下载资源的完整 URL
     * @param fileName 保存的文件名称（可选，若为空则从 URL 中自动提取）
     * @return 下载是否成功
     */
    public static boolean doDownload(String url, String fileName) {
        try {
            // 确保目录存在
            File dir = DEFAULT_DIR.toFile();
            FileUtil.mkdir(dir);

            // 如果未指定文件名，从 URL 中提取
            String targetFileName = fileName;
            if (targetFileName == null || targetFileName.trim().isEmpty()) {
                targetFileName = FileUtil.getName(url);
            }

            // 构建完整路径
            File targetFile = DEFAULT_DIR.resolve(targetFileName).toFile();

            // 使用 hutool 下载文件
            long size = HttpUtil.downloadFile(url, targetFile);
            
            log.info("文件下载成功: {} -> {}, 大小: {} bytes", url, targetFile.getAbsolutePath(), size);
            return size > 0;
        } catch (Exception e) {
            log.error("下载文件失败: {}", url, e);
            return false;
        }
    }

    /**
     * 下载文件到本地的工具方法（指定文件名）。
     */
    @Tool(description = "从指定 URL 下载文件到本地 temp/download 目录。")
    public String downloadFile(@ToolParam(description = "下载资源的完整 URL 地址") String url,
                               @ToolParam(description = "保存的文件名称") String fileName) {
        if (doDownload(url, fileName)) {
            Path filePath = DEFAULT_DIR.resolve(fileName);
            return String.format("下载文件成功! URL: %s, 保存路径: %s", url, filePath.toAbsolutePath());
        }
        return String.format("下载文件失败! URL: %s", url);
    }

    /**
     * 下载文件到本地的工具方法（自动提取文件名）。
     */
    @Tool(description = "从指定 URL 下载文件到本地 temp/download 目录，自动从 URL 中提取文件名。")
    public String downloadFileAuto(@ToolParam(description = "下载资源的完整 URL 地址") String url) {
        String fileName = FileUtil.getName(url);
        if (doDownload(url, fileName)) {
            Path filePath = DEFAULT_DIR.resolve(fileName);
            return String.format("下载文件成功! URL: %s, 保存路径: %s", url, filePath.toAbsolutePath());
        }
        return String.format("下载文件失败! URL: %s", url);
    }

}
