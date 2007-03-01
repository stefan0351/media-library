/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 6, 2003
 * Time: 11:43:11 AM
 */
package com.kiwisoft.media.ui;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.kiwisoft.media.Language;
import com.kiwisoft.utils.gui.IconManager;

public class LanguageTableCellRenderer extends DefaultTableCellRenderer
{
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
		if (value instanceof Language)
		{
			Language language=(Language)value;
			setText(language.getName());
			setIcon(IconManager.getIcon("com/kiwisoft/media/icons/flag_"+language.getSymbol()+".gif"));
		}
		else
		{
			setText(String.valueOf(value));
			setIcon(null);
		}
        if (!isSelected)
        {
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		else
		{
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		}
    	return this;
    }
}
