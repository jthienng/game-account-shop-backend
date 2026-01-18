CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
--ROLE-------
CREATE TABLE roles (
                       id SERIAL  PRIMARY KEY, -- khoá chính
                       name VARCHAR(50) NOT NULL UNIQUE, -- tên vai trò vd (user, admin,...)
                       description VARCHAR(255) --mô tả vai trò
);

--USER-------
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

                       username VARCHAR(255) NOT NULL UNIQUE,
                       mail VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,

                       role_id INT NOT NULL,

                       enabled BOOLEAN DEFAULT TRUE,
                       account_locked BOOLEAN DEFAULT FALSE,
                       fail_login_attempts INT DEFAULT 0, -- số lần thử đăng nhập thất bại
                       last_login TIMESTAMP, -- thời điểm đăng nhập lần cuối

                       create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, --thời gian tạo
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,-- thời gian cập nhật lần cuối

                       CONSTRAINT fk_user_role -- ràng buộc mỗi người dùng có vai trò hợp lệ
                           FOREIGN KEY (role_id)
                               REFERENCES roles(id)

);

CREATE TABLE refresh_tokens (
                                id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

                                user_id UUID NOT NULL, -- liên kết tới người dùng sở hữu token
                                token VARCHAR(500) NOT NULL UNIQUE, --chuỗi token
                                expires_at TIMESTAMP NOT NULL, -- hết hạn của token
                                revoked BOOLEAN DEFAULT FALSE, -- token bị thu hồi hay không

                                create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- thời gian tạo token
                                update_at TIMESTAMP, -- thời gian cập nhật lần cuối

                                CONSTRAINT fk_refresh_token_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES users(id)
                                        ON DELETE CASCADE --khi ngươfi dùng bị xóa các dữ liệu liên quan đều bị xóa
);

INSERT INTO roles (name, description)
VALUES
    ('USER', 'Normal User'),
    ('ADMIN','administrator');