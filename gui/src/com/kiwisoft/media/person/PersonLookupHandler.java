package com.kiwisoft.media.person;

import com.kiwisoft.swing.lookup.LookupHandler;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.GuiUtils;

/**
 * @author Stefan Stiller
*/
public class PersonLookupHandler implements LookupHandler<Person>
{
	public boolean isCreateAllowed()
	{
		return true;
	}

	public Person createObject(LookupField<Person> lookupField)
	{
		return PersonDetailsView.createDialog(GuiUtils.getWindow(lookupField), lookupField.getText());
	}

	public boolean isEditAllowed()
	{
		return true;
	}

	public void editObject(LookupField<Person> lookupField, Person value)
	{
		PersonDetailsView.createDialog(GuiUtils.getWindow(lookupField), value);
	}
}
