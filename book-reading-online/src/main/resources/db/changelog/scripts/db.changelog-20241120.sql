-- liquibase formatted sql

-- changeset congqt:20241120.133400.01

CREATE TABLE models
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    model_data LONGBLOB
);

-- changeset congqt:20241120.133500.02

CREATE TABLE model_x
(
    item_id INT PRIMARY KEY,
    factors TEXT NOT NULL
);

CREATE TABLE model_w
(
    user_id INT PRIMARY KEY,
    factors TEXT NOT NULL
);

-- changeset congqt:20241120.133500.03

ALTER TABLE model_x
    CHANGE COLUMN factors factors JSON;

ALTER TABLE model_w
    CHANGE COLUMN factors factors JSON;

-- changeset congqt:20241120.133500.04

ALTER TABLE model_x DROP PRIMARY KEY;
ALTER TABLE model_x
    CHANGE COLUMN item_id book_id INT;
ALTER TABLE model_x
    ADD PRIMARY KEY (book_id);
