-- liquibase formatted sql

-- changeset congqt:20241105.133400.01

CREATE TABLE book
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(255),
    summary    TEXT,
    avg_rating DOUBLE,
    thumbnail  VARCHAR(255),
    view       INT,
    status     VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by INT NULL,
    updated_by INT NULL,
    deleted_at DATETIME NULL
);

CREATE TABLE book_aud
(
    id         INT    NOT NULL,
    REV        BIGINT NOT NULL,
    REVTYPE    INT    NOT NULL,
    title      VARCHAR(255),
    summary    TEXT,
    avg_rating DOUBLE,
    thumbnail  VARCHAR(255),
    view       INT,
    status     VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME,
    created_by INT,
    updated_by INT,
    deleted_at DATETIME,
    PRIMARY KEY (id, REV)
);

-- changeset congqt:20241105.133400.02

CREATE TABLE chapter
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(255),
    order_chap      INT,
    book_id    INT,
    file_key   VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by INT NULL,
    updated_by INT NULL,
    deleted_at DATETIME NULL,
    CONSTRAINT fk_chapter_book_id
        FOREIGN KEY (book_id) REFERENCES book (id)
);

CREATE TABLE chapter_aud
(
    id         INT    NOT NULL,
    REV        BIGINT NOT NULL,
    REVTYPE    INT    NOT NULL,
    title      VARCHAR(255),
    order_chap      INT,
    book_id    INT,
    file_key   VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME,
    created_by INT,
    updated_by INT,
    deleted_at DATETIME,
    PRIMARY KEY (id, REV)
);

-- changeset congqt:20241105.133401.03

CREATE TABLE category_book
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    book_id     INT,
    category_id INT,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by  INT NULL,
    updated_by  INT NULL,
    deleted_at  DATETIME NULL,
    CONSTRAINT fk_category_book_book_id
        FOREIGN KEY (book_id) REFERENCES book (id),
    CONSTRAINT fk_category_book_category_id
        FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE TABLE category_book_aud
(
    id          INT    NOT NULL,
    REV         BIGINT NOT NULL,
    REVTYPE     INT    NOT NULL,
    book_id     INT,
    category_id INT,
    created_at  DATETIME,
    updated_at  DATETIME,
    created_by  INT,
    updated_by  INT,
    deleted_at  DATETIME,
    PRIMARY KEY (id, REV)
);

-- changeset congqt:20241105.133401.04
ALTER TABLE book
    ADD COLUMN author_id INT AFTER status,
    ADD CONSTRAINT fk_book_author_id
    FOREIGN KEY (author_id) REFERENCES author (id);

ALTER TABLE book_aud
    ADD COLUMN author_id INT AFTER status;

-- changeset congqt:20241105.133402.05

ALTER TABLE user
DROP INDEX username;

ALTER TABLE user_aud
DROP INDEX username;

