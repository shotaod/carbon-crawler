create table host
(
  id     bigint auto_increment primary key,
  url    varchar(255)                       not null,
  title  varchar(255)                       not null,
  memo   text                               null,
  ins_at datetime default CURRENT_TIMESTAMP not null,
  upd_at datetime                           null,
  del_at datetime                           null,

  constraint host_url_uk
  unique (url)
);

create table page
(
  id      bigint auto_increment primary key,
  host_id bigint                             not null,
  title   varchar(255)                       not null,
  url     varchar(255)                       not null,
  ins_at  datetime default CURRENT_TIMESTAMP not null,
  upd_at  datetime                           null,
  del_at  datetime                           null,

  constraint page_host_id_fk
  foreign key (host_id) references host (id),

  constraint page_detail_url_uk
  unique (url)
);

create table page_attribute
(
  id      bigint auto_increment primary key,
  page_id bigint                             not null,
  `key`   varchar(255)                       not null,
  value   text                               not null,
  type    char(10)                           not null,
  ins_at  datetime default CURRENT_TIMESTAMP not null,
  upd_at  datetime                           null,
  del_at  datetime                           null,

  constraint page_attribute_page_id_fk
  foreign key (page_id) references page (id),

  constraint page_attribute_uk
  unique (page_id, `key`)
);

