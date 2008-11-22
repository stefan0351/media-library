package com.kiwisoft.media.dataimport;

import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.GuiUtils;

public class ResolveCastStringDialog extends JDialog
{
	private boolean returnValue;

	private JTextField fullCastField;
	private JTextField actorField;
	private JTextField characterField;
	private String fullCast;
	private String actor;
	private String character;

	public ResolveCastStringDialog(Window frame, String fullCast)
	{
		super(frame, "Complex Cast Description found", ModalityType.APPLICATION_MODAL);
		this.fullCast=fullCast;
		createContentPanel();
		initializeData();
		setSize(new Dimension(400, 200));
		GuiUtils.centerWindow(frame, this);
	}

	private void initializeData()
	{
		fullCastField.setText(fullCast);
	}

	private void createContentPanel()
	{
		fullCastField=new JTextField(100);
		fullCastField.setEditable(false);
		actorField=new JTextField(100);
		characterField=new JTextField(100);

		JPanel pnlContent=new JPanel(new GridBagLayout());
		int row=0;
		pnlContent.add(new JLabel("Text:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(fullCastField,
					   new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		row++;
		pnlContent.add(new JLabel("Actor:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(actorField,
					   new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		row++;
		pnlContent.add(new JLabel("Role:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(characterField,
					   new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		JPanel pnlButtons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnOk=new JButton(new OkAction());
		pnlButtons.add(btnOk);
		pnlButtons.add(new JButton(new CancelAction()));

		JPanel panel=new JPanel(new GridBagLayout());
		panel.add(pnlContent, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, CENTER, BOTH, new Insets(10, 10, 0, 10), 0, 0));
		panel.add(pnlButtons, new GridBagConstraints(0, 4, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		setContentPane(panel);

		getRootPane().setDefaultButton(btnOk);
	}

	public boolean isOk()
	{
		return returnValue;
	}

	public String getActor()
	{
		return actor;
	}

	public String getCharacter()
	{
		return character;
	}

	private boolean apply()
	{
		actor=actorField.getText();
		if (StringUtils.isEmpty(actor)) return false;
		character=characterField.getText();
		return true;
	}

	private class OkAction extends AbstractAction
	{
		public OkAction()
		{
			super("Ok", Icons.getIcon("ok"));
		}

		public void actionPerformed(ActionEvent e)
		{
			if (apply())
			{
				returnValue=true;
				dispose();
			}
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
