select 'airdates' as 'table', id, 'episode_id' as 'column' from airdates where episode_id not in (select id from episodes)
union select 'airdates', id, 'channel_id' from airdates where channel_id not in (select id from channels)
union select 'airdates', id, 'show_id' from airdates where show_id not in (select id from shows)
union select 'airdates', id, 'language_id' from airdates where language_id not in (select id from languages)
union select 'airdates', id, 'movie_id' from airdates where movie_id not in (select id from movies)

union select 'books', id, 'cover_id' from books where cover_id not in (select id from mediafiles)
union select 'books', id, 'language_id' from books where language_id not in (select id from languages)

union select 'cast', id, 'actor_id' from cast where actor_id not in (select id from persons)
union select 'cast', id, 'episode_id' from cast where episode_id not in (select id from episodes)
union select 'cast', id, 'show_id' from cast where show_id not in (select id from shows)
union select 'cast', id, 'movie_id' from cast where movie_id not in (select id from movies)
union select 'cast', id, 'picture_id' from cast where picture_id not in (select id from mediafiles)

union select 'channels', id, 'language_id' from channels where language_id not in (select id from languages)
union select 'channels', id, 'logo_id' from channels where logo_id not in (select id from mediafiles)

union select 'contactmedia', id, 'author_id' from contactmedia where author_id not in (select id from fanficauthors)

union select 'credit', id, 'person_id' from credit where person_id not in (select id from persons)
union select 'credit', id, 'episode_id' from credit where episode_id not in (select id from episodes)
union select 'credit', id, 'movie_id' from credit where movie_id not in (select id from movies)
union select 'credit', id, 'song_id' from credit where song_id not in (select id from songs)

union select 'episodes', id, 'show_id' from episodes where show_id not in (select id from shows)

union select 'fandoms', id, 'show_id' from fandoms where show_id not in (select id from shows)
union select 'fandoms', id, 'movie_id' from fandoms where movie_id not in (select id from movies)
union select 'fandoms', id, 'linkgroup_id' from shows where linkgroup_id not in (select id from linkgroups)

union select 'fanfics', id, 'sequel_id' from fanfics where sequel_id not in (select id from fanfics)
union select 'fanfics', id, 'prequel_id' from fanfics where prequel_id not in (select id from fanfics)

union select 'fanfic_parts', id, 'fanfic_id' from fanfic_parts where fanfic_id not in (select id from fanfics)

union select 'links', id, 'linkgroup_id' from links where linkgroup_id not in (select id from linkgroups)

union select 'linkgroups', id, 'parentgroup_id' from linkgroups where parentgroup_id not in (select id from linkgroups)

union select 'mediafiles', id, 'thumbnail_id' from mediafiles where thumbnail_id not in (select id from imagefiles)
union select 'mediafiles', id, 'thumbnail_50x50_id' from mediafiles where thumbnail_50x50_id not in (select id from imagefiles)
union select 'mediafiles', id, 'thumbnail_sidebar_id' from mediafiles where thumbnail_sidebar_id not in (select id from imagefiles)

union select 'movies', id, 'show_id' from movies where show_id not in (select id from shows)
union select 'movies', id, 'poster_id' from movies where poster_id not in (select id from mediafiles)

union select 'names', id, 'type=1 and ref_id' from names where type=1 and ref_id not in (select id from shows)
union select 'names', id, 'type=2 and ref_id' from names where type=2 and ref_id not in (select id from episodes)
union select 'names', id, 'type=3 and ref_id' from names where type=3 and ref_id not in (select id from channels)
union select 'names', id, 'type=4 and ref_id' from names where type=4 and ref_id not in (select id from movies)
union select 'names', id, 'language_id' from names where language_id not in (select id from languages)

union select 'persons', id, 'picture_id' from persons where picture_id not in (select id from mediafiles)

union select 'photos', id, 'photogallery_id' from photos where photogallery_id not in (select id from photogalleries)
union select 'photos', id, 'original_pic_id' from photos where original_pic_id not in (select id from imagefiles)
union select 'photos', id, 'thumbnail_id' from photos where thumbnail_id not in (select id from imagefiles)

union select 'searchpatterns', id, 'movie_id' from searchpatterns where movie_id not in (select id from movies)
union select 'searchpatterns', id, 'show_id' from searchpatterns where show_id not in (select id from shows)
union select 'searchpatterns', id, 'person_id' from searchpatterns where person_id not in (select id from persons)

union select 'seasons', id, 'show_id' from seasons where show_id not in (select id from shows)
union select 'seasons', id, 'firstepisode_id' from seasons where firstepisode_id not in (select id from episodes)
union select 'seasons', id, 'lastepisode_id' from seasons where lastepisode_id not in (select id from episodes)
union select 'seasons', id, 'logo_id' from seasons where logo_id not in (select id from mediafiles)

union select 'shows', id, 'language_id' from shows where language_id not in (select id from languages)
union select 'shows', id, 'defaultinfo_id' from shows where defaultinfo_id not in (select id from showinfos)
union select 'shows', id, 'linkgroup_id' from shows where linkgroup_id not in (select id from linkgroups)
union select 'shows', id, 'logo_id' from shows where logo_id not in (select id from mediafiles)

union select 'showinfos', id, 'show_id' from showinfos where show_id not in (select id from shows)
union select 'showinfos', id, 'language_id' from showinfos where language_id not in (select id from languages)

union select 'summary', id, 'episode_id' from summary where episode_id not in (select id from episodes)
union select 'summary', id, 'movie_id' from summary where movie_id not in (select id from movies)
union select 'summary', id, 'language_id' from summary where language_id not in (select id from languages)

union select 'tracks', id, 'medium_id' from tracks where medium_id not in (select id from media)
union select 'tracks', id, 'episode_id' from tracks where episode_id not in (select id from episodes)
union select 'tracks', id, 'show_id' from tracks where show_id not in (select id from shows)
union select 'tracks', id, 'movie_id' from tracks where movie_id not in (select id from movies)
union select 'tracks', id, 'song_id' from tracks where song_id not in (select id from songs)
union select 'tracks', id, 'language_id' from tracks where language_id not in (select id from languages)

union select 'map_fanfic_pairing', fanfic_id, 'fanfic_id' from map_fanfic_pairing where fanfic_id not in (select id from fanfics)
union select 'map_fanfic_pairing', pairing_id, 'pairing_id' from map_fanfic_pairing where pairing_id not in (select id from pairings)

union select 'map_fanfic_author', fanfic_id, 'fanfic_id' from map_fanfic_author where fanfic_id not in (select id from fanfics)
union select 'map_fanfic_author', author_id, 'author_id' from map_fanfic_author where author_id not in (select id from fanficauthors)

union select 'map_fanfic_fandom', fanfic_id, 'fanfic_id' from map_fanfic_fandom where fanfic_id not in (select id from fanfics)
union select 'map_fanfic_fandom', fandom_id, 'fandom_id' from map_fanfic_fandom where fandom_id not in (select id from fandoms)

union select 'map_show_genre', show_id, 'show_id' from map_show_genre where show_id not in (select id from shows)
union select 'map_show_genre', genre_id, 'genre_id' from map_show_genre where genre_id not in (select id from genres)

union select 'map_movie_genre', movie_id, 'movie_id' from map_movie_genre where movie_id not in (select id from movies)
union select 'map_movie_genre', genre_id, 'genre_id' from map_movie_genre where genre_id not in (select id from genres)

union select 'map_movie_language', movie_id, 'movie_id' from map_movie_language where movie_id not in (select id from movies)
union select 'map_movie_language', language_id, 'language_id' from map_movie_language where language_id not in (select id from languages)

union select 'map_movie_country', movie_id, 'movie_id' from map_movie_country where movie_id not in (select id from movies)
union select 'map_movie_country', country_id, 'country_id' from map_movie_country where country_id not in (select id from countries)

union select 'map_book_author', book_id, 'book_id' from map_book_author where book_id not in (select id from books)
union select 'map_book_author', author_id, 'author_id' from map_book_author where author_id not in (select id from persons)

union select 'map_book_translator', book_id, 'book_id' from map_book_translator where book_id not in (select id from books)
union select 'map_book_translator', translator_id, 'translator_id' from map_book_translator where translator_id not in (select id from persons)

union select 'relatedlinkgroups', linkgroup_id, 'linkgroup_id' from relatedlinkgroups where linkgroup_id not in (select id from linkgroups)
union select 'relatedlinkgroups', relatedgroup_id, 'relatedgroup_id' from relatedlinkgroups where relatedgroup_id not in (select id from linkgroups)

union select 'mediafile_shows', show_id, 'show_id' from mediafile_shows where show_id not in (select id from shows)
union select 'mediafile_shows', mediafile_id, 'mediafile_id' from mediafile_shows where mediafile_id not in (select id from mediafiles)

union select 'mediafile_episodes', episode_id, 'episode_id' from mediafile_episodes where episode_id not in (select id from episodes)
union select 'mediafile_episodes', mediafile_id, 'mediafile_id' from mediafile_episodes where mediafile_id not in (select id from mediafiles)

union select 'mediafile_movies', movie_id, 'movie_id' from mediafile_movies where movie_id not in (select id from movies)
union select 'mediafile_movies', mediafile_id, 'mediafile_id' from mediafile_movies where mediafile_id not in (select id from mediafiles)

union select 'mediafile_persons', person_id, 'person_id' from mediafile_persons where person_id not in (select id from persons)
union select 'mediafile_persons', mediafile_id, 'mediafile_id' from mediafile_persons where mediafile_id not in (select id from mediafiles)

union select 'airdate_persons', person_id, 'person_id' from airdate_persons where person_id not in (select id from persons)
union select 'airdate_persons', airdate_id, 'airdate_id' from airdate_persons where airdate_id not in (select id from airdates)