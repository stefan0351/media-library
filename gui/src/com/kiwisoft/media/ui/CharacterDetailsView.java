package com.kiwisoft.media.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.CharacterManager;
import com.kiwisoft.media.Sex;
import com.kiwisoft.media.ShowCharacter;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.DetailsFrame;
import com.kiwisoft.utils.gui.DetailsDialog;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;

public class CharacterDetailsView extends DetailsView
{
	public static void create(ShowCharacter character)
	{
		new DetailsFrame(new CharacterDetailsView(character)).show();
	}

	public static ShowCharacter createDialog(JFrame owner, String text)
	{
		CharacterDetailsView view=new CharacterDetailsView(text);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.character;
		return null;
	}

	public static ShowCharacter createDialog(JFrame owner, ShowCharacter character)
	{
		CharacterDetailsView view=new CharacterDetailsView(character);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.character;
		return null;
	}

	private ShowCharacter character;

	// Konfigurations Panel
	private JTextField tfName;
	private JTextField tfNickName;
	private JComboBox cbxSex;

	private CharacterDetailsView(ShowCharacter character)
	{
		this.character=character;
		createContentPanel();
		initializeData();
	}

	private CharacterDetailsView(String text)
	{
		createContentPanel();
		initializeData();
		tfName.setText(text);
	}

	protected void createContentPanel()
	{
		tfName=new JTextField();
		tfNickName=new JTextField();
		cbxSex=new JComboBox(new Object[]{Sex.FEMALE, Sex.MALE});
		cbxSex.updateUI();

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 150));
		int row=0;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfName, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Spitzname:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfNickName, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Geschlecht:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(cbxSex, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		FrameTitleUpdater titleUpdater=new FrameTitleUpdater();
		tfName.getDocument().addDocumentListener(titleUpdater);
	}

	private void initializeData()
	{
		if (character!=null)
		{
			tfName.setText(character.getName());
			tfNickName.setText(character.getNickName());
			cbxSex.setSelectedItem(character.getSex());
		}
		else
		{
			cbxSex.setSelectedItem(Sex.FEMALE);
		}
	}

	public boolean apply()
	{
		String name=tfName.getText();
		if (StringUtils.isEmpty(name))
		{
			JOptionPane.showMessageDialog(this, "Name fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfName.requestFocus();
			return false;
		}
		else name=name.trim();
		String nickName=tfNickName.getText();
		if (StringUtils.isEmpty(name)) nickName=null;
		Sex sex=(Sex)cbxSex.getSelectedItem();
		if (sex==null)
		{
			JOptionPane.showMessageDialog(this, "Kein Geschlecht gewählt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			cbxSex.requestFocus();
			return false;
		}

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (character==null) character=CharacterManager.getInstance().createCharacter();
			character.setName(name);
			character.setNickName(nickName);
			character.setSex(sex);
			transaction.close();
			return true;
		}
		catch (Exception e)
		{
			if (transaction!=null)
			{
				try
				{
					transaction.rollback();
				}
				catch (SQLException e1)
				{
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		public void changedUpdate(DocumentEvent e)
		{
			String name=tfName.getText();
			if (StringUtils.isEmpty(name)) name="<Name>";
			else name=name.trim();
			setTitle("Charakter: "+name);
		}
	}

}
