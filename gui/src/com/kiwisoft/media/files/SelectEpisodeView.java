package com.kiwisoft.media.files;

import java.awt.*;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowLookup;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.EpisodeLookup;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.InvalidDataException;

/**
 * @author Stefan Stiller
 */
public class SelectEpisodeView extends DetailsView
{
	public static Episode createDialog(Window owner)
	{
		SelectEpisodeView view=new SelectEpisodeView();
		DetailsDialog dialog=new DetailsDialog(owner, view, DetailsDialog.OK_CANCEL_ACTIONS);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK)
		{
			return view.episode;
		}
		return null;
	}

	private Episode episode;

	private LookupField<Show> showField;
	private LookupField<Episode> episodeField;

	private SelectEpisodeView()
	{
		setTitle("Select Episode");
		initComponents();
	}

	@Override
	public JComponent getDefaultFocusComponent()
	{
		return showField;
	}

	private void initComponents()
	{
		showField=new LookupField<Show>(new ShowLookup());
		showField.setPreferredSize(new Dimension(300, showField.getPreferredSize().height));
		episodeField=new LookupField<Episode>(new EpisodeLookup(null)
		{
			@Override
			protected Show getShow()
			{
				return showField.getValue();
			}
		});
		episodeField.setPreferredSize(new Dimension(300, episodeField.getPreferredSize().height));

		setLayout(new GridBagLayout());
		add(new JLabel("Show:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(showField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		add(new JLabel("Episode:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(episodeField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
	}


	@Override
	public boolean apply() throws InvalidDataException
	{
		episode=episodeField.getValue();
		return true;
	}
}
