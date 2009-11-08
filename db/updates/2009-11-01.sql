alter table books add series_name varchar(200);
alter table books add series_number int;

alter table books add index_by varchar(200);
update books set index_by=ucase(title) where index_by is null and series_name is null;
update books set index_by=ucase(concat(title, ' ', series_name)) where index_by is null and series_name is not null;
commit;
alter table books modify column index_by varchar(200) not null;