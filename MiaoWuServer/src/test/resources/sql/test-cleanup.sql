-- Removes the fixtures created by test-seed.sql (ids in the 900000+ range).
DELETE FROM article_label WHERE article_id = 900001;
DELETE FROM article WHERE id = 900001;
DELETE FROM animal WHERE id = 900001;
DELETE FROM user WHERE id = 900001 OR phone = '19900000000';
