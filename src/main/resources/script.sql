-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS defaultdb;

-- Switch to the created database
USE defaultdb;

CREATE TABLE IF NOT EXISTS customer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    pwd VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

INSERT INTO customer (email, pwd, role) 
VALUES ('bashajavi@gmail.com', '$2a$10$Bo7KEslHgVbSbY/uttsEUOh4MJ1tKXSoz7cOoQo//Ily0tGaIY49m', 'user')
ON DUPLICATE KEY UPDATE email=email;

CREATE TABLE IF NOT EXISTS sponsor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    logo_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS partner (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    logo_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS poster (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS associate (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    logo_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS gallery (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS participant (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    age INT NOT NULL,    
    email VARCHAR(255) NOT NULL,
    category VARCHAR(50),
    gender VARCHAR(50),
    address VARCHAR(255),
    bib_number INT NOT NULL DEFAULT 2000,
    tshirt_size VARCHAR(20),
    finish_time TIME(6), -- TIME data type with nanosecond precision (adjust precision as needed)
    status VARCHAR(50) -- Assuming status can be a VARCHAR with 50 characters
);

CREATE TABLE IF NOT EXISTS contact (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    message TEXT NOT NULL,
    status VARCHAR(50)
);
