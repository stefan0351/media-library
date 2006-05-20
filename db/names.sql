drop table if exists names;
create table names
(
	id                  bigint not null,
	ref_id              bigint not null,
	type                int,
	name                varchar(200),
	language_id         bigint,

	primary key (id)
) type=innodb;
