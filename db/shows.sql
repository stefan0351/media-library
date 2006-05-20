drop table if exists shows;
create table shows
(
	id                  bigint not null,
	userkey             varchar(20) not null,
	name                varchar(100) not null,
	name_original		varchar(100),
	internet            bool,
	episode_length      int,
	webdatesfile        varchar(200),
	language_id			bigint,
	lastmodified		timestamp,
	type_id				bigint,
	logo_mini			varchar(200),
	defaultinfo_id		bigint,

	primary key (id)
) type=innodb;
