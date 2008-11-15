package com.kiwisoft.media.person;

import java.awt.event.ActionEvent;
import java.util.List;

import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class MergePersonsAction extends MultiContextAction
{
	private ApplicationFrame frame;

	protected MergePersonsAction(ApplicationFrame frame)
	{
		super(Person.class, "Merge");
		this.frame=frame;
	}


	@Override
	public void update(List objects)
	{
		super.update(objects);
		setEnabled(getObjects().size()>1);
	}

	public void actionPerformed(ActionEvent e)
	{
		//noinspection unchecked
		List<Person> persons=getObjects();
		if (persons.size()>1)
		{
			Person person=PersonMergeView.createDialog(frame, persons);
			if (person!=null)
			{
				PersonDetailsView.create(person);
			}
		}
	}
}
