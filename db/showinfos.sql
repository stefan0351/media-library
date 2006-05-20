drop table if exists showinfos;
create table showinfos
(
	id					bigint not null,
	show_id				bigint,
	language_id			bigint,
	name				varchar(50),
	path				varchar(200),
	lastmodified		timestamp,

	primary key (id)
) type=innodb;
