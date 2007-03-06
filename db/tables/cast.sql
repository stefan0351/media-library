DROP TABLE IF EXISTS `cast`;
CREATE TABLE `cast` (
  `id` bigint(20) NOT NULL default '0',
  `actor_id` bigint(20) default NULL,
  `show_id` bigint(20) default NULL,
  `episode_id` bigint(20) default NULL,
  `movie_id` bigint(20) default NULL,
  `type` int(11) default NULL,
  `voice` varchar(100) default NULL,
  `lastmodified` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `image_small` varchar(200) default NULL,
  `image_large` varchar(200) default NULL,
  `description` text,
  `character_name` varchar(200) default NULL,
  `credit_order` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
