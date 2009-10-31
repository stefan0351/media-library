package com.kiwisoft.media.photos;

import com.kiwisoft.media.BaseAction;
import com.kiwisoft.web.RecentItemManager;
import com.kiwisoft.web.RecentIdObject;

/**
 * @author Stefan Stiller
 * @since 03.10.2009
 */
public class PhotoGalleryDetailsAction extends BaseAction
{
	private Long galleryId;
	private PhotoGallery gallery;

	@Override
	public String getPageTitle()
	{
		return "Photos";
	}

	@Override
	public String execute() throws Exception
	{
		if (galleryId!=null) gallery=PhotoManager.getInstance().getGallery(galleryId);
		if (gallery!=null)
			RecentItemManager.getInstance().addItem(new RecentIdObject<PhotoGallery>(PhotoGallery.class, gallery));

		return super.execute();
	}

	public Long getGalleryId()
	{
		return galleryId;
	}

	public void setGalleryId(Long galleryId)
	{
		this.galleryId=galleryId;
	}

	public PhotoGallery getGallery()
	{
		return gallery;
	}
}
