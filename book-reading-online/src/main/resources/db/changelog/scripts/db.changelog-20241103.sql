-- liquibase formatted sql

-- changeset congqt:20241103.133400.01

CREATE TABLE author
(
    id                       INT AUTO_INCREMENT PRIMARY KEY,
    name                     VARCHAR(255),
    created_at               DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at               DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by               INT NULL,
    updated_by               INT NULL,
    deleted_at               DATETIME NULL
);

CREATE TABLE author_aud
(
    id                       INT    NOT NULL,
    REV                      BIGINT NOT NULL,
    REVTYPE                  INT    NOT NULL,
    name                     VARCHAR(255),
    created_at               DATETIME,
    updated_at               DATETIME,
    created_by               INT,
    updated_by               INT,
    deleted_at               DATETIME,
    PRIMARY KEY (id, REV)
);

CREATE TABLE category
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(255)                       NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by      INT NULL,
    updated_by      INT NULL,
    deleted_at      DATETIME NULL
);

CREATE TABLE category_aud
(
    id              INT    NOT NULL,
    REV             BIGINT NOT NULL,
    REVTYPE         INT    NOT NULL,
    name            VARCHAR(255),
    created_at      DATETIME,
    updated_at      DATETIME,
    created_by      INT,
    updated_by      INT,
    deleted_at      DATETIME,
    PRIMARY KEY (id, REV)
);

-- changeset congqt:20241103.133400.02 endDelimiter:##

DROP FUNCTION IF EXISTS LIKE_IGNORE_ACCENT;
##

CREATE FUNCTION LIKE_IGNORE_ACCENT(string1 TEXT, string2 TEXT) RETURNS BIT
    DETERMINISTIC
BEGIN
RETURN string1 LIKE string2 COLLATE utf8mb4_0900_ai_ci;
END;
##