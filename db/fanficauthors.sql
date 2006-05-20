drop table if exists fanficauthors;
create table fanficauthors
(
	id					bigint not null,
	name				varchar(200),
	path				varchar(50),
	lastmodified		timestamp,

	primary key (id)
) type=innodb;
