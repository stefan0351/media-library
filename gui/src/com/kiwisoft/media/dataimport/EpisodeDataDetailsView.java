package com.kiwisoft.media.dataimport;

import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.date.DateField;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.text.preformat.PreformatTextController;
import com.kiwisoft.utils.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import static java.awt.GridBagConstraints.*;

public class EpisodeDataDetailsView extends DetailsView
{
	public static void create(Window owner, EpisodeData episodeData)
	{
		EpisodeDataDetailsView view=new EpisodeDataDetailsView(episodeData);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
	}

	private EpisodeData episodeData;

	private JTextField userKeyField;
	private JTextField titleField;
	private JTextField germanTitleField;
	private JTextField productionCodeField;
	private DateField firstAiredField;
	private PreformatTextController germanSummaryController;
	private PreformatTextController englishSummaryController;

	private EpisodeDataDetailsView(EpisodeData episodeData)
	{
		this.episodeData=episodeData;
		initialize();
	}

	@Override
	protected void initializeData()
	{
		titleField.setText(episodeData.getTitle());
		germanTitleField.setText(episodeData.getGermanTitle());
		userKeyField.setText(episodeData.getKey());
		productionCodeField.setText(episodeData.getProductionCode());
		firstAiredField.setDate(episodeData.getFirstAirdate());
		germanSummaryController.setText(episodeData.getGermanSummary());
		englishSummaryController.setText(episodeData.getEnglishSummary());
	}

	@Override
	public boolean apply() throws InvalidDataException
	{
		String title=titleField.getText();
		if (StringUtils.isEmpty(title)) throw new InvalidDataException("Title must not be empty!", titleField);
		String userKey=userKeyField.getText();
		if (StringUtils.isEmpty(userKey)) throw new InvalidDataException("Number must not be empty!", userKeyField);

		episodeData.setKey(userKey);
		episodeData.setTitle(title);
		episodeData.setGermanTitle(germanTitleField.getText());
		episodeData.setProductionCode(productionCodeField.getText());
		episodeData.setFirstAirdate(firstAiredField.getDate());
		episodeData.setEnglishSummary(englishSummaryController.getText());
		episodeData.setGermanSummary(germanSummaryController.getText());

		return true;
	}

	@Override
	protected void initializeComponents()
	{
		titleField=new JTextField();
		germanTitleField=new JTextField();
		userKeyField=new JTextField(5);
		userKeyField.setMinimumSize(new Dimension(100, userKeyField.getPreferredSize().height));
		firstAiredField=new DateField();
		productionCodeField=new JTextField(10);
		germanSummaryController=new PreformatTextController();
		englishSummaryController=new PreformatTextController();
		germanSummaryController.getComponent().setPreferredSize(new Dimension(400, 150));
		englishSummaryController.getComponent().setPreferredSize(new Dimension(400, 150));

		setLayout(new GridBagLayout());
		int row=0;
		add(new JLabel("Number:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(userKeyField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Production Code:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(productionCodeField, new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("First Aired:"), new GridBagConstraints(4, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(firstAiredField, new GridBagConstraints(5, row, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Title:", Icons.getIcon("language.en"), SwingConstants.RIGHT),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(titleField, new GridBagConstraints(1, row, 5, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Title:", Icons.getIcon("language.de"), SwingConstants.RIGHT),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(germanTitleField, new GridBagConstraints(1, row, 5, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Summary:", Icons.getIcon("language.en"), SwingConstants.RIGHT),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(englishSummaryController.getComponent(),
			new GridBagConstraints(1, row, 5, 1, 1.0, 0.5, CENTER, BOTH, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Summary:", Icons.getIcon("language.de"), SwingConstants.RIGHT),
				new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(germanSummaryController.getComponent(),
			new GridBagConstraints(1, row, 5, 1, 1.0, 0.5, CENTER, BOTH, new Insets(10, 5, 0, 0), 0, 0));

		titleField.getDocument().addDocumentListener(new FrameTitleUpdater());
	}

	@Override
	public JComponent getDefaultFocusComponent()
	{
		return userKeyField;
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		@Override
		public void changedUpdate(DocumentEvent e)
		{
			String name=titleField.getText();
			if (StringUtils.isEmpty(name)) name="<unknown>";
			setTitle("Episode: "+name);
		}
	}
}
