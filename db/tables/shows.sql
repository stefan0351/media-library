DROP TABLE IF EXISTS `shows`;
CREATE TABLE `shows` (
  `id` bigint(20) NOT NULL default '0',
  `userkey` varchar(20) NOT NULL default '',
  `name` varchar(100) NOT NULL default '',
  `internet` tinyint(1) default NULL,
  `episode_length` int(11) default NULL,
  `webdatesfile` varchar(200) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `language_id` bigint(20) default NULL,
  `logo_mini` varchar(200) default NULL,
  `defaultinfo_id` bigint(20) default NULL,
  `name_original` varchar(100) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
