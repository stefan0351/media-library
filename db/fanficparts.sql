drop table if exists fanfic_parts;
create table fanfic_parts
(
	id					bigint not null,
	fanfic_id			bigint,
	source				varchar(200),
	sequence			int,
	lastmodified		timestamp,

	primary key (id)
) type=innodb;
