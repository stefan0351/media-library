DROP TABLE IF EXISTS `persons`;
CREATE TABLE `persons` (
  `id` bigint(20) NOT NULL default '0',
  `firstname` varchar(50) default NULL,
  `middlename` varchar(50) default NULL,
  `surname` varchar(50) default NULL,
  `sex_id` bigint(20) default NULL,
  `actor` tinyint(1) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `name` varchar(200) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
