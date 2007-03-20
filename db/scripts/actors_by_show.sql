select s.id, s.name, count(*) from 
	(select distinct persons.id, persons.name 'name', ifnull(cast.show_id, episodes.show_id) as 'show'
		from cast
			join persons on persons.id=cast.actor_id
			left join episodes on episodes.id=cast.episode_id
		where cast.show_id is not null or cast.episode_id is not null) s
group by s.id, s.name
having count(*)>2