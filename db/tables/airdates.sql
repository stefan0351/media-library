DROP TABLE IF EXISTS `airdates`;
CREATE TABLE `airdates` (
  `id` bigint(20) NOT NULL,
  `event` varchar(200) default NULL,
  `viewdate` timestamp NULL default NULL,
  `channel_id` bigint(20) default NULL,
  `channel` varchar(100) default NULL,
  `show_id` bigint(20) default NULL,
  `episode_id` bigint(20) default NULL,
  `movie_id` bigint(20) default NULL,
  `language_id` bigint(20) default NULL,
  `reminder` tinyint(1) default NULL,
  `source_id` bigint(20) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
