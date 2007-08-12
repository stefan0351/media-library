package com.kiwisoft.media.person;

import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.media.pics.PictureLookup;
import com.kiwisoft.media.pics.PictureLookupHandler;
import com.kiwisoft.media.pics.PicturePreviewUpdater;
import com.kiwisoft.media.show.Show;
import static com.kiwisoft.utils.StringUtils.isEmpty;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.*;
import com.kiwisoft.utils.gui.lookup.*;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsFrame;

public class CastDetailsView extends DetailsView
{
	public static void create(CastMember cast)
	{
		new DetailsFrame(new CastDetailsView(cast)).show();
	}

	public static void create(Show show, CreditType type)
	{
		new DetailsFrame(new CastDetailsView(show, type)).show();
	}

	private CastMember cast;
	private Show show;
	private CreditType type;

	// Konfigurations Panel
	private JTextField tfCharacter;
	private LookupField<Person> tfActor;
	private JTextField tfVoice;
	private JTextPane tfDescription;
	private LookupField<Picture> pictureField;

	private CastDetailsView(CastMember cast)
	{
		this.cast=cast;
		setTitle("Darsteller");
		createContentPanel();
		initializeData();
	}

	private CastDetailsView(Show show, CreditType type)
	{
		this.show=show;
		this.type=type;
		if (type==CreditType.MAIN_CAST) setTitle("New Main Cast");
		else if (type==CreditType.RECURRING_CAST) setTitle("New Recurring Cast");
		else setTitle("New Cast");
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		tfCharacter=new JTextField(40);
		tfActor=new LookupField<Person>(new PersonLookup(), new PersonLookupHandler());
		tfVoice=new JTextField();
		tfDescription=new JTextPane();
		ImagePanel picturePreview=new ImagePanel(new Dimension(150, 200));
		picturePreview.setBorder(new EtchedBorder());
		pictureField=new LookupField<Picture>(new PictureLookup(), new PictureLookupHandler());

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(600, 300));
		int row=0;
		row++;
		add(picturePreview,
			new GridBagConstraints(0, row, 1, 10, 0.0, 0.0, NORTHWEST, NONE, new Insets(0, 0, 0, 10), 0, 0));
		add(new JLabel("Role:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfCharacter,
			new GridBagConstraints(2, row, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Actor/Actress:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfActor,
			new GridBagConstraints(2, row, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("German Voice:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfVoice,
			new GridBagConstraints(2, row, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Picture:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(pictureField,
			new GridBagConstraints(2, row, 2, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Summary:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(tfDescription),
			new GridBagConstraints(2, row, 2, 1, 1.0, 0.5, WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

		new PicturePreviewUpdater(pictureField, picturePreview);
	}

	private void initializeData()
	{
		if (cast!=null)
		{
			tfActor.setValue(cast.getActor());
			tfCharacter.setText(cast.getCharacterName());
			tfVoice.setText(cast.getVoice());
			pictureField.setValue(cast.getPicture());
			tfDescription.setText(cast.getDescription());
		}
	}

	public boolean apply()
	{
		String character=tfCharacter.getText();
		Person actor=tfActor.getValue();
		String voice=tfVoice.getText();
		if (isEmpty(voice)) voice=null;
		Picture picture=pictureField.getValue();
		String description=tfDescription.getText();

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (cast==null)
			{
				if (type==CreditType.MAIN_CAST) cast=show.createMainCast();
				else cast=show.createRecurringCast();
			}
			cast.setActor(actor);
			cast.setCharacterName(character);
			cast.setVoice(voice);
			cast.setPicture(picture);
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

	private class PersonLookupHandler implements LookupHandler<Person>
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
