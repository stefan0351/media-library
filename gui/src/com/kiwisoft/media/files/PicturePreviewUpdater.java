package com.kiwisoft.media.files;

import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;

import com.kiwisoft.swing.lookup.LookupSelectionListener;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.lookup.LookupEvent;
import com.kiwisoft.swing.ImagePanel;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.files.MediaFileUtils;

/**
 * @author Stefan Stiller
*/
public class PicturePreviewUpdater implements LookupSelectionListener
{
	private LookupField<MediaFile> lookupField;
	private ImagePanel preview;
	private MediaFile defaultPicture;

	public PicturePreviewUpdater(LookupField<MediaFile> lookupField, ImagePanel preview)
	{
		this.lookupField=lookupField;
		this.preview=preview;
		lookupField.addSelectionListener(this);
	}

	public void setDefaultPicture(MediaFile defaultPicture)
	{
		this.defaultPicture=defaultPicture;
		if (lookupField.getValue()==null) selectionChanged(null);
	}

	public void selectionChanged(LookupEvent event)
	{
		MediaFile picture=lookupField.getValue();
		if (picture==null) picture=defaultPicture;
		if (picture!=null)
		{
			File file=picture.getPhysicalFile();
			if (file.exists())
			{
				try
				{
					ImageIcon icon=MediaFileUtils.loadIcon(file.toURI().toURL());
					preview.setImage(icon);
					StringBuilder toolTip=new StringBuilder("<html>");
					toolTip.append("<b>File:</b> ").append(file.getAbsolutePath()).append("<br>");
					toolTip.append("<b>Size:</b> ").append(icon.getIconWidth()).append("x").append(icon.getIconHeight());
					toolTip.append("</html>");
					preview.setToolTipText(toolTip.toString());
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(preview, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					preview.setImage(Icons.getIcon("no-photo-available"));
					StringBuilder toolTip=new StringBuilder("<html>");
					toolTip.append("<b>File:</b> ").append(file.getAbsolutePath());
					toolTip.append("</html>");
					preview.setToolTipText(toolTip.toString());
				}
			}
			else
			{
				preview.setImage(Icons.getIcon("no-photo-available"));
				StringBuilder toolTip=new StringBuilder("<html>");
				toolTip.append("<b>File:</b> ").append(file.getAbsolutePath());
				toolTip.append("</html>");
				preview.setToolTipText(toolTip.toString());
			}
		}
		else
		{
			preview.setImage(null);
			preview.setToolTipText(null);
		}
	}
}
