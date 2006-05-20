drop table if exists map_fanfic_pairing;
create table map_fanfic_pairing
(
	fanfic_id			bigint not null,
	pairing_id			bigint not null,

	unique index (fanfic_id, pairing_id)
) type=innodb;
