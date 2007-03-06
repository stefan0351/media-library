DROP TABLE IF EXISTS `map_fanfic_pairing`;
CREATE TABLE `map_fanfic_pairing` (
  `fanfic_id` bigint(20) NOT NULL default '0',
  `pairing_id` bigint(20) NOT NULL default '0',
  UNIQUE KEY `fanfic_id` (`fanfic_id`,`pairing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
