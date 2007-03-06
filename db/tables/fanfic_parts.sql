DROP TABLE IF EXISTS `fanfic_parts`;
CREATE TABLE `fanfic_parts` (
  `id` bigint(20) NOT NULL default '0',
  `fanfic_id` bigint(20) default NULL,
  `source` varchar(200) default NULL,
  `sequence` int(11) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
