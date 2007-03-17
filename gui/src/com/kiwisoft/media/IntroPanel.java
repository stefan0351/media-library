/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:44:47 PM
 */
package com.kiwisoft.media;

import java.awt.GridBagLayout;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.ImagePanel;
import com.kiwisoft.utils.gui.UIUtils;

public class IntroPanel extends JPanel
{
	public IntroPanel()
	{
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setLayout(new GridBagLayout());
		Icon image=Icons.getIcon("splash");
		ImagePanel imagePanel=new ImagePanel(image);
		add(imagePanel, UIUtils.createConstraints(0, 0));
	}
}
