drop table if exists movies;
create table movies
(
	id                  bigint not null,
	show_id             bigint,
	type_id				bigint,
	name                varchar(200),
	name_original		varchar(200),
	seen                bool,
	good                bool,
	record              bool,
	javascript          varchar(50),
	webscriptfile       varchar(200),
	lastmodified		timestamp,
	defaultinfo_id		bigint,

	primary key (id)
) type=innodb;
