drop table if exists recordings;
create table recordings
(
	id                  bigint not null,
	longplay            bool,
	length              int,
	event               varchar(200),
	show_id             bigint,
	episode_id          bigint,
	movie_id			bigint,
	language_id         bigint,
	video_id            bigint,
	sequence            int,
	lastmodified		timestamp,

	primary key (id)
) type=innodb;
