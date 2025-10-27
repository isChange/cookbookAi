package com.ly.cookbook.aitools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceDownloadToolsTest {
    ResourceDownloadTools resourceDownloadTools = new ResourceDownloadTools();
    @Test
    void downloadFile() {
        String url = "https://yun-picture-ly.oss-cn-chengdu.aliyuncs.com/yun-picture/public/1932055329728696322/20250615145700_185b412c05504edf918c28bc49644ad4_8644ebf81a4c510f7cfd6de73bb6b62bd52aa50c.jpeg";
        String fileName = "logo.png";
        String s = resourceDownloadTools.downloadFileAuto(url);
        System.out.println(s);
    }

    @Test
    void checkFileExists() {
    }
}