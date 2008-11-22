package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;
import java.util.List;

import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.dataimport.SearchPattern;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;

/**
 * @author Stefan Stiller
*/
public class DeleteSearchPatternAction extends MultiContextAction
{
	private ApplicationFrame frame;

	public DeleteSearchPatternAction(ApplicationFrame frame)
	{
		super(SearchPattern.class, "Remove", Icons.getIcon("delete"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		final List<SearchPattern> patterns=getObjects();
		DBSession.execute(new Transactional()
		{
			public void run() throws Exception
			{
				for (SearchPattern pattern : patterns)
				{
					pattern.delete();
				}
			}

			public void handleError(Throwable throwable, boolean rollback)
			{
				GuiUtils.handleThrowable(frame, throwable);
			}
		});
	}
}
