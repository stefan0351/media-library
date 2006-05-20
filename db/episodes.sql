drop table if exists episodes;
create table episodes
(
	id                  bigint not null,
	show_id             bigint,
	userkey             varchar(10),
	sequence            int not null,
	name                varchar(200),
	name_original		varchar(200),
	seen                bool,
	good                bool,
	record              bool,
	feature             bool,
	javascript          varchar(50),
	webscriptfile       varchar(200),
	defaultinfo_id		bigint,
	lastmodified		timestamp,

	primary key (id)
) type=innodb;
