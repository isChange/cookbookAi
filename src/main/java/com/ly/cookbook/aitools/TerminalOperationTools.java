package com.ly.cookbook.aitools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description 可以通过 Java 的 Process API 实现终端命令执行，注意 Windows和其他操作系统下的实现略有区别）
 * @createDate：2025/10/3 21:18
 * @email liuyia2022@163.com
 */
@Slf4j
public class TerminalOperationTools {

    private static final int DEFAULT_TIMEOUT = 60; // 默认超时时间（秒）
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    private static final boolean IS_WINDOWS = OS_NAME.contains("windows");

    /**
     * 执行终端命令并返回输出结果。
     *
     * @param command 要执行的命令
     * @return 命令执行结果
     */
    @Tool(description = "执行终端命令并返回输出结果（支持 Windows 和 Linux/Mac）。")
    public String executeCommand(@ToolParam(description = "要执行的终端命令") String command) {
        return executeCommand(command, DEFAULT_TIMEOUT);
    }

    /**
     * 执行终端命令并返回输出结果（可指定超时时间）。
     *
     * @param command 要执行的命令
     * @param timeoutSeconds 超时时间（秒）
     * @return 命令执行结果
     */
    @Tool(description = "执行终端命令并返回输出结果，可指定超时时间。")
    public String executeCommandWithTimeout(
            @ToolParam(description = "要执行的终端命令") String command,
            @ToolParam(description = "超时时间（秒）") int timeoutSeconds) {
        return executeCommand(command, timeoutSeconds);
    }

    /**
     * 内部方法：执行命令的具体实现。
     */
    private String executeCommand(String command, int timeoutSeconds) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        
        // 根据操作系统设置不同的命令执行方式
        if (IS_WINDOWS) {
            processBuilder.command("cmd.exe", "/c", command);
        } else {
            processBuilder.command("sh", "-c", command);
        }
        
        processBuilder.redirectErrorStream(true);
        
        try {
            log.info("执行命令: {}", command);
            Process process = processBuilder.start();
            
            // 读取命令输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            // 等待命令执行完成
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            
            if (!finished) {
                process.destroyForcibly();
                log.warn("命令执行超时: {}", command);
                return String.format("命令执行超时（%d秒）！命令: %s", timeoutSeconds, command);
            }
            
            int exitCode = process.exitValue();
            String result = output.toString();
            
            if (exitCode == 0) {
                log.info("命令执行成功: {}", command);
                return String.format("命令执行成功！\n输出:\n%s", result);
            } else {
                log.error("命令执行失败，退出码: {}", exitCode);
                return String.format("命令执行失败！退出码: %d\n输出:\n%s", exitCode, result);
            }
            
        } catch (IOException e) {
            log.error("执行命令时发生 IO 错误: {}", command, e);
            return String.format("命令执行失败！IO 错误: %s", e.getMessage());
        } catch (InterruptedException e) {
            log.error("命令执行被中断: {}", command, e);
            Thread.currentThread().interrupt();
            return String.format("命令执行被中断！命令: %s", command);
        }
    }

    /**
     * 获取当前工作目录。
     *
     * @return 当前工作目录路径
     */
    @Tool(description = "获取当前工作目录路径。")
    public String getCurrentDirectory() {
        String command = IS_WINDOWS ? "cd" : "pwd";
        return executeCommand(command, 5);
    }

    /**
     * 列出指定目录的文件和文件夹。
     *
     * @param directory 目录路径（可选，默认为当前目录）
     * @return 目录内容列表
     */
    @Tool(description = "列出指定目录的文件和文件夹。")
    public String listDirectory(@ToolParam(description = "目录路径，留空表示当前目录") String directory) {
        String command;
        if (directory == null || directory.trim().isEmpty()) {
            command = IS_WINDOWS ? "dir" : "ls -la";
        } else {
            command = IS_WINDOWS ? "dir \"" + directory + "\"" : "ls -la \"" + directory + "\"";
        }
        return executeCommand(command, 10);
    }
}
