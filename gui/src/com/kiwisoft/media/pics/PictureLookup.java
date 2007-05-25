package com.kiwisoft.media.pics;

import java.util.Collection;

import com.kiwisoft.utils.gui.lookup.ListLookup;
import com.kiwisoft.utils.db.DBLoader;

/**
 * @author Stefan Stiller
 */
public class PictureLookup extends ListLookup<Picture>
{
	public Collection<Picture> getValues(String text, Picture currentValue, boolean lookup)
	{
		if (text==null) return PictureManager.getInstance().getPictures();
		else
		{
			if (text.indexOf('*')>=0) text=text.replace('*', '%');
			else text=text+"%";
			return DBLoader.getInstance().loadSet(Picture.class, null, "name like ?", text);
		}
	}
}