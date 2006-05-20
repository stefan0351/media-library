drop table if exists sequences;
create table sequences
(
	name            varchar(10) not null,
	value           bigint,

	primary key (name)
) type=innodb;

insert into sequences (name, value) values ('id', 1);
insert into sequences (name, value) values ('fanfic', 1);
commit;
