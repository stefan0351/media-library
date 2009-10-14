package com.kiwisoft.media.photos;

import com.kiwisoft.media.BaseAction;
import com.kiwisoft.format.FormatStringComparator;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Stefan Stiller
 * @since 03.10.2009
 */
public class ListPhotoGalleriesAction extends BaseAction
{
	private List<PhotoGallery> galleries;

	@Override
	public String getPageTitle()
	{
		return "Photos";
	}

	@Override
	public String execute() throws Exception
	{
		galleries=new ArrayList<PhotoGallery>(PhotoManager.getInstance().getGalleries());
		Collections.sort(galleries, new FormatStringComparator());
		return super.execute();
	}

	public List<PhotoGallery> getGalleries()
	{
		return galleries;
	}
}
