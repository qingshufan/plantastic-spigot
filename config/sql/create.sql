
USE crops_process_db;
DROP TABLE IF EXISTS loc_data;
DROP TABLE IF EXISTS crop_data;
DROP TABLE IF EXISTS rank_data;
DROP TABLE IF EXISTS crop_diseases;
DROP DATABASE IF EXISTS crops_process_db;


CREATE DATABASE crops_process_db;
USE crops_process_db;

CREATE TABLE crop_data (
    world_name VARCHAR(255) NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    z DOUBLE NOT NULL,
    name VARCHAR(255),
    water INT,
    nutrition INT,
    fall BOOLEAN,
    disease BOOLEAN,
    short_water BOOLEAN,
    short_nutrition BOOLEAN,
    death BOOLEAN,
    higher_water BOOLEAN,
    higher_nutrition BOOLEAN,
    deletes BOOLEAN DEFAULT FALSE,
    op BOOLEAN DEFAULT FALSE,
    file_name VARCHAR(255),
    period INT,
    delay INT,
    shows VARCHAR(255),
    soil_type ENUM('SALINE', 'ACIDIC', 'DRY') NOT NULL,
    high BOOLEAN,
    own_green_house BOOLEAN,
    temperature_error BOOLEAN,
    uuid VARCHAR(255),
    mature_time BIGINT,
    insect BOOLEAN,
    PRIMARY KEY (world_name, x, y, z)
);

CREATE TABLE crop_diseases (
    id INT AUTO_INCREMENT PRIMARY KEY,
    world_name VARCHAR(255) NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    z DOUBLE NOT NULL,
    disease_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (world_name, x, y, z) REFERENCES crop_data(world_name, x, y, z)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE rank_data (
    uuid VARCHAR(255) PRIMARY KEY,
    prevalence_level INT NOT NULL,
    resistance_level INT NOT NULL,
    yield_level INT NOT NULL,
    books BLOB,
    hoe_level INT NOT NULL,
    shovel_level INT NOT NULL,
    bottle_level INT NOT NULL,
    nutrition_level INT NOT NULL,
    grow_level INT NOT NULL,
    harvest_level INT NOT NULL
);

