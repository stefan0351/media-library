DROP TABLE IF EXISTS `summary`;
CREATE TABLE `summary` (
  `id` bigint(20) NOT NULL,
  `episode_id` bigint(20) default NULL,
  `language_id` bigint(20) NOT NULL,
  `summary` mediumtext,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `movie_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
