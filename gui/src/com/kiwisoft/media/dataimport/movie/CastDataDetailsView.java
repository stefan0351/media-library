package com.kiwisoft.media.dataimport.movie;

import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.dataimport.CastData;
import com.kiwisoft.media.dataimport.MatchingPanel;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.PersonLookup;
import com.kiwisoft.swing.InvalidDataException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @author Stefan Stiller
 * @since 26.02.11
 */
public class CastDataDetailsView extends DetailsView
{
	private CastData cast;

	private JTextField keyField;
	private JTextField nameField;
	private JTextField listedAsField;
	private JTextField roleField;
	private MatchingPanel<Person> matchingPanel;

	public CastDataDetailsView(CastData cast)
	{
		this.cast=cast;
		initComponents();
		initData();
	}

	public static void create(Window owner, CastData castData)
	{
		CastDataDetailsView view=new CastDataDetailsView(castData);
		DetailsDialog dialog=new DetailsDialog(owner, view, DetailsDialog.OK_CANCEL_ACTIONS);
		dialog.show();
	}

	private void initComponents()
	{
		keyField=new JTextField(15);
		nameField=new JTextField(30);
		listedAsField=new JTextField(30);
		listedAsField.setEditable(false);
		roleField=new JTextField(30);
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
		add(new JLabel("Role:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(roleField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Key:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(keyField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(matchingPanel, new GridBagConstraints(0, row, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
	}

	private void initData()
	{
		if (cast!=null)
		{
			keyField.setText(cast.getKey());
			nameField.setText(cast.getName());
			listedAsField.setText(cast.getListedAs());
			roleField.setText(cast.getRole());
			matchingPanel.setMatches(cast.getPersons());
		}
	}

	@Override
	public boolean apply() throws InvalidDataException
	{
		cast.setKey(keyField.getText());
		cast.setName(nameField.getText());
		cast.setRole(roleField.getText());
		cast.setPersons(matchingPanel.getMatches());
		return true;
	}
}
