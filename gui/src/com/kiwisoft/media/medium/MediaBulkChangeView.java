package com.kiwisoft.media.medium;

import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;

import javax.swing.*;
import java.util.List;
import java.awt.*;

/**
 * @author Stefan Stiller
 */
public class MediaBulkChangeView extends DetailsView
{
	private List<Medium> media;
	private JRadioButton storageButton;
	private JTextField storageField;
	private JRadioButton typeButton;
	private LookupField<MediumType> typeField;

	public static void create(Window parent, List<Medium> media)
	{
		DetailsDialog dialog=new DetailsDialog(parent, new MediaBulkChangeView(media), DetailsDialog.OK_CANCEL_ACTIONS);
		dialog.setVisible(true);
	}

	public MediaBulkChangeView(List<Medium> media)
	{
		setTitle("Media Bulk Change");
		this.media=media;
		initialize();
	}

	@Override
	protected void initializeComponents()
	{
		ButtonGroup buttonGroup=new ButtonGroup();

		buttonGroup.add(storageButton=new JRadioButton("Change Storage Location"));
		JLabel storageLabel=new JLabel("Storage:");
		storageField=new JTextField(10);
		getListenerList().installComponentEnabler(storageButton, storageLabel, storageField);

		buttonGroup.add(typeButton=new JRadioButton("Change Medium Type"));
		JLabel typeLabel=new JLabel("Type:");
		typeField=new LookupField<MediumType>(new MediumTypeLookup());
		getListenerList().installComponentEnabler(typeButton, typeLabel, typeField);

		setLayout(new GridBagLayout());
		int row=0;
		add(storageButton, new GridBagConstraints(0, row++, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(Box.createHorizontalStrut(30), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(storageLabel, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(storageField, new GridBagConstraints(2, row++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));

		add(typeButton, new GridBagConstraints(0, row++, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(typeLabel, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(typeField, new GridBagConstraints(2, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
	}

	@Override
	public boolean apply() throws InvalidDataException
	{
		if (storageButton.isSelected())
		{
			String storage=storageField.getText();
			if (StringUtils.isEmpty(storage)) throw new InvalidDataException("Storage is missing!", storageField);
			storage=storage.trim();
			final String finalStorage=storage;
			return DBSession.execute(new Transactional()
			{
				@Override
				public void run() throws Exception
				{
					for (Medium medium : media) medium.setStorage(finalStorage);
				}

				@Override
				public void handleError(Throwable throwable, boolean rollback)
				{
					throw new RuntimeException("Error saving storage change.", throwable);
				}
			});
		}
		else if (typeButton.isSelected())
		{
			final MediumType type=typeField.getValue();
			if (type==null) throw new InvalidDataException("Typetorage is missing!", storageField);
			return DBSession.execute(new Transactional()
			{
				@Override
				public void run() throws Exception
				{
					for (Medium medium : media) medium.setType(type);
				}

				@Override
				public void handleError(Throwable throwable, boolean rollback)
				{
					throw new RuntimeException("Error saving type change.", throwable);
				}
			});
		}
		return false;
	}
}
