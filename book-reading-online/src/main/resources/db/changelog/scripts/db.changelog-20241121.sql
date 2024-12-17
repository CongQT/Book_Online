-- liquibase formatted sql

-- changeset congqt:20241121.133400.01

CREATE TABLE history
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    chapter_id INT,
    book_id    INT,
    user_id    INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by INT NULL,
    updated_by INT NULL,
    deleted_at DATETIME NULL,
    CONSTRAINT fk_history_book_id
        FOREIGN KEY (book_id) REFERENCES book (id),
    CONSTRAINT fk_history_user_id
        FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT fk_history_chapter_id
        FOREIGN KEY (chapter_id) REFERENCES chapter (id)
);

CREATE TABLE history_aud
(
    id         INT    NOT NULL,
    REV        BIGINT NOT NULL,
    REVTYPE    INT    NOT NULL,
    chapter_id INT,
    book_id    INT,
    user_id    INT,
    created_at DATETIME,
    updated_at DATETIME,
    created_by INT,
    updated_by INT,
    deleted_at DATETIME,
    PRIMARY KEY (id, REV)
);

-- changeset congqt:20241121.133400.02

ALTER TABLE book
    ADD COLUMN banner VARCHAR(255) AFTER status;

ALTER TABLE book_aud
    ADD COLUMN banner VARCHAR(255) AFTER status;

-- changeset congqt:20241121.133400.03

ALTER TABLE author
    ADD COLUMN image_avt VARCHAR(255) AFTER name,
    ADD COLUMN description TEXT AFTER image_avt;

ALTER TABLE author_aud
    ADD COLUMN image_avt VARCHAR(255) AFTER name,
    ADD COLUMN description TEXT AFTER image_avt;

-- changeset congqt:20241121.133400.04

ALTER TABLE model_x
    CHANGE COLUMN factors factors JSON;

ALTER TABLE model_w
    CHANGE COLUMN factors factors JSON;

-- changeset congqt:20241121.133400.05


ALTER TABLE book
    CHANGE COLUMN thumbnail thumbnail TEXT,
    CHANGE COLUMN banner banner TEXT;

ALTER TABLE book_aud
    CHANGE COLUMN thumbnail thumbnail TEXT,
    CHANGE COLUMN banner banner TEXT;

-- changeset congqt:20241121.133400.06

CREATE TABLE predictions
(
    user_id          INT,
    book_id          INT,
    predicted_rating FLOAT
);

