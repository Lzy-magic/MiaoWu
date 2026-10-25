-- MiaoWu database schema
-- NOTE: This file was REVERSE-ENGINEERED from the MyBatis mapper XMLs
-- (src/main/resources/mapper/**) and bean classes (com.apps.miaowu.bean.*).
-- It is not the original author's DDL; types/lengths/indexes are best guesses.
CREATE DATABASE IF NOT EXISTS miaowu DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE miaowu;
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS country (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(64) DEFAULT NULL,
  _language VARCHAR(64) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(64) DEFAULT NULL,
  id_code VARCHAR(32) DEFAULT NULL,
  password VARCHAR(255) DEFAULT NULL,
  phone VARCHAR(32) DEFAULT NULL,
  birthday DATETIME DEFAULT NULL,
  country_id BIGINT DEFAULT NULL,
  create_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_name (name),
  KEY idx_user_phone (phone),
  KEY idx_user_country (country_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS animal (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(64) DEFAULT NULL,
  category VARCHAR(64) DEFAULT NULL,
  age INT DEFAULT NULL,
  gender VARCHAR(16) DEFAULT NULL,
  birthday DATETIME DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS article (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(255) DEFAULT NULL,
  content TEXT,
  source VARCHAR(255) DEFAULT NULL,
  type INT DEFAULT NULL,
  write_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  last_update DATETIME DEFAULT CURRENT_TIMESTAMP,
  thumb_up BIGINT DEFAULT 0,
  author_id BIGINT DEFAULT NULL,
  animal_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_article_author (author_id),
  KEY idx_article_animal (animal_id),
  KEY idx_article_last_update (last_update)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS label (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(64) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS article_label (
  id BIGINT NOT NULL AUTO_INCREMENT,
  article_id BIGINT DEFAULT NULL,
  label_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_al_article (article_id),
  KEY idx_al_label (label_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS comment_table (
  id BIGINT NOT NULL AUTO_INCREMENT,
  comment_content TEXT,
  article_id BIGINT DEFAULT NULL,
  user_id BIGINT DEFAULT NULL,
  date DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_comment_article (article_id),
  KEY idx_comment_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS clip (
  id BIGINT NOT NULL AUTO_INCREMENT,
  article_id BIGINT DEFAULT NULL,
  user_id BIGINT DEFAULT NULL,
  date DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_clip_user (user_id),
  KEY idx_clip_article (article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS follow (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT DEFAULT NULL COMMENT 'followed user',
  fans_id BIGINT DEFAULT NULL COMMENT 'follower',
  PRIMARY KEY (id),
  KEY idx_follow_user (user_id),
  KEY idx_follow_fans (fans_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS found_table (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT DEFAULT NULL,
  animal_id BIGINT DEFAULT NULL,
  site VARCHAR(255) DEFAULT NULL,
  found_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_found_user (user_id),
  KEY idx_found_animal (animal_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS save (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT DEFAULT NULL,
  animal_id BIGINT DEFAULT NULL,
  save_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_save_user (user_id),
  KEY idx_save_animal (animal_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS thumb_up (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT DEFAULT NULL,
  article_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_thumb_user (user_id),
  KEY idx_thumb_article (article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
