/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 2, 2003
 * Time: 11:01:14 PM
 */
package com.kiwisoft.media;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import com.kiwisoft.utils.gui.IconManager;

public class LanguageComboBoxRenderer extends DefaultListCellRenderer
{
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		Language language=(Language)value;
		if (language!=null)
		{
			Component component=super.getListCellRendererComponent(list, language.getName(), index, isSelected, cellHasFocus);
			if (component instanceof JLabel)
			{
				JLabel label=(JLabel)component;
				label.setIcon(IconManager.getIcon("com/kiwisoft/media/icons/languages/"+language.getSymbol()+".png"));
			}
			return component;
		}
		else
			return super.getListCellRendererComponent(list, "", index, isSelected, cellHasFocus);
	}
}
