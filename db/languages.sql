drop table if exists languages;
create table languages
(
	id                  bigint not null,
	symbol              varchar(2) not null,
	name                varchar(50) not null,

	primary key (id)
) type=innodb;

insert into languages (id, symbol, name) values (1, 'de', 'Deutsch');
insert into languages (id, symbol, name) values (2, 'en', 'Englisch');
commit;
