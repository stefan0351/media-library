package com.kiwisoft.media.links;

import com.kiwisoft.swing.TextFieldAction;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.ActionField;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.WebUtils;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * @author Stefan Stiller
*/
public class OpenLinkFieldAction extends TextFieldAction
{
    public OpenLinkFieldAction()
    {
        super(Icons.getIcon("link.open"), "Open Link in external Browser");
    }

    @Override
	public void actionPerformed(ActionField actionField, ActionEvent e)
    {
        String link=actionField.getText();
        if (!StringUtils.isEmpty(link))
        {
            try
            {
                WebUtils.openURL(new URL(link));
            }
            catch (MalformedURLException e1)
            {
                GuiUtils.handleThrowable(actionField, e1);
            }
        }
    }
}
