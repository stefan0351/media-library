DROP TABLE IF EXISTS `map_movie_language`;
CREATE TABLE `map_movie_language` (
  `movie_id` bigint(20) NOT NULL,
  `language_id` bigint(20) NOT NULL,
  UNIQUE KEY `movie_id` (`movie_id`,`language_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
