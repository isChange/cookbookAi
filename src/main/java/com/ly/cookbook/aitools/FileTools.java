package com.ly.cookbook.aitools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Optional;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/9/24 15:43
 * @email liuyia2022@163.com
 */
@Slf4j
public class FileTools {

    private static final Path DEFAULT_DIR = Paths.get(System.getProperty("user.dir"), "temp", "file");

    /**
    * 将文本内容写入项目根目录下 temp/file 目录中的指定文件（UTF-8）。
    *
    * 1) 当文件不存在时自动创建父目录
    * 2) append=true 追加写入；append=false 覆盖写入
    *
    * @param fileName  目标文件名称，例如 output.txt
    * @param content   待写入的文本内容
    * @param append    是否追加写入
    * @return 写入是否成功
    */
    public static boolean doWrite(String fileName, String content, boolean append) {
        Path path = DEFAULT_DIR.resolve(fileName);
        try {
            Files.createDirectories(path.getParent());

            OpenOption[] options = append
                    ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND}
                    : new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE};

            Files.write(path, content.getBytes(StandardCharsets.UTF_8), options);
            return true;
        } catch (IOException e) {
            log.error("写入文件失败: {}", path, e);
            return false;
        }
    }

    /**
    * 覆盖写入的便捷方法（append=false）。
    */
    @Tool(description = "将文本内容写入指定文件（UTF-8）。")
    public String writeText(@ToolParam(description = "写入文件的文件名称") String fileName,
                          @ToolParam(description = "待写入的文本内容") String content) {
        if (doWrite(fileName, content, false)) {
            return String.format("写入文件成功! 文件名: %s", fileName);
        }
        return String.format("写入文件失败! 文件名: %s", fileName);
    }

    /**
    * 从项目根目录下 temp/file 目录读取指定文件的文本内容（UTF-8）。
    *
    * @param fileName 目标文件名称，例如 output.txt
    * @return 读取到的文本内容
    */
    public static Optional<String> doRead(String fileName) {
        Path path = DEFAULT_DIR.resolve(fileName);
        if (!Files.exists(path)) {
            log.warn("读取文件失败，文件不存在: {}", path);
            return Optional.empty();
        }
        try {
            return Optional.of(Files.readString(path, StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("读取文件失败: {}", path, e);
            return Optional.empty();
        }
    }

    @Tool(description = "读取指定文件的文本内容（UTF-8）。")
    public String readText(@ToolParam(description = "读取文件的文件名称") String fileName) {
        return doRead(fileName)
                .orElseGet(() -> String.format("读取文件失败! 文件名: %s", fileName));
    }

}
