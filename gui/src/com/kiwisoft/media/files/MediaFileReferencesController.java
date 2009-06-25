package com.kiwisoft.media.files;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collection;

import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.actions.ComplexAction;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.table.DefaultSortableTableModel;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.swing.table.TableController;

/**
 * @author Stefan Stiller
 */
public class MediaFileReferencesController extends TableController<IDObject>
{
	public MediaFileReferencesController()
	{
		super(new DefaultSortableTableModel<IDObject>("type", "name"),
			  new DefaultTableConfiguration("mediafiles.references", MediaFileReferencesController.class, "references"));
		setTitle("References");
		setShowBorder(true);
	}

	@Override
	public List<ContextAction> getContextActions()
	{
		return super.getContextActions();
	}

	@Override
	public List<ContextAction> getToolBarActions()
	{
		List<ContextAction> actions=new ArrayList<ContextAction>();
		actions.add(new ComplexAction("Add", Icons.getIcon("add"),
									  new AddShowAction(),
									  new AddEpisodeAction(),
									  new AddMovieAction(),
									  new AddPersonAction()));
		actions.add(new RemoveRowAction());
		return actions;
	}

	public void addReference(IDObject object)
	{
		getModel().addRow(new TableRow(object));
	}

	public void addReferences(Set<? extends IDObject> objects)
	{
		List<TableRow> rows=new ArrayList<TableRow>();
		for (IDObject object : objects)
		{
			rows.add(new TableRow(object));
		}
		getModel().addRows(rows);
		getModel().sort();
	}

	public Collection<IDObject> getReferences()
	{
		return getModel().getObjects();
	}

	private class AddShowAction extends ContextAction
	{
		public AddShowAction()
		{
			super("Show");
		}

		public void actionPerformed(ActionEvent e)
		{
			Show show=SelectShowView.createDialog(GuiUtils.getWindow(getTable()));
			if (show!=null) addReference(show);
		}

	}

	private class AddEpisodeAction extends ContextAction
	{

		public AddEpisodeAction()
		{
			super("Episode");
		}

		public void actionPerformed(ActionEvent e)
		{
			Episode episode=SelectEpisodeView.createDialog(GuiUtils.getWindow(getTable()));
			if (episode!=null) addReference(episode);
		}

	}

	private class AddMovieAction extends ContextAction
	{
		public AddMovieAction()
		{
			super("Movie");
		}

		public void actionPerformed(ActionEvent e)
		{
			Movie movie=SelectMovieView.createDialog(GuiUtils.getWindow(getTable()));
			if (movie!=null) addReference(movie);
		}
	}

	private class AddPersonAction extends ContextAction
	{
		public AddPersonAction()
		{
			super("Person");
		}

		public void actionPerformed(ActionEvent e)
		{
			Person person=SelectPersonView.createDialog(GuiUtils.getWindow(getTable()));
			if (person!=null) addReference(person);
		}

	}

	private static class TableRow extends SortableTableRow<IDObject>
	{
		public TableRow(IDObject reference)
		{
			super(reference);
		}

		@Override
		public String getCellFormat(int column, String property)
		{
			if ("name".equals(property)) return "full";
			return super.getCellFormat(column, property);
		}

		@Override
		public Object getDisplayValue(int column, String property)
		{
			IDObject object=getUserObject();
			if ("type".equals(property))
			{
				if (object instanceof Show) return "Show";
				if (object instanceof Episode) return "Episode";
				if (object instanceof Movie) return "Movie";
				if (object instanceof Person) return "Person";
			}
			else if ("name".equals(property)) return object;
			return "";
		}
	}
}
