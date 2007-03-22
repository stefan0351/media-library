package com.kiwisoft.media.dataImport;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.EpisodeDetailsView;
import com.kiwisoft.media.show.EpisodeLookup;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.WindowManager;
import com.kiwisoft.utils.gui.lookup.DateField;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.lookup.LookupHandler;

public class NoEpisodeDialog extends JDialog
{
	private Show show;
	private ImportEpisode episodeData;
	private Episode episode;

	private boolean returnValue;

	private LookupField<Episode> episodeField;
	private JTextField titleField;
	private JTextField productionCodeField;
	private DateField firstAiredField;
	private JTextField germanTitleField;

	public NoEpisodeDialog(JFrame frame, Show show, ImportEpisode episodeData)
	{
		super(frame, "No Episode found", true);
		this.show=show;
		this.episodeData=episodeData;
		createContentPanel();
		episodeField.requestFocus();
		initializeData();
		setSize(new Dimension(500, 250));
		WindowManager.arrange(frame, this);
	}

	private void initializeData()
	{
		titleField.setText(episodeData.getEpisodeTitle());
		germanTitleField.setText(episodeData.getGermanEpisodeTitle());
		firstAiredField.setDate(episodeData.getFirstAirdate());
		productionCodeField.setText(episodeData.getProductionCode());
	}

	private void createContentPanel()
	{
		titleField=new JTextField(100);
		titleField.setEditable(false);
		germanTitleField=new JTextField(100);
		germanTitleField.setEditable(false);
		productionCodeField=new JTextField(10);
		productionCodeField.setEditable(false);
		firstAiredField=new DateField();
		firstAiredField.setEditable(false);
		episodeField=new LookupField<Episode>(new EpisodeLookup(show), new EpisodeLookupHandler());

		JPanel pnlContent=new JPanel(new GridBagLayout());
		int row=0;
		pnlContent.add(new JLabel("Title:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(titleField,
					   new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		row++;
		pnlContent.add(new JLabel("German Title:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(germanTitleField,
					   new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		row++;
		pnlContent.add(new JLabel("Production Code:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(productionCodeField,
					   new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		pnlContent.add(new JLabel("First Aired:"),
					   new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 15, 5, 0), 0, 0));
		pnlContent.add(firstAiredField,
					   new GridBagConstraints(3, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		row++;
		pnlContent.add(new JSeparator(JSeparator.HORIZONTAL),
					   new GridBagConstraints(0, row, 4, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		row++;
		pnlContent.add(new JLabel("Episode:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(episodeField,
					   new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		JPanel pnlButtons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnOk=new JButton(new NoEpisodeDialog.OkAction());
		pnlButtons.add(btnOk);
		pnlButtons.add(new JButton(new NoEpisodeDialog.CancelAction()));

		JPanel panel=new JPanel(new GridBagLayout());
		panel.add(pnlContent, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
													 GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
		panel.add(pnlButtons, new GridBagConstraints(0, 4, 3, 1, 1.0, 0.0,
													 GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		setContentPane(panel);

		getRootPane().setDefaultButton(btnOk);
	}

	public boolean isOk()
	{
		return returnValue;
	}

	public LookupField<Episode> getEpisodeField()
	{
		return episodeField;
	}

	private boolean apply()
	{
		episode=episodeField.getValue();
		return true;
	}

	public Episode getEpisode()
	{
		return episode;
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

	private class EpisodeLookupHandler implements LookupHandler<Episode>
	{
		public boolean isCreateAllowed()
		{
			return true;
		}

		public Episode createObject(LookupField<Episode> lookupField)
		{
			return EpisodeDetailsView.createDialog(null, show, episodeData);
		}

		public boolean isEditAllowed()
		{
			return true;
		}

		public void editObject(Episode value)
		{
			EpisodeDetailsView.createDialog(NoEpisodeDialog.this, value);
		}
	}
}
