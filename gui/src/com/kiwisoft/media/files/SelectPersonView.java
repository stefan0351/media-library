package com.kiwisoft.media.files;

import java.awt.*;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.PersonLookup;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.InvalidDataException;

/**
 * @author Stefan Stiller
 */
public class SelectPersonView extends DetailsView
{
	public static Person createDialog(Window owner)
	{
		SelectPersonView view=new SelectPersonView();
		DetailsDialog dialog=new DetailsDialog(owner, view, DetailsDialog.OK_CANCEL_ACTIONS);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK)
		{
			return view.person;
		}
		return null;
	}

	private Person person;

	private LookupField<Person> personField;

	private SelectPersonView()
	{
		setTitle("Select Person");
		initComponents();
	}

	@Override
	public JComponent getDefaultFocusComponent()
	{
		return personField;
	}

	private void initComponents()
	{
		personField=new LookupField<Person>(new PersonLookup());
		personField.setPreferredSize(new Dimension(300, personField.getPreferredSize().height));

		setLayout(new GridBagLayout());
		add(new JLabel("Person:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(personField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
	}


	@Override
	public boolean apply() throws InvalidDataException
	{
		person=personField.getValue();
		return true;
	}
}
