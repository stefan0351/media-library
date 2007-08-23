package com.kiwisoft.media.person;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.*;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.dataImport.SearchPattern;
import com.kiwisoft.media.pics.PictureLookup;
import com.kiwisoft.media.pics.PictureLookupHandler;
import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.media.pics.PicturePreviewUpdater;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.utils.gui.*;
import com.kiwisoft.utils.gui.lookup.*;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsFrame;

public class PersonDetailsView extends DetailsView
{
	public static void create(Person person, boolean actor)
	{
		new DetailsFrame(new PersonDetailsView(person, actor)).show();
	}

	public static Person createDialog(JFrame owner, Person person, boolean actor)
	{
		PersonDetailsView view=new PersonDetailsView(person, actor);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.person;
		return null;
	}

	public static Person createDialog(JFrame owner, String text, boolean actor)
	{
		PersonDetailsView view=new PersonDetailsView(text, actor);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.person;
		return null;
	}

	private Person person;

	// Konfigurations Panel
	private JTextField nameField;
	private JTextField firstNameField;
	private JTextField middleNameField;
	private JTextField surnameField;
	private LookupField<Gender> genderField;
	private JCheckBox actorField;
	private DialogLookupField patternField;
	private LookupField<Picture> pictureField;

	private PersonDetailsView(Person person, boolean actor)
	{
		createContentPanel();
		setPerson(person, actor);
	}

	private PersonDetailsView(String text, boolean actor)
	{
		createContentPanel();
		setPerson(null, actor);
		List<String> names=new ArrayList<String>();
		nameField.setText(text);
		for (StringTokenizer tokens=new StringTokenizer(text, " "); tokens.hasMoreTokens();) names.add(tokens.nextToken());
		int nameCount=names.size();
		if (nameCount>0)
		{
			firstNameField.setText(names.get(0));
			if (nameCount>1)
			{
				if (nameCount==2) surnameField.setText(names.get(1));
				else
				{
					StringBuilder middleName=new StringBuilder();
					for (int i=1; i<nameCount-1; i++)
					{
						if (i>1) middleName.append(" ");
						middleName.append(names.get(i));
					}
					middleNameField.setText(middleName.toString());
					surnameField.setText(names.get(nameCount-1));
				}
			}
		}
	}

	protected void createContentPanel()
	{
		nameField=new JTextField(30);
		firstNameField=new JTextField(15);
		middleNameField=new JTextField(15);
		surnameField=new JTextField(15);
		patternField=new DialogLookupField(new TVTVPatternLookup());
		genderField=new LookupField<Gender>(new GenderLookup());
		actorField=new JCheckBox();
		ImagePanel picturePreview=new ImagePanel(new Dimension(150, 200));
		picturePreview.setBorder(new EtchedBorder());
		pictureField=new LookupField<Picture>(new PictureLookup(), new PictureLookupHandler()
		{
			@Override
			public String getDefaultName()
			{
				return nameField.getText();
			}
		});

		setLayout(new GridBagLayout());
//		setPreferredSize(new Dimension(600, 220));
		int row=0;
		add(picturePreview, new GridBagConstraints(0, 0, 1, 7, 0.0, 0.0, NORTH, NONE, new Insets(0, 0, 0, 10), 0, 0));
		add(new JLabel("Name:"), new GridBagConstraints(1, row, 2, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(nameField, new GridBagConstraints(2, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("First Name:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(firstNameField, new GridBagConstraints(2, row, 1, 1, 0.5, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Middle Name:"), new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(middleNameField, new GridBagConstraints(4, row, 1, 1, 0.5, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Surname:"), new GridBagConstraints(1, row, 2, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(surnameField, new GridBagConstraints(2, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Gender:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(genderField, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Actor/Actress:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(actorField, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Search Pattern:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(patternField, new GridBagConstraints(2, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Picture:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(pictureField, new GridBagConstraints(2, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		FrameTitleUpdater titleUpdater=new FrameTitleUpdater();
		nameField.getDocument().addDocumentListener(titleUpdater);
		new PicturePreviewUpdater(pictureField, picturePreview);
	}

	private void setPerson(Person person, boolean actor)
	{
		this.person=person;
		if (person!=null)
		{
			nameField.setText(person.getName());
			firstNameField.setText(person.getFirstName());
			middleNameField.setText(person.getMiddleName());
			surnameField.setText(person.getSurname());
			genderField.setValue(person.getGender());
			actorField.setSelected(person.isActor());
			String pattern=person.getSearchPattern(SearchPattern.TVTV);
			if (pattern!=null) patternField.setText(pattern);
			pictureField.setValue(person.getPicture());
		}
		else
		{
			actorField.setSelected(actor);
			genderField.setValue(Gender.FEMALE);
		}
	}

	public boolean apply()
	{
		String name=nameField.getText();
		if (StringUtils.isEmpty(name))
		{
			JOptionPane.showMessageDialog(this, "Name is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			firstNameField.requestFocus();
			return false;
		}
		else name=name.trim();

		String firstName=firstNameField.getText();
		if (!StringUtils.isEmpty(firstName)) firstName=firstName.trim();
		else firstName=null;

		String surname=surnameField.getText();
		if (!StringUtils.isEmpty(firstName)) surname=surname.trim();
		else surname=null;

		String middleName=middleNameField.getText();
		if (!StringUtils.isEmpty(middleName)) middleName=middleName.trim();
		else middleName=null;

		Gender gender=genderField.getValue();
		String tvtvPattern=patternField.getText();
		boolean actor=actorField.isSelected();
		Picture picture=pictureField.getValue();

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (person==null) person=PersonManager.getInstance().createPerson();
			person.setName(name);
			person.setFirstName(firstName);
			person.setMiddleName(middleName);
			person.setSurname(surname);
			person.setSex(gender);
			person.setSearchPattern(SearchPattern.TVTV, tvtvPattern);
			person.setActor(actor);
			person.setPicture(picture);
			transaction.close();
			return true;
		}
		catch (Exception e)
		{
			if (transaction!=null)
			{
				try
				{
					transaction.rollback();
				}
				catch (SQLException e1)
				{
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	private String buildName()
	{
		StringBuilder name=new StringBuilder();
		String firstName=firstNameField.getText().trim();
		if (!StringUtils.isEmpty(firstName)) name.append(firstName);
		String middleName=middleNameField.getText().trim();
		if (!StringUtils.isEmpty(middleName))
		{
			if (name.length()>0) name.append(" ");
			name.append(middleName);
		}
		String surname=surnameField.getText().trim();
		if (!StringUtils.isEmpty(surname))
		{
			if (name.length()>0) name.append(" ");
			name.append(surname);
		}
		return name.toString();
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		public void changedUpdate(DocumentEvent e)
		{
			String name=nameField.getText();
			if (StringUtils.isEmpty(name)) setTitle("Person: <unknown>");
			else setTitle("Person: "+name);
		}
	}

	private class TVTVPatternLookup implements DialogLookup
	{
		public void open(JTextField field)
		{
			try
			{
				field.setText(URLEncoder.encode(buildName(), "UTF-8"));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(field, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}
		}

		public Icon getIcon()
		{
			return Icons.getIcon("lookup.create");
		}
	}

}
