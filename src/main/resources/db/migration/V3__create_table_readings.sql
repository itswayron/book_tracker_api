CREATE TABLE readings (
  id SERIAL PRIMARY KEY,

  book_id INT NOT NULL,
  user_id VARCHAR(22) NOT NULL,

  progress_in_percentage DOUBLE PRECISION NOT NULL,
  total_progress INT NOT NULL,
  pages INT NOT NULL,
  chapters INT DEFAULT 0,

  reading_state VARCHAR(50) NOT NULL DEFAULT 'TO_READ',
  tracking_method VARCHAR(50) NOT NULL DEFAULT 'PAGES',

  daily_goal INT NOT NULL DEFAULT 0,

  start_reading_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  end_reading_date TIMESTAMP,
  estimated_completion_date TIMESTAMP,

  CONSTRAINT fk_book FOREIGN KEY (book_id)
    REFERENCES books(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

  CONSTRAINT fk_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);
