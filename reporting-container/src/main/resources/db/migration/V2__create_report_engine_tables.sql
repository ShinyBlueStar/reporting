-- Report engine (command side): definitions, parameters, schedules, templates, validation rules, executions, files.

CREATE TABLE report_category (
    id NUMBER(19,0) NOT NULL,
    is_active NUMBER(1,0) CHECK ((is_active IN (0,1))),
    category_name VARCHAR2(100 char) NOT NULL,
    description VARCHAR2(500 char),
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (id),
    CONSTRAINT uk_report_category_name UNIQUE (category_name)
);

CREATE TABLE report_definition (
    id NUMBER(19,0) NOT NULL,
    is_active NUMBER(1,0) CHECK ((is_active IN (0,1))),
    timeout_seconds NUMBER(10,0),
    version NUMBER(10,0),
    max_export_rows NUMBER(19,0),
    output_type VARCHAR2(50 char),
    category VARCHAR2(100 char),
    report_code VARCHAR2(100 char) NOT NULL,
    report_name VARCHAR2(200 char) NOT NULL,
    report_description VARCHAR2(1000 char),
    sql_query CLOB,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (id),
    CONSTRAINT uk_report_definition_report_code UNIQUE (report_code),
    CONSTRAINT uk_report_definition_report_name UNIQUE (report_name)
);

CREATE TABLE report_parameter (
    id NUMBER(19,0) NOT NULL,
    display_order NUMBER(10,0),
    max_length NUMBER(10,0),
    min_length NUMBER(10,0),
    required NUMBER(1,0) CHECK ((required IN (0,1))),
    report_definition_id NUMBER(19,0),
    parameter_type VARCHAR2(50 char),
    parameter_name VARCHAR2(100 char) NOT NULL,
    parameter_label VARCHAR2(200 char),
    default_value VARCHAR2(500 char),
    validation_regex VARCHAR2(500 char),
    help_text VARCHAR2(1000 char),
    placeholder VARCHAR2(255 char),
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (id)
);

CREATE TABLE report_schedule (
    id NUMBER(19,0) NOT NULL,
    is_active NUMBER(1,0) CHECK ((is_active IN (0,1))),
    last_execution_time TIMESTAMP(6),
    next_execution_time TIMESTAMP(6),
    report_definition_id NUMBER(19,0),
    output_type VARCHAR2(50 char),
    cron_expression VARCHAR2(100 char) NOT NULL,
    schedule_name VARCHAR2(200 char) NOT NULL,
    notify_email VARCHAR2(500 char),
    last_status VARCHAR2(255 char),
    schedule_parameters CLOB,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (id)
);

CREATE TABLE report_template (
    id NUMBER(19,0) NOT NULL,
    report_definition_id NUMBER(19,0) NOT NULL,
    sheet_name VARCHAR2(200 char) NOT NULL,
    template_name VARCHAR2(200 char) NOT NULL,
    columns_json CLOB NOT NULL,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (id),
    CONSTRAINT uk_report_template_name UNIQUE (report_definition_id, template_name)
);

CREATE TABLE report_validation_rule (
    id NUMBER(19,0) NOT NULL,
    enabled NUMBER(1,0) CHECK ((enabled IN (0,1))),
    execution_order NUMBER(10,0),
    report_definition_id NUMBER(19,0) NOT NULL,
    error_code VARCHAR2(50 char),
    rule_type VARCHAR2(50 char) NOT NULL CHECK ((rule_type IN ('REQUIRED','AT_LEAST_ONE_REQUIRED','ALL_OR_NONE','EXACTLY_ONE','DATE_RANGE','MAX_DATE_RANGE','GREATER_THAN','LESS_THAN','BETWEEN','MAX_LENGTH','MIN_LENGTH','FIXED_LENGTH','REGEX','POSITIVE','NEGATIVE','NUMBER_RANGE','PAST_DATE','FUTURE_DATE','DATE_DIFF','IN_LIST','NOT_IN_LIST','REQUIRED_IF','FORBIDDEN_IF','UNIQUE','EXISTS','NOT_EXISTS','CUSTOM_EXPRESSION'))),
    error_message VARCHAR2(1000 char),
    configuration CLOB,
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (id)
);

CREATE TABLE report_file (
    id NUMBER(19,0) NOT NULL,
    file_size NUMBER(19,0),
    file_extension VARCHAR2(20 char),
    content_type VARCHAR2(100 char),
    checksum VARCHAR2(128 char),
    bucket_name VARCHAR2(200 char),
    file_name VARCHAR2(500 char) NOT NULL,
    original_file_name VARCHAR2(500 char),
    object_name VARCHAR2(1000 char),
    storage_provider VARCHAR2(255 char),
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (id)
);

CREATE TABLE report_execution (
    id NUMBER(19,0) NOT NULL,
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
    PRIMARY KEY (id),
    CONSTRAINT uk_report_execution_generated_file_id UNIQUE (generated_file_id)
);

CREATE TABLE report_execution_detail (
    id NUMBER(19,0) NOT NULL,
    step_order NUMBER(10,0),
    duration_ms NUMBER(19,0),
    end_time TIMESTAMP(6),
    report_execution_id NUMBER(19,0) NOT NULL,
    start_time TIMESTAMP(6),
    step_status VARCHAR2(50 char),
    step_name VARCHAR2(200 char) NOT NULL,
    error_message VARCHAR2(2000 char),
    message VARCHAR2(2000 char),
    created_date TIMESTAMP(6),
    last_modified_date TIMESTAMP(6),
    created_by VARCHAR2(255 char),
    last_modified_by VARCHAR2(255 char),
    PRIMARY KEY (id)
);

ALTER TABLE report_execution
    ADD CONSTRAINT fk_report_execution_generated_file_id FOREIGN KEY (generated_file_id) REFERENCES report_file (id);

ALTER TABLE report_execution
    ADD CONSTRAINT fk_report_execution_report_definition_id FOREIGN KEY (report_definition_id) REFERENCES report_definition (id);

ALTER TABLE report_parameter
    ADD CONSTRAINT fk_report_parameter_report_definition_id FOREIGN KEY (report_definition_id) REFERENCES report_definition (id);

ALTER TABLE report_schedule
    ADD CONSTRAINT fk_report_schedule_report_definition_id FOREIGN KEY (report_definition_id) REFERENCES report_definition (id);

ALTER TABLE report_template
    ADD CONSTRAINT fk_report_template_report_definition_id FOREIGN KEY (report_definition_id) REFERENCES report_definition (id);

ALTER TABLE report_validation_rule
    ADD CONSTRAINT fk_report_validation_rule_report_definition_id FOREIGN KEY (report_definition_id) REFERENCES report_definition (id);
