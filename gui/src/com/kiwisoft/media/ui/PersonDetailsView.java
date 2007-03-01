package com.kiwisoft.media.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.net.URLEncoder;
import javax.swing.*;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.Person;
import com.kiwisoft.media.PersonManager;
import com.kiwisoft.media.Sex;
import com.kiwisoft.media.SearchPattern;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.DialogLookup;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.DetailsFrame;
import com.kiwisoft.utils.gui.DetailsDialog;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;

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
	private JTextField tfFirstName;
	private JTextField tfMiddleName;
	private JTextField tfSurname;
	private JComboBox cbxSex;
	private JCheckBox cbActor;
	private DialogLookupField tfPrismaPattern;
	private DialogLookupField tfTVTVPattern;

	private PersonDetailsView(Person person, boolean actor)
	{
		createContentPanel();
		setPerson(person, actor);
	}

	private PersonDetailsView(String text, boolean actor)
	{
		createContentPanel();
		setPerson(null, actor);
		List names=new ArrayList();
		for (StringTokenizer tokens=new StringTokenizer(text, " "); tokens.hasMoreTokens();) names.add(tokens.nextToken());
		int nameCount=names.size();
		if (nameCount>0)
		{
			tfFirstName.setText((String)names.get(0));
			if (nameCount>1)
			{
				if (nameCount==2) tfSurname.setText((String)names.get(1));
				else
				{
					StringBuffer middleName=new StringBuffer();
					for (int i=1;i<nameCount-1;i++)
					{
						if (i>1) middleName.append(" ");
						middleName.append(names.get(i));
					}
					tfMiddleName.setText(middleName.toString());
					tfSurname.setText((String)names.get(nameCount-1));
				}
			}

		}
	}

	protected void createContentPanel()
	{
		tfFirstName=new JTextField();
		tfMiddleName=new JTextField();
		tfSurname=new JTextField();
		tfPrismaPattern=new DialogLookupField(new PrismaPatternLookup());
		tfTVTVPattern=new DialogLookupField(new TVTVPatternLookup());
		cbxSex=new JComboBox(new Object[]{Sex.FEMALE, Sex.MALE});
		cbxSex.updateUI();
		cbActor=new JCheckBox();

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 220));
		int row=0;
		add(new JLabel("Vorname:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfFirstName, new GridBagConstraints(1, row, 1, 1, 0.5, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		add(new JLabel("Mittelname:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
		add(tfMiddleName, new GridBagConstraints(3, row, 1, 1, 0.5, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Nachname:"), new GridBagConstraints(0, row, 2, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfSurname, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Geschlecht:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(cbxSex, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Darsteller:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(cbActor, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Suchmuster:"), new GridBagConstraints(0, row, 4, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));

		row++;
		add(new JLabel("Prisma-Online:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 15, 0, 0), 0, 0));
		add(tfPrismaPattern, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("TVTV:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 15, 0, 0), 0, 0));
		add(tfTVTVPattern, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

		FrameTitleUpdater titleUpdater=new FrameTitleUpdater();
		tfFirstName.getDocument().addDocumentListener(titleUpdater);
		tfMiddleName.getDocument().addDocumentListener(titleUpdater);
		tfSurname.getDocument().addDocumentListener(titleUpdater);
	}

	private void setPerson(Person person, boolean actor)
	{
		this.person=person;
		if (person!=null)
		{
			tfFirstName.setText(person.getFirstName());
			tfMiddleName.setText(person.getMiddleName());
			tfSurname.setText(person.getSurname());
			cbxSex.setSelectedItem(person.getSex());
			cbActor.setSelected(person.isActor());
			String pattern=person.getSearchPattern(SearchPattern.PRISMA_ONLINE);
			if (pattern!=null) tfPrismaPattern.setText(pattern);
			pattern=person.getSearchPattern(SearchPattern.TVTV);
			if (pattern!=null) tfTVTVPattern.setText(pattern);
		}
		else
		{
			cbActor.setSelected(actor);
			cbxSex.setSelectedItem(Sex.FEMALE);
		}
	}

	public boolean apply()
	{
		String firstName=tfFirstName.getText();
		if (StringUtils.isEmpty(firstName))
		{
			JOptionPane.showMessageDialog(this, "Vorname fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfFirstName.requestFocus();
			return false;
		}
		else firstName=firstName.trim();
		String surname=tfSurname.getText();
		if (StringUtils.isEmpty(firstName))
		{
			JOptionPane.showMessageDialog(this, "Nachname fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfSurname.requestFocus();
			return false;
		}
		else surname=surname.trim();
		String middleName=tfMiddleName.getText();
		if (StringUtils.isEmpty(middleName)) middleName=null;
		Sex sex=(Sex)cbxSex.getSelectedItem();
		if (sex==null)
		{
			JOptionPane.showMessageDialog(this, "Kein Geschlecht gewählt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			cbxSex.requestFocus();
			return false;
		}
		String prismaPattern=tfPrismaPattern.getText();
		String tvtvPattern=tfTVTVPattern.getText();
		boolean actor=cbActor.isSelected();

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (person==null) person=PersonManager.getInstance().createPerson();
			person.setFirstName(firstName);
			person.setMiddleName(middleName);
			person.setSurname(surname);
			person.setSex(sex);
			person.setSearchPattern(SearchPattern.PRISMA_ONLINE, prismaPattern);
			person.setSearchPattern(SearchPattern.TVTV, tvtvPattern);
			person.setActor(actor);
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
		StringBuffer name=new StringBuffer();
		String firstName=tfFirstName.getText().trim();
		if (!StringUtils.isEmpty(firstName)) name.append(firstName);
		String middleName=tfMiddleName.getText().trim();
		if (!StringUtils.isEmpty(middleName))
		{
			if (name.length()>0) name.append(" ");
			name.append(middleName);
		}
		String surname=tfSurname.getText().trim();
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
			StringBuffer name=new StringBuffer();
			String surname=tfSurname.getText().trim();
			if (StringUtils.isEmpty(surname)) surname="<Nachname>";
			name.append(surname);
			name.append(", ");
			String firstName=tfFirstName.getText().trim();
			if (StringUtils.isEmpty(firstName)) firstName="<Vorname>";
			name.append(firstName);
			String middleName=tfMiddleName.getText().trim();
			if (!StringUtils.isEmpty(middleName)) name.append(" ").append(middleName);
			String nameString=name.toString();
			setTitle("Person: "+nameString);
		}
	}

	private class PrismaPatternLookup implements DialogLookup
	{
		public void open(JTextField field)
		{
			try
			{
				field.setText("pattern="+URLEncoder.encode(buildName(), "UTF-8"));
			}
			catch (Exception e)
			{
				e.printStackTrace();
            	JOptionPane.showMessageDialog(field, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}
		}

		public Icon getIcon()
		{
			return IconManager.getIcon("com/kiwisoft/utils/icons/lookup_create.gif");
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
			return IconManager.getIcon("com/kiwisoft/utils/icons/lookup_create.gif");
		}
	}

}
