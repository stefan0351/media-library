DROP TABLE IF EXISTS `searchpatterns`;
CREATE TABLE `searchpatterns` (
  `id` bigint(20) NOT NULL default '0',
  `type` int(11) default NULL,
  `show_id` bigint(20) default NULL,
  `pattern` varchar(200) default NULL,
  `movie_id` bigint(20) default NULL,
  `actor_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
