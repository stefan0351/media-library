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
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.lookup.DialogLookup;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.StringTableModel;
import com.kiwisoft.utils.gui.table.DefaultTableConfiguration;
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
	private JTextField tfName;
	private StringTableModel tmMails;
	private StringTableModel tmWeb;
	private DialogLookupField tfPath;

	private AuthorDetailsView(Author author)
	{
		this.author=author;
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		tfName=new JTextField();
		tfName.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void changedUpdate(DocumentEvent e)
			{
				if (author==null) tfPath.setText(buildPath(tfName.getText()));
			}
		});
		tfPath=new DialogLookupField(new PathLookup());
		tmMails=new StringTableModel("address");
		SortableTable tblMails=new SortableTable(tmMails);
		tblMails.initializeColumns(new DefaultTableConfiguration(AuthorDetailsView.class, "mail"));
		tmWeb=new StringTableModel("address");
		SortableTable tblWeb=new SortableTable(tmWeb);
		tblWeb.initializeColumns(new DefaultTableConfiguration(AuthorDetailsView.class, "web"));

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 200));

		int row=0;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfName, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
										   GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Directory:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
															   GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfPath, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
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
		tfName.getDocument().addDocumentListener(titleUpdater);
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
			tfName.setText(author.getName());
			tfPath.setText(author.getPath());
			Iterator it=author.getMail().iterator();
			while (it.hasNext())
			{
				ContactMedium medium=(ContactMedium)it.next();
				tmMails.addString(medium.getValue());
			}
			tmMails.sort();
			it=author.getWeb().iterator();
			while (it.hasNext())
			{
				ContactMedium medium=(ContactMedium)it.next();
				tmWeb.addString(medium.getValue());
			}
			tmWeb.sort();
		}
	}

	public boolean apply()
	{
		String name=tfName.getText();
		if (StringUtils.isEmpty(name))
		{
			JOptionPane.showMessageDialog(this, "Name is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			tfName.requestFocus();
			return false;
		}
		else name=name.trim();
		Set<String> mail=tmMails.getStrings();
		Set<String> web=tmWeb.getStrings();
		String path=tfPath.getText();

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

		public void changedUpdate(DocumentEvent e)
		{
			String name=tfName.getText();
			if (StringUtils.isEmpty(name)) name="<Name>";
			else name=name.trim();
			setTitle("Author: "+name);
		}
	}

	private class PathLookup implements DialogLookup
	{
		public void open(JTextField field)
		{
			try
			{
				field.setText(buildPath(tfName.getText()));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(field, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}

		public Icon getIcon()
		{
			return Icons.getIcon("lookup.create");
		}
	}

}
