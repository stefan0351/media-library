DROP TABLE IF EXISTS `map_movie_genre`;
CREATE TABLE `map_movie_genre` (
  `movie_id` bigint(20) NOT NULL,
  `genre_id` bigint(20) NOT NULL,
  UNIQUE KEY `movie_id` (`movie_id`,`genre_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
