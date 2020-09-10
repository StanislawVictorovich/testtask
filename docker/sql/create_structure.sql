CREATE DATABASE month_snapshot;
CREATE DATABASE month_snapshot_test;

CREATE TABLE month_snapshot.dmp(
    dmp_id_str VARCHAR(64) PRIMARY KEY,
    dmp_id BIGINT UNIQUE
) ENGINE=InnoDB;

CREATE TABLE month_snapshot.snapshot(
    dmp_id BIGINT PRIMARY KEY,
    page_views INT NOT NULL,
    first_seen INT NOT NULL,
    last_seen INT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE month_snapshot.country(
    id BIGINT PRIMARY KEY,
    name VARCHAR(64) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE month_snapshot.city(
    id BIGINT PRIMARY KEY,
    name VARCHAR(64) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE month_snapshot.country_stat(
    dmp_id BIGINT,
    country_id BIGINT NOT NULL,
    seen_count INT NOT NULL,
    last_seen INT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE month_snapshot.city_stat(
    dmp_id BIGINT,
    city_id BIGINT NOT NULL,
    seen_count INT NOT NULL,
    last_seen INT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE month_snapshot.gender_stat(
    dmp_id BIGINT,
    gender_id INT NOT NULL,
    seen_count INT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE month_snapshot.yob_stat(
    dmp_id BIGINT,
    yob INT NOT NULL,
    seen_count INT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE month_snapshot.keyword_stat(
    dmp_id BIGINT,
    keyword_id INT NOT NULL,
    seen_count INT NOT NULL,
    last_seen INT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE month_snapshot.site_stat(
    dmp_id BIGINT,
    site_id INT NOT NULL,
    seen_count INT NOT NULL,
    last_seen INT NOT NULL
) ENGINE=InnoDB;