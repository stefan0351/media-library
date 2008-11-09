package com.kiwisoft.media.person;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.show.Production;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.utils.StringUtils;

public class CreditDetailsView extends DetailsView
{
	public static void create(Credit credit)
	{
		new DetailsFrame(new CreditDetailsView(credit)).show();
	}

	public static void create(Production production)
	{
		new DetailsFrame(new CreditDetailsView(production)).show();
	}

	private Credit credit;
	private Production production;

	// Konfigurations Panel
	private LookupField<CreditType> typeField;
	private JTextField subTypeField;
	private LookupField<Person> personField;
	private JTextField productionField;

	private CreditDetailsView(Credit credit)
	{
		this.credit=credit;
		this.production=credit.getProduction();
		setTitle("Credit");
		createContentPanel();
		initializeData();
	}

	private CreditDetailsView(Production production)
	{
		this.production=production;
		setTitle("New Credit");
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		typeField=new LookupField<CreditType>(new CreditTypeLookup());
		personField=new LookupField<Person>(new PersonLookup(), new PersonLookupHandler());
		subTypeField=new JTextField();
		productionField=new JTextField();
		productionField.setEditable(false);

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 150));
		int row=0;
		row++;
		add(new JLabel("Production:"),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(productionField,
			new GridBagConstraints(1, row, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Type:"),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(typeField,
			new GridBagConstraints(1, row, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Subtype:"),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(subTypeField,
			new GridBagConstraints(1, row, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Person"),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(personField,
			new GridBagConstraints(1, row, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
	}

	private void initializeData()
	{
		if (credit!=null)
		{
			personField.setValue(credit.getPerson());
			typeField.setValue(credit.getCreditType());
			subTypeField.setText(credit.getSubType());
		}
		if (production!=null)
		{
			productionField.setText(production.getProductionTitle());
		}
	}

	public boolean apply() throws InvalidDataException
	{
		final CreditType type=typeField.getValue();
		if (type==null) throw new InvalidDataException("Type is missing!", typeField);
		final String subType=StringUtils.empty2null(subTypeField.getText());
		final Person person=personField.getValue();
		if (person==null) throw new InvalidDataException("Person is missing!", personField);

		return DBSession.execute(new Transactional()
		{
			public void run() throws Exception
			{
				if (credit==null) credit=production.createCredit();
				credit.setCreditType(type);
				credit.setSubType(subType);
				credit.setPerson(person);
			}

			public void handleError(Throwable throwable, boolean rollback)
			{
				GuiUtils.handleThrowable(CreditDetailsView.this, throwable);
			}
		});
	}
}
