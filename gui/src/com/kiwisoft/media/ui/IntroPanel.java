/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:44:47 PM
 */
package com.kiwisoft.media.ui;

import java.awt.GridBagLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.ImagePanel;
import com.kiwisoft.utils.gui.UIUtils;

public class IntroPanel extends JPanel
{
	public IntroPanel()
	{
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setLayout(new GridBagLayout());
		ImageIcon image=IconManager.getIcon("com/kiwisoft/media/icons/splash.jpg");
		ImagePanel imagePanel=new ImagePanel(image);
		add(imagePanel, UIUtils.createConstraints(0, 0));
	}
}
