-- Read-model projections fed by integration events (RabbitMQ).

CREATE TABLE report_event (
    id NUMBER(19,0) NOT NULL,
    aggregate_version NUMBER(19,0),
    event_timestamp TIMESTAMP(6),
    processed_at TIMESTAMP(6),
    status VARCHAR2(30 char) CHECK ((status IN ('RECEIVED','PROCESSED','FAILED','RETRIED'))),
    correlation_id VARCHAR2(100 char),
    event_type VARCHAR2(100 char) NOT NULL,
    source_system VARCHAR2(100 char),
    aggregate_id VARCHAR2(255 char),
    aggregate_type VARCHAR2(255 char),
    error_message VARCHAR2(4000 char),
    event_id VARCHAR2(255 char) NOT NULL,
    payload CLOB,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (id),
    CONSTRAINT uk_report_event_event_id UNIQUE (event_id)
);

CREATE TABLE report_party (
    id NUMBER(19,0) NOT NULL,
    deleted NUMBER(1,0) NOT NULL CHECK ((deleted IN (0,1))),
    aggregate_version NUMBER(19,0) NOT NULL,
    report_date TIMESTAMP(6),
    mobile VARCHAR2(20 char),
    mobile_number VARCHAR2(20 char),
    national_code VARCHAR2(20 char),
    party_no VARCHAR2(50 char),
    status VARCHAR2(50 char),
    last_event_id VARCHAR2(100 char) NOT NULL,
    email VARCHAR2(150 char),
    party_family VARCHAR2(150 char),
    party_name VARCHAR2(150 char),
    aggregate_id VARCHAR2(255 char) NOT NULL,
    payload CLOB,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (id),
    CONSTRAINT uk_report_party_aggregate_id UNIQUE (aggregate_id)
);

CREATE TABLE report_card (
    id NUMBER(19,0) NOT NULL,
    deleted NUMBER(1,0) NOT NULL CHECK ((deleted IN (0,1))),
    aggregate_version NUMBER(19,0) NOT NULL,
    report_date TIMESTAMP(6),
    national_code VARCHAR2(20 char),
    card_number VARCHAR2(30 char),
    account_number VARCHAR2(50 char),
    card_status VARCHAR2(50 char),
    status VARCHAR2(50 char),
    last_event_id VARCHAR2(100 char) NOT NULL,
    aggregate_id VARCHAR2(255 char) NOT NULL,
    payload CLOB,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (id),
    CONSTRAINT uk_report_card_aggregate_id UNIQUE (aggregate_id)
);

CREATE TABLE report_account (
    id NUMBER(19,0) NOT NULL,
    balance NUMBER(18,2),
    deleted NUMBER(1,0) NOT NULL CHECK ((deleted IN (0,1))),
    account_created_at TIMESTAMP(6),
    account_updated_at TIMESTAMP(6),
    aggregate_version NUMBER(19,0) NOT NULL,
    party_id NUMBER(19,0),
    national_code VARCHAR2(20 char),
    account_number VARCHAR2(50 char),
    account_type VARCHAR2(50 char),
    status VARCHAR2(50 char),
    last_event_id VARCHAR2(100 char) NOT NULL,
    party_name VARCHAR2(200 char),
    aggregate_id VARCHAR2(255 char) NOT NULL,
    payload CLOB,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (id),
    CONSTRAINT uk_report_account_aggregate_id UNIQUE (aggregate_id)
);

CREATE TABLE report_transaction (
    id NUMBER(19,0) NOT NULL,
    amount NUMBER(18,2),
    deleted NUMBER(1,0) NOT NULL CHECK ((deleted IN (0,1))),
    aggregate_version NUMBER(19,0) NOT NULL,
    report_date TIMESTAMP(6),
    transaction_date TIMESTAMP(6),
    account_number VARCHAR2(50 char),
    status VARCHAR2(50 char),
    transaction_type VARCHAR2(50 char),
    last_event_id VARCHAR2(100 char) NOT NULL,
    transaction_ref VARCHAR2(100 char),
    aggregate_id VARCHAR2(255 char) NOT NULL,
    payload CLOB,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (id),
    CONSTRAINT uk_report_transaction_aggregate_id UNIQUE (aggregate_id)
);

CREATE TABLE report_product (
    id NUMBER(19,0) NOT NULL,
    deleted NUMBER(1,0) NOT NULL CHECK ((deleted IN (0,1))),
    aggregate_version NUMBER(19,0) NOT NULL,
    product_code NUMBER(19,0),
    report_date TIMESTAMP(6),
    national_id VARCHAR2(20 char),
    status VARCHAR2(50 char),
    last_event_id VARCHAR2(100 char) NOT NULL,
    product_name VARCHAR2(200 char),
    aggregate_id VARCHAR2(255 char) NOT NULL,
    payload CLOB,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (id),
    CONSTRAINT uk_report_product_aggregate_id UNIQUE (aggregate_id)
);

CREATE TABLE report_loan (
    id NUMBER(19,0) NOT NULL,
    deleted NUMBER(1,0) NOT NULL CHECK ((deleted IN (0,1))),
    installment_count NUMBER(10,0),
    paid_installment_count NUMBER(10,0),
    requested_amount NUMBER(18,2),
    total_interest NUMBER(18,2),
    total_remaining_before_due_date NUMBER(18,2),
    total_remaining_interest NUMBER(18,2),
    total_remaining_past_due_date NUMBER(18,2),
    total_remaining_penalty NUMBER(18,2),
    total_remaining_post_maturity_interest NUMBER(18,2),
    total_remaining_principal NUMBER(18,2),
    aggregate_version NUMBER(19,0) NOT NULL,
    creation_date TIMESTAMP(6),
    first_installment_date TIMESTAMP(6),
    last_installment_date TIMESTAMP(6),
    party_id NUMBER(19,0),
    source_loan_id NUMBER(19,0),
    national_code VARCHAR2(20 char),
    unit_code VARCHAR2(20 char),
    file_no VARCHAR2(50 char),
    loan_file_no VARCHAR2(50 char),
    loan_status VARCHAR2(50 char),
    last_event_id VARCHAR2(100 char) NOT NULL,
    loan_profile_name VARCHAR2(200 char),
    national_name VARCHAR2(200 char),
    unit_name VARCHAR2(200 char),
    aggregate_id VARCHAR2(255 char) NOT NULL,
    payload CLOB,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (id),
    CONSTRAINT uk_report_loan_aggregate_id UNIQUE (aggregate_id)
);

CREATE TABLE report_installment (
    id NUMBER(19,0) NOT NULL,
    deleted NUMBER(1,0) NOT NULL CHECK ((deleted IN (0,1))),
    installment_amount NUMBER(18,2),
    installment_no NUMBER(10,0),
    remaining_interest NUMBER(18,2),
    remaining_penalty NUMBER(18,2),
    remaining_post_maturity_interest NUMBER(18,2),
    remaining_principal NUMBER(18,2),
    remaining_total NUMBER(18,2),
    total_paid NUMBER(18,2),
    aggregate_version NUMBER(19,0) NOT NULL,
    due_date TIMESTAMP(6),
    last_payment_date TIMESTAMP(6),
    loan_id NUMBER(19,0) NOT NULL,
    source_installment_id NUMBER(19,0),
    installment_status VARCHAR2(50 char),
    loan_file_no VARCHAR2(50 char),
    last_event_id VARCHAR2(100 char) NOT NULL,
    aggregate_id VARCHAR2(255 char) NOT NULL,
    payload CLOB,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (id),
    CONSTRAINT uk_report_installment_aggregate_id UNIQUE (aggregate_id)
);

ALTER TABLE report_installment
    ADD CONSTRAINT fk_report_installment_loan_id FOREIGN KEY (loan_id) REFERENCES report_loan (id);

CREATE INDEX idx_report_account_number ON report_account (account_number);

CREATE INDEX idx_report_event_aggregate_id ON report_event (aggregate_id);

CREATE INDEX idx_report_event_event_type ON report_event (event_type);

CREATE INDEX idx_report_event_timestamp ON report_event (event_timestamp);

CREATE INDEX idx_report_installment_loan ON report_installment (loan_id);

CREATE INDEX idx_report_installment_loan_file ON report_installment (loan_file_no);

CREATE INDEX idx_report_installment_due_date ON report_installment (due_date);

CREATE INDEX idx_report_installment_status ON report_installment (installment_status);

CREATE INDEX idx_report_loan_party ON report_loan (party_id);

CREATE INDEX idx_report_loan_file_no ON report_loan (file_no);

CREATE INDEX idx_report_loan_loan_file_no ON report_loan (loan_file_no);

CREATE INDEX idx_report_loan_national_code ON report_loan (national_code);

CREATE INDEX idx_report_loan_unit_code ON report_loan (unit_code);

CREATE INDEX idx_report_loan_creation_date ON report_loan (creation_date);
