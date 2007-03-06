DROP TABLE IF EXISTS `fanfics`;
CREATE TABLE `fanfics` (
  `id` bigint(20) NOT NULL default '0',
  `title` varchar(200) default NULL,
  `rating` varchar(10) default NULL,
  `description` text,
  `sequel_id` bigint(20) default NULL,
  `prequel_id` bigint(20) default NULL,
  `finished` tinyint(1) default NULL,
  `spoiler` text,
  `url` varchar(200) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
