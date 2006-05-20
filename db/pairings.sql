drop table if exists pairings;
create table pairings
(
	id					bigint not null,
	name				varchar(200),
	lastmodified		timestamp,

	primary key (id)
) type=innodb;
