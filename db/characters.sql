drop table if exists characters;
create table characters
(
	id					bigint not null,
	name				varchar(200),
	nickname			varchar(100),
	sex_id				bigint,
	lastmodified		timestamp,

	primary key (id)
) type=innodb;
