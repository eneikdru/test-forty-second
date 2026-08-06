CREATE TABLE academic_term (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    CONSTRAINT chk_term_dates CHECK (start_date < end_date)
);

CREATE TABLE budget_record (
    id VARCHAR(36) PRIMARY KEY,
    term_id VARCHAR(36) NOT NULL,
    category VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    allocated_amount DECIMAL(19, 4) NOT NULL,
    CONSTRAINT fk_budget_term FOREIGN KEY (term_id) REFERENCES academic_term(id),
    CONSTRAINT chk_budget_amount CHECK (amount >= 0),
    CONSTRAINT chk_allocated_amount CHECK (allocated_amount >= 0),
    CONSTRAINT chk_allocated_within_budget CHECK (allocated_amount <= amount)
);

CREATE TABLE workload_record (
    id VARCHAR(36) PRIMARY KEY,
    term_id VARCHAR(36) NOT NULL,
    instructor_name VARCHAR(255) NOT NULL,
    planned_hours INT NOT NULL,
    hourly_rate DECIMAL(19, 4) NOT NULL,
    CONSTRAINT fk_workload_term FOREIGN KEY (term_id) REFERENCES academic_term(id),
    CONSTRAINT chk_planned_hours CHECK (planned_hours >= 0),
    CONSTRAINT chk_hourly_rate CHECK (hourly_rate >= 0)
);

CREATE TABLE scholarship_record (
    id VARCHAR(36) PRIMARY KEY,
    student_id VARCHAR(255) NOT NULL,
    student_name VARCHAR(255) NOT NULL,
    term_id VARCHAR(36) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    calculated_at TIMESTAMP NOT NULL,
    hash_checksum VARCHAR(64) NOT NULL,
    CONSTRAINT fk_scholarship_term FOREIGN KEY (term_id) REFERENCES academic_term(id),
    CONSTRAINT chk_scholarship_amount CHECK (amount >= 0)
);
