CREATE TABLE books (
  id SERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  author VARCHAR(255) NOT NULL,
  pages INT NOT NULL,
  chapters INT,
  cover_url TEXT,

  user_id VARCHAR(22), -- agora pode ser NULL
  synopsis TEXT,
  publisher VARCHAR(255),
  publication_date DATE,
  language VARCHAR(100),
  isbn10 VARCHAR(20),
  isbn13 VARCHAR(20),
  type_of_media VARCHAR(100),
  genres JSONB,

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
);
