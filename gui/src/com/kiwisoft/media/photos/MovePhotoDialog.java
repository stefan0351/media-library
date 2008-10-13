package com.kiwisoft.media.photos;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import javax.swing.*;

import com.kiwisoft.utils.ListenerSupport;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.format.FormatStringComparator;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.FormatBasedListRenderer;
import com.kiwisoft.swing.*;

public class MovePhotoDialog extends JDialog
{
	private boolean returnValue;

	private boolean newGallery;
	private String galleryName;
	private PhotoGallery gallery;

	private JComboBox galleriesField;
	private JTextField galleryNameField;
	private JRadioButton newGalleryField;
	private JRadioButton existingGalleryField;
	private ListenerSupport listenerSupport=new ListenerSupport();
	private PhotoGallery oldGallery;

	public MovePhotoDialog(Window frame, PhotoGallery gallery)
	{
		super(frame, "Move Photo", ModalityType.APPLICATION_MODAL);
		this.oldGallery=gallery;
		createContentPanel();
		existingGalleryField.requestFocus();
		initializeData();
		pack();
		GuiUtils.centerWindow(frame, this);
	}

	private void initializeData()
	{
		Vector<PhotoGallery> galleries=new Vector<PhotoGallery>(PhotoManager.getInstance().getGalleries());
		if (oldGallery!=null) galleries.remove(oldGallery);
		Collections.sort(galleries, new FormatStringComparator());
		galleriesField.setModel(new DefaultComboBoxModel(galleries));
		existingGalleryField.setSelected(true);
	}

	private void createContentPanel()
	{
		newGalleryField=new JRadioButton();
		existingGalleryField=new JRadioButton();
		galleriesField=new JComboBox();
		galleriesField.setRenderer(new FormatBasedListRenderer());
		galleriesField.setEnabled(false);
		galleryNameField=new JTextField(30);
		galleryNameField.setEnabled(false);
		JLabel galleryNameLabel=new JLabel("Name:");
		galleryNameLabel.setEnabled(false);
		ButtonGroup buttonGroup=new ButtonGroup();
		buttonGroup.add(newGalleryField);
		buttonGroup.add(existingGalleryField);

		listenerSupport.installComponentEnabler(newGalleryField, galleryNameLabel, galleryNameField);
		listenerSupport.installComponentEnabler(existingGalleryField, galleriesField);

		JPanel pnlContent=new JPanel(new GridBagLayout());
		int row=0;
		pnlContent.add(existingGalleryField,
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
		pnlContent.add(GuiUtils.createBoldLabel("Existing Gallery"),
					   new GridBagConstraints(1, row, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
		row++;
		pnlContent.add(galleriesField,
					   new GridBagConstraints(1, row, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		row++;
		pnlContent.add(newGalleryField,
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(15, 5, 0, 0), 0, 0));
		pnlContent.add(GuiUtils.createBoldLabel("New Gallery"),
					   new GridBagConstraints(1, row, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 0, 5), 0, 0));
		row++;
		pnlContent.add(galleryNameLabel,
					   new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(galleryNameField,
					   new GridBagConstraints(2, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		JPanel pnlButtons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnOk=new JButton(new OkAction());
		pnlButtons.add(btnOk);
		pnlButtons.add(new JButton(new CancelAction()));

		JPanel panel=new JPanel(new GridBagLayout());
		panel.add(pnlContent, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
													 GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
		panel.add(pnlButtons, new GridBagConstraints(0, 4, 3, 1, 1.0, 0.0,
													 GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		setContentPane(panel);

		getRootPane().setDefaultButton(btnOk);
	}

	@Override
	public void dispose()
	{
		listenerSupport.dispose();
		super.dispose();
	}

	public boolean isOk()
	{
		return returnValue;
	}

	public boolean isNewGallery()
	{
		return newGallery;
	}

	public String getGalleryName()
	{
		return galleryName;
	}

	public PhotoGallery getGallery()
	{
		return gallery;
	}

	private boolean apply()
	{
		if (newGalleryField.isSelected())
		{
			newGallery=true;
			galleryName=galleryNameField.getText();
			if (StringUtils.isEmpty(galleryName)) return false;
		}
		else
		{
			newGallery=false;
			gallery=(PhotoGallery)galleriesField.getSelectedItem();
			if (gallery==null) return false;
		}
		return true;
	}

	private class OkAction extends AbstractAction
	{
		public OkAction()
		{
			super("Ok", Icons.getIcon("ok"));
		}

		public void actionPerformed(ActionEvent e)
		{
			if (apply())
			{
				returnValue=true;
				dispose();
			}
		}
	}

	private class CancelAction extends AbstractAction
	{
		public CancelAction()
		{
			super("Cancel", Icons.getIcon("cancel"));
		}

		public void actionPerformed(ActionEvent e)
		{
			dispose();
		}
	}
}
