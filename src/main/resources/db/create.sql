CREATE TABLE IF NOT EXISTS users
(
    user_id  VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255),
    name     VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS posts
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    title     VARCHAR(255) NOT NULL,
    content   TEXT,
    author_id VARCHAR(255),
    FOREIGN KEY (author_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    content   VARCHAR(255) NOT NULL,
    post_id   BIGINT,
    author_id VARCHAR(255),

    FOREIGN KEY (post_id) REFERENCES posts (id),
    FOREIGN KEY (author_id) REFERENCES users (user_id)
);