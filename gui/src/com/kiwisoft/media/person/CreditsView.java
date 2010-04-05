package com.kiwisoft.media.person;

import java.util.List;
import java.util.ArrayList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;

import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.media.show.Production;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.medium.Song;

/**
 * @author Stefan Stiller
 */
public abstract class CreditsView extends ViewPanel
{
	private TableController<CastMember> castTableController;
	private TableController<Credit> creditTableController;

	public TableController<CastMember> getCastTableController()
	{
		return castTableController;
	}

	public TableController<Credit> getCreditTableController()
	{
		return creditTableController;
	}

	protected abstract String[] getCastTableColumns();

	protected JComponent createCastPane(final ApplicationFrame frame)
	{
		SortableTableModel<CastMember> castTableModel=new DefaultSortableTableModel<CastMember>(getCastTableColumns());

		castTableController=new TableController<CastMember>(castTableModel, getCastTableConfiguration())
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				return getCastToolBarActions(frame);
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				return getCastContextActions(frame);
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new CastDetailsAction();
			}
		};
		return castTableController.getComponent();
	}

	protected abstract DefaultTableConfiguration getCastTableConfiguration();

	protected abstract String[] getCreditTableColumns();

	protected JComponent createCreditsPane(final ApplicationFrame frame)
	{
		SortableTableModel<Credit> creditsTableModel=new DefaultSortableTableModel<Credit>(getCreditTableColumns());

		creditTableController=new TableController<Credit>(creditsTableModel, getCreditsTableConfiguration())
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				return getCreditsToolBarActions(frame);
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				return getCreditsContextActions(frame);
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new CreditDetailsAction();
			}
		};
		return creditTableController.getComponent();
	}

	protected abstract DefaultTableConfiguration getCreditsTableConfiguration();

	protected List<ContextAction> getCreditsContextActions(ApplicationFrame frame)
	{
		List<ContextAction> actions=new ArrayList<ContextAction>();
		actions.add(new CreditDetailsAction());
		return actions;
	}

	protected List<ContextAction> getCreditsToolBarActions(ApplicationFrame frame)
	{
		List<ContextAction> actions=new ArrayList<ContextAction>();
		actions.add(new CreditDetailsAction());
		return actions;
	}

	protected List<ContextAction> getCastContextActions(ApplicationFrame frame)
	{
		List<ContextAction> actions=new ArrayList<ContextAction>();
		actions.add(new CastDetailsAction());
		return actions;
	}

	protected List<ContextAction> getCastToolBarActions(ApplicationFrame frame)
	{
		List<ContextAction> actions=new ArrayList<ContextAction>();
		actions.add(new CastDetailsAction());
		return actions;
	}

	@Override
	protected void installComponentListeners()
	{
		castTableController.installListeners();
		if (creditTableController!=null) creditTableController.installListeners();
		super.installComponentListeners();
	}

	@Override
	protected void removeComponentListeners()
	{
		castTableController.removeListeners();
		if (creditTableController!=null) creditTableController.removeListeners();
		super.removeComponentListeners();
	}

	@Override
	public void dispose()
	{
		castTableController.dispose();
		if (creditTableController!=null) creditTableController.dispose();
		super.dispose();
	}

	@Override
	public boolean isBookmarkable()
	{
		return true;
	}

	private static String getProductionType(Production production)
	{
		if (production instanceof Movie) return "Movie";
		if (production instanceof Show) return "Show";
		if (production instanceof Episode) return "Episode";
		if (production instanceof Song) return "Song";
		return null;
	}

	protected static class CastTableRow extends SortableTableRow<CastMember> implements PropertyChangeListener
	{
		public CastTableRow(CastMember cast)
		{
			super(cast);
		}

		@Override
		public void installListener()
		{
			getUserObject().addPropertyChangeListener(this);
		}

		@Override
		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		@Override
		public int getSortPriority()
		{
			CreditType creditType=getUserObject().getCreditType();
			if (creditType==CreditType.MAIN_CAST) return 1;
			if (creditType==CreditType.RECURRING_CAST) return 2;
			if (creditType==CreditType.GUEST_CAST) return 3;
			return 4;
		}

		@Override
		public Object getDisplayValue(int column, String property)
		{
			CastMember cast=getUserObject();
			if ("type".equals(property)) return cast.getCreditType();
			if ("character".equals(property)) return cast.getCharacterName();
			if ("actor".equals(property)) return cast.getActor();
			if ("voice".equals(property)) return cast.getVoice();
			if ("production".equals(property)) 
			{
				Production production=cast.getProduction();
				return production!=null ? production.getProductionTitle() : null;
			}
			if ("productionType".equals(property)) return getProductionType(cast.getProduction());
			return null;
		}
	}

	protected static class CreditTableRow extends SortableTableRow<Credit> implements PropertyChangeListener
	{
		public CreditTableRow(Credit credit)
		{
			super(credit);
		}

		@Override
		public void installListener()
		{
			getUserObject().addPropertyChangeListener(this);
		}

		@Override
		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		@Override
		public Object getDisplayValue(int column, String property)
		{
			Credit credit=getUserObject();
			if ("type".equals(property)) return credit.getCreditType();
			if ("subType".equals(property)) return credit.getSubType();
			if ("person".equals(property)) return credit.getPerson();
			if ("production".equals(property))
			{
				Production production=credit.getProduction();
				return production!=null ? production.getProductionTitle() : null;
			}
			if ("productionType".equals(property)) return getProductionType(credit.getProduction());
			return null;
		}
	}
}
