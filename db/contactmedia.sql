drop table if exists contactmedia;
create table contactmedia
(
	id					bigint not null,
	type				int,
	value				varchar(200),
	author_id			bigint,
	lastmodified		timestamp,

	primary key (id)
) type=innodb;
