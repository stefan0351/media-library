alter table sequences add cache bigint not null default 1;
update sequences set cache=50 where name='id';
commit;
