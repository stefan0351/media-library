package com.kiwisoft.media;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class PinAction extends ContextAction
{
	private Pinnable pinnable;

	public PinAction(Pinnable pinnable)
	{
		super(pinnable.isPinned() ? "Unpin" : "Pin", Icons.getIcon("pin"));
		this.pinnable=pinnable;
	}

	public void actionPerformed(ActionEvent e)
	{
		pinnable.setPinned(!pinnable.isPinned());
		putValue(NAME, pinnable.isPinned() ? "Unpin" : "Pin");
	}
}
