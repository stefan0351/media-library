DROP TABLE IF EXISTS `channels`;
CREATE TABLE `channels` (
  `id` bigint(20) NOT NULL default '0',
  `name` varchar(50) NOT NULL default '',
  `logo` varchar(200) default NULL,
  `language_id` bigint(20) default NULL,
  `receivable` tinyint(1) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
