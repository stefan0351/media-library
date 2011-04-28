alter table airdate_persons add constraint fk_airdate_persons_airdates foreign key (person_id) references persons(id);
alter table airdate_persons add constraint fk_airdate_persons_persons foreign key (airdate_id) references airdates(id);

alter table airdates add constraint fk_airdates_channel foreign key (channel_id) references channels(id);
alter table airdates add constraint fk_airdates_episode foreign key (episode_id) references episodes(id);
alter table airdates add constraint fk_airdates_language foreign key (language_id) references languages(id);
alter table airdates add constraint fk_airdates_movie foreign key (movie_id) references movies(id);
alter table airdates add constraint fk_airdates_show foreign key (show_id) references shows(id);

alter table books add constraint fk_books_cover foreign key (cover_id) references mediafiles(id);
alter table books add constraint fk_books_language foreign key (language_id) references languages(id);
alter table books add constraint fk_books_show foreign key (show_id) references shows(id);

alter table cast add constraint fk_cast_actor foreign key (actor_id) references persons(id);
alter table cast add constraint fk_cast_episode foreign key (episode_id) references episodes(id);
alter table cast add constraint fk_cast_movie foreign key (movie_id) references movies(id);
alter table cast add constraint fk_cast_picture foreign key (picture_id) references mediafiles(id);
alter table cast add constraint fk_cast_show foreign key (show_id) references shows(id);

alter table channels add constraint fk_channels_language foreign key (language_id) references languages(id);
alter table channels add constraint fk_channels_logo foreign key (logo_id) references mediafiles(id);

alter table contactmedia add constraint fk_contactmedia_author foreign key (author_id) references fanficauthors(id);

alter table credit add constraint fk_credit_episode foreign key (episode_id) references episodes(id);
alter table credit add constraint fk_credit_movie foreign key (movie_id) references movies(id);
alter table credit add constraint fk_credit_person foreign key (person_id) references persons(id);
alter table credit add constraint fk_credit_song foreign key (song_id) references songs(id);

alter table episodes add constraint fk_episodes_show foreign key (show_id) references shows(id);

alter table fandoms add constraint fk_fandoms_linkgroup foreign key (linkgroup_id) references linkgroups(id);
alter table fandoms add constraint fk_fandoms_movie foreign key (movie_id) references movies(id);
alter table fandoms add constraint fk_fandoms_show foreign key (show_id) references shows(id);

alter table fanfic_parts add constraint fk_fanfic_parts_fanfic foreign key (fanfic_id) references fanfics(id);

alter table fanfics add constraint fk_fanfics_prequel foreign key (prequel_id) references fanfics(id);
alter table fanfics add constraint fk_fanfics_sequel foreign key (sequel_id) references fanfics(id);

alter table linkgroups add constraint fk_linkgroups_parentgroup foreign key (parentgroup_id) references linkgroups(id);

alter table links add constraint fk_links_group foreign key (linkgroup_id) references linkgroups(id);

alter table map_book_author add constraint fk_map_book_author_authors foreign key (book_id) references books(id);
alter table map_book_author add constraint fk_map_book_author_writtenbooks foreign key (author_id) references persons(id);

alter table map_book_translator add constraint fk_map_book_translator_translatedbooks foreign key (translator_id) references persons(id);
alter table map_book_translator add constraint fk_map_book_translator_translators foreign key (book_id) references books(id);

alter table map_fanfic_author add constraint fk_map_fanfic_author_authors foreign key (fanfic_id) references fanfics(id);
alter table map_fanfic_author add constraint fk_map_fanfic_author_fanfics foreign key (author_id) references fanficauthors(id);

alter table map_fanfic_fandom add constraint fk_map_fanfic_fandom_fandoms foreign key (fanfic_id) references fanfics(id);
alter table map_fanfic_fandom add constraint fk_map_fanfic_fandom_fanfics foreign key (fandom_id) references fandoms(id);

alter table map_fanfic_pairing add constraint fk_map_fanfic_pairing_fanfics foreign key (pairing_id) references pairings(id);
alter table map_fanfic_pairing add constraint fk_map_fanfic_pairing_pairings foreign key (fanfic_id) references fanfics(id);

alter table map_movie_country add constraint fk_map_movie_country_countries foreign key (movie_id) references movies(id);
alter table map_movie_country add constraint fk_map_movie_country_movies foreign key (country_id) references countries(id);

alter table map_movie_genre add constraint fk_map_movie_genre_genres foreign key (movie_id) references movies(id);
alter table map_movie_genre add constraint fk_map_movie_genre_movies foreign key (genre_id) references genres(id);

alter table map_movie_language add constraint fk_map_movie_language_languages foreign key (movie_id) references movies(id);
alter table map_movie_language add constraint fk_map_movie_language_movies foreign key (language_id) references languages(id);

alter table map_show_genre add constraint fk_map_show_genre_genres foreign key (show_id) references shows(id);
alter table map_show_genre add constraint fk_map_show_genre_shows foreign key (genre_id) references genres(id);

alter table mediafile_episodes add constraint fk_mediafile_episodes_episodes foreign key (mediafile_id) references mediafiles(id);
alter table mediafile_episodes add constraint fk_mediafile_episodes_mediafiles foreign key (episode_id) references episodes(id);

alter table mediafile_movies add constraint fk_mediafile_movies_mediafiles foreign key (movie_id) references movies(id);
alter table mediafile_movies add constraint fk_mediafile_movies_movies foreign key (mediafile_id) references mediafiles(id);

alter table mediafile_persons add constraint fk_mediafile_persons_mediafiles foreign key (person_id) references persons(id);
alter table mediafile_persons add constraint fk_mediafile_persons_persons foreign key (mediafile_id) references mediafiles(id);

alter table mediafile_shows add constraint fk_mediafile_shows_mediafiles foreign key (show_id) references shows(id);
alter table mediafile_shows add constraint fk_mediafile_shows_shows foreign key (mediafile_id) references mediafiles(id);

alter table mediafiles add constraint fk_mediafiles_thumbnail foreign key (thumbnail_id) references imagefiles(id);
alter table mediafiles add constraint fk_mediafiles_thumbnail50x50 foreign key (thumbnail_50x50_id) references imagefiles(id);
alter table mediafiles add constraint fk_mediafiles_thumbnailsidebar foreign key (thumbnail_sidebar_id) references imagefiles(id);

alter table movies add constraint fk_movies_poster foreign key (poster_id) references mediafiles(id);
alter table movies add constraint fk_movies_show foreign key (show_id) references shows(id);

alter table names add constraint fk_names_language foreign key (language_id) references languages(id);

alter table persons add constraint fk_persons_picture foreign key (picture_id) references mediafiles(id);

alter table photogalleries_structure add constraint fk_photogalleries_structure_children foreign key (parent_id) references photogalleries(id);
alter table photogalleries_structure add constraint fk_photogalleries_structure_parents foreign key (child_id) references photogalleries(id);

alter table photos add constraint fk_photos_gallery foreign key (photogallery_id) references photogalleries(id);
alter table photos add constraint fk_photos_originalpicture foreign key (original_pic_id) references imagefiles(id);
alter table photos add constraint fk_photos_thumbnail foreign key (thumbnail_id) references imagefiles(id);

alter table relatedlinkgroups add constraint fk_relatedlinkgroups_alternategroups foreign key (relatedgroup_id) references linkgroups(id);
alter table relatedlinkgroups add constraint fk_relatedlinkgroups_relatedgroups foreign key (linkgroup_id) references linkgroups(id);

alter table searchpatterns add constraint fk_searchpatterns_movie foreign key (movie_id) references movies(id);
alter table searchpatterns add constraint fk_searchpatterns_person foreign key (person_id) references persons(id);
alter table searchpatterns add constraint fk_searchpatterns_show foreign key (show_id) references shows(id);

alter table seasons add constraint fk_seasons_firstepisode foreign key (firstepisode_id) references episodes(id);
alter table seasons add constraint fk_seasons_lastepisode foreign key (lastepisode_id) references episodes(id);
alter table seasons add constraint fk_seasons_logo foreign key (logo_id) references mediafiles(id);
alter table seasons add constraint fk_seasons_show foreign key (show_id) references shows(id);

alter table showinfos add constraint fk_showinfos_language foreign key (language_id) references languages(id);
alter table showinfos add constraint fk_showinfos_show foreign key (show_id) references shows(id);

alter table shows add constraint fk_shows_defaultinfo foreign key (defaultinfo_id) references showinfos(id);
alter table shows add constraint fk_shows_language foreign key (language_id) references languages(id);
alter table shows add constraint fk_shows_linkgroup foreign key (linkgroup_id) references linkgroups(id);
alter table shows add constraint fk_shows_logo foreign key (logo_id) references mediafiles(id);

alter table summary add constraint fk_summary_book foreign key (book_id) references books(id);
alter table summary add constraint fk_summary_episode foreign key (episode_id) references episodes(id);
alter table summary add constraint fk_summary_language foreign key (language_id) references languages(id);
alter table summary add constraint fk_summary_movie foreign key (movie_id) references movies(id);

alter table tracks add constraint fk_tracks_episode foreign key (episode_id) references episodes(id);
alter table tracks add constraint fk_tracks_language foreign key (language_id) references languages(id);
alter table tracks add constraint fk_tracks_medium foreign key (medium_id) references media(id);
alter table tracks add constraint fk_tracks_movie foreign key (movie_id) references movies(id);
alter table tracks add constraint fk_tracks_show foreign key (show_id) references shows(id);
alter table tracks add constraint fk_tracks_song foreign key (song_id) references songs(id);
