-- Sequences used by the JPA entities (allocationSize = 1) and by Envers (revinfo_seq, allocationSize = 50).

CREATE SEQUENCE report_category_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE report_definition_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE report_event_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE report_execution_detail_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE report_execution_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE report_file_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE report_parameter_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE report_schedule_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE report_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE report_template_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE report_validation_rule_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE revinfo_seq START WITH 1 INCREMENT BY 50;
