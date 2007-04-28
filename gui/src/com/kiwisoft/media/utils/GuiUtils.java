package com.kiwisoft.media.utils;

import java.util.Iterator;
import java.util.List;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Component;
import javax.swing.JToolBar;
import javax.swing.Action;

import com.kiwisoft.utils.gui.ToolBar;
import com.kiwisoft.utils.gui.actions.ContextAction;

/**
 * @author Stefan Stiller
 */
public class GuiUtils
{
	private GuiUtils()
	{
	}

	public static <T> JToolBar createToolBar(List<ContextAction<? super T>> toolBarActions)
	{
		ToolBar toolBar=new ToolBar();
		for (Iterator<ContextAction<? super T>> it=toolBarActions.iterator(); it.hasNext();)
		{
			Action action=it.next();
			toolBar.add(action);
			if (it.hasNext()) toolBar.addSeparator(new Dimension(10, 10));
		}
		return toolBar;
	}

	public static int indexOf(Container container, Component component)
	{
		for (int i=0;i<container.getComponentCount();i++)
		{
			if (container.getComponent(i)==component) return i;
		}
		return -1;
	}

}
