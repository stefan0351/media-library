package com.kiwisoft.media;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.WindowManager;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.FileLookup;

public class EpisodeImportDialog extends JDialog
{
	private DialogLookupField tfSource;
	private boolean value;

	public EpisodeImportDialog(JFrame frame, String source)
	{
		super(frame, "Konvertiere Episoden", true);
		createContentPanel();
		initializeData(source);
		pack();
		WindowManager.arrange(frame, this);
	}

	private void initializeData(String source)
	{
		tfSource.setText(source);
	}

	private void createContentPanel()
	{
		tfSource=new DialogLookupField(new FileLookup(JFileChooser.FILES_AND_DIRECTORIES, true));
		tfSource.setColumns(40);

		JPanel pnlContent=new JPanel(new GridBagLayout());
		pnlContent.add(new JLabel("Quelle:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pnlContent.add(tfSource, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		JPanel pnlButtons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnOk=new JButton(new OkAction());
		pnlButtons.add(btnOk);
		pnlButtons.add(new JButton(new CancelAction()));

		JPanel panel=new JPanel(new GridBagLayout());
		panel.add(pnlContent, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
		panel.add(pnlButtons, new GridBagConstraints(0, 4, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		setContentPane(panel);

		getRootPane().setDefaultButton(btnOk);
	}

	private boolean apply()
	{
		return true;
	}

	public boolean getValue()
	{
		return value;
	}

	public String getSource()
	{
		return tfSource.getText();
	}

	private class OkAction extends AbstractAction
	{
		public OkAction()
		{
			super("Ok", IconManager.getIcon("com/kiwisoft/utils/icons/ok.gif"));
		}

		public void actionPerformed(ActionEvent e)
		{
			if (apply())
			{
				value=true;
				dispose();
			}
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
