DROP TABLE IF EXISTS `languages`;
CREATE TABLE `languages` (
  `id` bigint(20) NOT NULL default '0',
  `symbol` char(2) NOT NULL default '',
  `name` varchar(50) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
