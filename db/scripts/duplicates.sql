select 'persons', name, count(*), min(id), max(id) from persons group by name having count(*)>1;
select 'movies', title, year, count(*), min(id), max(id)  from movies group by title, year having count(*)>1;

