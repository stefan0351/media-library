package com.kiwisoft.media.person;

import java.util.List;
import java.util.Collections;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.media.show.Production;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.medium.Song;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.actions.ComplexAction;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.utils.CollectionPropertyChangeAdapter;
import com.kiwisoft.utils.CollectionPropertyChangeEvent;

public class ProductionCreditsView extends CreditsView
{
	private Production production;

	public ProductionCreditsView(Production production)
	{
		this.production=production;
	}

	@Override
	public String getTitle()
	{
		return production.getProductionTitle()+" - Credits";
	}

	@Override
	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		JTabbedPane tabs=new JTabbedPane();
		if (!(production instanceof Song)) tabs.addTab("Cast", createCastPane(frame));
		if (!(production instanceof Show)) tabs.addTab("Credits", createCreditsPane(frame));

		getModelListenerList().installPropertyChangeListener(production, new CollectionChangeObserver());

		return tabs;
	}

	@Override
	protected String[] getCastTableColumns()
	{
		return new String[]{"type", "character", "actor", "voice"};
	}

	@Override
	protected DefaultTableConfiguration getCastTableConfiguration()
	{
		return new DefaultTableConfiguration("production.cast", ProductionCreditsView.class, "cast");
	}

	@Override
	protected DefaultTableConfiguration getCreditsTableConfiguration()
	{
		return new DefaultTableConfiguration("production.credits", ProductionCreditsView.class, "credits");
	}

	@Override
	protected List<ContextAction> getCastToolBarActions(ApplicationFrame frame)
	{
		List<ContextAction> actions=super.getCastToolBarActions(frame);
		actions.add(createNewCastAction());
		actions.add(new DeleteCastAction(production, frame));
		return actions;
	}

	@Override
	protected List<ContextAction> getCastContextActions(ApplicationFrame frame)
	{
		List<ContextAction> actions=super.getCastContextActions(frame);
		actions.add(null);
		actions.add(createNewCastAction());
		actions.add(new DeleteCastAction(production, frame));
		return actions;
	}

	@Override
	protected String[] getCreditTableColumns()
	{
		return new String[]{"type", "subType", "person"};
	}

	@Override
	protected List<ContextAction> getCreditsContextActions(ApplicationFrame frame)
	{
		List<ContextAction> actions=super.getCreditsContextActions(frame);
		actions.add(null);
		actions.add(new NewCreditAction(production));
		actions.add(new DeleteCreditsAction(production, frame));
		return actions;
	}

	@Override
	protected List<ContextAction> getCreditsToolBarActions(ApplicationFrame frame)
	{
		List<ContextAction> actions=super.getCreditsToolBarActions(frame);
		actions.add(new NewCreditAction(production));
		actions.add(new DeleteCreditsAction(production, frame));
		return actions;
	}

	private ComplexAction createNewCastAction()
	{
		ComplexAction newAction=new ComplexAction("New", Icons.getIcon("add"));
		for (CreditType creditType : production.getSupportedCastTypes())
		{
			newAction.addAction(new NewCastAction(production, creditType));
		}
		newAction.update(Collections.emptyList());
		return newAction;
	}

	@Override
	protected void initializeData()
	{
		super.initializeData();
		SortableTableModel<CastMember> castTableModel=getCastTableController().getModel();
		for (CastMember castMember : production.getCastMembers()) castTableModel.addRow(new CastTableRow(castMember));
		castTableModel.sort();
		if (getCreditTableController()!=null)
		{
			SortableTableModel<Credit> creditTableModel=getCreditTableController().getModel();
			for (Credit credit : production.getCredits()) creditTableModel.addRow(new CreditTableRow(credit));
			creditTableModel.sort();
		}
	}

	private class CollectionChangeObserver extends CollectionPropertyChangeAdapter
	{
		@Override
		public void collectionChange(CollectionPropertyChangeEvent event)
		{
			if (Production.CAST_MEMBERS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionPropertyChangeEvent.ADDED:
						CastMember newCast=(CastMember)event.getElement();
						getCastTableController().getModel().addRow(new CastTableRow(newCast));
						getCastTableController().getModel().sort();
						break;
					case CollectionPropertyChangeEvent.REMOVED:
						CastMember removedCast=(CastMember)event.getElement();
						int index=getCastTableController().getModel().indexOf(removedCast);
						if (index>=0) getCastTableController().getModel().removeRowAt(index);
						break;
				}
			}
		}
	}

	@Override
	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getTitle(), ProductionCreditsView.class);
		bookmark.setParameter("production.class", production.getClass().getName());
		bookmark.setParameter("production.id", String.valueOf(production.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		try
		{
			Class productionClass=Class.forName(bookmark.getParameter("production.class"));
			//noinspection unchecked
			Production production=(Production)DBLoader.getInstance().load(productionClass, new Long(bookmark.getParameter("production.id")));
			frame.setCurrentView(new ProductionCreditsView(production));
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}