package com.kiwisoft.media.pics;

import java.io.File;
import javax.swing.JOptionPane;

import com.kiwisoft.swing.lookup.LookupSelectionListener;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.lookup.LookupEvent;
import com.kiwisoft.swing.ImagePanel;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.media.MediaConfiguration;

/**
 * @author Stefan Stiller
*/
public class PicturePreviewUpdater implements LookupSelectionListener
{
	private LookupField<Picture> lookupField;
	private ImagePanel preview;

	public PicturePreviewUpdater(LookupField<Picture> lookupField, ImagePanel preview)
	{
		this.lookupField=lookupField;
		this.preview=preview;
		lookupField.addSelectionListener(this);
	}

	public void selectionChanged(LookupEvent event)
	{
		Picture picture=lookupField.getValue();
		if (picture!=null)
		{
			File file=picture.getPhysicalFile();
			if (file.exists())
			{
				try
				{
					preview.setImage(PictureUtils.loadIcon(file.toURI().toURL()));
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(preview, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					preview.setImage(Icons.getIcon("no-photo-available"));
				}

			}
			else preview.setImage(Icons.getIcon("no-photo-available"));
		}
		else preview.setImage(null);
	}
}
