DROP TABLE IF EXISTS `showinfos`;
CREATE TABLE `showinfos` (
  `id` bigint(20) NOT NULL default '0',
  `show_id` bigint(20) default NULL,
  `language_id` bigint(20) default NULL,
  `name` varchar(50) default NULL,
  `path` varchar(200) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
