package com.kiwisoft.media.links;

import static java.awt.GridBagConstraints.WEST;
import java.awt.*;
import javax.swing.*;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.media.*;

public class LinkDetailsView extends DetailsView
{
	public static void create(Linkable linkable)
	{
		new DetailsFrame(new LinkDetailsView(linkable)).show();
	}

	public static void create(Link link)
	{
		new DetailsFrame(new LinkDetailsView(link)).show();
	}

	public static void createDialog(Window frame, String name, String url)
	{
		new DetailsDialog(frame, new LinkDetailsView(name, url)).show();
	}

	private Linkable linkable;
	private Link link;

	// Konfigurations Panel
	private LookupField<Linkable> linkableField;
	private JTextField nameField;
	private JTextField urlField;
	private LookupField<Language> languageField;

	private LinkDetailsView(Linkable linkable)
	{
		assert linkable!=null;
		this.linkable=linkable;
		createContentPanel();
		initialize();
	}

	private LinkDetailsView(String name, String url)
	{
		createContentPanel();
		nameField.setText(name);
		urlField.setText(url);
	}

	private LinkDetailsView(Link link)
	{
		assert link!=null;
		this.linkable=link.getGroup();
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
		linkableField=new LookupField<Linkable>(new LinkableLookup());
		urlField=new JTextField();
		nameField=new JTextField();
		languageField=new LookupField<Language>(new LanguageLookup());

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 150));
		int row=0;
		add(new JLabel("Group:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(linkableField, new GridBagConstraints(1, row, 4, 1, 1.0, 0.0, WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(nameField, new GridBagConstraints(1, row, 4, 1, 1.0, 0.0, WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("URL:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(urlField, new GridBagConstraints(1, row, 4, 1, 1.0, 0.0, WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Language:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(languageField, new GridBagConstraints(1, row, 4, 1, 1.0, 0.0, WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
	}

	private void initialize()
	{
		if (linkable!=null) linkableField.setValue(linkable);
		if (link!=null)
		{
			nameField.setText(link.getName());
			urlField.setText(link.getUrl());
			languageField.setValue(link.getLanguage());
		}
	}

	public boolean apply()
	{
		try
		{
			final Linkable linkable=linkableField.getValue();
			if (linkable==null) throw new InvalidDataException("Group is missing!", linkableField);
			final String name=nameField.getText();
			if (StringUtils.isEmpty(name)) throw new InvalidDataException("Name is mssing!", nameField);
			final String url=urlField.getText();
			if (StringUtils.isEmpty(url)) throw new InvalidDataException("URL is missing!", urlField);
			final Language language=languageField.getValue();
			if (language==null) throw new InvalidDataException("Language is missing!", languageField);

			return DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					if (link==null) link=linkable.getLinkGroup(true).createLink();
					link.setName(name);
					link.setUrl(url);
					link.setLanguage(language);
				}

				public void handleError(Throwable throwable, boolean rollback)
				{
					GuiUtils.handleThrowable(LinkDetailsView.this, throwable);
				}
			});
		}
		catch (InvalidDataException e)
		{
			e.handle();
			return false;
		}

	}
}
