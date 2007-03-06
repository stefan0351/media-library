DROP TABLE IF EXISTS `countries`;
CREATE TABLE `countries` (
  `id` bigint(20) NOT NULL default '0',
  `symbol` char(2) NOT NULL default '',
  `name` varchar(50) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
