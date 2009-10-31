package com.kiwisoft.media.fanfic;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.ContactMedium;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.lookup.DialogLookup;
import com.kiwisoft.swing.lookup.DialogLookupField;
import com.kiwisoft.swing.table.SortableTable;
import com.kiwisoft.swing.table.StringTableModel;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;

public class AuthorDetailsView extends DetailsView
{
	public static void create(Author author)
	{
		new DetailsFrame(new AuthorDetailsView(author)).show();
	}

	private Author author;

	// Konfigurations Panel
	private JTextField nameField;
	private StringTableModel mailModel;
	private StringTableModel webModel;
	private DialogLookupField pathField;

	private AuthorDetailsView(Author author)
	{
		this.author=author;
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		nameField=new JTextField();
		nameField.getDocument().addDocumentListener(new DocumentAdapter()
		{
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				if (author==null) pathField.setText(buildPath(nameField.getText()));
			}
		});
		pathField=new DialogLookupField(new PathLookup());
		mailModel=new StringTableModel("address");
		SortableTable tblMails=new SortableTable(mailModel);
		tblMails.configure(new DefaultTableConfiguration("author.mail", AuthorDetailsView.class, "mail"));
		webModel=new StringTableModel("address");
		SortableTable tblWeb=new SortableTable(webModel);
		tblWeb.configure(new DefaultTableConfiguration("author.web", AuthorDetailsView.class, "web"));

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 200));

		int row=0;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(nameField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
										   GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Directory:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
															   GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(pathField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
										   GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("EMail:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														 GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(tblMails), new GridBagConstraints(1, row, 2, 1, 1.0, 0.5,
															  GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Web:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
													   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(tblWeb), new GridBagConstraints(1, row, 2, 1, 1.0, 0.5,
															GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

		FrameTitleUpdater titleUpdater=new FrameTitleUpdater();
		nameField.getDocument().addDocumentListener(titleUpdater);
	}

	private String buildPath(String name)
	{
		StringBuilder buffer=new StringBuilder();
		for (StringTokenizer tokens=new StringTokenizer(name, " .,-\""); tokens.hasMoreTokens();)
		{
			buffer.append(tokens.nextToken().toLowerCase());
			if (tokens.hasMoreTokens()) buffer.append("_");
		}
		return buffer.toString();
	}

	private void initializeData()
	{
		if (author!=null)
		{
			nameField.setText(author.getName());
			pathField.setText(author.getPath());
			Iterator it=author.getMail().iterator();
			while (it.hasNext())
			{
				ContactMedium medium=(ContactMedium)it.next();
				mailModel.addString(medium.getValue());
			}
			mailModel.sort();
			it=author.getWeb().iterator();
			while (it.hasNext())
			{
				ContactMedium medium=(ContactMedium)it.next();
				webModel.addString(medium.getValue());
			}
			webModel.sort();
		}
	}

	@Override
	public boolean apply()
	{
		String name=nameField.getText();
		if (StringUtils.isEmpty(name))
		{
			JOptionPane.showMessageDialog(this, "Name is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			nameField.requestFocus();
			return false;
		}
		else name=name.trim();
		Set<String> mail=mailModel.getStrings();
		Set<String> web=webModel.getStrings();
		String path=pathField.getText();

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (author==null) author=FanFicManager.getInstance().createAuthor();
			author.setName(name);
			author.setPath(path);
			for (ContactMedium altMedium : new HashSet<ContactMedium>(author.getMail()))
			{
				if (mail.contains(altMedium.getValue())) mail.remove(altMedium.getValue());
				else author.dropMail(altMedium);
			}
			for (String address : mail)
			{
				ContactMedium medium=author.createMail();
				medium.setValue(address);
			}
			for (ContactMedium altMedium : new HashSet<ContactMedium>(author.getWeb()))
			{
				if (web.contains(altMedium.getValue())) web.remove(altMedium.getValue());
				else author.dropWeb(altMedium);
			}
			for (String address : web)
			{
				ContactMedium medium=author.createWeb();
				medium.setValue(address);
			}
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
		public FrameTitleUpdater()
		{
			changedUpdate(null);
		}

		@Override
		public void changedUpdate(DocumentEvent e)
		{
			String name=nameField.getText();
			if (StringUtils.isEmpty(name)) name="<Name>";
			else name=name.trim();
			setTitle("Author: "+name);
		}
	}

	private class PathLookup implements DialogLookup
	{
		@Override
		public void open(JTextField field)
		{
			try
			{
				field.setText(buildPath(nameField.getText()));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(field, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}

		@Override
		public Icon getIcon()
		{
			return Icons.getIcon("lookup.create");
		}
	}

}
