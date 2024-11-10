CREATE TABLE wedding (
                          wedding_id SERIAL PRIMARY KEY,
                          wedding_name VARCHAR(100) NOT NULL,
                          wedding_date DATE,
                          location VARCHAR(255),
                          accessCode VARCHAR(20) UNIQUE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE application_user (
                                  id SERIAL PRIMARY KEY,
                                  email VARCHAR(255) NOT NULL UNIQUE,
                                  encrypted_password VARCHAR(255) NOT NULL,
                                  first_name VARCHAR(50) NOT NULL,
                                  last_name VARCHAR(50) NOT NULL,
                                  phone_number VARCHAR(15),
                                  role VARCHAR(20) NOT NULL,
                                  wedding_id INT,
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (wedding_id) REFERENCES wedding(wedding_id) -- Klucz obcy do tabeli "weddings"
);
ALTER TABLE wedding
    ADD COLUMN application_user_id INTEGER,
    ADD CONSTRAINT fk_application_user
        FOREIGN KEY (application_user_id) REFERENCES application_user(id);


INSERT INTO application_user (email, encrypted_password, first_name, last_name, phone_number, role)
VALUES
    ('nobis171wp.pl', '$2y$12$I1NKGY1Vms5tW0nocLjituayDOtaZPRgyc9885IZGqEfpTbE2f7zm', 'Wiktor', 'Nobis', '123456789', 'ADMIN');

INSERT INTO wedding (wedding_id, wedding_name, wedding_date, location, accessCode, application_user_id)
VALUES
    (1, 'Wesele Wiktor-Wiktoria', CURRENT_DATE, 'Warszawa', 'ABC123', 1);



ALTER TABLE wedding
    DROP CONSTRAINT fk_application_user;

-- Usunięcie klucza obcego z tabeli 'application_user'
ALTER TABLE application_user
    DROP CONSTRAINT application_user_wedding_id_fkey;

