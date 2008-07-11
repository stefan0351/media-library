select 'airdates' as 'table', id, 'episode_id' as 'column' from airdates where episode_id not in (select id from episodes)
union select 'airdates', id, 'channel_id' from airdates where channel_id not in (select id from channels)
union select 'airdates', id, 'show_id' from airdates where show_id not in (select id from shows)
union select 'airdates', id, 'language_id' from airdates where language_id not in (select id from languages)
union select 'airdates', id, 'movie_id' from airdates where movie_id not in (select id from movies)

union select 'cast', id, 'actor_id' from cast where actor_id not in (select id from persons)
union select 'cast', id, 'episode_id' from cast where episode_id not in (select id from episodes)
union select 'cast', id, 'show_id' from cast where show_id not in (select id from shows)
union select 'cast', id, 'movie_id' from cast where movie_id not in (select id from movies)

union select 'credit', id, 'person_id' from credit where person_id not in (select id from persons)
union select 'credit', id, 'episode_id' from credit where episode_id not in (select id from episodes)
union select 'credit', id, 'movie_id' from credit where movie_id not in (select id from movies)

union select 'channels', id, 'language_id' from channels where language_id not in (select id from languages)

union select 'contactmedia', id, 'author_id' from contactmedia where author_id not in (select id from fanficauthors)

union select 'episodes', id, 'show_id' from episodes where show_id not in (select id from shows)

union select 'fandoms', id, 'show_id' from fandoms where show_id not in (select id from shows)
union select 'fandoms', id, 'movie_id' from fandoms where movie_id not in (select id from movies)
union select 'fandoms', id, 'linkgroup_id' from shows where linkgroup_id not in (select id from linkgroups)

union select 'fanfic_parts', id, 'fanfic_id' from fanfic_parts where fanfic_id not in (select id from fanfics)

union select 'fanfics', id, 'sequel_id' from fanfics where sequel_id not in (select id from fanfics)
union select 'fanfics', id, 'prequel_id' from fanfics where prequel_id not in (select id from fanfics)

union select 'links', id, 'linkgroup_id' from links where linkgroup_id not in (select id from linkgroups)

union select 'linkgroups', id, 'parentgroup_id' from linkgroups where parentgroup_id not in (select id from linkgroups)

union select 'map_fanfic_author', fanfic_id, 'fanfic_id' from map_fanfic_author where fanfic_id not in (select id from fanfics)
union select 'map_fanfic_author', author_id, 'author_id' from map_fanfic_author where author_id not in (select id from fanficauthors)

union select 'map_fanfic_fandom', fanfic_id, 'fanfic_id' from map_fanfic_fandom where fanfic_id not in (select id from fanfics)
union select 'map_fanfic_fandom', fandom_id, 'fandom_id' from map_fanfic_fandom where fandom_id not in (select id from fandoms)

union select 'map_fanfic_pairing', fanfic_id, 'fanfic_id' from map_fanfic_pairing where fanfic_id not in (select id from fanfics)
union select 'map_fanfic_pairing', pairing_id, 'pairing_id' from map_fanfic_pairing where pairing_id not in (select id from pairings)

union select 'map_movie_country', movie_id, 'movie_id' from map_movie_country where movie_id not in (select id from movies)
union select 'map_movie_country', country_id, 'country_id' from map_movie_country where country_id not in (select id from countries)

union select 'map_movie_genre', movie_id, 'movie_id' from map_movie_genre where movie_id not in (select id from movies)
union select 'map_movie_genre', genre_id, 'genre_id' from map_movie_genre where genre_id not in (select id from genres)

union select 'map_movie_language', movie_id, 'movie_id' from map_movie_language where movie_id not in (select id from movies)
union select 'map_movie_language', language_id, 'language_id' from map_movie_language where language_id not in (select id from languages)

union select 'map_show_genre', show_id, 'show_id' from map_show_genre where show_id not in (select id from shows)
union select 'map_show_genre', genre_id, 'genre_id' from map_show_genre where genre_id not in (select id from genres)

union select 'relatedlinkgroups', linkgroup_id, 'linkgroup_id' from relatedlinkgroups where linkgroup_id not in (select id from linkgroups)
union select 'relatedlinkgroups', relatedgroup_id, 'relatedgroup_id' from relatedlinkgroups where relatedgroup_id not in (select id from linkgroups)

union select 'movies', id, 'show_id' from movies where show_id not in (select id from shows)

union select 'names', id, 'type=1 and ref_id' from names where type=1 and ref_id not in (select id from shows)
union select 'names', id, 'type=2 and ref_id' from names where type=2 and ref_id not in (select id from episodes)
union select 'names', id, 'type=3 and ref_id' from names where type=3 and ref_id not in (select id from channels)
union select 'names', id, 'type=4 and ref_id' from names where type=4 and ref_id not in (select id from movies)
union select 'names', id, 'language_id' from names where language_id not in (select id from languages)

union select 'tracks', id, 'medium_id' from tracks where medium_id not in (select id from media)
union select 'tracks', id, 'episode_id' from tracks where episode_id not in (select id from episodes)
union select 'tracks', id, 'show_id' from tracks where show_id not in (select id from shows)
union select 'tracks', id, 'movie_id' from tracks where movie_id not in (select id from movies)
union select 'tracks', id, 'language_id' from tracks where language_id not in (select id from languages)

union select 'searchpatterns', id, 'movie_id' from searchpatterns where movie_id not in (select id from movies)
union select 'searchpatterns', id, 'show_id' from searchpatterns where show_id not in (select id from shows)
union select 'searchpatterns', id, 'actor_id' from searchpatterns where actor_id not in (select id from persons)

union select 'seasons', id, 'show_id' from seasons where show_id not in (select id from shows)
union select 'seasons', id, 'firstepisode_id' from seasons where firstepisode_id not in (select id from episodes)
union select 'seasons', id, 'lastepisode_id' from seasons where lastepisode_id not in (select id from episodes)

union select 'showinfos', id, 'show_id' from showinfos where show_id not in (select id from shows)
union select 'showinfos', id, 'language_id' from showinfos where language_id not in (select id from languages)

union select 'shows', id, 'language_id' from shows where language_id not in (select id from languages)
union select 'shows', id, 'defaultinfo_id' from shows where defaultinfo_id not in (select id from showinfos)
union select 'shows', id, 'linkgroup_id' from shows where linkgroup_id not in (select id from linkgroups)

union select 'summary', id, 'episode_id' from summary where episode_id not in (select id from episodes)
union select 'summary', id, 'movie_id' from summary where movie_id not in (select id from movies)
union select 'summary', id, 'language_id' from summary where language_id not in (select id from languages)
;