package com.ly.cookbook.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CookBookAppTest {

    @Resource
    private CookBookApp cookBookApp;
    @Test
    void chat() {
        String response = cookBookApp.chat("我要吃一个鸡蛋饼,怎么做。可以提供一个视频嘛.");
        System.out.println(response);
    }

    @Test
    void chatWithMemory() {
        cookBookApp.chatWithMemory("2", "我想有牛肉和鸡蛋，可以做什么菜，推荐一下");
        cookBookApp.chatWithMemory("3", "我还希望做法简单一些");
    }
}