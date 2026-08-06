CREATE TABLE student_performance (
    student_id VARCHAR(255) PRIMARY KEY,
    student_name VARCHAR(255) NOT NULL,
    gpa DECIMAL(5, 2) NOT NULL
);

INSERT INTO student_performance (student_id, student_name, gpa) VALUES ('ST-001', 'Ivanov Ivan', 4.85);
INSERT INTO student_performance (student_id, student_name, gpa) VALUES ('ST-002', 'Petrov Petr', 4.20);
INSERT INTO student_performance (student_id, student_name, gpa) VALUES ('ST-003', 'Sidorov Sidor', 3.50);
INSERT INTO student_performance (student_id, student_name, gpa) VALUES ('ST-004', 'Kuznetsov Aleksey', 2.90);
