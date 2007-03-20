select persons.id, name, count(distinct movie_id)
from persons
	join crew on crew.person_id=persons.id
where crew.movie_id is not null
group by crew.person_id
having count(*)>5