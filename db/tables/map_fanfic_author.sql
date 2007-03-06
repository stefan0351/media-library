DROP TABLE IF EXISTS `map_fanfic_author`;
CREATE TABLE `map_fanfic_author` (
  `fanfic_id` bigint(20) NOT NULL default '0',
  `author_id` bigint(20) NOT NULL default '0',
  UNIQUE KEY `fanfic_id` (`fanfic_id`,`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
