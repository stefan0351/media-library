package com.kiwisoft.media.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.text.NumberFormatter;

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
		for (int i=0; i<container.getComponentCount(); i++)
		{
			if (container.getComponent(i)==component) return i;
		}
		return -1;
	}

	public static <T extends Number & Comparable> JFormattedTextField createNumberField(Class<T> type, int columns, T minimum, T max)
	{
		NumberFormatter formatter=new NumberFormatter(new DecimalFormat("#.#"));
		formatter.setValueClass(type);
		formatter.setMinimum(minimum);
		formatter.setMaximum(max);
		JFormattedTextField field=new JFormattedTextField(formatter);
		field.setColumns(columns);
		field.setHorizontalAlignment(SwingConstants.TRAILING);
		return field;
	}

	public static void handleThrowable(Component component, Throwable throwable)
	{
		throwable.printStackTrace();
		showMessageDialog(component, throwable.getClass().getSimpleName()+":\n"+throwable.getMessage(), "Error", ERROR_MESSAGE);
	}

	public static JLabel createBoldLabel(String name)
	{
		JLabel label=new JLabel(name);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		return label;
	}
}
