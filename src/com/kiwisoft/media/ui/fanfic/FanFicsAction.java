/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 24, 2003
 * Time: 10:42:43 PM
 */
package com.kiwisoft.media.ui.fanfic;

import java.util.Set;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;

import com.kiwisoft.media.fanfic.FanFicGroup;
import com.kiwisoft.media.ui.MediaManagerFrame;

public class FanFicsAction extends AbstractAction
{
	private FanFicGroup group;
	private JComponent component;

	public FanFicsAction(JComponent component, Set<? extends FanFicGroup> groups)
	{
		super("Fan Fiction");
		this.component=component;
		if (groups.size()==1) group=groups.iterator().next();
		setEnabled(group!=null);
	}

	public void actionPerformed(ActionEvent e)
	{
		MediaManagerFrame wizard=(MediaManagerFrame)component.getTopLevelAncestor();
		wizard.setCurrentView(new FanFicsView(group), true);
	}
}
