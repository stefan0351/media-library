package com.kiwisoft.media.person;

import com.kiwisoft.swing.TextFieldAction;
import com.kiwisoft.swing.ActionField;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.WebUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Stefan Stiller
 */
public class OpenTvComAction extends TextFieldAction
{
    public OpenTvComAction()
    {
        super(Icons.getIcon("link.open"), "Open TV.com");
    }

    public void actionPerformed(ActionField actionField, ActionEvent event)
    {
        String key=actionField.getText();
        if (!StringUtils.isEmpty(key))
        {
            try
            {
                WebUtils.openURL(new URL("http://www.tv.com/text/person/"+key+"/summary.html"));
            }
            catch (MalformedURLException e1)
            {
                JOptionPane.showMessageDialog(actionField, "Invalid key!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
