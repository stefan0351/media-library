DROP TABLE IF EXISTS `map_movie_country`;
CREATE TABLE `map_movie_country` (
  `movie_id` bigint(20) NOT NULL,
  `country_id` bigint(20) NOT NULL,
  UNIQUE KEY `movie_id` (`movie_id`,`country_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
