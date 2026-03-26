-- 创建向量扩展
CREATE EXTENSION IF NOT EXISTS vector;

-- 向量资源表
CREATE TABLE IF NOT EXISTS vector_resources (
                                                id VARCHAR(100) PRIMARY KEY,
    resource_type VARCHAR(20) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    keywords JSONB,
    sample_questions JSONB,
    metadata JSONB,
    embedding vector(1536),
    weight FLOAT DEFAULT 1.0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 创建向量索引
CREATE INDEX IF NOT EXISTS idx_vector_embedding
    ON vector_resources
    USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100);

-- 消息日志表
CREATE TABLE IF NOT EXISTS message_logs (
                                            id BIGSERIAL PRIMARY KEY,
                                            trace_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(100),
    user_message TEXT,
    matched_resource_id VARCHAR(100),
    matched_resource_name VARCHAR(200),
    similarity_score FLOAT,
    summary TEXT,
    target_platform VARCHAR(50),
    push_status INTEGER DEFAULT 0,
    error_message TEXT,
    context JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX idx_trace_id ON message_logs(trace_id);
CREATE INDEX idx_push_status ON message_logs(push_status);
CREATE INDEX idx_created_at ON message_logs(created_at);