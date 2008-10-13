package com.kiwisoft.media.pics;

import com.kiwisoft.swing.lookup.LookupHandler;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.GuiUtils;

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
		return PictureDetailsView.createDialog(GuiUtils.getWindow(lookupField), getDefaultName());
	}

	public boolean isEditAllowed()
	{
		return true;
	}

	public void editObject(LookupField<Picture> lookupField, Picture picture)
	{
		PictureDetailsView.createDialog(GuiUtils.getWindow(lookupField), picture);
	}
}
