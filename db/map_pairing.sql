drop table if exists map_pairing;
create table map_pairing
(
	pairing_id			bigint not null,
	character_id		bigint not null,

	unique index (pairing_id, character_id)
) type=innodb;
