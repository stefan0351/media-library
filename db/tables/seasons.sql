DROP TABLE IF EXISTS `seasons`;
CREATE TABLE `seasons` (
  `id` bigint(20) NOT NULL default '0',
  `number` int(11) default NULL,
  `show_id` bigint(20) default NULL,
  `firstepisode_id` bigint(20) default NULL,
  `lastepisode_id` bigint(20) default NULL,
  `startYear` int(11) default NULL,
  `endYear` int(11) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
