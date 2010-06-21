package com.kiwisoft.swing;

import com.kiwisoft.swing.lookup.*;

import javax.swing.*;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.*;

/**
 * @author Stefan Stiller
 * @since 15.05.2010
 */
public class MultiLookupField<T> extends JPanel
{
	private Lookup<T> lookup;
	private LookupHandler<T> lookupHandler;

	public MultiLookupField(Lookup<T> lookup)
	{
		this(lookup, null);
	}

	public MultiLookupField(Lookup<T> lookup, LookupHandler<T> lookupHandler)
	{
		this.lookup=lookup;
		this.lookupHandler=lookupHandler;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		updateEmptyFields();
	}

	protected LookupField<T> createLookupField()
	{
		LookupField<T> field=new LookupField<T>(lookup, lookupHandler);
		LookupSelectionListener selectionListener=new LookupSelectionListener()
		{
			@Override
			public void selectionChanged(LookupEvent event)
			{
				updateEmptyFields();
			}
		};
		field.addSelectionListener(selectionListener); // todo remove listener
		field.putClientProperty("selectionListener", selectionListener);
		return field;
	}

	private void removeLookupField(LookupField lookupField)
	{
		LookupSelectionListener selectionListener=(LookupSelectionListener) lookupField.getClientProperty("selectionListener");
		if (selectionListener!=null) lookupField.removeSelectionListener(selectionListener);
		remove(lookupField);
	}

	private void updateEmptyFields()
	{
		LookupField emptyField=null;
		boolean lastField=false;
		for (Component component : getComponents())
		{
			if (component instanceof LookupField)
			{
				lastField=false;
				LookupField lookupField=(LookupField) component;
				if (lookupField.getValue()==null)
				{
					if (emptyField!=null) removeLookupField(emptyField);
					emptyField=lookupField;
					lastField=true;
				}
			}
		}
		if (emptyField==null)
		{
			add(createLookupField());
		}
		else if (!lastField)
		{
			removeLookupField(emptyField);
			add(createLookupField());
		}
		updateUI();
	}

	public List<T> getValues()
	{
		List<T> values=new ArrayList<T>();
		for (Component component : getComponents())
		{
			if (component instanceof LookupField)
			{
				T value=(T) ((LookupField) component).getValue();
				if (value!=null) values.add(value);
			}
		}
		return values;
	}

	public void setValues(Collection<T> values)
	{
		for (Component component : getComponents())
		{
			if (component instanceof LookupField) removeLookupField((LookupField) component);
		}
		if (values==null) values=Collections.emptySet();
		for (T value : values)
		{
			if (value!=null)
			{
				LookupField<T> lookupField=createLookupField();
				lookupField.setValue(value);
				add(lookupField);
			}
		}
		updateEmptyFields();
	}
}
