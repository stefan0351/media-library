select persons.id, name, count(distinct movie_id)
from persons
	join credit on credit.person_id=persons.id
where credit.movie_id is not null
group by credit.person_id
having count(*)>5