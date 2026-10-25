-- Removes the fixtures created by test-seed.sql (ids in the 900000+ range).
DELETE FROM article_label WHERE article_id = 900001;
DELETE FROM article WHERE id = 900001;
DELETE FROM animal WHERE id = 900001;
DELETE FROM user WHERE id = 900001 OR phone = '19900000000';

-- Self-contained fixtures for MiaowuApplicationTests.
-- 19900000000 is a placeholder phone number, not a real one.
INSERT INTO user (id, name, id_code, password, phone, create_date)
VALUES (900001, 'test-user-900001', 'TEST900001', 'test-pass-900001', '19900000000', NOW());
INSERT INTO animal (id, name, category, age, gender)
VALUES (900001, 'test-animal-900001', 'cat', 1, 'female');
INSERT INTO article (id, title, content, source, type, author_id, animal_id, thumb_up)
VALUES (900001, 'test-article-900001', 'test content', 'test', 1, 900001, 900001, 0);
