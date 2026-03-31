-- MySQL 8.x schema for archive and warning services

-- 人员档案表
CREATE TABLE IF NOT EXISTS persons (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    id_card VARCHAR(18) UNIQUE,
    department VARCHAR(50),
    position VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_persons_department ON persons(department);
CREATE INDEX idx_persons_name ON persons(name);

-- 监控记录表
CREATE TABLE IF NOT EXISTS monitor_records (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    person_id BIGINT NOT NULL,
    person_name VARCHAR(50),
    person_id_card VARCHAR(18),
    department VARCHAR(50),
    position VARCHAR(50),
    analysis_type VARCHAR(50),
    analysis_content TEXT,
    analysis_result TEXT,
    risk_level VARCHAR(20),
    risk_score INT,
    suggestions TEXT,
    ai_model VARCHAR(100),
    prompt_tokens INT,
    completion_tokens INT,
    total_tokens INT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    update_by VARCHAR(64),
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_monitor_person_id ON monitor_records(person_id);
CREATE INDEX idx_monitor_risk_level ON monitor_records(risk_level);
CREATE INDEX idx_monitor_department ON monitor_records(department);
CREATE INDEX idx_monitor_create_time ON monitor_records(create_time);
CREATE INDEX idx_monitor_deleted ON monitor_records(deleted);
