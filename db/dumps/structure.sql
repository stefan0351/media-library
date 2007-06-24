-- MySQL dump 10.10
--
-- Host: localhost    Database: media
-- ------------------------------------------------------
-- Server version	5.0.27-community-nt

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `airdates`
--

DROP TABLE IF EXISTS airdates;
CREATE TABLE airdates (
  id bigint(20) NOT NULL,
  event varchar(200) default NULL,
  viewdate timestamp NULL default NULL,
  channel_id bigint(20) default NULL,
  channel varchar(100) default NULL,
  show_id bigint(20) default NULL,
  episode_id bigint(20) default NULL,
  movie_id bigint(20) default NULL,
  language_id bigint(20) default NULL,
  reminder tinyint(1) default NULL,
  source_id bigint(20) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `books`
--

DROP TABLE IF EXISTS books;
CREATE TABLE books (
  id bigint(20) NOT NULL,
  title varchar(200) NOT NULL,
  binding varchar(200) default NULL,
  cover_id bigint(20) default NULL,
  publisher varchar(200) default NULL,
  published_year int(11) default NULL,
  edition varchar(50) default NULL,
  language_id bigint(20) default NULL,
  pages int(11) default NULL,
  isbn10 varchar(20) default NULL,
  isbn13 varchar(20) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `cast`
--

DROP TABLE IF EXISTS cast;
CREATE TABLE cast (
  id bigint(20) NOT NULL default '0',
  credit_type_id bigint(20) NOT NULL,
  actor_id bigint(20) NOT NULL,
  show_id bigint(20) default NULL,
  episode_id bigint(20) default NULL,
  movie_id bigint(20) default NULL,
  voice varchar(100) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  description text,
  character_name varchar(400) default NULL,
  credit_order int(11) default NULL,
  picture_id bigint(20) default NULL,
  PRIMARY KEY  (id),
  KEY ix_cast_actor (movie_id),
  KEY ix_cast_show (show_id),
  KEY ix_cast_episode (episode_id),
  KEY ix_cast_movie (movie_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `channels`
--

DROP TABLE IF EXISTS channels;
CREATE TABLE channels (
  id bigint(20) NOT NULL default '0',
  `name` varchar(50) NOT NULL default '',
  logo varchar(200) default NULL,
  language_id bigint(20) default NULL,
  receivable tinyint(1) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `contactmedia`
--

DROP TABLE IF EXISTS contactmedia;
CREATE TABLE contactmedia (
  id bigint(20) NOT NULL default '0',
  `type` int(11) default NULL,
  `value` varchar(200) default NULL,
  author_id bigint(20) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `countries`
--

DROP TABLE IF EXISTS countries;
CREATE TABLE countries (
  id bigint(20) NOT NULL default '0',
  symbol varchar(3) NOT NULL,
  `name` varchar(50) NOT NULL default '',
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `credit_types`
--

DROP TABLE IF EXISTS credit_types;
CREATE TABLE credit_types (
  id bigint(20) NOT NULL,
  by_name varchar(20) default NULL,
  as_name varchar(20) default NULL,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `crew`
--

DROP TABLE IF EXISTS crew;
CREATE TABLE crew (
  id bigint(20) NOT NULL,
  credit_type_id bigint(20) NOT NULL,
  episode_id bigint(20) default NULL,
  person_id bigint(20) NOT NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  sub_type varchar(200) default NULL,
  movie_id bigint(20) default NULL,
  PRIMARY KEY  (id),
  KEY ix_crew_episode (episode_id),
  KEY ix_crew_movie (movie_id),
  KEY ix_crew_person (person_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `episodes`
--

DROP TABLE IF EXISTS episodes;
CREATE TABLE episodes (
  id bigint(20) NOT NULL default '0',
  show_id bigint(20) default NULL,
  userkey varchar(10) default NULL,
  sequence int(11) NOT NULL default '0',
  german_title varchar(200) default NULL,
  seen tinyint(1) default NULL,
  good tinyint(1) default NULL,
  record tinyint(1) default NULL,
  javascript varchar(50) default NULL,
  webscriptfile varchar(200) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  title varchar(200) default NULL,
  production_code varchar(20) default NULL,
  airdate date default NULL,
  PRIMARY KEY  (id),
  KEY ix_episode_show (show_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `fandoms`
--

DROP TABLE IF EXISTS fandoms;
CREATE TABLE fandoms (
  id bigint(20) NOT NULL default '0',
  `name` varchar(200) default NULL,
  show_id bigint(20) default NULL,
  movie_id bigint(20) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `fanfic_parts`
--

DROP TABLE IF EXISTS fanfic_parts;
CREATE TABLE fanfic_parts (
  id bigint(20) NOT NULL default '0',
  fanfic_id bigint(20) default NULL,
  source varchar(200) default NULL,
  sequence int(11) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `fanficauthors`
--

DROP TABLE IF EXISTS fanficauthors;
CREATE TABLE fanficauthors (
  id bigint(20) NOT NULL default '0',
  `name` varchar(200) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  path varchar(50) default NULL,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `fanfics`
--

DROP TABLE IF EXISTS fanfics;
CREATE TABLE fanfics (
  id bigint(20) NOT NULL default '0',
  title varchar(200) default NULL,
  rating varchar(10) default NULL,
  description text,
  sequel_id bigint(20) default NULL,
  prequel_id bigint(20) default NULL,
  finished tinyint(1) default NULL,
  spoiler text,
  url varchar(200) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `genres`
--

DROP TABLE IF EXISTS genres;
CREATE TABLE genres (
  id bigint(20) NOT NULL,
  `name` varchar(200) NOT NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `languages`
--

DROP TABLE IF EXISTS languages;
CREATE TABLE languages (
  id bigint(20) NOT NULL default '0',
  symbol varchar(10) NOT NULL,
  `name` varchar(50) NOT NULL default '',
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `links`
--

DROP TABLE IF EXISTS links;
CREATE TABLE links (
  id bigint(20) NOT NULL default '0',
  show_id bigint(20) default NULL,
  language_id bigint(20) default NULL,
  `name` varchar(200) default NULL,
  url varchar(200) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  fandom_id bigint(20) default NULL,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `map_book_author`
--

DROP TABLE IF EXISTS map_book_author;
CREATE TABLE map_book_author (
  book_id bigint(20) NOT NULL,
  author_id bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `map_book_translator`
--

DROP TABLE IF EXISTS map_book_translator;
CREATE TABLE map_book_translator (
  book_id bigint(20) NOT NULL,
  translator_id bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `map_fanfic_author`
--

DROP TABLE IF EXISTS map_fanfic_author;
CREATE TABLE map_fanfic_author (
  fanfic_id bigint(20) NOT NULL default '0',
  author_id bigint(20) NOT NULL default '0',
  UNIQUE KEY fanfic_id (fanfic_id,author_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `map_fanfic_fandom`
--

DROP TABLE IF EXISTS map_fanfic_fandom;
CREATE TABLE map_fanfic_fandom (
  fanfic_id bigint(20) NOT NULL default '0',
  fandom_id bigint(20) NOT NULL default '0',
  UNIQUE KEY fanfic_id (fanfic_id,fandom_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `map_fanfic_pairing`
--

DROP TABLE IF EXISTS map_fanfic_pairing;
CREATE TABLE map_fanfic_pairing (
  fanfic_id bigint(20) NOT NULL default '0',
  pairing_id bigint(20) NOT NULL default '0',
  UNIQUE KEY fanfic_id (fanfic_id,pairing_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `map_movie_country`
--

DROP TABLE IF EXISTS map_movie_country;
CREATE TABLE map_movie_country (
  movie_id bigint(20) NOT NULL,
  country_id bigint(20) NOT NULL,
  UNIQUE KEY movie_id (movie_id,country_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `map_movie_genre`
--

DROP TABLE IF EXISTS map_movie_genre;
CREATE TABLE map_movie_genre (
  movie_id bigint(20) NOT NULL,
  genre_id bigint(20) NOT NULL,
  UNIQUE KEY movie_id (movie_id,genre_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `map_movie_language`
--

DROP TABLE IF EXISTS map_movie_language;
CREATE TABLE map_movie_language (
  movie_id bigint(20) NOT NULL,
  language_id bigint(20) NOT NULL,
  UNIQUE KEY movie_id (movie_id,language_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `map_show_genre`
--

DROP TABLE IF EXISTS map_show_genre;
CREATE TABLE map_show_genre (
  show_id bigint(20) NOT NULL,
  genre_id bigint(20) NOT NULL,
  UNIQUE KEY show_id (show_id,genre_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `movies`
--

DROP TABLE IF EXISTS movies;
CREATE TABLE movies (
  id bigint(20) NOT NULL default '0',
  show_id bigint(20) default NULL,
  record tinyint(1) default NULL,
  javascript varchar(50) default NULL,
  webscriptfile varchar(200) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  german_title varchar(200) default NULL,
  title varchar(200) default NULL,
  `year` int(11) default NULL,
  runtime int(11) default NULL,
  index_by varchar(200) NOT NULL,
  imdb_key varchar(20) default NULL,
  poster_id bigint(20) default NULL,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `names`
--

DROP TABLE IF EXISTS names;
CREATE TABLE `names` (
  id bigint(20) NOT NULL default '0',
  ref_id bigint(20) NOT NULL default '0',
  `type` int(11) default NULL,
  `name` varchar(200) default NULL,
  language_id bigint(20) default NULL,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `pairings`
--

DROP TABLE IF EXISTS pairings;
CREATE TABLE pairings (
  id bigint(20) NOT NULL default '0',
  `name` varchar(200) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `persons`
--

DROP TABLE IF EXISTS persons;
CREATE TABLE persons (
  id bigint(20) NOT NULL default '0',
  firstname varchar(50) default NULL,
  middlename varchar(50) default NULL,
  surname varchar(50) default NULL,
  gender_id bigint(20) default NULL,
  actor tinyint(1) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  `name` varchar(200) NOT NULL,
  imdb_key varchar(20) default NULL,
  tvcom_key varchar(20) default NULL,
  picture_id bigint(20) default NULL,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `photogalleries`
--

DROP TABLE IF EXISTS photogalleries;
CREATE TABLE photogalleries (
  id bigint(20) NOT NULL,
  `name` varchar(200) default NULL,
  creation_date date default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `photos`
--

DROP TABLE IF EXISTS photos;
CREATE TABLE photos (
  id bigint(20) NOT NULL,
  photogallery_id bigint(20) default NULL,
  gallery_photo tinyint(1) default '0',
  creation_date timestamp NULL default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  rotation int(11) default NULL,
  original_pic_id bigint(20) NOT NULL,
  thumbnail_id bigint(20) default NULL,
  sequence int(11) NOT NULL,
  camera_model varchar(100) default NULL,
  camera_make varchar(100) default NULL,
  color_depth smallint(6) default NULL,
  description text,
  xresolution int(11) default NULL,
  yresolution int(11) default NULL,
  f_number double default NULL,
  focal_length double default NULL,
  iso_speed int(11) default NULL,
  exposure_time double default NULL,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `picture_files`
--

DROP TABLE IF EXISTS picture_files;
CREATE TABLE picture_files (
  id bigint(20) NOT NULL,
  `file` varchar(200) NOT NULL,
  width int(11) NOT NULL,
  height int(11) NOT NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `pictures`
--

DROP TABLE IF EXISTS pictures;
CREATE TABLE pictures (
  id bigint(20) NOT NULL,
  `name` varchar(200) NOT NULL,
  `file` varchar(200) NOT NULL,
  width int(11) NOT NULL,
  height int(11) NOT NULL,
  thumbnail_50x50_id bigint(20) default NULL,
  thumbnail_sidebar_id bigint(20) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `recordings`
--

DROP TABLE IF EXISTS recordings;
CREATE TABLE recordings (
  id bigint(20) NOT NULL default '0',
  longplay tinyint(1) default NULL,
  length int(11) default NULL,
  event varchar(200) default NULL,
  show_id bigint(20) default NULL,
  episode_id bigint(20) default NULL,
  language_id bigint(20) default NULL,
  video_id bigint(20) default NULL,
  sequence int(11) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  movie_id bigint(20) default NULL,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `searchpatterns`
--

DROP TABLE IF EXISTS searchpatterns;
CREATE TABLE searchpatterns (
  id bigint(20) NOT NULL default '0',
  `type` int(11) default NULL,
  show_id bigint(20) default NULL,
  pattern varchar(200) default NULL,
  movie_id bigint(20) default NULL,
  actor_id bigint(20) default NULL,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `seasons`
--

DROP TABLE IF EXISTS seasons;
CREATE TABLE seasons (
  id bigint(20) NOT NULL default '0',
  number int(11) default NULL,
  show_id bigint(20) default NULL,
  firstepisode_id bigint(20) default NULL,
  lastepisode_id bigint(20) default NULL,
  startYear int(11) default NULL,
  endYear int(11) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `sequences`
--

DROP TABLE IF EXISTS sequences;
CREATE TABLE sequences (
  `name` varchar(10) NOT NULL default '',
  `value` bigint(20) default NULL,
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `showinfos`
--

DROP TABLE IF EXISTS showinfos;
CREATE TABLE showinfos (
  id bigint(20) NOT NULL default '0',
  show_id bigint(20) default NULL,
  language_id bigint(20) default NULL,
  `name` varchar(50) default NULL,
  path varchar(200) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `shows`
--

DROP TABLE IF EXISTS shows;
CREATE TABLE shows (
  id bigint(20) NOT NULL default '0',
  userkey varchar(20) NOT NULL default '',
  german_title varchar(100) default NULL,
  index_by varchar(100) NOT NULL,
  internet tinyint(1) default NULL,
  episode_length int(11) default NULL,
  webdatesfile varchar(200) default NULL,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  language_id bigint(20) default NULL,
  logo_mini varchar(200) default NULL,
  defaultinfo_id bigint(20) default NULL,
  title varchar(100) NOT NULL,
  start_year int(11) default NULL,
  end_year int(11) default NULL,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `summary`
--

DROP TABLE IF EXISTS summary;
CREATE TABLE summary (
  id bigint(20) NOT NULL,
  episode_id bigint(20) default NULL,
  language_id bigint(20) NOT NULL,
  summary mediumtext,
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  movie_id bigint(20) default NULL,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `videos`
--

DROP TABLE IF EXISTS videos;
CREATE TABLE videos (
  id bigint(20) NOT NULL default '0',
  userkey int(11) default NULL,
  `name` varchar(200) default NULL,
  length int(11) NOT NULL default '0',
  remaininglength int(11) NOT NULL default '0',
  lastmodified timestamp NOT NULL default CURRENT_TIMESTAMP,
  type_id bigint(20) default NULL,
  `storage` varchar(50) default NULL,
  obsolete tinyint(1) default NULL,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping routines for database 'media'
--
DELIMITER ;;
/*!50003 DROP FUNCTION IF EXISTS sort_letter */;;
/*!50003 SET SESSION SQL_MODE="STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER"*/;;
/*!50003 CREATE*/ /*!50020 DEFINER=root@localhost*/ /*!50003 FUNCTION sort_letter(name varchar(200)) RETURNS char(1) CHARSET utf8
    DETERMINISTIC
begin
	declare letter char(1);
	set letter=upper(left(name, 1));	
	if (letter>='0' and letter<='9') then
		set letter='0';
	end if;
	return letter;
end */;;
/*!50003 SET SESSION SQL_MODE=@OLD_SQL_MODE*/;;
DELIMITER ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2007-05-30 17:00:27
