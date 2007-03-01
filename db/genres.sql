DROP TABLE IF EXISTS genres;
CREATE TABLE genres (
  id bigint(20) NOT NULL,
  name varchar(200) not null,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into genres (id, name, lastmodified) values (1, 'Comedy', now());
insert into genres (id, name, lastmodified) values (2, 'Action', now());
insert into genres (id, name, lastmodified) values (3, 'Mystery', now());
insert into genres (id, name, lastmodified) values (4, 'Science Fiction', now());
insert into genres (id, name, lastmodified) values (5, 'Drama', now());
insert into genres (id, name, lastmodified) values (6, 'Cartoon', now());
insert into genres (id, name, lastmodified) values (7, 'Fantasy', now());
insert into genres (id, name, lastmodified) values (8, 'Teenager', now());
insert into genres (id, name, lastmodified) values (9, 'Familie', now());
insert into genres (id, name, lastmodified) values (10, 'Erotik', now());
insert into genres (id, name, lastmodified) values (11, 'Romantik', now());
insert into genres (id, name, lastmodified) values (12, 'Animation', now());
commit;
