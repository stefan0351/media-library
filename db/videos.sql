drop table if exists videos;
create table videos
(
	id                  bigint not null,
	userkey             varchar(10) not null,
	name                varchar(100),
	length              int not null,
	remaininglength     int not null,
	lastmodified		timestamp,

	primary key (id)
) type=innodb;
