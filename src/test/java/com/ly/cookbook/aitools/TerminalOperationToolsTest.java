package com.ly.cookbook.aitools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerminalOperationToolsTest {
    TerminalOperationTools terminalOperationTools = new TerminalOperationTools();
    @Test
    void executeCommand() {
        String s = terminalOperationTools.executeCommand("ping www.baidu.com");
        System.out.println(s);
    }
}