drop table if exists channels;
create table channels
(
	id                  bigint not null,
	name                varchar(50) not null,
	logo                varchar(200),
	language_id         bigint,
	receivable          bool,
	lastmodified		timestamp,

	primary key (id)
) type=innodb;
