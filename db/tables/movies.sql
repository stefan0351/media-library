DROP TABLE IF EXISTS `movies`;
CREATE TABLE `movies` (
  `id` bigint(20) NOT NULL default '0',
  `show_id` bigint(20) default NULL,
  `record` tinyint(1) default NULL,
  `javascript` varchar(50) default NULL,
  `webscriptfile` varchar(200) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `poster_mini` varchar(200) default NULL,
  `german_title` varchar(200) default NULL,
  `title` varchar(200) default NULL,
  `year` int(11) default NULL,
  `runtime` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
