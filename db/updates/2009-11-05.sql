alter table airdates add enddate timestamp;
update airdates set enddate=viewdate;
commit;
alter table airdates modify enddate timestamp not null;
