drop table if exists credit_types;
create table credit_types
(
	id bigint(20) not null,
	by_name varchar(50) not null,
	as_name varchar(50) not null,
	primary key (id)
) engine=InnoDB default charset=utf8;
