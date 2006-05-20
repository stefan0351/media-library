drop table if exists fanfics;
create table fanfics
(
	id					bigint not null,
	title				varchar(200),
	rating				varchar(10),
	description			text,
	sequel_id			bigint,
	prequel_id			bigint,
	finished			bool,
	spoiler				text,
	url					varchar(200),
	lastmodified		timestamp,

	primary key (id)
) type=innodb;
