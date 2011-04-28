package com.kiwisoft.media.dataimport;

import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.Country;
import com.kiwisoft.media.CountryLookup;
import com.kiwisoft.swing.InvalidDataException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @author Stefan Stiller
 * @since 27.02.11
 */
public class CountryDataDetailsView extends DetailsView
{
	private CountryData country;

	private JTextField isoCodeField;
	private JTextField nameField;
	private MatchingPanel<Country> matchingPanel;

	public CountryDataDetailsView(CountryData country)
	{
		this.country=country;
		initComponents();
		initData();
	}

	public static void create(Window owner, CountryData countryData)
	{
		CountryDataDetailsView view=new CountryDataDetailsView(countryData);
		DetailsDialog dialog=new DetailsDialog(owner, view, DetailsDialog.OK_CANCEL_ACTIONS);
		dialog.show();
	}

	private void initComponents()
	{
		isoCodeField=new JTextField(5);
		nameField=new JTextField(30);
		matchingPanel=new MatchingPanel<Country>(new CountryLookup());

		setLayout(new GridBagLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		int row=0;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(nameField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("ISO Code:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(isoCodeField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(matchingPanel, new GridBagConstraints(0, row, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
	}

	private void initData()
	{
		if (country!=null)
		{
			isoCodeField.setText(country.getSymbol());
			nameField.setText(country.getName());
			matchingPanel.setMatches(country.getCountries());
		}
	}

	@Override
	public boolean apply() throws InvalidDataException
	{
		country.setSymbol(isoCodeField.getText());
		country.setName(nameField.getText());
		country.setCountries(matchingPanel.getMatches());
		return true;
	}
	
}
