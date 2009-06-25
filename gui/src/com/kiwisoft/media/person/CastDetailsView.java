package com.kiwisoft.media.person;

import static java.awt.GridBagConstraints.*;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.files.ImageLookupHandler;
import com.kiwisoft.media.files.PicturePreviewUpdater;
import com.kiwisoft.media.show.Production;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.files.MediaFileLookup;
import com.kiwisoft.media.files.*;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.swing.ImagePanel;
import com.kiwisoft.swing.lookup.LookupEvent;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.lookup.LookupSelectionListener;
import static com.kiwisoft.utils.StringUtils.isEmpty;

public class CastDetailsView extends DetailsView
{
	private PicturePreviewUpdater previewUpdater;

	public static void create(CastMember cast)
	{
		new DetailsFrame(new CastDetailsView(cast)).show();
	}

	public static void create(Production production, CreditType type)
	{
		new DetailsFrame(new CastDetailsView(production, type)).show();
	}

	private CastMember cast;
	private Production production;
	private CreditType type;

	// Konfigurations Panel
	private JTextField characterField;
	private LookupField<Person> actorField;
	private JTextField voiceField;
	private JTextPane descriptionField;
	private LookupField<MediaFile> pictureField;

	private CastDetailsView(CastMember cast)
	{
		this.cast=cast;
		setTitle("Cast");
		createContentPanel();
		initializeData();
	}

	private CastDetailsView(Production production, CreditType type)
	{
		this.production=production;
		this.type=type;
		setTitle("New "+type.getByName());
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		characterField=new JTextField(40);
		actorField=new LookupField<Person>(new PersonLookup(), new PersonLookupHandler());
		voiceField=new JTextField();
		descriptionField=new JTextPane();
		ImagePanel picturePreview=new ImagePanel(new Dimension(150, 200));
		picturePreview.setBorder(new EtchedBorder());
		pictureField=new LookupField<MediaFile>(new MediaFileLookup(MediaType.IMAGE), new ImageLookupHandler());

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(600, 300));
		int row=0;
		row++;
		add(picturePreview,
			new GridBagConstraints(0, row, 1, 10, 0.0, 0.0, NORTHWEST, NONE, new Insets(0, 0, 0, 10), 0, 0));
		add(new JLabel("Role:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(characterField,
			new GridBagConstraints(2, row, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Actor/Actress:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(actorField,
			new GridBagConstraints(2, row, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("German Voice:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(voiceField,
			new GridBagConstraints(2, row, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Picture:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(pictureField,
			new GridBagConstraints(2, row, 2, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Summary:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(descriptionField),
			new GridBagConstraints(2, row, 2, 1, 1.0, 0.5, WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

		previewUpdater=new PicturePreviewUpdater(pictureField, picturePreview);
		getListenerList().installSelectionListener(actorField, new ActorSelectionListener());
	}

	private void initializeData()
	{
		if (cast!=null)
		{
			actorField.setValue(cast.getActor());
			characterField.setText(cast.getCharacterName());
			voiceField.setText(cast.getVoice());
			pictureField.setValue(cast.getPicture());
			descriptionField.setText(cast.getDescription());
		}
	}

	@Override
	public boolean apply()
	{
		String character=characterField.getText();
		Person actor=actorField.getValue();
		String voice=voiceField.getText();
		if (isEmpty(voice)) voice=null;
		MediaFile picture=pictureField.getValue();
		String description=descriptionField.getText();

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (cast==null) cast=production.createCastMember(type);
			cast.setActor(actor);
			cast.setCharacterName(character);
			cast.setVoice(voice);
			cast.setPicture(picture);
			if (picture!=null)
			{
				if (production instanceof Movie) picture.addMovie((Movie)production);
				if (production instanceof Show) picture.addShow((Show)production);
				if (production instanceof Episode) picture.addEpisode((Episode)production);
				if (actor!=null) picture.addPerson(actor);
			}
			cast.setDescription(description);
			transaction.close();
			return true;
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			try
			{
				if (transaction!=null) transaction.rollback();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			JOptionPane.showMessageDialog(this, t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private class ActorSelectionListener implements LookupSelectionListener
	{
		public void selectionChanged(LookupEvent event)
		{
			Person actor=actorField.getValue();
			if (actor!=null) previewUpdater.setDefaultPicture(actor.getPicture());
		}
	}
}
