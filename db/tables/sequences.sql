DROP TABLE IF EXISTS `sequences`;
CREATE TABLE `sequences` (
  `name` varchar(10) NOT NULL default '',
  `value` bigint(20) default NULL,
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
