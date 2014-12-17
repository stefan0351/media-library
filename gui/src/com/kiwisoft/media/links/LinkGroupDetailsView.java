package com.kiwisoft.media.links;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.format.FormatUtils;
import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.media.Linkable;
import com.kiwisoft.media.LinkManager;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.utils.StringUtils;

public class LinkGroupDetailsView extends DetailsView
{
	public static void openNew(Linkable linkable)
	{
		new DetailsFrame(new LinkGroupDetailsView(linkable, null)).show();
	}

	public static void openEdit(LinkGroup linkGroup)
	{
		new DetailsFrame(new LinkGroupDetailsView(linkGroup.getParentGroup(), linkGroup)).show();
	}

	private Linkable parent;
	private LinkGroup group;

	// Konfigurations Panel
	private JTextField nameField;
	private JTextField parentField;

	private LinkGroupDetailsView(Linkable parent, LinkGroup group)
	{
		this.group=group;
		this.parent=parent;
		initialize();
	}

	@Override
	public String getTitle()
	{
		return "Link Group";
	}

	@Override
	protected void initializeComponents()
	{
		parentField=new JTextField();
		parentField.setEditable(false);
		nameField=new JTextField();

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(300, 150));
		int row=0;
		add(new JLabel("Parent:"),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(parentField, new GridBagConstraints(1, row, 4, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Name:"),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(nameField, new GridBagConstraints(1, row, 4, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
	}

	@Override
	protected void initializeData()
	{
		if (parent!=null) parentField.setText(FormatUtils.format(parent));
		if (group!=null) nameField.setText(group.getName());
	}

	@Override
	public boolean apply()
	{
		try
		{
			final String name=nameField.getText();
			if (StringUtils.isEmpty(name)) throw new InvalidDataException("Name is mssing!", nameField);

			return DBSession.execute(new Transactional()
			{
				@Override
				public void run() throws Exception
				{
					if (group==null)
					{
						if (parent==null) group=LinkManager.getInstance().createRootGroup(name);
						else group=parent.getLinkGroup(true).createSubGroup(name);
					}
					group.setName(name);
				}

				@Override
				public void handleError(Throwable throwable, boolean rollback)
				{
					GuiUtils.handleThrowable(LinkGroupDetailsView.this, throwable);
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
