-- PostgreSQL Chat Memory Table
-- 用于存储Spring AI的对话记忆

CREATE TABLE IF NOT EXISTS chat_memory (
    id VARCHAR(255) PRIMARY KEY,
    conversation_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    content TEXT NOT NULL,
    metadata TEXT,
    message_type VARCHAR(50) NOT NULL
);

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_conversation_id ON chat_memory(conversation_id);
CREATE INDEX IF NOT EXISTS idx_created_at ON chat_memory(created_at);

-- 添加注释
COMMENT ON TABLE chat_memory IS 'Spring AI对话记忆存储表';
COMMENT ON COLUMN chat_memory.id IS '消息唯一标识';
COMMENT ON COLUMN chat_memory.conversation_id IS '会话ID，用于区分不同的对话';
COMMENT ON COLUMN chat_memory.created_at IS '创建时间';
COMMENT ON COLUMN chat_memory.content IS '消息内容';
COMMENT ON COLUMN chat_memory.metadata IS '元数据（JSON格式）';
COMMENT ON COLUMN chat_memory.message_type IS '消息类型：USER/ASSISTANT/SYSTEM';


