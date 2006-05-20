drop table if exists persons;
create table persons
(
	id					bigint not null,
	firstname			varchar(50),
	middlename			varchar(50),
	surname				varchar(50),
	sex_id				bigint,
	actor				bool,
	lastmodified		timestamp,

	primary key (id)
) type=innodb;
