drop table if exists map_fanfic_author;
create table map_fanfic_author
(
	fanfic_id			bigint not null,
	author_id			bigint not null,

	unique index (fanfic_id, author_id)
) type=innodb;
