package com.kiwisoft.media.person;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.table.SortableTableModel;
import com.kiwisoft.swing.table.DefaultTableConfiguration;

public class PersonCreditsView extends CreditsView
{
	private Person person;

	public PersonCreditsView(Person person)
	{
		this.person=person;
	}

	public String getTitle()
	{
		return person.getName()+" - Credits";
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		JTabbedPane tabs=new JTabbedPane();
		tabs.addTab("Acting Credits", createCastPane(frame));
		tabs.addTab("Crew Credits", createCreditsPane(frame));
		return tabs;
	}

	protected String[] getCastTableColumns()
	{
		return new String[]{"productionType", "production", "character", "type"};
	}

	protected String[] getCreditTableColumns()
	{
		return new String[]{"productionType", "production", "type", "subType"};
	}

	protected DefaultTableConfiguration getCastTableConfiguration()
	{
		return new DefaultTableConfiguration(PersonCreditsView.class, "cast");
	}

	protected DefaultTableConfiguration getCreditsTableConfiguration()
	{
		return new DefaultTableConfiguration(PersonCreditsView.class, "credits");
	}

	@Override
	protected void initializeData()
	{
		super.initializeData();
		SortableTableModel<CastMember> castTableModel=getCastTableController().getModel();
		for (CastMember castMember : person.getActingCredits()) castTableModel.addRow(new CastTableRow(castMember));
		castTableModel.sort();
		SortableTableModel<Credit> creditTableModel=getCreditTableController().getModel();
		for (Credit credit : person.getCrewCredits()) creditTableModel.addRow(new CreditTableRow(credit));
		creditTableModel.sort();
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getTitle(), PersonCreditsView.class);
		bookmark.setParameter("person.id", String.valueOf(person.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Person person=DBLoader.getInstance().load(Person.class, new Long(bookmark.getParameter("person.id")));
		frame.setCurrentView(new PersonCreditsView(person));
	}
}
