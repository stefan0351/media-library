DROP TABLE IF EXISTS `map_show_genre`;
CREATE TABLE `map_show_genre` (
  `show_id` bigint(20) NOT NULL,
  `genre_id` bigint(20) NOT NULL,
  UNIQUE KEY `show_id` (`show_id`,`genre_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
