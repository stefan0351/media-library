/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 7, 2003
 * Time: 6:01:43 PM
 */
package com.kiwisoft.media.dataImport;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import javax.swing.*;

import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.WindowManager;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.FileLookup;
import com.kiwisoft.utils.gui.progress.ProgressDialog;
import com.kiwisoft.media.MediaConfiguration;

public class TVTVDeLoaderDialog<T> extends JDialog
{
	private DialogLookupField tfPath;
	private List<T> objects;

	public TVTVDeLoaderDialog(JFrame owner, List<T> objects) throws HeadlessException
	{
		super(owner, "Load Schedule from TVTV.de", true);
		this.objects=objects;
		setContentPane(createContentPanel());
		initialize();
		pack();
		WindowManager.arrange(owner, this);
	}

	private JPanel createContentPanel()
	{
		tfPath=new DialogLookupField(new FileLookup(JFileChooser.DIRECTORIES_ONLY, false));

		JPanel pnlButtons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnlButtons.add(new JButton(new ApplyAction()));
		pnlButtons.add(new JButton(new CancelAction()));

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setPreferredSize(new Dimension(400, 100));
		int row=0;
		panel.add(new JLabel("Path:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
															  GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(tfPath, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
												 GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 10), 0, 0));

		row++;
		panel.add(pnlButtons, new GridBagConstraints(0, row, 2, 1, 1.0, 0.0,
													 GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		return panel;
	}

	private void initialize()
	{
		tfPath.setText(MediaConfiguration.getRecentSchedulePath());
	}

	private class ApplyAction extends AbstractAction
	{
		public ApplyAction()
		{
			super("Ok", Icons.getIcon("ok"));
		}

		public void actionPerformed(ActionEvent e)
		{
			String pathName=tfPath.getText();
			if (StringUtils.isEmpty(pathName))
			{
				JOptionPane.showMessageDialog(TVTVDeLoaderDialog.this,
											  "No path selected.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			File path=new File(pathName);
			if (!path.exists())
			{
				int option=JOptionPane.showConfirmDialog(TVTVDeLoaderDialog.this,
														 "Directory '"+path+"' doesn't exist.\nCreate?",
														 "Confirmation",
														 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (option!=JOptionPane.YES_OPTION) return;
			}
			try
			{
				path.mkdirs();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
				JOptionPane.showMessageDialog(TVTVDeLoaderDialog.this,
											  e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (objects==null)
			{
				MediaConfiguration.setRecentSchedulePath(pathName);
				Configurator.getInstance().saveUserValues();
			}
			dispose();
			new ProgressDialog((JFrame)getOwner(), new TVTVDeLoader(pathName, objects)).show();
		}
	}

	private class CancelAction extends AbstractAction
	{
		public CancelAction()
		{
			super("Cancel", Icons.getIcon("cancel"));
		}

		public void actionPerformed(ActionEvent e)
		{
			dispose();
		}
	}
}
