CREATE TABLE crawl_query
(
  id                BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  crawl_root_doc_id BIGINT        NOT NULL,
  name              VARCHAR(255)  NOT NULL,
  query             VARCHAR(1023) NOT NULL,
  CONSTRAINT scraping_attribute_direction_dictionary_id_fk
  UNIQUE (crawl_root_doc_id)
)
  ENGINE = InnoDB;

CREATE TABLE crawl_root_doc
(
  id                BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  dictionary_id     BIGINT                             NOT NULL,
  list_page_path    VARCHAR(1023)                      NOT NULL,
  list_holder_query VARCHAR(1023)                      NOT NULL,
  list_item_query   VARCHAR(1023)                      NOT NULL,
  ins_at            DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  upd_at            DATETIME                           NULL,
  del_at            DATETIME                           NULL,
  CONSTRAINT dictionary_id
  UNIQUE (dictionary_id),
  CONSTRAINT crawl_root_doc_dictionary_id_fk
  FOREIGN KEY (dictionary_id) REFERENCES dictionary (id)
)
  ENGINE = InnoDB;

ALTER TABLE crawl_query
  ADD CONSTRAINT crawl_query_crawl_root_doc_id_fk
FOREIGN KEY (crawl_root_doc_id) REFERENCES crawl_root_doc (id);

CREATE TABLE crawl_source
(
  id                BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  crawl_root_doc_id BIGINT                             NOT NULL,
  url               VARCHAR(768)                       NOT NULL,
  ins_at            DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  upd_at            DATETIME                           NULL,
  del_at            DATETIME                           NULL,
  CONSTRAINT crawl_source_url_uindex
  UNIQUE (url),
  CONSTRAINT crawl_source_crawl_root_doc_id_fk
  FOREIGN KEY (crawl_root_doc_id) REFERENCES crawl_root_doc (id)
)
  ENGINE = InnoDB;

CREATE INDEX crawl_source_crawl_root_doc_id_fk
  ON crawl_source (crawl_root_doc_id);

