-- Drop tables if they exist
DROP TABLE IF EXISTS beer CASCADE;
DROP TABLE IF EXISTS customer CASCADE;

-- Create beer table
CREATE TABLE beer (
  id            VARCHAR(36) NOT NULL PRIMARY KEY,
  beer_name     VARCHAR(50),
  upc           VARCHAR(255),
  beer_style    SMALLINT NOT NULL CHECK (beer_style BETWEEN 0 AND 9),
  price         NUMERIC(38, 2) NOT NULL,
  quantity_on_hand INTEGER,
  version       INTEGER,
  created_date  TIMESTAMP(6),
  update_date   TIMESTAMP(6)
);

-- Create customer table
CREATE TABLE customer (
  id            VARCHAR(36) NOT NULL PRIMARY KEY,
  name          VARCHAR(255),
  version       INTEGER,
  created_date  TIMESTAMP(6),
  update_date   TIMESTAMP(6)
);