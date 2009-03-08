package com.kiwisoft.media.person;

import com.kiwisoft.swing.TextFieldAction;
import com.kiwisoft.swing.ActionField;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.WebUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * @author Stefan Stiller
*/
public class OpenImdbAction extends TextFieldAction
{
    public OpenImdbAction()
    {
        super(Icons.getIcon("link.open"), "Open Imdb.com");
    }

    public void actionPerformed(ActionField actionField, ActionEvent event)
    {
        String key=actionField.getText();
        if (!StringUtils.isEmpty(key))
        {
            try
            {
                WebUtils.openURL(new URL("http://www.imdb.com/name/"+key));
            }
            catch (MalformedURLException e1)
            {
                JOptionPane.showMessageDialog(actionField, "Invalid key!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
