DROP TABLE IF EXISTS `links`;
CREATE TABLE `links` (
  `id` bigint(20) NOT NULL default '0',
  `show_id` bigint(20) default NULL,
  `language_id` bigint(20) default NULL,
  `name` varchar(200) default NULL,
  `url` varchar(200) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `fandom_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
