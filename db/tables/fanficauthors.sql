DROP TABLE IF EXISTS `fanficauthors`;
CREATE TABLE `fanficauthors` (
  `id` bigint(20) NOT NULL default '0',
  `name` varchar(200) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `path` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
