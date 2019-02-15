create table expedition
(
  id      char(32) primary key,
  host_id char(32)                           not null,
  ins_at  datetime default CURRENT_TIMESTAMP not null,
  upd_at  datetime                           null,
  constraint host_id_uk
    unique (host_id),
  constraint crawl_routing_host_id_fk
    foreign key (host_id) references host (id)
);

create table crawl_routing
(
  id                 char(32) primary key,
  expedition_id      char(32)                           not null,
  base_url           varchar(127)                       not null,
  listing_page_path  varchar(1023)                      not null,
  listing_link_query varchar(1023)                      not null,
  ins_at             datetime default CURRENT_TIMESTAMP not null,
  upd_at             datetime                           null,
  constraint expedition_id_uk
    unique (expedition_id),
  constraint base_url_uk
    unique (base_url),
  constraint crawl_routing_expedition_id_fk
    foreign key (expedition_id) references expedition (id)
);

create table scraping_policy
(
  id            char(32) primary key,
  expedition_id char(32)                           not null,
  name          varchar(255)                       not null,
  query         varchar(1023)                      not null,
  type          varchar(63)                        not null,
  ins_at        datetime default CURRENT_TIMESTAMP not null,
  upd_at        datetime                           null,
  constraint scraping_policy_expedition_id_name_uk
    unique (expedition_id, name),
  constraint scraping_policy_expedition_id_fk
    foreign key (expedition_id) references expedition (id)
);
