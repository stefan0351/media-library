DROP TABLE IF EXISTS `pairings`;
CREATE TABLE `pairings` (
  `id` bigint(20) NOT NULL default '0',
  `name` varchar(200) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;