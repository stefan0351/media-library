package com.kiwisoft.media.ui;

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

public class EpisodeFormatterDialog extends JDialog
{
	private DialogLookupField tfSource;
	private DialogLookupField tfTarget;
	private boolean value;

	public EpisodeFormatterDialog(JFrame frame, String source, String target)
	{
		super(frame, "Konvertiere Episoden", true);
		createContentPanel();
		initializeData(source, target);
		pack();
		WindowManager.arrange(frame, this);
	}

	private void initializeData(String source, String target)
	{
		tfSource.setText(source);
		tfTarget.setText(target);
	}

	private void createContentPanel()
	{
		tfSource=new DialogLookupField(new FileLookup(JFileChooser.FILES_AND_DIRECTORIES, true));
		tfSource.setColumns(40);
		tfTarget=new DialogLookupField(new FileLookup(JFileChooser.DIRECTORIES_ONLY, false));
		tfTarget.setColumns(40);

		JPanel pnlContent=new JPanel(new GridBagLayout());
		pnlContent.add(new JLabel("Quelle:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pnlContent.add(tfSource, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		pnlContent.add(new JLabel("Ziel:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
		pnlContent.add(tfTarget, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

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

	public String getTarget()
	{
		return tfTarget.getText();
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
