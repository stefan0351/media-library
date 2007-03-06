DROP TABLE IF EXISTS `map_fanfic_fandom`;
CREATE TABLE `map_fanfic_fandom` (
  `fanfic_id` bigint(20) NOT NULL default '0',
  `fandom_id` bigint(20) NOT NULL default '0',
  UNIQUE KEY `fanfic_id` (`fanfic_id`,`fandom_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
