package com.kiwisoft.media.person;

import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;

import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.PersonManager;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 04.03.2007
 * Time: 11:27:46
 * To change this template use File | Settings | File Templates.
 */
public class DeletePersonAction extends SimpleContextAction<Person>
{
	private ApplicationFrame frame;

	public DeletePersonAction(ApplicationFrame frame)
	{
		super("Löschen");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent event)
	{
		final Person person=getObject();
		if (person.isUsed())
		{
			showMessageDialog(frame, "Person '"+person.getName()+"' kann nicht gelöscht werden.", "Meldung", INFORMATION_MESSAGE);
			return;
		}
		int option=showConfirmDialog(frame, "Person '"+person.getName()+"' wirklick löschen?", "Löschen?", YES_NO_OPTION,QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					PersonManager.getInstance().dropPerson(person);
				}

				public void handleError(Throwable throwable)
				{
					showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
}
