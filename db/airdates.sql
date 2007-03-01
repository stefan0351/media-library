/*
$Revision: 1.2 $
*/
drop table if exists airdates;
create table airdates
(
	id                  bigint not null,
	event               varchar(200),
	viewdate            timestamp not null,
	channel_id          bigint,
	channel             varchar(100),
	show_id             bigint,
	episode_id          bigint,
	movie_id			bigint,
	language_id         bigint,
	reminder            bool,
	source_id			bigint,
	lastmodified		timestamp null,

	primary key (id)
) type=innodb;
