drop table if exists searchpatterns;
create table searchpatterns
(
	id                  bigint not null,
	type                int,
	show_id             bigint,
	movie_id			bigint,
	actor_id			bigint,
	pattern             varchar(200),

	primary key (id)
) type=innodb;
