ALTER TABLE books ADD COLUMN visibility VARCHAR(50);

UPDATE books SET visibility = 'PRIVATE';

ALTER TABLE books ALTER COLUMN visibility SET NOT NULL;