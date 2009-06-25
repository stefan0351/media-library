package com.kiwisoft.media.person;

import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.List;
import java.util.Enumeration;
import javax.swing.*;

import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;

/**
 * @author Stefan Stiller
 */
public class PersonMergeView extends DetailsView
{
	private ButtonGroup buttonGroup;

	public static Person createDialog(Window owner, List<Person> persons)
	{
		PersonMergeView view=new PersonMergeView(persons);
		DetailsDialog dialog=new DetailsDialog(owner, view, DetailsDialog.OK_CANCEL_ACTIONS);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.person;
		return null;
	}

	private List<Person> persons;
	private Person person;

	public PersonMergeView(List<Person> persons)
	{
		this.persons=persons;
		setTitle("Merge Persons");
		initializeComponents();
	}

	private void initializeComponents()
	{
		setLayout(new GridBagLayout());

		int row=0;
		add(new JLabel("Select Base Person:"), new GridBagConstraints(0, row++, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		buttonGroup=new ButtonGroup();
		for (Person person : persons)
		{
			JRadioButton button=new JRadioButton(person.getName());
			buttonGroup.add(button);
			add(button, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0, WEST, NONE, new Insets(5, 0, 0, 0), 0, 0));
			button.putClientProperty(Person.class, person);
		}
	}

	@Override
	public boolean apply() throws InvalidDataException
	{
		person=null;
		Enumeration<AbstractButton> buttons=buttonGroup.getElements();
		while (buttons.hasMoreElements())
		{
			AbstractButton button=buttons.nextElement();
			if (button.isSelected()) person=(Person)button.getClientProperty(Person.class);
		}
		if (person==null) throw new InvalidDataException("No base person selected!", this);
		return DBSession.execute(new Transactional()
		{
			public void run() throws Exception
			{
				PersonManager.getInstance().mergePersons(person, persons);
			}

			public void handleError(Throwable throwable, boolean rollback)
			{
				GuiUtils.handleThrowable(PersonMergeView.this, throwable);
			}
		});
	}
}
