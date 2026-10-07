-- Values aligned with the LoanStatus and InstallmentStatus enums of the loan domain.

INSERT INTO loan_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('CREATED', 'تشکیل شده', 'Created', 1, 1);
INSERT INTO loan_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('NOT_DUE', 'سررسید نشده', 'Not Due', 2, 1);
INSERT INTO loan_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('AFTER_DUE', 'بعد از سررسید', 'After Due', 3, 1);
INSERT INTO loan_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('PAST_DUE', 'سررسید گذشته', 'Past Due', 4, 1);
INSERT INTO loan_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('OVERDUE', 'معوق', 'Overdue', 5, 1);
INSERT INTO loan_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('DOUBTFUL', 'مشکوک الوصول', 'Doubtful', 6, 1);
INSERT INTO loan_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('LOSS', 'مشکوک الوصول 2', 'Loss', 7, 1);
INSERT INTO loan_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('CLOSED', 'بسته شده', 'Closed', 8, 1);
INSERT INTO loan_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('CANCELLED', 'ابطال شده', 'Cancelled', 9, 1);

INSERT INTO installment_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('NOT_DUE', 'سررسید نشده', 'Not Due', 1, 1);
INSERT INTO installment_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('DUE', 'سررسید شده', 'Due', 2, 1);
INSERT INTO installment_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('AFTER_DUE', 'بعد از سررسید', 'After Due', 3, 1);
INSERT INTO installment_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('PAST_DUE', 'سررسید گذشته', 'Past Due', 4, 1);
INSERT INTO installment_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('OVERDUE', 'معوق', 'Overdue', 5, 1);
INSERT INTO installment_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('DOUBTFUL', 'مشکوک الوصول', 'Doubtful', 6, 1);
INSERT INTO installment_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('LOSS', 'مشکوک الوصول 2', 'Loss', 7, 1);
INSERT INTO installment_status_lookup (status_code, status_title_fa, status_title_en, display_order, is_active)
VALUES ('CANCELLED', 'باطل شده', 'Cancelled', 8, 1);
