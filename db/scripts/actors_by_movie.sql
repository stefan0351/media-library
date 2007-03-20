select name, count(*)
from persons
	join cast on cast.actor_id=persons.id
where cast.movie_id is not null
group by cast.actor_id
having count(*)>5