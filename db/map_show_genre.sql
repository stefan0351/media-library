drop table if exists map_show_genre;
create table map_show_genre
(
	show_id			bigint not null,
	genre_id		bigint not null,
	unique index (show_id, genre_id)
) engine=innodb;
