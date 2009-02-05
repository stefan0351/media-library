package com.kiwisoft.media.person;

import com.kiwisoft.swing.TextFieldAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.WebUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
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

    public void actionPerformed(JTextComponent textComponent, ActionEvent event)
    {
        String key=textComponent.getText();
        if (!StringUtils.isEmpty(key))
        {
            try
            {
                WebUtils.openURL(new URL("http://www.tv.com/text/person/"+key+"/summary.html"));
            }
            catch (MalformedURLException e1)
            {
                JOptionPane.showMessageDialog(textComponent, "Invalid key!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
