package com.kiwisoft.media.fanfic;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * @author Stefan Stiller
 */
public class DeleteFanFicPartAction extends MultiContextAction
{
	private ApplicationFrame frame;
	private FanFic fanFic;

	public DeleteFanFicPartAction(ApplicationFrame frame)
	{
		super(FanFicPart.class, "Delete", Icons.getIcon("delete"));
		this.frame=frame;
		update(null);
	}

	public void setFanFic(FanFic fanFic)
	{
		this.fanFic=fanFic;
		update(getObjects());
	}

	@Override
	protected boolean isValid(Object object)
	{
		return super.isValid(object) && fanFic!=null;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		final List<FanFicPart> parts=Utils.cast(getObjects());
		int option=JOptionPane.showConfirmDialog(frame, "Delete "+parts.size()+" fanfic part(s)?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			DBSession.execute(new Transactional()
			{
				@Override
				public void run() throws Exception
				{
					for (FanFicPart part : parts)
					{
						fanFic.dropPart(part);
					}
				}

				@Override
				public void handleError(Throwable throwable, boolean rollback)
				{
					GuiUtils.handleThrowable(frame, throwable);
				}
			});
		}
	}
}