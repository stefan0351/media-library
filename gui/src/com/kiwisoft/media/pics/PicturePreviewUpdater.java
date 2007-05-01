package com.kiwisoft.media.pics;

import java.io.File;
import javax.swing.JOptionPane;

import com.kiwisoft.utils.gui.lookup.LookupSelectionListener;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.lookup.LookupEvent;
import com.kiwisoft.utils.gui.ImagePanel;
import com.kiwisoft.utils.gui.ImageUtils;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.Configurator;

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
			File file=new File(Configurator.getInstance().getString("path.root"), picture.getFile());
			if (file.exists())
			{
				try
				{
					preview.setImage(ImageUtils.loadIcon(file.toURL()));
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
