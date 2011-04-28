package com.kiwisoft.media.dataimport.movie;

import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.dataimport.CrewData;
import com.kiwisoft.media.dataimport.MatchingPanel;
import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.media.person.CreditTypeLookup;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.PersonLookup;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.lookup.LookupField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @author Stefan Stiller
 * @since 26.02.11
 */
public class CrewDataDetailsView extends DetailsView
{
	private CrewData crew;

	private JTextField keyField;
	private JTextField nameField;
	private JTextField listedAsField;
	private LookupField<CreditType> typeField;
	private JTextField subTypeField;
	private MatchingPanel<Person> matchingPanel;

	public CrewDataDetailsView(CrewData crew)
	{
		this.crew=crew;
		initComponents();
		initData();
	}

	public static void create(Window owner, CrewData crewData)
	{
		CrewDataDetailsView view=new CrewDataDetailsView(crewData);
		DetailsDialog dialog=new DetailsDialog(owner, view, DetailsDialog.OK_CANCEL_ACTIONS);
		dialog.show();
	}

	private void initComponents()
	{
		keyField=new JTextField(15);
		nameField=new JTextField(30);
		listedAsField=new JTextField(30);
		listedAsField.setEditable(false);
		typeField=new LookupField<CreditType>(new CreditTypeLookup());
		subTypeField=new JTextField(30);
		matchingPanel=new MatchingPanel<Person>(new PersonLookup());

		setLayout(new GridBagLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		int row=0;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(nameField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Listed As:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(listedAsField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Type:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(typeField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Subtype:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(subTypeField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Key:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(keyField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(matchingPanel, new GridBagConstraints(0, row, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
	}

	private void initData()
	{
		if (crew!=null)
		{
			keyField.setText(crew.getKey());
			nameField.setText(crew.getName());
			listedAsField.setText(crew.getListedAs());
			typeField.setValue(crew.getType());
			subTypeField.setText(crew.getSubType());
			matchingPanel.setMatches(crew.getPersons());
		}
	}

	@Override
	public boolean apply() throws InvalidDataException
	{
		crew.setKey(keyField.getText());
		crew.setName(nameField.getText());
		crew.setPersons(matchingPanel.getMatches());
		crew.setType(typeField.getValue());
		crew.setSubType(subTypeField.getText());
		return true;
	}
}
