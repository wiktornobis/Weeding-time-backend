CREATE TABLE wedding (
             id BIGSERIAL PRIMARY KEY,
             wedding_name VARCHAR(128) DEFAULT NULL,
             wedding_date DATE,
             location VARCHAR(255),
             access_code VARCHAR(20) UNIQUE,
             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE application_user (
              id BIGSERIAL PRIMARY KEY,
              first_name VARCHAR(128) NOT NULL,
              last_name VARCHAR(128) NOT NULL,
              encrypted_password VARCHAR(64) NOT NULL,
              email VARCHAR(128) NOT NULL UNIQUE,
              phone_number VARCHAR(20),
              role VARCHAR(40) NOT NULL,
              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE application_user_wedding (
              id BIGSERIAL PRIMARY KEY,
              user_id BIGINT NOT NULL,
              wedding_id BIGINT NOT NULL,
              join_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
              CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES application_user(id) ON DELETE CASCADE,
              CONSTRAINT fk_wedding FOREIGN KEY (wedding_id) REFERENCES wedding(id) ON DELETE CASCADE,
              UNIQUE (user_id, wedding_id) -- Zapobieganie duplikatom w połączeniu użytkownika z weselem
);


INSERT INTO wedding (wedding_name, wedding_date, location, access_code)
VALUES ('Wiktor & Wiktoria Wedding', '2024-06-15', 'Warszawa', 'W123');

INSERT INTO application_user (first_name, last_name, encrypted_password, email, phone_number, role)
VALUES ('Wiktor', 'Nobis', '$2y$12$I1NKGY1Vms5tW0nocLjituayDOtaZPRgyc9885IZGqEfpTbE2f7zm', 'nobis171@wp.pl', '662650889', 'ADMIN');

INSERT INTO application_user_wedding (user_id, wedding_id, join_date)
VALUES (1, 1, '2024-06-15 12:00:00');


ALTER TABLE application_user_wedding
    DROP CONSTRAINT fk_user;

ALTER TABLE application_user_wedding
    DROP CONSTRAINT fk_wedding;
