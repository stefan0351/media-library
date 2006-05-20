package com.kiwisoft.media.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.*;
import java.net.URL;
import javax.swing.*;

import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.media.Link;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.*;
import com.kiwisoft.utils.db.*;
import com.kiwisoft.utils.gui.table.DynamicTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.TableConfiguration;

public class LinksView extends ViewPanel
{
	private Show show;

	// Dates Panel
	private DynamicTable tblLinks;
	private LinksTableModel tmLinks;
	private DoubleClickListener doubleClickListener;
	private CollectionChangeObserver collectionObserver;
	private JScrollPane scrlLinks;

	public LinksView(Show show)
	{
		this.show=show;
	}

	public String getName()
	{
		return show.getName()+" - Links";
	}

	public JComponent createContentPanel()
	{
		tmLinks=new LinksTableModel();
		createTableData();

		tblLinks=new DynamicTable(tmLinks);
		tblLinks.setPreferredScrollableViewportSize(new Dimension(200, 200));
		tblLinks.initializeColumns(new TableConfiguration(Configurator.getInstance(), MediaManagerFrame.class, "table.links"));
		tblLinks.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK), "new link");
		tblLinks.getActionMap().put("new link", new NewLinkAction());

		scrlLinks=new JScrollPane(tblLinks);
		return scrlLinks;
	}

	private void createTableData()
	{
		collectionObserver=new CollectionChangeObserver();
		for (Link link : show.getLinks()) tmLinks.addRow(new Row(link));
		tmLinks.sort();
		show.addCollectionChangeListener(collectionObserver);
	}

	protected void installComponentListener()
	{
		doubleClickListener=new DoubleClickListener();
		tblLinks.addMouseListener(doubleClickListener);
		scrlLinks.addMouseListener(doubleClickListener);
		super.installComponentListener();
	}

	protected void removeComponentListeners()
	{
		tblLinks.removeMouseListener(doubleClickListener);
		scrlLinks.removeMouseListener(doubleClickListener);
		super.removeComponentListeners();
	}

	public void dispose()
	{
		show.removeCollectionListener(collectionObserver);
		tmLinks.clear();
		super.dispose();
	}

	private class CollectionChangeObserver implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (Show.LINKS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Link newLink=(Link)event.getElement();
						Row row=new Row(newLink);
						int newIndex=tmLinks.addRow(row);
						tmLinks.sort();
						tblLinks.getSelectionModel().setSelectionInterval(newIndex, newIndex);
						tblLinks.scrollRectToVisible(tblLinks.getCellRect(newIndex, 0, false));
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tmLinks.indexOf(event.getElement());
						if (index>=0) tmLinks.removeRowAt(index);
						break;
				}
			}
		}
	}

	private class DoubleClickListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
			{
				int rowIndex=tblLinks.rowAtPoint(e.getPoint());
				if (rowIndex>=0)
				{
					Row row=tmLinks.getRow(rowIndex);
					if (row!=null) LinkDetailsView.create(row.getUserObject());
				}
				e.consume();
			}
			if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
			{
				int[] rows=tblLinks.getSelectedRows();
				Set<Link> links=new LinkedHashSet<Link>();
				for (int row : rows) links.add(tmLinks.getRow(row).getUserObject());
				JPopupMenu popupMenu=new JPopupMenu();
				popupMenu.add(new OpenLinkAction(links));
				popupMenu.addSeparator();
				popupMenu.add(new NewLinkAction());
				popupMenu.add(new DeleteLinkAction(links));
				popupMenu.show(tblLinks, e.getX(), e.getY());
				e.consume();
			}
			super.mouseClicked(e);
		}
	}

	private static class LinksTableModel extends SortableTableModel<Row>
	{
		private static final String[] COLUMNS={"name", "language", "url"};

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
		}
	}

	private class Row extends SortableTableRow<Link> implements PropertyChangeListener
	{
		public Row(Link link)
		{
			super(link);
		}

		public void installListener()
		{
			getUserObject().addPropertyChangeListener(this);
		}

		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		public Object getDisplayValue(int column, String property)
		{
			if ("name".equals(property))
				return getUserObject().getName();
			else if ("url".equals(property))
				return getUserObject().getUrl();
			else if ("language".equals(property))
				return getUserObject().getLanguage();
			else
				return "";
		}
	}

	private class NewLinkAction extends AbstractAction
	{
		public NewLinkAction()
		{
			super("Neu");
		}

		public void actionPerformed(ActionEvent e)
		{
			LinkDetailsView.create(show);
		}
	}

	private class OpenLinkAction extends AbstractAction
	{
		private Link link;

		public OpenLinkAction(Collection<Link> links)
		{
			super("Öffnen");
			if (links.size()==1) link=links.iterator().next();
			setEnabled(link!=null);
		}

		public void actionPerformed(ActionEvent e)
		{
			if (link!=null)
			{
				try
				{
					WebUtils.openURL(new URL(link.getUrl()));
				}
				catch (Exception e1)
				{
					JOptionPane.showMessageDialog(LinksView.this, e1.getMessage(), "Ausnahmefehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private class DeleteLinkAction extends AbstractAction
	{
		private Collection<Link> links;

		public DeleteLinkAction(Collection<Link> links)
		{
			super("Löschen");
			this.links=links;
			setEnabled(!links.isEmpty());
		}

		public void actionPerformed(ActionEvent e)
		{
			for (Link link : links)
			{
				if (link.isUsed())
				{
					JOptionPane.showMessageDialog(LinksView.this,
							"Der Link '"+link.getName()+"' kann nicht gelöscht werden.",
							"Meldung",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			int option=JOptionPane.showConfirmDialog(LinksView.this,
					"Links wirklick löschen?",
					"Löschen?",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (option==JOptionPane.YES_OPTION)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					for (Link link : links) show.dropLink(link);
					transaction.close();
				}
				catch (Exception e1)
				{
					try
					{
						if (transaction!=null) transaction.rollback();
					}
					catch (SQLException e2)
					{
						e2.printStackTrace();
					}
					e1.printStackTrace();
					JOptionPane.showMessageDialog(LinksView.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
		Bookmark bookmark=new Bookmark(getName(), LinksView.class);
		bookmark.setParameter("show", String.valueOf(show.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Long id=new Long(bookmark.getParameter("show"));
		Show show=ShowManager.getInstance().getShow(id);
		frame.setCurrentView(new LinksView(show), true);
	}
}