DROP TABLE IF EXISTS `fandoms`;
CREATE TABLE `fandoms` (
  `id` bigint(20) NOT NULL default '0',
  `name` varchar(200) default NULL,
  `show_id` bigint(20) default NULL,
  `movie_id` bigint(20) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
