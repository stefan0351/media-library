drop table if exists cast;
create table cast
(
	id					bigint not null,
	actor_id			bigint,
	character_id		bigint,
	show_id				bigint,
	episode_id			bigint,
	movie_id			bigint,
	type				int,
	voice				varchar(100),
	image_small			varchar(200),
	image_large			varchar(200),
	description			text,
	lastmodified		timestamp,

	 primary key (id)
) type=innodb;
