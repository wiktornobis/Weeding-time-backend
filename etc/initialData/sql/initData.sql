CREATE TABLE wedding (
                          wedding_id SERIAL PRIMARY KEY,
                          wedding_name VARCHAR(128) DEFAULT NULL,
                          wedding_date DATE,
                          location VARCHAR(255),
                          access_code VARCHAR(20) UNIQUE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);
CREATE TABLE application_user (
                  id SERIAL PRIMARY KEY,
                  first_name VARCHAR(128) NOT NULL,
                  last_name VARCHAR(128) NOT NULL,
                  email VARCHAR(128) NOT NULL UNIQUE,
                  encrypted_password VARCHAR(64) NOT NULL,
                  phone_number VARCHAR(20),
                  role VARCHAR(40) NOT NULL,
                  wedding_id INT,
                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  CONSTRAINT fk_wedding_id FOREIGN KEY (wedding_id) REFERENCES wedding(wedding_id) ON DELETE SET NULL
);


INSERT INTO application_user (email, encrypted_password, first_name, last_name, phone_number, role)
VALUES
    ('nobis171wp.pl', '$2y$12$I1NKGY1Vms5tW0nocLjituayDOtaZPRgyc9885IZGqEfpTbE2f7zm', 'Wiktor', 'Nobis', '123456789', 'ADMIN');

INSERT INTO wedding (wedding_name, wedding_date, location, access_code)
VALUES
    ('Wesele Wiktor-Wiktoria', CURRENT_DATE, 'Warszawa', 'ABC123');






-- Usunięcie klucza obcego z tabeli 'application_user'
ALTER TABLE application_user
    DROP CONSTRAINT fk_wedding_id;


