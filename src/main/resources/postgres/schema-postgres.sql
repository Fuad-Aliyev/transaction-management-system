CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL
);

CREATE TABLE wallet (
                        id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT,
                        name VARCHAR(255) NOT NULL,
                        balance DECIMAL(19, 2) NOT NULL,
                        CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);