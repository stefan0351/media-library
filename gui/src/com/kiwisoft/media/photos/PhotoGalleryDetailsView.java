package com.kiwisoft.media.photos;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.DetailsFrame;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.InvalidDataException;
import com.kiwisoft.utils.gui.lookup.DateField;

public class PhotoGalleryDetailsView extends DetailsView
{
	public static void create(PhotoGallery gallery)
	{
		new DetailsFrame(new PhotoGalleryDetailsView(gallery)).show();
	}

	public static void create()
	{
		new DetailsFrame(new PhotoGalleryDetailsView()).show();
	}

	private PhotoGallery photoGallery;

	private JTextField nameField;
	private DateField dateField;

	private PhotoGalleryDetailsView(PhotoGallery gallery)
	{
		createContentPanel();
		setBook(gallery);
	}

	private PhotoGalleryDetailsView()
	{
		createContentPanel();
		setBook(null);
	}

	protected void createContentPanel()
	{
		nameField=new JTextField(30);
		dateField=new DateField();

		setLayout(new GridBagLayout());
		int row=0;
		add(new JLabel("Name:"),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(nameField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Date:"),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(dateField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));

		nameField.getDocument().addDocumentListener(new PhotoGalleryDetailsView.FrameTitleUpdater());
	}

	private void setBook(PhotoGallery gallery)
	{
		this.photoGallery=gallery;
		if (gallery!=null)
		{
			nameField.setText(gallery.getName());
			dateField.setDate(gallery.getCreationDate());
		}
	}

	public boolean apply()
	{
		try
		{
			final String name=nameField.getText();
			if (StringUtils.isEmpty(name)) throw new InvalidDataException("Name is missing!", nameField);
			final Date date=dateField.getDate();
			return DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					if (photoGallery==null) photoGallery=PhotoManager.getInstance().createGallery();
					photoGallery.setName(name);
					photoGallery.setCreationDate(date);
				}

				public void handleError(Throwable throwable)
				{
					JOptionPane.showMessageDialog(PhotoGalleryDetailsView.this, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		catch (InvalidDataException e)
		{
			e.handle();
			return false;
		}
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		public FrameTitleUpdater()
		{
			changedUpdate(null);
		}

		public void changedUpdate(DocumentEvent e)
		{
			String name=nameField.getText();
			if (StringUtils.isEmpty(name)) name="<unknown>";
			setTitle("Photo Gallery: "+name);
		}
	}

}
