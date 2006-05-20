drop table if exists fandoms;
create table fandoms
(
	id					bigint not null,
	name				varchar(200),
	show_id				bigint,
	movie_id			bigint,
	lastmodified		timestamp,

	primary key (id)
) type=innodb;
