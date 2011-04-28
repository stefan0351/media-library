package com.kiwisoft.media;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.media.fanfic.FanFicTask;
import com.kiwisoft.media.files.MediaFilesTask;
import com.kiwisoft.media.photos.PhotosTask;

/**
 * @author Stefan Stiller
 */
public class FilesTask extends MenuSidebarItem.Task
{
	public FilesTask()
	{
		super("Files");
		if (MediaConfiguration.isFanFicsEnabled()) add(new FanFicTask());
		add(new MediaFilesTask());
		add(new PhotosTask());
	}
}
