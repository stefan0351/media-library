package com.kiwisoft.media.photos;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.date.DateField;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsFrame;

public class PhotoGalleryDetailsView extends DetailsView
{
	public static void create(PhotoGallery gallery)
	{
		new DetailsFrame(new PhotoGalleryDetailsView(gallery, null)).show();
	}

	public static void createNew(PhotoGallery parent)
	{
		new DetailsFrame(new PhotoGalleryDetailsView(null, parent)).show();
	}

	private PhotoGallery photoGallery;
	private PhotoGallery parent;

	private JTextField nameField;
	private DateField dateField;

	private PhotoGalleryDetailsView(PhotoGallery gallery, PhotoGallery parent)
	{
		this.parent=parent;
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

	@Override
	public boolean apply()
	{
		try
		{
			final String name=nameField.getText();
			if (StringUtils.isEmpty(name)) throw new InvalidDataException("Name is missing!", nameField);
			final Date date=dateField.getDate();
			return DBSession.execute(new Transactional()
			{
				@Override
				public void run() throws Exception
				{
					if (photoGallery==null)
					{
						if (parent==null) photoGallery=PhotoManager.getInstance().getRootGallery().createChildGallery();
						else photoGallery=parent.createChildGallery();
					}
					photoGallery.setName(name);
					photoGallery.setCreationDate(date);
				}

				@Override
				public void handleError(Throwable throwable, boolean rollback)
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

		@Override
		public void changedUpdate(DocumentEvent e)
		{
			String name=nameField.getText();
			if (StringUtils.isEmpty(name)) name="<unknown>";
			setTitle("Photo Gallery: "+name);
		}
	}

}
