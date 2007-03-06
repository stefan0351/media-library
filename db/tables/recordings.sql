DROP TABLE IF EXISTS `recordings`;
CREATE TABLE `recordings` (
  `id` bigint(20) NOT NULL default '0',
  `longplay` tinyint(1) default NULL,
  `length` int(11) default NULL,
  `event` varchar(200) default NULL,
  `show_id` bigint(20) default NULL,
  `episode_id` bigint(20) default NULL,
  `language_id` bigint(20) default NULL,
  `video_id` bigint(20) default NULL,
  `sequence` int(11) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `movie_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
