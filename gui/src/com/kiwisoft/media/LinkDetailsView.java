package com.kiwisoft.media;

import static java.awt.GridBagConstraints.WEST;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.DetailsFrame;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.InvalidDataException;
import com.kiwisoft.utils.gui.lookup.LookupField;

public class LinkDetailsView extends DetailsView
{
	public static void create(Show show)
	{
		new DetailsFrame(new LinkDetailsView(show)).show();
	}

	public static void create(Link link)
	{
		new DetailsFrame(new LinkDetailsView(link)).show();
	}

	private Show show;
	private Link link;

	// Konfigurations Panel
	private JTextField tfShow;
	private JTextField tfName;
	private JTextField tfUrl;
	private LookupField<Language> languageField;

	private LinkDetailsView(Show show)
	{
		assert show!=null;
		this.show=show;
		createContentPanel();
		initialize();
	}

	private LinkDetailsView(Link link)
	{
		assert link!=null;
		this.show=link.getShow();
		this.link=link;
		createContentPanel();
		initialize();
	}

	public String getTitle()
	{
		return "Link";
	}

	protected void createContentPanel()
	{
		tfShow=new JTextField();
		tfShow.setEditable(false);
		tfUrl=new JTextField();
		tfName=new JTextField();
		languageField=new LookupField<Language>(new LanguageLookup());

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 150));
		int row=0;
		add(new JLabel("Show:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfShow, new GridBagConstraints(1, row, 4, 1, 1.0, 0.0, WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfName, new GridBagConstraints(1, row, 4, 1, 1.0, 0.0, WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("URL:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfUrl, new GridBagConstraints(1, row, 4, 1, 1.0, 0.0, WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Language:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(languageField, new GridBagConstraints(1, row, 4, 1, 1.0, 0.0, WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
	}

	private void initialize()
	{
		if (show!=null) tfShow.setText(show.getTitle());
		if (link!=null)
		{
			tfName.setText(link.getName());
			tfUrl.setText(link.getUrl());
			languageField.setValue(link.getLanguage());
		}
	}

	public boolean apply()
	{
		try
		{
			String name=tfName.getText();
			if (StringUtils.isEmpty(name)) throw new InvalidDataException("Name is mssing!", tfName);
			String url=tfUrl.getText();
			if (StringUtils.isEmpty(url)) throw new InvalidDataException("URL is missing!", tfUrl);
			Language language=languageField.getValue();
			if (language==null) throw new InvalidDataException("Language is missing!", languageField);

			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				if (link==null) link=show.createLink();
				link.setName(name);
				link.setUrl(url);
				link.setLanguage(language);
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
			return true;
		}
		catch (InvalidDataException e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.getComponent().requestFocus();
			return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

	}
}
