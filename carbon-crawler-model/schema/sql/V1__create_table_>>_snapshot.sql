create table host
(
  id     char(32) primary key,
  url    varchar(255)                       not null,
  title  varchar(255)                       not null,
  memo   text                               null,
  ins_at datetime default CURRENT_TIMESTAMP not null,
  upd_at datetime                           null,

  constraint host_url_uk
    unique (url)
);

create table snapshot
(
  id      char(32) primary key,
  host_id char(32)                           not null,
  title   varchar(255)                       not null,
  url     varchar(255)                       not null,
  ins_at  datetime default CURRENT_TIMESTAMP not null,
  upd_at  datetime                           null,

  constraint snapshot_host_id_fk
    foreign key (host_id) references host (id),

  constraint snapshot_detail_url_uk
    unique (url)
);

create table snapshot_attribute
(
  id          char(32) primary key,
  snapshot_id char(32)                           not null,
  `key`       varchar(255)                       not null,
  value       text                               not null,
  type        char(10)                           not null,
  ins_at      datetime default CURRENT_TIMESTAMP not null,
  upd_at      datetime                           null,

  constraint snapshot_attribute_snapshot_id_fk
    foreign key (snapshot_id) references snapshot (id),

  constraint snapshot_attribute_uk
    unique (snapshot_id, `key`)
);

