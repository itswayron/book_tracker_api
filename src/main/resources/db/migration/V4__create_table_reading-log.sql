CREATE TABLE reading_log (
  id SERIAL PRIMARY KEY,

  user_id VARCHAR(22) NOT NULL,
  reading_id INT NOT NULL,

  date_of_reading TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  quantity_read INT NOT NULL CHECK (quantity_read > 0),

  CONSTRAINT fk_user_log FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

  CONSTRAINT fk_reading_session FOREIGN KEY (reading_id)
    REFERENCES readings(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);
