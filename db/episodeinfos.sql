drop table if exists episodeinfos;
create table episodeinfos
(
	id					bigint not null,
	episode_id			bigint,
	language_id			bigint,
	name				varchar(50),
	path				varchar(200),
	lastmodified		timestamp,

	primary key (id)
) type=innodb;
