/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:44:47 PM
 */
package com.kiwisoft.media;

import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.ImagePanel;

public class IntroPanel extends JPanel
{
	public IntroPanel()
	{
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setLayout(new GridBagLayout());
		Icon image=Icons.getIcon("splash");
		ImagePanel imagePanel=new ImagePanel(image);
		add(imagePanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(2, 2, 2, 2), 0, 0));
	}
}
