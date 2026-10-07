-- Status lookups. The foreign keys are owned by Flyway (entities use ConstraintMode.NO_CONSTRAINT).

CREATE TABLE loan_status_lookup (
    display_order NUMBER(10,0) NOT NULL,
    is_active NUMBER(1,0) NOT NULL CHECK ((is_active IN (0,1))),
    status_code VARCHAR2(50 char) NOT NULL,
    status_title_en VARCHAR2(200 char) NOT NULL,
    status_title_fa VARCHAR2(200 char) NOT NULL,
    PRIMARY KEY (status_code)
);

CREATE TABLE installment_status_lookup (
    display_order NUMBER(10,0) NOT NULL,
    is_active NUMBER(1,0) NOT NULL CHECK ((is_active IN (0,1))),
    status_code VARCHAR2(50 char) NOT NULL,
    status_title_en VARCHAR2(200 char) NOT NULL,
    status_title_fa VARCHAR2(200 char) NOT NULL,
    PRIMARY KEY (status_code)
);

ALTER TABLE report_loan
    ADD CONSTRAINT fk_report_loan_loan_status FOREIGN KEY (loan_status) REFERENCES loan_status_lookup (status_code);

ALTER TABLE report_installment
    ADD CONSTRAINT fk_report_installment_installment_status FOREIGN KEY (installment_status) REFERENCES installment_status_lookup (status_code);
