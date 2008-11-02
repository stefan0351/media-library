select * 
from pictures pi
	left outer join channels ch on ch.logo_id=pi.id
	left outer join movies mo on mo.poster_id=pi.id
	left outer join persons pe on pe.picture_id=pi.id
	left outer join books bo on bo.cover_id=pi.id
	left outer join shows sh on sh.logo_id=pi.id
	left outer join cast ca on ca.picture_id=pi.id
where ch.id is null and mo.id is null and pe.id is null and bo.id is null and sh.id is null and ca.id is null;
