CREATE TABLE dictionary
(
  id    BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  url   VARCHAR(255) NOT NULL,
  title VARCHAR(255) NOT NULL,
  memo  TEXT         NULL,
  CONSTRAINT dictionary_url_uindex
  UNIQUE (url)
)
  ENGINE = InnoDB;

CREATE TABLE tag
(
  id            BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  dictionary_id BIGINT       NOT NULL,
  name          VARCHAR(255) NOT NULL,
  CONSTRAINT category_name_uindex
  UNIQUE (name),
  CONSTRAINT tag_dictionary_id_fk
  FOREIGN KEY (dictionary_id) REFERENCES dictionary (id)
)
  ENGINE = InnoDB;

CREATE INDEX tag_dictionary_id_fk
  ON tag (dictionary_id);

CREATE TABLE page
(
  id            BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  dictionary_id BIGINT       NOT NULL,
  title         VARCHAR(255) NOT NULL,
  url           VARCHAR(255) NOT NULL,
  CONSTRAINT page_detail_url_uindex
  UNIQUE (url),
  CONSTRAINT page_dictionary_id_fk
  FOREIGN KEY (dictionary_id) REFERENCES dictionary (id)
)
  ENGINE = InnoDB;

CREATE INDEX table_name_dictionary_id_fk
  ON page (dictionary_id);

CREATE TABLE page_attribute
(
  id      BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  page_id BIGINT       NOT NULL,
  `key`   VARCHAR(255) NOT NULL,
  value   TEXT         NOT NULL,
  type    CHAR(10)     NOT NULL,
  CONSTRAINT page_attribute_page_id_fk
  FOREIGN KEY (page_id) REFERENCES page (id)
)
  ENGINE = InnoDB;

CREATE INDEX page_attribute_page_id_fk
  ON page_attribute (page_id);
