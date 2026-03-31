-- MySQL 8.x schema for push service

-- 向量资源元数据表
-- 当前项目实际向量数据存储在 Milvus，这里只保留 MySQL 可落库的资源元数据
CREATE TABLE IF NOT EXISTS vector_resources (
    id VARCHAR(100) NOT NULL PRIMARY KEY,
    resource_type VARCHAR(20) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    keywords JSON,
    sample_questions JSON,
    metadata JSON,
    embedding_json JSON NULL,
    weight DECIMAL(10,4) DEFAULT 1.0000,
    is_active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_vector_resource_type ON vector_resources(resource_type);
CREATE INDEX idx_vector_active ON vector_resources(is_active);
CREATE INDEX idx_vector_created_at ON vector_resources(created_at);

-- 消息日志表
CREATE TABLE IF NOT EXISTS message_logs (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    trace_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(100),
    user_message TEXT,
    matched_resource_id VARCHAR(100),
    matched_resource_name VARCHAR(200),
    similarity_score DOUBLE,
    summary TEXT,
    target_platform VARCHAR(50),
    push_status INT DEFAULT 0,
    error_message TEXT,
    context JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_trace_id ON message_logs(trace_id);
CREATE INDEX idx_push_status ON message_logs(push_status);
CREATE INDEX idx_created_at ON message_logs(created_at);
