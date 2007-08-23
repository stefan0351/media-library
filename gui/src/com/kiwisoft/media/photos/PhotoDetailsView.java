package com.kiwisoft.media.photos;

import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.pics.PictureFile;
import com.kiwisoft.utils.gui.GuiUtils;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsFrame;

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
	private JTextField isoSpeedField;
	private JTextField focalLengthField;
	private JTextField resolutionField;
	private JTextField fNumberField;
	private JCheckBox galleryPhotoField;

	private PhotoDetailsView(Photo photo)
	{
		createContentPanel();
		setPhoto(photo);
	}

	protected void createContentPanel()
	{
		setLayout(new GridBagLayout());
		add(createContentDetailsPanel(),
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(createPictureDetailsPanel(),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(createShootingDetailsPanel(),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
	}

	private JPanel createContentDetailsPanel()
	{
		descriptionField=new JTextPane();
		JScrollPane descriptionPane=new JScrollPane(descriptionField);
		descriptionPane.setPreferredSize(new Dimension(400, 100));
		galleryPhotoField=new JCheckBox("Gallery Photo");

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Content Details"));
		int row=0;
		panel.add(descriptionPane, new GridBagConstraints(0, row, 1, 1, 1.0, 1.0, WEST, BOTH, new Insets(5, 5, 0, 5), 0, 0));
		row++;
		panel.add(galleryPhotoField, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 5, 5, 5), 0, 0));
		return panel;
	}

	private JPanel createShootingDetailsPanel()
	{
		dateField=new JTextField();
		dateField.setHorizontalAlignment(SwingConstants.TRAILING);
		dateField.setEditable(false);
		cameraMakeField=new JTextField();
		cameraMakeField.setEditable(false);
		cameraModelField=new JTextField();
		cameraModelField.setEditable(false);
		exposureTimeField=new JTextField(5);
		exposureTimeField.setHorizontalAlignment(SwingConstants.TRAILING);
		exposureTimeField.setEditable(false);
		isoSpeedField=new JTextField(5);
		isoSpeedField.setHorizontalAlignment(SwingConstants.TRAILING);
		isoSpeedField.setEditable(false);
		focalLengthField=new JTextField(5);
		focalLengthField.setHorizontalAlignment(SwingConstants.TRAILING);
		focalLengthField.setEditable(false);
		fNumberField=new JTextField(5);
		fNumberField.setHorizontalAlignment(SwingConstants.TRAILING);
		fNumberField.setEditable(false);
		isoSpeedField=new JTextField(5);
		isoSpeedField.setHorizontalAlignment(SwingConstants.TRAILING);
		isoSpeedField.setEditable(false);

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Shooting Details"));

		int row=0;
		panel.add(new JLabel("Date:"),
				  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 0, 0), 0, 0));
		panel.add(dateField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
		row++;
		panel.add(new JLabel("Camera Vendor:"),
				  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(cameraMakeField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0));
		row++;
		panel.add(new JLabel("Camera Model:"),
				  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(cameraModelField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0));
		row++;
		panel.add(new JLabel("Focal Length (mm):"),
				  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 5, 5, 0), 0, 0));
		panel.add(focalLengthField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 5, 5), 0, 0));
		panel.add(new JLabel("F-Number:"),
				  new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 5, 0), 0, 0));
		panel.add(fNumberField, new GridBagConstraints(3, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 5, 5), 0, 0));
		row++;
		panel.add(new JLabel("Exposure Time (s):"),
				  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 5, 5, 0), 0, 0));
		panel.add(exposureTimeField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 5, 5), 0, 0));
		panel.add(new JLabel("ISO-Speed:"),
				  new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 5, 0), 0, 0));
		panel.add(isoSpeedField, new GridBagConstraints(3, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 5, 5), 0, 0));
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
		resolutionField=new JTextField();
		resolutionField.setEditable(false);

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
		row++;
		panel.add(new JLabel("Resolution (dpi):"),
				  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 5, 5, 0), 0, 0));
		panel.add(resolutionField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 5, 0), 0, 0));
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
			Date date=photo.getCreationDate();
			if (date!=null)
			{
				DateFormat dateFormat=DateFormat.getDateTimeInstance();
				dateFormat.setTimeZone(DateUtils.GMT);
				dateField.setText(dateFormat.format(date));
			}
			Double exposureTime=photo.getExposureTime();
			if (exposureTime!=null) exposureTimeField.setText(new DecimalFormat("#.######").format(exposureTime));
			Integer isoSpeed=photo.getIsoSpeed();
			if (isoSpeed!=null) isoSpeedField.setText(isoSpeed.toString());
			Double fNumber=photo.getFNumber();
			if (fNumber!=null) fNumberField.setText(new DecimalFormat("#.##").format(fNumber));
			Double focalLength=photo.getFocalLength();
			if (focalLength!=null) focalLengthField.setText(new DecimalFormat("#.##").format(focalLength));
			colorDepthField.setText(photo.getColorDepth()+" bit");
			PictureFile picture=photo.getOriginalPicture();
			sizeField.setText(picture.getWidth()+"x"+picture.getHeight());
			resolutionField.setText(photo.getXResolution()+"x"+photo.getYResolution());
			galleryPhotoField.setSelected(photo.isGalleryPhoto());
			try
			{
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
				if (galleryPhotoField.isSelected()) photo.getGallery().setGalleryPhoto(photo);
				else photo.getGallery().setGalleryPhoto(null);
			}

			public void handleError(Throwable throwable, boolean rollback)
			{
				GuiUtils.handleThrowable(PhotoDetailsView.this, throwable);
			}
		});
	}
}
