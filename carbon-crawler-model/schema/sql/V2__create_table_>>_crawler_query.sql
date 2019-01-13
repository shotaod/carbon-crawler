create table crawl_list_query
(
  id                 bigint auto_increment primary key,
  host_id            bigint                             not null,
  listing_page_path  varchar(1023)                      not null,
  listing_link_query varchar(1023)                      not null,
  ins_at             datetime default CURRENT_TIMESTAMP not null,
  upd_at             datetime                           null,
  del_at             datetime                           null,
  constraint host_id_uk
  unique (host_id),
  constraint crawl_list_query_host_id_fk
  foreign key (host_id) references host (id)
);

create table crawl_detail_query
(
  id      bigint auto_increment primary key,
  host_id bigint                             not null,
  name    varchar(255)                       not null,
  query   varchar(1023)                      not null,
  type    varchar(63)                        not null,
  ins_at  datetime default CURRENT_TIMESTAMP not null,
  upd_at  datetime                           null,
  del_at  datetime                           null,
  constraint crawl_detail_query_host_id_name_uk
  unique (host_id, name),
  constraint crawl_detail_query_host_id_fk
  foreign key (host_id) references host (id)
);

