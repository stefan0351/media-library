package com.kiwisoft.media.dataImport;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.EpisodeDetailsView;
import com.kiwisoft.media.show.EpisodeLookup;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.WindowManager;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.lookup.LookupHandler;
import com.kiwisoft.utils.gui.lookup.DateField;

public class NoEpisodeDialog extends JDialog
{
	private Show show;
	private ImportEpisode episodeData;
	private Episode episode;

	private boolean returnValue;

	private LookupField<Episode> episodeField;
	private JTextField nameField;
	private JTextField productionCodeField;
	private DateField firstAiredField;
	private JTextField originalNameField;

	public NoEpisodeDialog(JFrame frame, Show show, ImportEpisode episodeData)
	{
		super(frame, "Keine Episode gefunden", true);
		this.show=show;
		this.episodeData=episodeData;
		createContentPanel();
		initializeData();
		setSize(new Dimension(500, 250));
		WindowManager.arrange(frame, this);
	}

	private void initializeData()
	{
		nameField.setText(episodeData.getEpisodeTitle());
		originalNameField.setText(episodeData.getOriginalEpisodeTitle());
		firstAiredField.setDate(episodeData.getFirstAirdate());
		productionCodeField.setText(episodeData.getProductionCode());
	}

	private void createContentPanel()
	{
		nameField=new JTextField(100);
		nameField.setEditable(false);
		originalNameField=new JTextField(100);
		originalNameField.setEditable(false);
		productionCodeField=new JTextField(10);
		productionCodeField.setEditable(false);
		firstAiredField=new DateField();
		firstAiredField.setEditable(false);
		episodeField=new LookupField<Episode>(new EpisodeLookup(show), new EpisodeLookupHandler());

		JPanel pnlContent=new JPanel(new GridBagLayout());
		int row=0;
		pnlContent.add(new JLabel("Name:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(nameField,
					   new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		row++;
		pnlContent.add(new JLabel("Originalname:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(originalNameField,
					   new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		row++;
		pnlContent.add(new JLabel("Produktionsnummer:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(productionCodeField,
					   new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		pnlContent.add(new JLabel("Erstausstrahlung:"),
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
			super("Ok", IconManager.getIcon("com/kiwisoft/utils/icons/ok.gif"));
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
			super("Abbrechen", IconManager.getIcon("com/kiwisoft/utils/icons/cancel.gif"));
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
