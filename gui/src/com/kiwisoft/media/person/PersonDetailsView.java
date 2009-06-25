package com.kiwisoft.media.person;

import static com.kiwisoft.utils.StringUtils.trimString;

import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.Name;
import com.kiwisoft.media.NamesTableModel;
import com.kiwisoft.media.files.*;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.*;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.swing.table.SortableTable;
import com.kiwisoft.utils.StringUtils;

public class PersonDetailsView extends DetailsView
{
	public static void create(Person person)
	{
		new DetailsFrame(new PersonDetailsView(person)).show();
	}

	public static Person createDialog(Window owner, Person person)
	{
		PersonDetailsView view=new PersonDetailsView(person);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.person;
		return null;
	}

	public static Person createDialog(Window owner, String text)
	{
		PersonDetailsView view=new PersonDetailsView(text);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.person;
		return null;
	}

	private Person person;

	// Konfigurations Panel
	private NameField nameField;
	private NameField firstNameField;
	private NameField middleNameField;
	private NameField surnameField;
	private LookupField<Gender> genderField;
	private LookupField<MediaFile> pictureField;
	private NamesTableModel namesModel;
    private ActionField imdbField;
    private ActionField tvcomField;

    private PersonDetailsView(Person person)
	{
		createContentPanel();
		setPerson(person);
	}

	private PersonDetailsView(String text)
	{
		createContentPanel();
		setPerson(null);
		nameField.setValue(text);
		updateSplitNames();
	}

	protected void createContentPanel()
	{
		nameField=new NameField(30, true);
		firstNameField=new NameField(15, false);
		middleNameField=new NameField(15, false);
		surnameField=new NameField(15, false);
		genderField=new LookupField<Gender>(new GenderLookup());
		ImagePanel picturePreview=new ImagePanel(new Dimension(150, 200));
		picturePreview.setBorder(new EtchedBorder());
		pictureField=new LookupField<MediaFile>(new MediaFileLookup(MediaType.IMAGE), new ImageLookupHandler()
		{
			@Override
			public String getDefaultName()
			{
				return nameField.getText();
			}
		});
		namesModel=new NamesTableModel(false);
		SortableTable namesTable=new SortableTable(namesModel);
		namesTable.setPreferredScrollableViewportSize(new Dimension(300, 100));
		namesTable.configure(new DefaultTableConfiguration("person.names", PersonDetailsView.class, "names"));
        imdbField=new ActionField(new OpenImdbAction());
        tvcomField=new ActionField(new OpenTvComAction());

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
		add(new JLabel("Picture:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(pictureField, new GridBagConstraints(2, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

        row++;
        add(new JLabel("IMDb Key:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
        add(imdbField, new GridBagConstraints(2, row, 1, 1, 0.5, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
        add(new JLabel("TV.com Key:"), new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
        add(tvcomField, new GridBagConstraints(4, row, 1, 1, 0.5, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Also known as:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(namesTable), new GridBagConstraints(2, row, 3, 1, 0.5, 0.5, NORTHWEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));

		FrameTitleUpdater titleUpdater=new FrameTitleUpdater();
		nameField.getDocument().addDocumentListener(titleUpdater);
		new PicturePreviewUpdater(pictureField, picturePreview);
	}

	private void setPerson(Person person)
	{
		this.person=person;
		if (person!=null)
		{
			nameField.setValue(person.getName());
			firstNameField.setValue(person.getFirstName());
			middleNameField.setValue(person.getMiddleName());
			surnameField.setValue(person.getSurname());
			updateSplitNames();
			genderField.setValue(person.getGender());
			pictureField.setValue(person.getPicture());
			Iterator<Name> it=person.getAltNames().iterator();
			while (it.hasNext())
			{
				Name name=it.next();
				namesModel.addName(name.getName(), name.getLanguage());
			}
			namesModel.sort();
            imdbField.setText(person.getImdbKey());
            tvcomField.setText(person.getTvcomKey());
        }
	}

	@Override
	public boolean apply() throws InvalidDataException
    {
		String name=nameField.getText();
		if (StringUtils.isEmpty(name)) throw new InvalidDataException("Name is missing!", nameField);
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
		final Set<String> names=namesModel.getNameSet();

		final Gender gender=genderField.getValue();
		final MediaFile picture=pictureField.getValue();
        final String imdbKey=imdbField.getText();
        final String tvcomKey=tvcomField.getText();

        final String name1=name;
        final String firstName1=firstName;
        final String middleName1=middleName;
        final String surname1=surname;
        return DBSession.execute(new Transactional()
        {
            public void run() throws Exception
            {
                if (person==null) person=PersonManager.getInstance().createPerson();
                person.setName(name1);
                person.setFirstName(firstName1);
                person.setMiddleName(middleName1);
                person.setSurname(surname1);
                person.setGender(gender);
                person.setPicture(picture);
                person.setImdbKey(imdbKey);
                person.setTvcomKey(tvcomKey);
                if (picture!=null) picture.addPerson(person);
                Iterator<Name> it=new HashSet<Name>(person.getAltNames()).iterator();
                while (it.hasNext())
                {
                    Name altName=it.next();
                    if (names.contains(altName.getName())) names.remove(altName.getName());
                    else person.dropAltName(altName);
                }
                Iterator<String> itNames=names.iterator();
                while (itNames.hasNext())
                {
                    String text=itNames.next();
                    Name altName=person.createAltName();
                    altName.setName(text);
                }
            }

            public void handleError(Throwable throwable, boolean rollback)
            {
                GuiUtils.handleThrowable(PersonDetailsView.this,  throwable);
            }
        });
	}

	private void updateFullName()
	{
		String name=trimString(nameField.getValue());

		StringBuilder oldCombinedName=new StringBuilder();
		if (!StringUtils.isEmpty(firstNameField.getValue()))
			oldCombinedName.append(firstNameField.getValue().trim());
		if (!StringUtils.isEmpty(middleNameField.getValue()))
			StringUtils.appendSpace(oldCombinedName).append(middleNameField.getValue().trim());
		if (!StringUtils.isEmpty(surnameField.getValue()))
			StringUtils.appendSpace(oldCombinedName).append(surnameField.getValue().trim());

		if (StringUtils.isEmpty(name) || name.equals(oldCombinedName.toString()))
		{
			StringBuilder newCombinedName=new StringBuilder();
			if (!StringUtils.isEmpty(firstNameField.getText()))
				newCombinedName.append(firstNameField.getText().trim());
			if (!StringUtils.isEmpty(middleNameField.getText()))
				StringUtils.appendSpace(newCombinedName).append(middleNameField.getText().trim());
			if (!StringUtils.isEmpty(surnameField.getText()))
				StringUtils.appendSpace(newCombinedName).append(surnameField.getText().trim());
			nameField.setValue(newCombinedName.toString());
		}
	}

	private void updateSplitNames()
	{
		String oldName=trimString(nameField.getValue());
		String newName=nameField.getText();
		StringBuilder oldCombinedName=new StringBuilder();
		if (!StringUtils.isEmpty(firstNameField.getValue()))
			oldCombinedName.append(firstNameField.getValue().trim());
		if (!StringUtils.isEmpty(middleNameField.getValue()))
			StringUtils.appendSpace(oldCombinedName).append(middleNameField.getValue().trim());
		if (!StringUtils.isEmpty(surnameField.getValue()))
			StringUtils.appendSpace(oldCombinedName).append(surnameField.getValue().trim());
		if (StringUtils.isEmpty(oldCombinedName.toString()) || oldCombinedName.toString().equals(oldName))
		{
			String names[]=newName.split(" ");
			if (names.length==1)
			{
				firstNameField.setValue(names[0]);
				middleNameField.setValue(null);
				surnameField.setValue(null);
			}
			else if (names.length==2)
			{
				firstNameField.setValue(names[0]);
				middleNameField.setValue(null);
				surnameField.setValue(names[1]);
			}
			else if (names.length==3)
			{
				firstNameField.setValue(names[0]);
				middleNameField.setValue(names[1]);
				surnameField.setValue(names[2]);
			}
		}
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		@Override
		public void changedUpdate(DocumentEvent e)
		{
			String name=nameField.getText();
			if (StringUtils.isEmpty(name)) setTitle("Person: <unknown>");
			else setTitle("Person: "+name);
		}
	}

	private class NameField extends JTextField implements DocumentListener
	{
		private String value;
		private boolean listen=true;
		private boolean fullName;

		public NameField(int columns, boolean fullName)
		{
			super(columns);
			this.fullName=fullName;
			getDocument().addDocumentListener(this);
		}

		public void setValue(String value)
		{
			this.value=value;
			listen=false;
			try
			{
				setText(value);
			}
			finally
			{
				listen=true;
			}
		}

		public String getValue()
		{
			return value;
		}

		public void setListen(boolean listen)
		{
			this.listen=listen;
		}

		public void insertUpdate(DocumentEvent e)
		{
			changedUpdate(e);
		}

		public void removeUpdate(DocumentEvent e)
		{
			changedUpdate(e);
		}

		public void changedUpdate(DocumentEvent e)
		{
			if (listen)
			{
				if (fullName) updateSplitNames();
				else updateFullName();
			}
			value=getText();
		}
	}

}
