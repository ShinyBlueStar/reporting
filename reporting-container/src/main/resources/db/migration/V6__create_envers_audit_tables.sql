-- Hibernate Envers audit tables (suffix _AUD). LOB columns are CLOB here (Hibernate generates varchar2(255) for them).

CREATE TABLE revinfo (
    rev NUMBER(10,0) NOT NULL,
    revtstmp NUMBER(19,0),
    PRIMARY KEY (rev)
);

CREATE TABLE report_category_aud (
    id NUMBER(19,0) NOT NULL,
    rev NUMBER(10,0) NOT NULL,
    revtype NUMBER(3,0),
    is_active NUMBER(1,0) CHECK ((is_active IN (0,1))),
    category_name VARCHAR2(100 char),
    description VARCHAR2(500 char),
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (rev, id)
);

CREATE TABLE report_definition_aud (
    id NUMBER(19,0) NOT NULL,
    rev NUMBER(10,0) NOT NULL,
    revtype NUMBER(3,0),
    is_active NUMBER(1,0) CHECK ((is_active IN (0,1))),
    timeout_seconds NUMBER(10,0),
    version NUMBER(10,0),
    max_export_rows NUMBER(19,0),
    output_type VARCHAR2(50 char),
    category VARCHAR2(100 char),
    report_code VARCHAR2(100 char),
    report_name VARCHAR2(200 char),
    report_description VARCHAR2(1000 char),
    sql_query CLOB,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (rev, id)
);

CREATE TABLE report_execution_aud (
    id NUMBER(19,0) NOT NULL,
    rev NUMBER(10,0) NOT NULL,
    revtype NUMBER(3,0),
    execution_duration_ms NUMBER(19,0),
    execution_end_time TIMESTAMP(6),
    execution_start_time TIMESTAMP(6),
    generated_file_id NUMBER(19,0),
    report_definition_id NUMBER(19,0),
    total_record_count NUMBER(19,0),
    execution_status VARCHAR2(50 char),
    correlation_id VARCHAR2(100 char),
    requested_by VARCHAR2(100 char),
    error_message VARCHAR2(2000 char),
    execution_source VARCHAR2(255 char),
    request_parameters CLOB,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (rev, id)
);

CREATE TABLE report_execution_detail_aud (
    id NUMBER(19,0) NOT NULL,
    rev NUMBER(10,0) NOT NULL,
    revtype NUMBER(3,0),
    step_order NUMBER(10,0),
    duration_ms NUMBER(19,0),
    end_time TIMESTAMP(6),
    report_execution_id NUMBER(19,0),
    start_time TIMESTAMP(6),
    step_status VARCHAR2(50 char),
    step_name VARCHAR2(200 char),
    error_message VARCHAR2(2000 char),
    message VARCHAR2(2000 char),
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (rev, id)
);

CREATE TABLE report_file_aud (
    id NUMBER(19,0) NOT NULL,
    rev NUMBER(10,0) NOT NULL,
    revtype NUMBER(3,0),
    file_size NUMBER(19,0),
    file_extension VARCHAR2(20 char),
    content_type VARCHAR2(100 char),
    checksum VARCHAR2(128 char),
    bucket_name VARCHAR2(200 char),
    file_name VARCHAR2(500 char),
    original_file_name VARCHAR2(500 char),
    object_name VARCHAR2(1000 char),
    storage_provider VARCHAR2(255 char),
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (rev, id)
);

CREATE TABLE report_parameter_aud (
    id NUMBER(19,0) NOT NULL,
    rev NUMBER(10,0) NOT NULL,
    revtype NUMBER(3,0),
    display_order NUMBER(10,0),
    max_length NUMBER(10,0),
    min_length NUMBER(10,0),
    required NUMBER(1,0) CHECK ((required IN (0,1))),
    report_definition_id NUMBER(19,0),
    parameter_type VARCHAR2(50 char),
    parameter_name VARCHAR2(100 char),
    parameter_label VARCHAR2(200 char),
    default_value VARCHAR2(500 char),
    validation_regex VARCHAR2(500 char),
    help_text VARCHAR2(1000 char),
    placeholder VARCHAR2(255 char),
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (rev, id)
);

CREATE TABLE report_schedule_aud (
    id NUMBER(19,0) NOT NULL,
    rev NUMBER(10,0) NOT NULL,
    revtype NUMBER(3,0),
    is_active NUMBER(1,0) CHECK ((is_active IN (0,1))),
    last_execution_time TIMESTAMP(6),
    next_execution_time TIMESTAMP(6),
    report_definition_id NUMBER(19,0),
    output_type VARCHAR2(50 char),
    cron_expression VARCHAR2(100 char),
    schedule_name VARCHAR2(200 char),
    notify_email VARCHAR2(500 char),
    last_status VARCHAR2(255 char),
    schedule_parameters CLOB,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (rev, id)
);

CREATE TABLE report_template_aud (
    id NUMBER(19,0) NOT NULL,
    rev NUMBER(10,0) NOT NULL,
    revtype NUMBER(3,0),
    report_definition_id NUMBER(19,0),
    sheet_name VARCHAR2(200 char),
    template_name VARCHAR2(200 char),
    columns_json CLOB,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (rev, id)
);

CREATE TABLE report_transaction_aud (
    id NUMBER(19,0) NOT NULL,
    rev NUMBER(10,0) NOT NULL,
    revtype NUMBER(3,0),
    amount NUMBER(18,2),
    report_date TIMESTAMP(6),
    transaction_date TIMESTAMP(6),
    account_number VARCHAR2(50 char),
    status VARCHAR2(50 char),
    transaction_type VARCHAR2(50 char),
    transaction_ref VARCHAR2(100 char),
    payload CLOB,
    PRIMARY KEY (rev, id)
);

CREATE TABLE report_validation_rule_aud (
    id NUMBER(19,0) NOT NULL,
    rev NUMBER(10,0) NOT NULL,
    revtype NUMBER(3,0),
    enabled NUMBER(1,0) CHECK ((enabled IN (0,1))),
    execution_order NUMBER(10,0),
    report_definition_id NUMBER(19,0),
    error_code VARCHAR2(50 char),
    rule_type VARCHAR2(50 char) CHECK ((rule_type IN ('REQUIRED','AT_LEAST_ONE_REQUIRED','ALL_OR_NONE','EXACTLY_ONE','DATE_RANGE','MAX_DATE_RANGE','GREATER_THAN','LESS_THAN','BETWEEN','MAX_LENGTH','MIN_LENGTH','FIXED_LENGTH','REGEX','POSITIVE','NEGATIVE','NUMBER_RANGE','PAST_DATE','FUTURE_DATE','DATE_DIFF','IN_LIST','NOT_IN_LIST','REQUIRED_IF','FORBIDDEN_IF','UNIQUE','EXISTS','NOT_EXISTS','CUSTOM_EXPRESSION'))),
    error_message VARCHAR2(1000 char),
    configuration CLOB,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (rev, id)
);

ALTER TABLE report_category_aud
    ADD CONSTRAINT fk_report_category_aud_rev FOREIGN KEY (rev) REFERENCES revinfo;

ALTER TABLE report_definition_aud
    ADD CONSTRAINT fk_report_definition_aud_rev FOREIGN KEY (rev) REFERENCES revinfo;

ALTER TABLE report_execution_aud
    ADD CONSTRAINT fk_report_execution_aud_rev FOREIGN KEY (rev) REFERENCES revinfo;

ALTER TABLE report_execution_detail_aud
    ADD CONSTRAINT fk_report_execution_detail_aud_rev FOREIGN KEY (rev) REFERENCES revinfo;

ALTER TABLE report_file_aud
    ADD CONSTRAINT fk_report_file_aud_rev FOREIGN KEY (rev) REFERENCES revinfo;

ALTER TABLE report_parameter_aud
    ADD CONSTRAINT fk_report_parameter_aud_rev FOREIGN KEY (rev) REFERENCES revinfo;

ALTER TABLE report_schedule_aud
    ADD CONSTRAINT fk_report_schedule_aud_rev FOREIGN KEY (rev) REFERENCES revinfo;

ALTER TABLE report_template_aud
    ADD CONSTRAINT fk_report_template_aud_rev FOREIGN KEY (rev) REFERENCES revinfo;

ALTER TABLE report_transaction_aud
    ADD CONSTRAINT fk_report_transaction_aud_rev FOREIGN KEY (rev) REFERENCES revinfo;

ALTER TABLE report_validation_rule_aud
    ADD CONSTRAINT fk_report_validation_rule_aud_rev FOREIGN KEY (rev) REFERENCES revinfo;
