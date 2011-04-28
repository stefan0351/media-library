package com.kiwisoft.media.fanfic;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.WEST;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;

public class FanFicDetailsView extends DetailsView
{
	public static void create(FanFic fanFic)
	{
		new DetailsFrame(new FanFicDetailsView(fanFic)).show();
	}

	public static void create(FanFicGroup group)
	{
		new DetailsFrame(new FanFicDetailsView(group)).show();
	}

	private FanFic fanFic;
	private FanFicGroup group;

	// Configurations Panel
	private JTextField idField;
	private JTextField titleField;
	private JTextField ratingField;
	private JTextField urlField;
	private JCheckBox finishedField;
	private JTextPane descriptionField;
	private JTextPane spoilerField;
	private LookupField<FanFic> prequelField;
	private SortableTable fandomsTable;
	private ObjectTableModel<FanDom> fandomsModel;
	private SortableTable pairingsTable;
	private ObjectTableModel<Pairing> pairingsModel;
	private SortableTable authorsTable;
	private ObjectTableModel<Author> authorsModel;

	private FanFicDetailsView(FanFic fanFic)
	{
		this.fanFic=fanFic;
		createContentPanel();
		initializeData();
		if (fanFic!=null)
			setTitle("FanFic - "+fanFic.getId());
		else
			setTitle("New FanFic");
	}

	private FanFicDetailsView(FanFicGroup group)
	{
		this.group=group;
		createContentPanel();
		initializeData();
		setTitle("New FanFic");
	}

	private void initializeData()
	{
		if (fanFic!=null)
		{
			titleField.setText(fanFic.getTitle());
			idField.setText(fanFic.getId().toString());
			urlField.setText(fanFic.getUrl());
			ratingField.setText(fanFic.getRating());
			finishedField.setSelected(fanFic.isFinished());
			descriptionField.setText(fanFic.getDescription());
			spoilerField.setText(fanFic.getSpoiler());
			prequelField.setValue(fanFic.getPrequel());
			for (Pairing pairing : fanFic.getPairings()) pairingsModel.addObject(pairing);
			for (FanDom fanDom : fanFic.getFanDoms()) fandomsModel.addObject(fanDom);
			for (Author author : fanFic.getAuthors()) authorsModel.addObject(author);
		}
		else
		{
			if (group instanceof Pairing) pairingsModel.addObject((Pairing)group);
			else if (group instanceof FanDom) fandomsModel.addObject((FanDom)group);
			else if (group instanceof Author) authorsModel.addObject((Author)group);
		}
		pairingsModel.addSortColumn(0, false);
		fandomsModel.addSortColumn(0, false);
		authorsModel.addSortColumn(0, false);
	}

	@Override
	public boolean apply()
	{
		String title=titleField.getText();
		if (StringUtils.isEmpty(title))
		{
			JOptionPane.showMessageDialog(this, "Title is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			titleField.requestFocus();
			return false;
		}
		String url=urlField.getText();
		String rating=ratingField.getText();
		boolean finished=finishedField.isSelected();
		String spoiler=spoilerField.getText();
		String description=descriptionField.getText();
		FanFic prequel=prequelField.getValue();
		Set<Author> authors=new HashSet<Author>(authorsModel.getObjects());
		Set<FanDom> fanDoms=new HashSet<FanDom>(fandomsModel.getObjects());
		Set<Pairing> pairings=new HashSet<Pairing>(pairingsModel.getObjects());
		if (authors.isEmpty() && fanDoms.isEmpty() && pairings.isEmpty())
		{
			JOptionPane.showMessageDialog(this, "No category (author, domain or pairing) selected.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (fanFic==null) fanFic=FanFicManager.getInstance().createFanFic();
			fanFic.setTitle(title);
			fanFic.setUrl(url);
			fanFic.setRating(rating);
			fanFic.setDescription(description);
			fanFic.setSpoiler(spoiler);
			fanFic.setPrequel(prequel);
			if (prequel!=null) prequel.setSequel(fanFic);
			fanFic.setFinished(finished);
			fanFic.setPairings(pairings);
			fanFic.setFanDoms(fanDoms);
			fanFic.setAuthors(authors);
			transaction.close();
			transaction=null;
			fanFic.notifyChanged();
			idField.setText(fanFic.getId().toString());
			return true;
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			try
			{
				if (transaction!=null) transaction.rollback();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			JOptionPane.showMessageDialog(this, t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	protected void createContentPanel()
	{
		idField=new JTextField(5);
		idField.setHorizontalAlignment(JTextField.TRAILING);
		idField.setEditable(false);
		titleField=new JTextField();
		descriptionField=new JTextPane();
		spoilerField=new JTextPane();
		finishedField=new JCheckBox();
		ratingField=new JTextField();
		urlField=new JTextField();
		prequelField=new LookupField<FanFic>(new FanFicLookup());

		pairingsModel=new ObjectTableModel<Pairing>("name", Pairing.class, null);
		pairingsTable=new SortableTable(pairingsModel);
		pairingsTable.configure(new DefaultTableConfiguration("fanfic.pairings", FanFicDetailsView.class, "pairings"));

		fandomsModel=new ObjectTableModel<FanDom>("name", FanDom.class, null);
		fandomsTable=new SortableTable(fandomsModel);
		fandomsTable.configure(new DefaultTableConfiguration("fanfic.fandoms", FanFicDetailsView.class, "fandoms"));

		authorsModel=new ObjectTableModel<Author>("name", Author.class, null);
		authorsTable=new SortableTable(authorsModel);
		authorsTable.configure(new DefaultTableConfiguration("fanfic.authors", FanFicDetailsView.class, "authors"));

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(600, 500));
		int row=0;
		add(new JLabel("Id:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
													  WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(idField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,
										 WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Title:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														 WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(titleField, new GridBagConstraints(1, row, 5, 1, 1.0, 0.0,
											WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Authors:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(authorsTable), new GridBagConstraints(1, row, 1, 1, 0.5, 0.3,
																WEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Domains:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
														   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
		add(new JScrollPane(fandomsTable), new GridBagConstraints(3, row, 1, 1, 0.5, 0.3,
																WEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Pairings:"), new GridBagConstraints(4, row, 1, 1, 0.0, 0.0,
															GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(new JScrollPane(pairingsTable), new GridBagConstraints(5, row, 1, 1, 0.5, 0.3,
																 WEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Rating:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														  WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(ratingField, new GridBagConstraints(1, row, 1, 1, 0.3, 0.0,
											 WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Finished:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
															WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(finishedField, new GridBagConstraints(3, row, 1, 1, 0.5, 0.0,
											   WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Summary:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(descriptionField), new GridBagConstraints(1, row, 5, 1, 1.0, 0.3,
																   WEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Spoiler:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(spoilerField), new GridBagConstraints(1, row, 5, 1, 1.0, 0.3,
															   WEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Sequel to:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
															 WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(prequelField, new GridBagConstraints(1, row, 5, 1, 1.0, 0.0,
											  WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("URL:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
													   WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(urlField, new GridBagConstraints(1, row, 5, 1, 1.0, 0.0,
										  WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
	}

	@Override
	public JComponent getDefaultFocusComponent()
	{
		return titleField;
	}
}
