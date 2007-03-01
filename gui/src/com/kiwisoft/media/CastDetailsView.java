package com.kiwisoft.media;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

import com.kiwisoft.media.Cast;
import com.kiwisoft.media.Person;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.ShowCharacter;
import com.kiwisoft.media.ActorLookup;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.lookup.LookupHandler;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.ImagePanel;
import com.kiwisoft.utils.gui.ImageUpdater;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.DetailsFrame;

public class CastDetailsView extends DetailsView
{
	public static void create(Cast cast)
	{
		new DetailsFrame(new CastDetailsView(cast)).show();
	}

	public static void create(Show show, int type)
	{
		new DetailsFrame(new CastDetailsView(show, type)).show();
	}

	private Cast cast;
	private Show show;
	private int type;

	// Konfigurations Panel
	private LookupField<ShowCharacter> tfCharacter;
	private LookupField<Person> tfActor;
	private JTextField tfVoice;
	private DialogLookupField tfImageSmall;
	private DialogLookupField tfImageLarge;
	private ImagePanel ipImageSmall;
	private ImagePanel ipImageLarge;
	private JTextPane tfDescription;

	private CastDetailsView(Cast cast)
	{
		this.cast=cast;
		setTitle("Darsteller");
		createContentPanel();
		initializeData();
	}

	private CastDetailsView(Show show, int type)
	{
		this.show=show;
		this.type=type;
		if (type==Cast.MAIN_CAST) setTitle("Neuer Hauptdarsteller");
		else if (type==Cast.RECURRING_CAST) setTitle("Neuer Wiederkehrender Darsteller");
		else setTitle("Neuer Darsteller");
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		tfCharacter=new LookupField<ShowCharacter>(new CharacterLookup(), new CharacterLookupHandler());
		tfActor=new LookupField<Person>(new ActorLookup(), new ActorLookupHandler());
		tfVoice=new JTextField();
		tfImageSmall=new DialogLookupField(new WebFileLookup(false));
		tfImageLarge=new DialogLookupField(new WebFileLookup(false));
		ipImageSmall=new ImagePanel(new Dimension(160, 120));
		ipImageSmall.setBorder(new EtchedBorder());
		ipImageLarge=new ImagePanel(new Dimension(160, 120));
		ipImageLarge.setBorder(new EtchedBorder());
		tfDescription=new JTextPane();

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(600, 450));
		int row=0;
		row++;
		add(new JLabel("Character:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfCharacter, new GridBagConstraints(1, row, 2, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Darsteller:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfActor, new GridBagConstraints(1, row, 2, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Synchronstimme:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfVoice, new GridBagConstraints(1, row, 2, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Bild (klein):"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfImageSmall, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
		        GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(ipImageSmall, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Bild (gross):"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfImageLarge, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
		        GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(ipImageLarge, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Beschreibung:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(tfDescription), new GridBagConstraints(1, row, 2, 1, 1.0, 0.5,
		        GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

		new ImageUpdater(tfImageSmall.getTextField(), ipImageSmall);
		new ImageUpdater(tfImageLarge.getTextField(), ipImageLarge);
	}

	private void initializeData()
	{
		if (cast!=null)
		{
			tfActor.setValue(cast.getActor());
			tfCharacter.setValue(cast.getCharacter());
			tfVoice.setText(cast.getVoice());
			String imageSmall=cast.getImageSmall();
			if (!StringUtils.isEmpty(imageSmall))
				tfImageSmall.setText(new File(Configurator.getInstance().getString("path.root"), imageSmall).getAbsolutePath());
			String imageLarge=cast.getImageLarge();
			if (!StringUtils.isEmpty(imageLarge))
				tfImageLarge.setText(new File(Configurator.getInstance().getString("path.root"), imageLarge).getAbsolutePath());
			tfDescription.setText(cast.getDescription());
		}
	}

	public boolean apply()
	{
		ShowCharacter character=tfCharacter.getValue();
		if (character==null)
		{
			JOptionPane.showMessageDialog(this, "Charakter fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfCharacter.requestFocus();
			return false;
		}
		Person actor=tfActor.getValue();
		String voice=tfVoice.getText();
		if (StringUtils.isEmpty(voice)) voice=null;
		String imageSmall=tfImageSmall.getText();
		String imageLarge=tfImageLarge.getText();
		try
		{
			if (!StringUtils.isEmpty(imageSmall))
			{
				imageSmall=FileUtils.getRelativePath(Configurator.getInstance().getString("path.root"), imageSmall);
				imageSmall=StringUtils.replaceStrings(imageSmall, "\\", "/");
			}
			else imageSmall=null;
			if (!StringUtils.isEmpty(imageLarge))
			{
				imageLarge=FileUtils.getRelativePath(Configurator.getInstance().getString("path.root"), imageLarge);
				imageLarge=StringUtils.replaceStrings(imageLarge, "\\", "/");
			}
			else imageLarge=null;
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Ausnahmefehler", JOptionPane.ERROR_MESSAGE);
			tfImageSmall.requestFocus();
			return false;
		}
		String description=tfDescription.getText();

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (cast==null)
			{
				if (type==Cast.MAIN_CAST) cast=show.createMainCast();
				else cast=show.createRecurringCast();
			}
			cast.setActor(actor);
			cast.setCharacter(character);
			cast.setVoice(voice);
			cast.setImageSmall(imageSmall);
			cast.setImageLarge(imageLarge);
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

	private class CharacterLookupHandler implements LookupHandler<ShowCharacter>
	{
		public boolean isCreateAllowed()
		{
			return true;
		}

		public ShowCharacter createObject(LookupField<ShowCharacter> lookupField)
		{
			Container window=getTopLevelAncestor();
			if (window instanceof JFrame) return CharacterDetailsView.createDialog((JFrame)window, lookupField.getText());
			return null;
		}

		public boolean isEditAllowed()
		{
			return true;
		}

		public void editObject(ShowCharacter value)
		{
			Container window=getTopLevelAncestor();
			if (window instanceof JFrame) CharacterDetailsView.createDialog((JFrame)window, value);
		}
	}

	private class ActorLookupHandler implements LookupHandler<Person>
	{
		public boolean isCreateAllowed()
		{
			return true;
		}

		public Person createObject(LookupField<Person> lookupField)
		{
			Container window=getTopLevelAncestor();
			if (window instanceof JFrame) return PersonDetailsView.createDialog((JFrame)window, lookupField.getText(), true);
			return null;
		}

		public boolean isEditAllowed()
		{
			return true;
		}

		public void editObject(Person value)
		{
			Container window=getTopLevelAncestor();
			if (window instanceof JFrame) PersonDetailsView.createDialog((JFrame)window, value, true);
		}
	}

}
