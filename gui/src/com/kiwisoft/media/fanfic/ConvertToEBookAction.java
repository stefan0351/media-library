package com.kiwisoft.media.fanfic;

import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.utils.Utils;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * @author Stefan Stiller
 */
public class ConvertToEBookAction extends MultiContextAction
{
	public ConvertToEBookAction()
	{
		super(FanFic.class, "Convert to Ebook");
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		List<FanFic> fanFics=Utils.cast(getObjects());
		for (FanFic fanFic : fanFics)
		{
			try
			{
				new FanFicEBookCreator(fanFic).convertToMobi();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
		}
	}
}
