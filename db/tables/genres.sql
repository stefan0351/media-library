DROP TABLE IF EXISTS `genres`;
CREATE TABLE `genres` (
  `id` bigint(20) NOT NULL,
  `name` varchar(200) NOT NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
