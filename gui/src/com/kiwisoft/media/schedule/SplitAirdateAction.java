package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JOptionPane;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

/**
 * @author Stefan Stiller
 */
public class SplitAirdateAction extends SimpleContextAction<Airdate>
{
	private ApplicationFrame frame;

	public SplitAirdateAction(ApplicationFrame frame)
	{
		super("Split Airdate");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		Airdate airdate=getObject();
		String event=airdate.getEvent();
		String[] events=event.split("/");
		if (events.length>1)
		{
			Show show=airdate.getShow();
			int length=show.getDefaultEpisodeLength();
			if (length>20 && length<=25) length=30;
			else if (length>40 && length<=45) length=60;
			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				for (int i=1; i<events.length; i++)
				{
					Airdate newAirdate=new Airdate();
					newAirdate.setShow(airdate.getShow());
					newAirdate.setEvent(events[i].trim());
					newAirdate.setChannel(airdate.getChannel());
					newAirdate.setDate(new Date(airdate.getDate().getTime()+i*length*DateUtils.MINUTE));
					newAirdate.setLanguage(airdate.getLanguage());
				}
				airdate.setEvent(events[0].trim());
				transaction.close();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
				try
				{
					if (transaction!=null) transaction.rollback();
				}
				catch (SQLException e2)
				{
					e2.printStackTrace();
				}
				JOptionPane.showMessageDialog(frame, e1.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
