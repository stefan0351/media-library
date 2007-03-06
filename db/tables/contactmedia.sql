DROP TABLE IF EXISTS `contactmedia`;
CREATE TABLE `contactmedia` (
  `id` bigint(20) NOT NULL default '0',
  `type` int(11) default NULL,
  `value` varchar(200) default NULL,
  `author_id` bigint(20) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
