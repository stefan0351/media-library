package com.kiwisoft.media.files;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class ThumbnailField extends ImageField
{
	private int thumbnailWidth;
	private int thumbnailHeight;
	private String suffix;
	private ImageField imageField;

	public ThumbnailField(String name, Dimension size, int thumbnailWidth, int thumbnailHeight, String suffix, ImageField imageField)
	{
		super(name, size);
		this.thumbnailWidth=thumbnailWidth;
		this.thumbnailHeight=thumbnailHeight;
		this.suffix=suffix;
		this.imageField=imageField;
	}

	protected Action[] getActions()
	{
		return new Action[]{new CreateThumbnailAction(), new EditAction()};
	}

	protected void createThumbnail(int width, int height, String suffix)
	{
		if (imageField!=null)
		{
			File file=getFile();
			File imageFile=imageField.getFile();
			if (imageFile!=null && imageFile.exists())
			{
				String name=FileUtils.getNameWithoutExtension(imageFile);
				file=new File(imageFile.getParentFile(), name+"_"+suffix+".jpg");
				MediaFileUtils.resize(imageFile, width, height, file);
			}
			if (file!=null && file.exists())
			{
				setFile(file);
			}
		}
	}

	private class CreateThumbnailAction extends AbstractAction
	{
		public CreateThumbnailAction()
		{
			super("Create", Icons.getIcon("add"));
		}

		public void actionPerformed(ActionEvent e)
		{
			createThumbnail(thumbnailWidth, thumbnailHeight, suffix);
		}
	}

	public class EditAction extends AbstractAction
	{
		public EditAction()
		{
			super("Edit", Icons.getIcon("edit"));
		}

		public void actionPerformed(ActionEvent e)
		{
			File file=getFile();
			if (file==null || !file.exists())
			{
				if (imageField!=null)
				{
					File imageFile=imageField.getFile();
					if (imageFile!=null && imageFile.exists())
					{
						String name=FileUtils.getNameWithoutExtension(imageFile);
						file=new File(imageFile.getParentFile(), name+"_"+suffix+".jpg");
						MediaFileUtils.convert(imageFile, file);
						if (file.exists())
						{
							setFile(file);
						}
					}
				}
			}
			edit();
		}
	}
}