package com.kiwisoft.media.pics;

import java.io.File;
import javax.swing.JFileChooser;

import com.kiwisoft.swing.lookup.LookupHandler;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.ImageFileChooser;
import com.kiwisoft.media.MediaConfiguration;

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
		ImageFileChooser fileChooser=new ImageFileChooser();
		String path=MediaConfiguration.getRecentPicturePath();
		if (path==null) path=MediaConfiguration.getRootPath();
		if (path!=null) fileChooser.setCurrentDirectory(new File(path));
		if (fileChooser.showOpenDialog(lookupField)==JFileChooser.APPROVE_OPTION)
		{
			File file=fileChooser.getSelectedFile();
			MediaConfiguration.setRecentPicturePath(file.getParent());
			return PictureDetailsView.createDialog(GuiUtils.getWindow(lookupField), getDefaultName(), file);
		}
		return null;
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
