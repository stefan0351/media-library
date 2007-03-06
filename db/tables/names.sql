DROP TABLE IF EXISTS `names`;
CREATE TABLE `names` (
  `id` bigint(20) NOT NULL default '0',
  `ref_id` bigint(20) NOT NULL default '0',
  `type` int(11) default NULL,
  `name` varchar(200) default NULL,
  `language_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
