drop table if exists movieinfos;
create table movieinfos
(
	id					bigint not null,
	movie_id			bigint,
	language_id			bigint,
	name				varchar(50),
	path				varchar(200),
	lastmodified		timestamp,

	primary key (id)
) type=innodb;
