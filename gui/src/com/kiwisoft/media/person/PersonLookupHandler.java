package com.kiwisoft.media.person;

import com.kiwisoft.swing.lookup.LookupHandler;
import com.kiwisoft.swing.lookup.LookupField;

import javax.swing.*;

/**
 * @author Stefan Stiller
*/
public class PersonLookupHandler implements LookupHandler<Person>
{
	@Override
	public boolean isCreateAllowed()
	{
		return true;
	}

	@Override
	public Person createObject(LookupField<Person> lookupField)
	{
		return PersonDetailsView.createDialog(SwingUtilities.getWindowAncestor(lookupField), lookupField.getText());
	}

	@Override
	public boolean isEditAllowed()
	{
		return true;
	}

	@Override
	public void editObject(LookupField<Person> lookupField, Person value)
	{
		PersonDetailsView.createDialog(SwingUtilities.getWindowAncestor(lookupField), value);
	}
}
