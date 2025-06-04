CREATE DATABASE IF NOT EXISTS hotel_reservation_system;

USE hotel_reservation_system;

CREATE TABLE reservation (
    reservation_id INT NOT NULL AUTO_INCREMENT,
    guest_name VARCHAR(10),
    room_number INT UNIQUE,
    contact_number VARCHAR(20),
    reservation_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (reservation_id)
);

select * from reservation;