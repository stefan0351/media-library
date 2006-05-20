/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.*;

import com.kiwisoft.media.Person;
import com.kiwisoft.media.PersonManager;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.table.DynamicTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.TableConfiguration;
import com.kiwisoft.utils.gui.ApplicationFrame;

public class ActorsView extends ViewPanel
{
	private DynamicTable tblActors;
	private ActorsTableModel tmActors;
	private DoubleClickListener doubleClickListener;
	private PersonListener personListener;

	public ActorsView()
	{
	}

	public String getName()
	{
		return "Darsteller";
	}

	public JComponent createContentPanel()
	{
		tmActors=new ActorsTableModel();
		Iterator it=PersonManager.getInstance().getActors().iterator();
		while (it.hasNext())
		{
			Person person=(Person)it.next();
			tmActors.addRow(new ActorTableRow(person));
		}
		tmActors.sort();
		personListener=new PersonListener();
		PersonManager.getInstance().addCollectionChangeListener(personListener);

		tblActors=new DynamicTable(tmActors);
		tblActors.setPreferredScrollableViewportSize(new Dimension(200, 200));
		tblActors.initializeColumns(new TableConfiguration(Configurator.getInstance(), MediaManagerFrame.class, "table.actors"));

		return new JScrollPane(tblActors);
	}

	protected void installComponentListener()
	{
		doubleClickListener=new DoubleClickListener();
		tblActors.addMouseListener(doubleClickListener);
	}

	protected void removeComponentListeners()
	{
		tblActors.removeMouseListener(doubleClickListener);
	}

	public void dispose()
	{
		PersonManager.getInstance().removeCollectionListener(personListener);
		tmActors.clear();
		super.dispose();
	}

	private class DoubleClickListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
			{
				int rowIndex=tblActors.rowAtPoint(e.getPoint());
				SortableTableRow row=tmActors.getRow(rowIndex);
				if (row!=null) PersonDetailsView.create((Person)row.getUserObject(), true);
				e.consume();
			}
			if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
			{
				int[] rows=tblActors.getSelectedRows();
				Set actors=new HashSet();
				for (int i=0; i<rows.length; i++) actors.add(tmActors.getObject(rows[i]));

				MediaManagerFrame wizard=(MediaManagerFrame)ActorsView.this.getTopLevelAncestor();

				JMenu menuDownload=new JMenu("Download");
				menuDownload.add(new DownloadPNWAction(wizard, actors));
				menuDownload.add(new DownloadTVTVAction(wizard, actors));

				JPopupMenu popupMenu=new JPopupMenu();
				popupMenu.add(new NewActorAction());
				popupMenu.add(new DeleteActorAction(actors));
				popupMenu.addSeparator();
				popupMenu.add(menuDownload);
				popupMenu.show(tblActors, e.getX(), e.getY());
				e.consume();
			}
		}
	}

	private class PersonListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (PersonManager.PERSONS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Person newPerson=(Person)event.getElement();
						if (newPerson.isActor())
						{
							ActorTableRow row=new ActorTableRow(newPerson);
							tmActors.addRow(row);
						}
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tmActors.indexOf(event.getElement());
						if (index>=0) tmActors.removeRowAt(index);
						break;
				}
			}
		}
	}

	private static class ActorsTableModel extends SortableTableModel
	{
		private static final String[] COLUMNS={"name"};

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
		}
	}

	private static class ActorTableRow extends SortableTableRow implements PropertyChangeListener
	{
		public ActorTableRow(Person person)
		{
			super(person);
		}

		public void installListener()
		{
			((Person)getUserObject()).addPropertyChangeListener(this);
		}

		public void removeListener()
		{
			((Person)getUserObject()).removePropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		public Object getDisplayValue(int column, String property)
		{
			switch (column)
			{
				case 0:
					return ((Person)getUserObject()).getName();
			}
			return null;
		}
	}

	private class NewActorAction extends AbstractAction
	{
		public NewActorAction()
		{
			super("Neu");
		}

		public void actionPerformed(ActionEvent e)
		{
			PersonDetailsView.create(null, true);
		}
	}

	public class DeleteActorAction extends AbstractAction
	{
		private Person actor;

		public DeleteActorAction(Set persons)
		{
			super("Löschen");
			if (persons.size()==1) actor=(Person)persons.iterator().next();
			setEnabled(actor!=null);
		}

		public void actionPerformed(ActionEvent event)
		{
			if (actor.isUsed())
			{
				JOptionPane.showMessageDialog(ActorsView.this,
				        "Der Darsteller '"+actor.getName()+"' kann nicht gelöscht werden.",
				        "Meldung",
				        JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			int option=JOptionPane.showConfirmDialog(ActorsView.this,
			        "Den Darsteller '"+actor.getName()+"' wirklick löschen?",
			        "Löschen?",
			        JOptionPane.YES_NO_OPTION,
			        JOptionPane.QUESTION_MESSAGE);
			if (option==JOptionPane.YES_OPTION)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					PersonManager.getInstance().dropPerson(actor);
					transaction.close();
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
							JOptionPane.showMessageDialog(ActorsView.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					e.printStackTrace();
					JOptionPane.showMessageDialog(ActorsView.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		return new Bookmark(getName(), ActorsView.class);
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new ActorsView(), true);
	}
}
