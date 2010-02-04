alter table books add storage varchar(50);
alter table books add original_title varchar(255);

drop table if exists photogalleries_structure;
create table photogalleries_structure
(
	parent_id bigint(20) NOT NULL,
  	child_id bigint(20) NOT NULL,
  	unique key parent_id (parent_id, child_id)
) engine=InnoDB default charset=utf8;

update photogalleries set parent_id=1 where parent_id is null;
insert into photogalleries values (1, 'ROOT', now(), now(), null);
insert into photogalleries_structure (parent_id, child_id) (select parent_id, id from photogalleries where parent_id is not null);
commit;

alter table photogalleries drop column parent_id;
