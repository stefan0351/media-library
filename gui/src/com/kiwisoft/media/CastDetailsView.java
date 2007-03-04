package com.kiwisoft.media;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import static com.kiwisoft.utils.StringUtils.isEmpty;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.DetailsFrame;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.ImagePanel;
import com.kiwisoft.utils.gui.ImageUpdater;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.lookup.LookupHandler;

public class CastDetailsView extends DetailsView
{
	public static void create(CastMember cast)
	{
		new DetailsFrame(new CastDetailsView(cast)).show();
	}

	public static void create(Show show, int type)
	{
		new DetailsFrame(new CastDetailsView(show, type)).show();
	}

	private CastMember cast;
	private Show show;
	private int type;

	// Konfigurations Panel
	private JTextField tfCharacter;
	private LookupField<Person> tfActor;
	private JTextField tfVoice;
	private DialogLookupField tfImageSmall;
	private DialogLookupField tfImageLarge;
	private JTextPane tfDescription;

	private CastDetailsView(CastMember cast)
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
		if (type==CastMember.MAIN_CAST) setTitle("Neuer Hauptdarsteller");
		else if (type==CastMember.RECURRING_CAST) setTitle("Neuer Wiederkehrender Darsteller");
		else setTitle("Neuer Darsteller");
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		tfCharacter=new JTextField(200);
		tfActor=new LookupField<Person>(new ActorLookup(), new ActorLookupHandler());
		tfVoice=new JTextField();
		tfImageSmall=new DialogLookupField(new WebFileLookup(false));
		tfImageLarge=new DialogLookupField(new WebFileLookup(false));
		ImagePanel ipImageSmall=new ImagePanel(new Dimension(160, 120));
		ipImageSmall.setBorder(new EtchedBorder());
		ImagePanel ipImageLarge=new ImagePanel(new Dimension(160, 120));
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
			tfCharacter.setText(cast.getCharacterName());
			tfVoice.setText(cast.getVoice());
			String imageSmall=cast.getImageSmall();
			if (!isEmpty(imageSmall))
				tfImageSmall.setText(new File(Configurator.getInstance().getString("path.root"), imageSmall).getAbsolutePath());
			String imageLarge=cast.getImageLarge();
			if (!isEmpty(imageLarge))
				tfImageLarge.setText(new File(Configurator.getInstance().getString("path.root"), imageLarge).getAbsolutePath());
			tfDescription.setText(cast.getDescription());
		}
	}

	public boolean apply()
	{
		String character=tfCharacter.getText();
		Person actor=tfActor.getValue();
		String voice=tfVoice.getText();
		if (isEmpty(voice)) voice=null;
		String imageSmall=tfImageSmall.getText();
		String imageLarge=tfImageLarge.getText();
		try
		{
			if (!isEmpty(imageSmall))
			{
				imageSmall=FileUtils.getRelativePath(Configurator.getInstance().getString("path.root"), imageSmall);
				imageSmall=StringUtils.replaceStrings(imageSmall, "\\", "/");
			}
			else imageSmall=null;
			if (!isEmpty(imageLarge))
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
				if (type==CastMember.MAIN_CAST) cast=show.createMainCast();
				else cast=show.createRecurringCast();
			}
			cast.setActor(actor);
			cast.setCharacterName(character);
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
