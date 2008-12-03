package com.kiwisoft.media.files;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.kiwisoft.swing.lookup.LookupHandler;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.ImageFileChooser;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.cfg.Configuration;

/**
 * @author Stefan Stiller
 */
public class ImageLookupHandler implements LookupHandler<MediaFile>
{
	public ImageLookupHandler()
	{
	}

	public boolean isCreateAllowed()
	{
		return true;
	}

	public String getDefaultName()
	{
		return null;
	}

	public MediaFile createObject(LookupField<MediaFile> lookupField)
	{
		ImageFileChooser fileChooser=new ImageFileChooser();
		String path=MediaConfiguration.getRecentMediaPath();
		if (path==null) path=MediaConfiguration.getRootPath();
		if (path!=null) fileChooser.setCurrentDirectory(new File(path));
		if (fileChooser.showOpenDialog(lookupField)==JFileChooser.APPROVE_OPTION)
		{
			File file=fileChooser.getSelectedFile();
			MediaConfiguration.setRecentMediaPath(file.getParent());
			String root=MediaFileUtils.getRootPath(file);
			if (root!=null)
			{
				MediaFileInfo fileInfo=MediaFileUtils.getMediaFileInfo(file);
				String filePath=FileUtils.getRelativePath(Configuration.getInstance().getString(root), file.getAbsolutePath());
				if (fileInfo.isImage()) return ImageDetailsView.createDialog(GuiUtils.getWindow(lookupField), getDefaultName(), root, filePath);
			}
			else JOptionPane.showMessageDialog(lookupField, "File is not located in a configured directory.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

	public boolean isEditAllowed()
	{
		return true;
	}

	public void editObject(LookupField<MediaFile> lookupField, MediaFile picture)
	{
		ImageDetailsView.createDialog(GuiUtils.getWindow(lookupField), picture);
	}
}
