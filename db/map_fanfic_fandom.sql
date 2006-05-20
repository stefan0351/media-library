drop table if exists map_fanfic_fandom;
create table map_fanfic_fandom
(
	fanfic_id			bigint not null,
	fandom_id			bigint not null,

	unique index (fanfic_id, fandom_id)
) type=innodb;
