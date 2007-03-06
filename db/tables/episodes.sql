DROP TABLE IF EXISTS `episodes`;
CREATE TABLE `episodes` (
  `id` bigint(20) NOT NULL default '0',
  `show_id` bigint(20) default NULL,
  `userkey` varchar(10) default NULL,
  `sequence` int(11) NOT NULL default '0',
  `name` varchar(200) default NULL,
  `seen` tinyint(1) default NULL,
  `good` tinyint(1) default NULL,
  `record` tinyint(1) default NULL,
  `javascript` varchar(50) default NULL,
  `webscriptfile` varchar(200) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `name_original` varchar(200) default NULL,
  `production_code` varchar(20) default NULL,
  `airdate` date default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
