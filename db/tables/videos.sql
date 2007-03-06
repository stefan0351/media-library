DROP TABLE IF EXISTS `videos`;
CREATE TABLE `videos` (
  `id` bigint(20) NOT NULL default '0',
  `userkey` varchar(10) NOT NULL default '',
  `name` varchar(100) default NULL,
  `length` int(11) NOT NULL default '0',
  `remaininglength` int(11) NOT NULL default '0',
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `type_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
