-- liquibase formatted sql

-- changeset congqt:20241111.133400.01

CREATE TABLE feedback
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    content    TEXT,
    rating     INT,
    book_id    INT,
    user_id    INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by INT NULL,
    updated_by INT NULL,
    deleted_at DATETIME NULL,
    CONSTRAINT fk_feedback_book_id
        FOREIGN KEY (book_id) REFERENCES book (id),
    CONSTRAINT fk_feedback_user_id
        FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE feedback_aud
(
    id         INT    NOT NULL,
    REV        BIGINT NOT NULL,
    REVTYPE    INT    NOT NULL,
    content    TEXT,
    rating     INT,
    book_id    INT,
    user_id    INT,
    created_at DATETIME,
    updated_at DATETIME,
    created_by INT,
    updated_by INT,
    deleted_at DATETIME,
    PRIMARY KEY (id, REV)
);

-- changeset congqt:20241111.133401.02

CREATE TABLE comment
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    comment     TEXT,
    title       TEXT,
    like_count  INT,
    reply_count INT,
    reply_name  VARCHAR(255),
    parentId    INT,
    book_id     INT,
    user_id     INT,
    chapter_id  INT,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by  INT NULL,
    updated_by  INT NULL,
    deleted_at  DATETIME NULL,
    CONSTRAINT fk_comment_book_id
        FOREIGN KEY (book_id) REFERENCES book (id),
    CONSTRAINT fk_comment_user_id
        FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT fk_comment_chapter_id
        FOREIGN KEY (chapter_id) REFERENCES chapter (id)
);

CREATE TABLE comment_aud
(
    id          INT    NOT NULL,
    REV         BIGINT NOT NULL,
    REVTYPE     INT    NOT NULL,
    comment     TEXT,
    title       TEXT,
    like_count  INT,
    reply_count INT,
    reply_name  VARCHAR(255),
    parentId    INT,
    book_id     INT,
    user_id     INT,
    chapter_id  INT,
    created_at  DATETIME,
    updated_at  DATETIME,
    created_by  INT,
    updated_by  INT,
    deleted_at  DATETIME,
    PRIMARY KEY (id, REV)
);

ALTER TABLE book
    ADD COLUMN feedback_count INT AFTER status;

ALTER TABLE book_aud
    ADD COLUMN feedback_count INT AFTER status;

-- changeset congqt:20241111.133401.03

ALTER TABLE comment
    CHANGE COLUMN parentId parent INT;

-- changeset congqt:20241111.133401.04

ALTER TABLE comment_aud
    CHANGE COLUMN parentId parent INT;

-- changeset congqt:20241111.133401.05

CREATE TABLE react
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    react      VARCHAR(64),
    comment_id INT,
    user_id    INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by INT NULL,
    updated_by INT NULL,
    deleted_at DATETIME NULL,
    CONSTRAINT fk_like_comment_id
        FOREIGN KEY (comment_id) REFERENCES comment (id),
    CONSTRAINT fk_like_user_id
        FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE react_aud
(
    id         INT    NOT NULL,
    REV        BIGINT NOT NULL,
    REVTYPE    INT    NOT NULL,
    react      VARCHAR(64),
    comment_id INT,
    user_id    INT,
    created_at DATETIME,
    updated_at DATETIME,
    created_by INT,
    updated_by INT,
    deleted_at DATETIME,
    PRIMARY KEY (id, REV)
);
