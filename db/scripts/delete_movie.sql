delete from mediafile_movies where movie_id=?/*=movie_id*/;
delete from airdates where movie_id=?/*=movie_id*/;
delete from cast where movie_id=?/*=movie_id*/;
delete from credit where movie_id=?/*=movie_id*/;
delete from map_movie_country where movie_id=?/*=movie_id*/;
delete from map_movie_language where movie_id=?/*=movie_id*/;
delete from map_movie_genre where movie_id=?/*=movie_id*/;
delete from summary where movie_id=?/*=movie_id*/;
delete from names where type=4 and ref_id=?/*=movie_id*/;
delete from movies where id=?/*=movie_id*/;
commit;