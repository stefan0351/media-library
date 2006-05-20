drop table if exists seasons;
create table seasons
(
	id					bigint not null,
	number				int,
	name				varchar(50),
	show_id				bigint,
	firstepisode_id		bigint,
	lastepisode_id		bigint,
	startYear			int,
	endYear				int,
	lastmodified		timestamp,

	 primary key (id)
) type=innodb;
