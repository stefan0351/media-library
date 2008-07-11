update sequences set value=(select max(userkey)+2 from media where mod(userkey,2)=1) where name='medium';
update sequences set value=(select max(id)+2 from (
    select max(id) id from persons where mod(id,2)=1
    union select max(id) id from movies where mod(id,2)=1
    union select max(id) id from cast where mod(id,2)=1
    union select max(id) id from episodes where mod(id,2)=1
    union select max(id) id from shows where mod(id,2)=1
    union select max(id) id from seasons where mod(id,2)=1
    union select max(id) id from summary where mod(id,2)=1
    union select max(id) id from tracks where mod(id,2)=1
) t) where name='id';
commit;
