package com.kiwisoft.media.fanfic;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.*;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.kiwisoft.media.MediaTableConfiguration;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.DetailsFrame;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.ObjectTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.xml.XMLWriter;

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

	private DeletePartAction deletePartAction;
	private PartTableListener partTableListener;
	private MoveUpAction moveUpAction;
	private MoveDownAction moveDownAction;
	private FilesAction filesAction;

	// Configurations Panel
	private JTextField tfId;
	private JTextField tfTitle;
	private JTextField tfRating;
	private JTextField tfUrl;
	private JCheckBox cbFinished;
	private JTextPane tfDescription;
	private JTextPane tfSpoiler;
	private LookupField tfPrequel;
	private SortableTable tblFanDoms;
	private ObjectTableModel tmFanDoms;
	private SortableTable tblPairings;
	private ObjectTableModel tmPairings;
	private SortableTable tblAuthors;
	private ObjectTableModel tmAuthors;
	private SortableTable tblParts;
	private FanFicPartsTableModel tmParts;

	private FanFicDetailsView(FanFic fanFic)
	{
		this.fanFic=fanFic;
		createContentPanel();
		initializeData();
		if (fanFic!=null)
			setTitle("FanFic - "+fanFic.getId());
		else
			setTitle("Neues FanFic");
		installListener();
	}

	private FanFicDetailsView(FanFicGroup group)
	{
		this.group=group;
		createContentPanel();
		initializeData();
		setTitle("Neues FanFic");
		installListener();
	}

	private void installListener()
	{
		partTableListener=new PartTableListener();
		tblParts.getSelectionModel().addListSelectionListener(partTableListener);
		tmParts.addTableModelListener(partTableListener);
	}

	private void initializeData()
	{
		if (fanFic!=null)
		{
			tfTitle.setText(fanFic.getTitle());
			tfId.setText(fanFic.getId().toString());
			tfUrl.setText(fanFic.getUrl());
			tfRating.setText(fanFic.getRating());
			cbFinished.setSelected(fanFic.isFinished());
			tfDescription.setText(fanFic.getDescription());
			tfSpoiler.setText(fanFic.getSpoiler());
			tfPrequel.setValue(fanFic.getPrequel());
			for (Iterator it=fanFic.getParts().iterator(); it.hasNext();)
			{
				String source=((FanFicPart)it.next()).getSource();
				String path=new File(Configurator.getInstance().getString("path.fanfics"), source).getAbsolutePath();
				tmParts.addPart(path);
			}
			for (Iterator it=fanFic.getPairings().iterator(); it.hasNext();) tmPairings.addObject(it.next());
			for (Iterator it=fanFic.getFanDoms().iterator(); it.hasNext();) tmFanDoms.addObject(it.next());
			for (Iterator it=fanFic.getAuthors().iterator(); it.hasNext();) tmAuthors.addObject(it.next());
		}
		else
		{
			if (group instanceof Pairing) tmPairings.addObject(group);
			else if (group instanceof FanDom) tmFanDoms.addObject(group);
			else if (group instanceof Author) tmAuthors.addObject(group);
		}
		tmPairings.addSortColumn(0, false);
		tmFanDoms.addSortColumn(0, false);
		tmAuthors.addSortColumn(0, false);
	}

	public boolean apply()
	{
		String title=tfTitle.getText();
		if (StringUtils.isEmpty(title))
		{
			JOptionPane.showMessageDialog(this, "Titel fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfTitle.requestFocus();
			return false;
		}
		String url=tfUrl.getText();
		String rating=tfRating.getText();
		boolean finished=cbFinished.isSelected();
		String spoiler=tfSpoiler.getText();
		String description=tfDescription.getText();
		FanFic prequel=(FanFic)tfPrequel.getValue();
		List sources=new ArrayList();
		for (int i=0; i<tmParts.getRowCount(); i++)
		{
			String source=(String)tmParts.getObject(i);
			if (StringUtils.isEmpty(source))
			{
				JOptionPane.showMessageDialog(this, "Quelle fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
				tblParts.getSelectionModel().setSelectionInterval(i, i);
				tblParts.requestFocus();
				return false;
			}
			try
			{
				source=FileUtils.getRelativePath(Configurator.getInstance().getString("path.fanfics"), source);
				source=StringUtils.replaceStrings(source, "\\", "/");
				sources.add(source);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(this, e.getMessage(), "IOException", JOptionPane.ERROR_MESSAGE);
				tblParts.getSelectionModel().setSelectionInterval(i, i);
				tblParts.requestFocus();
				return false;
			}
		}
		Set authors=new HashSet(tmAuthors.getObjects());
		if (authors.isEmpty())
		{
			JOptionPane.showMessageDialog(this, "Kein Autor eingegeben.", "Error", JOptionPane.ERROR_MESSAGE);
			tblAuthors.requestFocus();
			return false;
		}
		Set fanDoms=new HashSet(tmFanDoms.getObjects());
		if (fanDoms.isEmpty())
		{
			JOptionPane.showMessageDialog(this, "Keine Domäne eingegeben.", "Error", JOptionPane.ERROR_MESSAGE);
			tblFanDoms.requestFocus();
			return false;
		}
		Set pairings=new HashSet(tmPairings.getObjects());
		if (pairings.isEmpty())
		{
			JOptionPane.showMessageDialog(this, "Kein Paar eingegeben.", "Error", JOptionPane.ERROR_MESSAGE);
			tblPairings.requestFocus();
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
			Iterator itOldParts=new ArrayList(fanFic.getParts().elements()).iterator();
			Iterator itNewSources=sources.iterator();
			while (itOldParts.hasNext() || itNewSources.hasNext())
			{
				FanFicPart oldPart=(FanFicPart)(itOldParts.hasNext() ? itOldParts.next() : null);
				String newSource=(String)(itNewSources.hasNext() ? itNewSources.next() : null);
				if (oldPart!=null)
				{
					if (newSource!=null) oldPart.setSource(newSource);
					else fanFic.dropPart(oldPart);
				}
				else if (newSource!=null)
				{
					FanFicPart part=fanFic.createPart();
					part.setSource(newSource);
				}
			}
			transaction.close();
			transaction=null;
			fanFic.notifyChanged();
			filesAction.validate();
			tfId.setText(fanFic.getId().toString());
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
		tfId=new JTextField(5);
		tfId.setHorizontalAlignment(JTextField.TRAILING);
		tfId.setEditable(false);
		tfTitle=new JTextField();
		tfDescription=new JTextPane();
		tfSpoiler=new JTextPane();
		cbFinished=new JCheckBox();
		tfRating=new JTextField();
		tfUrl=new JTextField();
		tfPrequel=new LookupField(new FanFicLookup());

		tmPairings=new ObjectTableModel("pairings", Pairing.class, null);
		tblPairings=new SortableTable(tmPairings);
		tblPairings.initializeColumns(new MediaTableConfiguration("table.fanfic.detail"));

		tmFanDoms=new ObjectTableModel("fandoms", FanDom.class, null);
		tblFanDoms=new SortableTable(tmFanDoms);
		tblFanDoms.initializeColumns(new MediaTableConfiguration("table.fanfic.detail"));

		tmAuthors=new ObjectTableModel("authors", Author.class, null);
		tblAuthors=new SortableTable(tmAuthors);
		tblAuthors.initializeColumns(new MediaTableConfiguration("table.fanfic.detail"));

		createPartsPanel();

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(600, 500));
		int row=0;
		add(new JLabel("Id:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
													  GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfId, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0,
										 GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Titel:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														 GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfTitle, new GridBagConstraints(1, row, 5, 1, 1.0, 0.0,
											GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Parts:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														 GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(createPartsPanel(), new GridBagConstraints(1, row, 5, 1, 1.0, 0.0,
													   GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Autoren:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(tblAuthors), new GridBagConstraints(1, row, 1, 1, 0.5, 0.3,
																GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Domänen:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
														   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
		add(new JScrollPane(tblFanDoms), new GridBagConstraints(3, row, 1, 1, 0.5, 0.3,
																GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Paare:"), new GridBagConstraints(4, row, 1, 1, 0.0, 0.0,
														 GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(new JScrollPane(tblPairings), new GridBagConstraints(5, row, 1, 1, 0.5, 0.3,
																 GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Rating:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														  GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfRating, new GridBagConstraints(1, row, 1, 1, 0.3, 0.0,
											 GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Fertig:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
														  GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(cbFinished, new GridBagConstraints(3, row, 1, 1, 0.5, 0.0,
											   GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Beschreibung:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
																GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(tfDescription), new GridBagConstraints(1, row, 5, 1, 1.0, 0.3,
																   GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Spoiler:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(new JScrollPane(tfSpoiler), new GridBagConstraints(1, row, 5, 1, 1.0, 0.3,
															   GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Fortsetzung zu:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
																  GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfPrequel, new GridBagConstraints(1, row, 5, 1, 1.0, 0.0,
											  GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("URL:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
													   GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfUrl, new GridBagConstraints(1, row, 5, 1, 1.0, 0.0,
										  GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
	}

	private JPanel createPartsPanel()
	{
		tmParts=new FanFicPartsTableModel();
		tblParts=new SortableTable(tmParts);
		tblParts.initializeColumns(new MediaTableConfiguration("table.fanfic.detail"));

		deletePartAction=new DeletePartAction();
		moveUpAction=new MoveUpAction();
		moveDownAction=new MoveDownAction();
		filesAction=new FilesAction();

		JPanel pnlParts=new JPanel(new GridBagLayout());
		pnlParts.add(new JScrollPane(tblParts), new GridBagConstraints(0, 0, 1, 5, 1.0, 0.3,
																	   GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		int row=0;
		pnlParts.add(new JButton(moveUpAction), new GridBagConstraints(1, row++, 1, 1, 0.0, 0.0,
																	   GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 2, 0, 0), 0, 0));
		pnlParts.add(new JButton(moveDownAction), new GridBagConstraints(1, row++, 1, 1, 0.0, 0.0,
																		 GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 0, 0), 0, 0));
		pnlParts.add(new JButton(new AddAction()), new GridBagConstraints(1, row++, 1, 1, 0.0, 0.0,
																		  GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 0, 0), 0, 0));
		pnlParts.add(new JButton(filesAction), new GridBagConstraints(1, row++, 1, 1, 0.0, 0.0,
																	  GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 0, 0), 0, 0));
		pnlParts.add(new JButton(deletePartAction), new GridBagConstraints(1, row++, 1, 1, 0.0, 0.0,
																		   GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 0, 0), 0, 0));
		pnlParts.add(Box.createGlue(), new GridBagConstraints(1, row, 1, 1, 0.0, 1.0,
															  GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 0, 0), 0, 0));

		return pnlParts;
	}

	public JComponent getDefaultFocusComponent()
	{
		return tfTitle;
	}

	public void dispose()
	{
		super.dispose();
		tblParts.getSelectionModel().addListSelectionListener(partTableListener);
		tmParts.removeTableModelListener(partTableListener);
	}

	private class PartTableListener implements ListSelectionListener, TableModelListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting()) validateActions();
		}

		public void tableChanged(TableModelEvent e)
		{
			if (e.getType()==TableModelEvent.DELETE || e.getType()==TableModelEvent.INSERT) validateActions();
		}
	}

	private void validateActions()
	{
		deletePartAction.validate();
		moveUpAction.validate();
		moveDownAction.validate();
	}

	private class DeletePartAction extends AbstractAction
	{
		public DeletePartAction()
		{
			super("Löschen");
			setEnabled(false);
		}

		public void validate()
		{
			setEnabled(!tblParts.getSelectionModel().isSelectionEmpty());
		}

		public void actionPerformed(ActionEvent e)
		{
			int[] indices=tblParts.getSelectedRows();
			Set rows=new HashSet();
			for (int i=0; i<indices.length; i++)
			{
				rows.add(tmParts.getRow(indices[i]));
			}
			for (Iterator it=rows.iterator(); it.hasNext();)
			{
				SortableTableRow row=(SortableTableRow)it.next();
				tmParts.removeRow(row);
			}
		}
	}

	private class MoveUpAction extends AbstractAction
	{
		public MoveUpAction()
		{
			super("Nach Open");
			setEnabled(false);
		}

		public void validate()
		{
			setEnabled(isValid());
		}

		private boolean isValid()
		{
			ListSelectionModel selectionModel=tblParts.getSelectionModel();
			return !selectionModel.isSelectionEmpty() && selectionModel.getMinSelectionIndex()>0;
		}

		public void actionPerformed(ActionEvent e)
		{
			if (isValid())
			{
				int[] indices=tblParts.getSelectedRows();
				List rows=new ArrayList();
				for (int i=0; i<indices.length; i++)
				{
					rows.add(tmParts.getRow(indices[i]));
				}
				for (Iterator it=rows.iterator(); it.hasNext();)
				{
					SortableTableRow row=(SortableTableRow)it.next();
					int index=tmParts.indexOfRow(row);
					if (index>0)
					{
						tmParts.removeRow(row);
						tmParts.addRowAt(row, index-1);
					}
				}
				for (Iterator it=rows.iterator(); it.hasNext();)
				{
					SortableTableRow row=(SortableTableRow)it.next();
					int index=tmParts.indexOfRow(row);
					tblParts.getSelectionModel().addSelectionInterval(index, index);
				}
			}
		}
	}

	private class MoveDownAction extends AbstractAction
	{
		public MoveDownAction()
		{
			super("Nach Unten");
			setEnabled(false);
		}

		public void validate()
		{
			setEnabled(isValid());
		}

		private boolean isValid()
		{
			ListSelectionModel selectionModel=tblParts.getSelectionModel();
			return !selectionModel.isSelectionEmpty() && selectionModel.getMaxSelectionIndex()<tmParts.getRowCount()-1;
		}

		public void actionPerformed(ActionEvent e)
		{
			if (isValid())
			{
				int[] indices=tblParts.getSelectedRows();
				List rows=new ArrayList();
				for (int i=indices.length-1; i>=0; i--)
				{
					rows.add(tmParts.getRow(indices[i]));
				}
				for (Iterator it=rows.iterator(); it.hasNext();)
				{
					SortableTableRow row=(SortableTableRow)it.next();
					int index=tmParts.indexOfRow(row);
					if (index+1<tmParts.getRowCount())
					{
						tmParts.removeRow(row);
						tmParts.addRowAt(row, index+1);
					}
				}
				for (Iterator it=rows.iterator(); it.hasNext();)
				{
					SortableTableRow row=(SortableTableRow)it.next();
					int index=tmParts.indexOfRow(row);
					tblParts.getSelectionModel().addSelectionInterval(index, index);
				}
			}
		}
	}

	private class AddAction extends AbstractAction
	{
		public AddAction()
		{
			super("Hinzufügen");
		}

		public void actionPerformed(ActionEvent e)
		{
			int rows=tmParts.getRowCount();
			if (rows>0)
			{
				String lastPath=(String)tmParts.getObject(rows-1);
				File file=new File(lastPath);
				tmParts.addPart(StringUtils.increase(file.getPath()));
			}
			else
			{
				Author author;
				switch (tmAuthors.getRowCount())
				{
					case 1:
						JOptionPane.showMessageDialog(FanFicDetailsView.this, "Autor fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
						return;
					case 2:
						author=(Author)tmAuthors.getObject(0);
						break;
					default:
						int row=tblAuthors.getSelectedRow();
						if (row<0)
						{
							JOptionPane.showMessageDialog(FanFicDetailsView.this, "Wähle einen Autor!", "Fehler", JOptionPane.ERROR_MESSAGE);
							return;
						}
						author=(Author)tmAuthors.getObject(row);
				}
				int option=JOptionPane.showOptionDialog(FanFicDetailsView.this, "Erzeuge mehrteiliges FanFic?", "Frage",
														JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (option==JOptionPane.NO_OPTION)
				{
					StringBuffer path=buildFileName(author, tfTitle.getText());
					path.append(".xp");
					tmParts.addPart(path.toString());
				}
				else if (option==JOptionPane.YES_OPTION)
				{
					StringBuffer path=buildFileName(author, tfTitle.getText());
					path.append(File.separator);
					path.append("01.xp");
					tmParts.addPart(path.toString());
				}
			}
		}

		private StringBuffer buildFileName(Author author, String title)
		{
			title=title.toLowerCase();
			StringBuffer path=new StringBuffer(Configurator.getInstance().getString("path.fanfics"));
			path.append(File.separator);
			path.append(author.getPath());
			path.append(File.separator);
			for (StringTokenizer tokens=new StringTokenizer(title, " ,-\"?!"); tokens.hasMoreTokens();)
			{
				path.append(tokens.nextToken());
				if (tokens.hasMoreTokens()) path.append("_");
			}
			return path;
		}
	}

	private class FilesAction extends AbstractAction
	{
		public FilesAction()
		{
			super("Dateien");
			setEnabled(fanFic!=null);
		}

		public void validate()
		{
			setEnabled(fanFic!=null);
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				StringBuilder buffer=new StringBuilder();
				int errors=0;
				for (int i=0; i<tmParts.getRowCount(); i++)
				{
					String fileName=(String)tmParts.getObject(i);
					if (!StringUtils.isEmpty(fileName))
					{
						File file=new File(fileName);
						if (!file.exists() && !file.isDirectory())
						{
							file.getParentFile().mkdirs();
							if (fileName.endsWith(".xp"))
							{
								XMLWriter xmlWriter=new XMLWriter(new FileWriter(file), null);
								xmlWriter.start();
								xmlWriter.startElement("fanfic");
								xmlWriter.setAttribute("id", fanFic.getId().toString());
								xmlWriter.startElement("chapter");
								xmlWriter.newLine();
								xmlWriter.closeElement("chapter");
								xmlWriter.closeElement("fanfic");
								xmlWriter.close();
							}
						}
						else if (file.exists() && !file.isDirectory())
						{
							BufferedReader reader=new BufferedReader(new FileReader(file));
							String line;
							int lineNumber=1;
							while ((line=reader.readLine())!=null)
							{
								for (int c=0; c<line.length() && errors<25; c++)
								{
									char ch=line.charAt(c);
									if (ch>=128)
									{
										if (buffer.length()>0) buffer.append("<br>");
										buffer.append(file);
										buffer.append(":");
										buffer.append(lineNumber);
										buffer.append(":");
										buffer.append(c+1);
										buffer.append(": Invalid Character '");
										buffer.append(ch);
										buffer.append("'.");
										errors++;
									}
								}
								lineNumber++;
							}
							reader.close();
						}
					}
				}
				if (buffer.length()>0)
				{
					JOptionPane.showMessageDialog(FanFicDetailsView.this, "<html>"+buffer+"</html>", "Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
			catch (Exception e1)
			{
				JOptionPane.showMessageDialog(FanFicDetailsView.this, e1.getLocalizedMessage(), "Ausnahmefehler", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
