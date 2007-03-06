DROP TABLE IF EXISTS `crew`;
CREATE TABLE `crew` (
  `id` bigint(20) NOT NULL,
  `type` varchar(100) NOT NULL,
  `episode_id` bigint(20) default NULL,
  `person_id` bigint(20) NOT NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `sub_type` varchar(200) default NULL,
  `movie_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
