/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 7, 2003
 * Time: 6:01:43 PM
 */
package com.kiwisoft.media.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.kiwisoft.media.dataImport.PrismaNetWorldInfoLoader;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.FileLookup;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.WindowManager;
import com.kiwisoft.utils.gui.progress.ProgressDialog;

public class DownloadPrismaOnlineDialog extends JDialog
{
	private DialogLookupField tfPath;
	private Set shows;

	public DownloadPrismaOnlineDialog(JFrame owner, Set shows) throws HeadlessException
	{
		super(owner, "Lade Prisma-Online Termine", true);
		this.shows=shows;
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
		panel.add(new JLabel("Pfad:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
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
		tfPath.setText(Configurator.getInstance().getString("path.dates.pnw", ""));
	}

	private class ApplyAction extends AbstractAction
	{
		public ApplyAction()
		{
			super("Ok", IconManager.getIcon("com/kiwisoft/utils/icons/ok.gif"));
		}

		public void actionPerformed(ActionEvent e)
		{
			String pathName=tfPath.getText();
			if (StringUtils.isEmpty(pathName))
			{
				JOptionPane.showMessageDialog(DownloadPrismaOnlineDialog.this,
				        "Kein Pfad eingegeben.", "Fehler", JOptionPane.ERROR_MESSAGE);
				return;
			}
			File path=new File(pathName);
			if (!path.exists())
			{
				int option=JOptionPane.showConfirmDialog(DownloadPrismaOnlineDialog.this,
				        "Das Verzeichnis '"+path+"' existiert nicht.\nSoll es angelegt werden?",
				        "Verzeichnis anlegen?",
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
				JOptionPane.showMessageDialog(DownloadPrismaOnlineDialog.this,
				        e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (shows==null)
			{
				Configurator.getInstance().setString("path.dates.pnw", pathName);
				Configurator.getInstance().saveUserValues();
			}
			dispose();
			new ProgressDialog((JFrame)getOwner(), new PrismaNetWorldInfoLoader(pathName, shows)).show();
		}
	}

	private class CancelAction extends AbstractAction
	{
		public CancelAction()
		{
			super("Abbrechen", IconManager.getIcon("com/kiwisoft/utils/icons/cancel.gif"));
		}

		public void actionPerformed(ActionEvent e)
		{
			dispose();
		}
	}
}
