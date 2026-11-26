CREATE DATABASE IF NOT EXISTS petdb;
USE petdb;

CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash CHAR(64)     NOT NULL
);

CREATE TABLE IF NOT EXISTS pets (
    pet_id       INT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    species      VARCHAR(50)  NOT NULL,
    age          INT NOT NULL CHECK (age >= 0),
    adoption_fee DOUBLE NOT NULL CHECK (adoption_fee >= 0),
    adopted      BOOLEAN NOT NULL DEFAULT FALSE
);
