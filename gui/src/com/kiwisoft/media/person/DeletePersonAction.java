package com.kiwisoft.media.person;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.*;

import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class DeletePersonAction extends SimpleContextAction<Person>
{
	private ApplicationFrame frame;

	public DeletePersonAction(ApplicationFrame frame)
	{
		super("Delete", Icons.getIcon("delete"));
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
		int option=showConfirmDialog(frame, "Person '"+person.getName()+"' wirklick löschen?", "Löschen?", YES_NO_OPTION, QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					PersonManager.getInstance().dropPerson(person);
				}

				public void handleError(Throwable throwable, boolean rollback)
				{
					showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
}
