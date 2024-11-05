-- liquibase formatted sql

-- changeset congqt:20241009.133400.01
SELECT 1;

-- changeset congqt:20241009.133500.02

CREATE TABLE revinfo
(
    id             BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    REVTSTMP       BIGINT NULL,
    request_time   DATETIME NULL,
    request_id     VARCHAR(64) NULL,
    user_id        INT NULL,
    request_method VARCHAR(64) NULL,
    request_uri    VARCHAR(255) NULL
);


-- changeset congqt:20241009.133530.03

CREATE TABLE user
(
    id                       INT AUTO_INCREMENT PRIMARY KEY,
    username                 VARCHAR(64)                        NOT NULL UNIQUE,
    role_id                  INT                                NOT NULL,
    password                 VARCHAR(255),
    name                     VARCHAR(255),
    birthday                 DATE,
    email                    VARCHAR(64),
    gender                   VARCHAR(64),
    phone                    VARCHAR(64),
    avatar                   VARCHAR(255),
    created_at               DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at               DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by               INT NULL,
    updated_by               INT NULL,
    deleted_at               DATETIME NULL
);

CREATE TABLE user_aud
(
    id                       INT    NOT NULL,
    REV                      BIGINT NOT NULL,
    REVTYPE                  INT    NOT NULL,
    username                 VARCHAR(64)                        NOT NULL UNIQUE,
    role_id                  INT                                NOT NULL,
    password                 VARCHAR(255),
    name                     VARCHAR(255),
    birthday                 DATE,
    email                    VARCHAR(64),
    gender                   VARCHAR(64),
    phone                    VARCHAR(64),
    avatar                   VARCHAR(255),
    created_at               DATETIME,
    updated_at               DATETIME,
    created_by               INT,
    updated_by               INT,
    deleted_at               DATETIME,
    PRIMARY KEY (id, REV)
);

CREATE TABLE role
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(255)                       NOT NULL,
    allowed_sign_up BIT(1)                             NOT NULL DEFAULT TRUE,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by      INT NULL,
    updated_by      INT NULL,
    deleted_at      DATETIME NULL
);

CREATE TABLE role_aud
(
    id              INT    NOT NULL,
    REV             BIGINT NOT NULL,
    REVTYPE         INT    NOT NULL,
    name            VARCHAR(255),
    allowed_sign_up BIT(1),
    created_at      DATETIME,
    updated_at      DATETIME,
    created_by      INT,
    updated_by      INT,
    deleted_at      DATETIME,
    PRIMARY KEY (id, REV)
);

ALTER TABLE user
    ADD CONSTRAINT fk_user_role_id
        FOREIGN KEY (role_id) REFERENCES role (id);


-- changeset congqt:20241009.133630.04

INSERT INTO role (id, name, allowed_sign_up)
VALUES (1, 'ADMIN', FALSE);

INSERT INTO role (id, name, allowed_sign_up)
VALUES (2, 'USER', TRUE);

INSERT INTO user (username, role_id, name, email)
VALUES ('admin@gmail.com', 1, 'Admin', 'admin@admin.com');


