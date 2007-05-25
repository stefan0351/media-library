package com.kiwisoft.media.photos;

import static java.awt.GridBagConstraints.*;
import java.awt.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.kiwisoft.utils.gui.DetailsFrame;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.utils.GuiUtils;
import com.kiwisoft.media.pics.PictureFile;

public class PhotoDetailsView extends DetailsView
{
	public static void create(Photo photo)
	{
		new DetailsFrame(new PhotoDetailsView(photo)).show();
	}

	private Photo photo;

	private JTextField cameraMakeField;
	private JTextField cameraModelField;
	private JTextField dateField;
	private JTextField fileField;
	private JTextField exposureTimeField;
	private JTextField colorDepthField;
	private JTextField sizeField;
	private JTextPane descriptionField;

	private PhotoDetailsView(Photo photo)
	{
		createContentPanel();
		setPhoto(photo);
	}

	protected void createContentPanel()
	{
		setLayout(new GridBagLayout());
		add(createDescriptionPanel(),
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(createPictureDetailsPanel(),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(createShootingDetailsPanel(),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
	}

	private JPanel createDescriptionPanel()
	{
		descriptionField=new JTextPane();
		JScrollPane descriptionPane=new JScrollPane(descriptionField);
		descriptionPane.setPreferredSize(new Dimension(400, 100));
		JPanel panel=new JPanel(new BorderLayout());
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Description Details"));
		panel.add(descriptionPane, BorderLayout.CENTER);
		return panel;
	}

	private JPanel createShootingDetailsPanel()
	{
		dateField=new JTextField();
		dateField.setEditable(false);
		cameraMakeField=new JTextField();
		cameraMakeField.setEditable(false);
		cameraModelField=new JTextField();
		cameraModelField.setEditable(false);
		exposureTimeField=new JTextField();
		exposureTimeField.setEditable(false);

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Shooting Details"));

		int row=0;
		panel.add(new JLabel("Date:"),
				  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 0, 0), 0, 0));
		panel.add(dateField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
		row++;
		panel.add(new JLabel("Camera Make:"),
				  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(cameraMakeField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0));
		row++;
		panel.add(new JLabel("Camera Model:"),
				  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(cameraModelField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0));
		row++;
		panel.add(new JLabel("Exposure Time:"),
				  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 5, 5, 0), 0, 0));
		panel.add(exposureTimeField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 5, 5), 0, 0));
		return panel;
	}

	private JPanel createPictureDetailsPanel()
	{
		fileField=new JTextField();
		fileField.setEditable(false);
		colorDepthField=new JTextField();
		colorDepthField.setEditable(false);
		sizeField=new JTextField();
		sizeField.setEditable(false);

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Picture Details"));

		int row=0;
		panel.add(new JLabel("File:"),
				  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 0, 0), 0, 0));
		panel.add(fileField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
		row++;
		panel.add(new JLabel("Size:"),
				  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 5, 5, 0), 0, 0));
		panel.add(sizeField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 5, 0), 0, 0));
		panel.add(new JLabel("Color Depth:"),
				  new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 5, 0), 0, 0));
		panel.add(colorDepthField, new GridBagConstraints(3, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 5, 5), 0, 0));
		return panel;
	}

	private void setPhoto(Photo photo)
	{
		this.photo=photo;
		if (photo!=null)
		{
			descriptionField.setText(photo.getDescription());
			cameraMakeField.setText(photo.getCameraMake());
			cameraModelField.setText(photo.getCameraModel());
			dateField.setText(String.valueOf(photo.getCreationDate()));
			exposureTimeField.setText(photo.getExposureTime());
			colorDepthField.setText(photo.getColorDepth()+" bit");
			PictureFile picture=photo.getOriginalPicture();
			try
			{
				sizeField.setText(picture.getWidth()+"x"+picture.getHeight());
				fileField.setText(FileUtils.getFile(MediaConfiguration.getRootPath(), picture.getFile()).getCanonicalPath());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public boolean apply()
	{
		final String description=descriptionField.getText();
		return DBSession.execute(new Transactional()
		{
			public void run() throws Exception
			{
				photo.setDescription(description);
			}

			public void handleError(Throwable throwable)
			{
				GuiUtils.handleThrowable(PhotoDetailsView.this, throwable);
			}
		});
	}
}
