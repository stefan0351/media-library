drop table if exists links;
create table links
(
	id                  bigint not null,
	show_id             bigint,
	fandom_id			bigint,
	language_id         bigint,
	name                varchar(200),
	url					varchar(200),
	lastmodified		timestamp,

	primary key (id)
) type=innodb;
