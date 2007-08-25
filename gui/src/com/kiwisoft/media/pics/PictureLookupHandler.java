package com.kiwisoft.media.pics;

import com.kiwisoft.swing.lookup.LookupHandler;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.media.pics.PictureDetailsView;

/**
 * @author Stefan Stiller
 */
public class PictureLookupHandler implements LookupHandler<Picture>
{
	public boolean isCreateAllowed()
	{
		return true;
	}

	public String getDefaultName()
	{
		return null;
	}

	public Picture createObject(LookupField<Picture> lookupField)
	{
		return PictureDetailsView.createDialog(null, getDefaultName());
	}

	public boolean isEditAllowed()
	{
		return true;
	}

	public void editObject(Picture picture)
	{
		PictureDetailsView.createDialog(null, picture);
	}
}
