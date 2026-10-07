CREATE SEQUENCE REPORT_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE REPORT_EVENT_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE report_event (
    id BIGINT PRIMARY KEY,
    event_id VARCHAR(100),
    event_type VARCHAR(100),
    aggregate_type VARCHAR(100),
    aggregate_id VARCHAR(100),
    aggregate_version BIGINT,
    payload CLOB,
    source_system VARCHAR(100),
    correlation_id VARCHAR(100),
    status VARCHAR(30),
    processed_at TIMESTAMP,
    error_message VARCHAR(4000),
    event_timestamp TIMESTAMP,
    created_by VARCHAR(255),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP
);

CREATE TABLE report_account (
    id BIGINT PRIMARY KEY,
    aggregate_id VARCHAR(100),
    aggregate_version BIGINT,
    last_event_id VARCHAR(100),
    deleted BOOLEAN,
    party_id BIGINT,
    national_code VARCHAR(20),
    party_name VARCHAR(200),
    account_number VARCHAR(50),
    account_type VARCHAR(50),
    balance DECIMAL(18, 2),
    account_created_at TIMESTAMP,
    account_updated_at TIMESTAMP,
    created_by VARCHAR(255),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(255),
    last_modified_date TIMESTAMP
);
