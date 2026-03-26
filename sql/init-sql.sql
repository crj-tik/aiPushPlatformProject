-- 人员档案表
CREATE TABLE IF NOT EXISTS persons (
                                       id BIGSERIAL PRIMARY KEY,
                                       name VARCHAR(50) NOT NULL,
    id_card VARCHAR(18) UNIQUE,
    department VARCHAR(50),
    position VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 创建索引
CREATE INDEX idx_persons_department ON persons(department);
CREATE INDEX idx_persons_name ON persons(name);

-- 监控记录表
CREATE TABLE IF NOT EXISTS monitor_records (
                                               id BIGSERIAL PRIMARY KEY,
                                               person_id BIGINT NOT NULL,
                                               person_name VARCHAR(50),
    analysis_type VARCHAR(50),
    analysis_result TEXT,
    risk_level VARCHAR(20),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 创建索引
CREATE INDEX idx_monitor_person_id ON monitor_records(person_id);
CREATE INDEX idx_monitor_risk_level ON monitor_records(risk_level);