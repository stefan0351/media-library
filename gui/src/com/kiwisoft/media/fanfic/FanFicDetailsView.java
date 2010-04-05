package com.kiwisoft.media.fanfic;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.WEST;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.*;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;

import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.utils.xml.XMLWriter;
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
	private TableController<FanFicPart> partsController;

	private FanFicDetailsView(FanFic fanFic)
	{
		this.fanFic=fanFic;
		createContentPanel();
		initializeData();
		if (fanFic!=null)
			setTitle("FanFic - "+fanFic.getId());
		else
			setTitle("New FanFic");
		installListener();
	}

	private FanFicDetailsView(FanFicGroup group)
	{
		this.group=group;
		createContentPanel();
		initializeData();
		setTitle("New FanFic");
		installListener();
	}

	private void installListener()
	{
		partsController.installListeners();
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
			for (Iterator<FanFicPart> it=fanFic.getParts().iterator(); it.hasNext();)
				partsController.getModel().addRow(new FanFicPartTableRow(it.next()));
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
		SortableTableModel<FanFicPart> tmParts=partsController.getModel();
		SortableTable tblParts=partsController.getTable();
		List<FanFicPartTableRow> partRows=new ArrayList<FanFicPartTableRow>();
		for (int i=0; i<tmParts.getRowCount(); i++)
		{
			FanFicPartTableRow partRow=(FanFicPartTableRow) tmParts.getRow(i);
			if (StringUtils.isEmpty(partRow.getSource()))
			{
				JOptionPane.showMessageDialog(this, "Path is missing!", "Error", JOptionPane.ERROR_MESSAGE);
				tblParts.getSelectionModel().setSelectionInterval(i, i);
				tblParts.requestFocus();
				return false;
			}
			partRows.add(partRow);
		}
		Set<Author> authors=new HashSet<Author>(authorsModel.getObjects());
		if (authors.isEmpty())
		{
			JOptionPane.showMessageDialog(this, "No author selected.", "Error", JOptionPane.ERROR_MESSAGE);
			authorsTable.requestFocus();
			return false;
		}
		Set<FanDom> fanDoms=new HashSet<FanDom>(fandomsModel.getObjects());
		if (fanDoms.isEmpty())
		{
			JOptionPane.showMessageDialog(this, "No domain selected.", "Error", JOptionPane.ERROR_MESSAGE);
			fandomsTable.requestFocus();
			return false;
		}
		Set<Pairing> pairings=new HashSet<Pairing>(pairingsModel.getObjects());
		if (pairings.isEmpty())
		{
			JOptionPane.showMessageDialog(this, "No pairing selected.", "Error", JOptionPane.ERROR_MESSAGE);
			pairingsTable.requestFocus();
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
			Iterator<FanFicPart> itOldParts=new ArrayList<FanFicPart>(fanFic.getParts().elements()).iterator();
			Iterator<FanFicPartTableRow> itNewParts=partRows.iterator();
			while (itOldParts.hasNext() || itNewParts.hasNext())
			{
				FanFicPart oldPart=itOldParts.hasNext() ? itOldParts.next() : null;
				FanFicPartTableRow newPartRow=itNewParts.hasNext() ? itNewParts.next() : null;
				if (oldPart!=null)
				{
					if (newPartRow!=null)
					{
						oldPart.setName(newPartRow.getName());
						oldPart.setSource(newPartRow.getSource());
					}
					else fanFic.dropPart(oldPart);
				}
				else if (newPartRow!=null)
				{
					FanFicPart part=fanFic.createPart();
					part.setName(newPartRow.getName());
					part.setSource(newPartRow.getSource());
				}
			}
			transaction.close();
			transaction=null;
			fanFic.notifyChanged();
			partsController.selectionChanged();
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

		DefaultSortableTableModel<FanFicPart> tmParts=new DefaultSortableTableModel<FanFicPart>("name", "path");
		tmParts.setResortable(false);
		partsController=new TableController<FanFicPart>(tmParts, new DefaultTableConfiguration("fanfic.parts", FanFicDetailsView.class, "parts"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new AddPartAction());
				actions.add(new DeletePartAction());
				actions.add(new MovePartUpAction());
				actions.add(new MovePartDownAction());
				actions.add(new FilesAction());
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new AddPartAction());
				actions.add(new DeletePartAction());
				return actions;
			}
		};

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
		add(new JLabel("Parts:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														 GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(partsController.getComponent(), new GridBagConstraints(1, row, 5, 1, 1.0, 0.3,
													   WEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));

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

	@Override
	public void dispose()
	{
		super.dispose();
		partsController.removeListeners();
		partsController.dispose();
	}

	private class DeletePartAction extends SimpleContextAction
	{
		public DeletePartAction()
		{
			super(String.class, "Delete", Icons.getIcon("delete"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			int[] indexes=partsController.getTable().getSelectedRows();
			Set<SortableTableRow<FanFicPart>> rows=new HashSet<SortableTableRow<FanFicPart>>();
			SortableTableModel<FanFicPart> model=partsController.getModel();
			for (int index : indexes) rows.add(model.getRow(index));
			for (SortableTableRow<FanFicPart> row : rows) model.removeRow(row);
		}
	}

	private class MovePartUpAction extends ContextAction
	{
		public MovePartUpAction()
		{
			super("Move Up", Icons.getIcon("move.up"));
		}

		@Override
		public void update(List objects)
		{
			setEnabled(isValid());
		}

		private boolean isValid()
		{
			SortableTable tblParts=partsController.getTable();
			ListSelectionModel selectionModel=tblParts.getSelectionModel();
			return !selectionModel.isSelectionEmpty() && selectionModel.getMinSelectionIndex()>0;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (isValid())
			{
				SortableTable tblParts=partsController.getTable();
				SortableTableModel<FanFicPart> tmParts=partsController.getModel();
				int[] indexes=tblParts.getSelectedRows();
				List<SortableTableRow<FanFicPart>> rows=new ArrayList<SortableTableRow<FanFicPart>>();
				for (int index : indexes) rows.add(tmParts.getRow(index));
				for (SortableTableRow<FanFicPart> row : rows)
				{
					int index=tmParts.indexOfRow(row);
					if (index>0)
					{
						tmParts.removeRow(row);
						tmParts.addRowAt(row, index-1);
					}
				}
				for (SortableTableRow<FanFicPart> row : rows)
				{
					int index=tmParts.indexOfRow(row);
					tblParts.getSelectionModel().addSelectionInterval(index, index);
				}
			}
		}
	}

	private class MovePartDownAction extends ContextAction
	{
		public MovePartDownAction()
		{
			super("Move Down", Icons.getIcon("move.down"));
			setEnabled(false);
		}

		@Override
		public void update(List objects)
		{
			setEnabled(isValid());
		}

		private boolean isValid()
		{
			ListSelectionModel selectionModel=partsController.getTable().getSelectionModel();
			return !selectionModel.isSelectionEmpty() && selectionModel.getMaxSelectionIndex()<partsController.getModel().getRowCount()-1;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (isValid())
			{
				SortableTable tblParts=partsController.getTable();
				SortableTableModel<FanFicPart> tmParts=partsController.getModel();
				int[] indexes=tblParts.getSelectedRows();
				List<SortableTableRow<FanFicPart>> rows=new ArrayList<SortableTableRow<FanFicPart>>();
				for (int i=indexes.length-1; i>=0; i--)
				{
					rows.add(tmParts.getRow(indexes[i]));
				}
				for (SortableTableRow<FanFicPart> row : rows)
				{
					int index=tmParts.indexOfRow(row);
					if (index+1<tmParts.getRowCount())
					{
						tmParts.removeRow(row);
						tmParts.addRowAt(row, index+1);
					}
				}
				for (SortableTableRow<FanFicPart> row : rows)
				{
					int index=tmParts.indexOfRow(row);
					tblParts.getSelectionModel().addSelectionInterval(index, index);
				}
			}
		}
	}

	private class AddPartAction extends ContextAction
	{
		public AddPartAction()
		{
			super("Add", Icons.getIcon("add"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			SortableTableModel<FanFicPart> tmParts=partsController.getModel();
			int rows=tmParts.getRowCount();
			if (rows>0)
			{
				String lastSource=((FanFicPartTableRow) tmParts.getRow(rows-1)).getSource();
				tmParts.addRow(new FanFicPartTableRow(StringUtils.increase(lastSource)));
			}
			else
			{
				Author author;
				switch (authorsModel.getRowCount())
				{
					case 1:
						JOptionPane.showMessageDialog(FanFicDetailsView.this, "Author fehlt!", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					case 2:
						author=authorsModel.getObject(0);
						break;
					default:
						int row=authorsTable.getSelectedRow();
						if (row<0)
						{
							JOptionPane.showMessageDialog(FanFicDetailsView.this, "Choose an author!", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						author=authorsModel.getObject(row);
				}
				int option=JOptionPane.showOptionDialog(FanFicDetailsView.this, "Create multi-part fanfic?", "Question",
														JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (option==JOptionPane.NO_OPTION)
				{
					StringBuilder source=buildFileName(author, titleField.getText());
					source.append(".html");
					tmParts.addRow(new FanFicPartTableRow(source.toString()));
				}
				else if (option==JOptionPane.YES_OPTION)
				{
					StringBuilder source=buildFileName(author, titleField.getText());
					source.append("/01.html");
					tmParts.addRow(new FanFicPartTableRow(source.toString()));
				}
			}
		}

		private StringBuilder buildFileName(Author author, String title)
		{
			title=title.toLowerCase();
			StringBuilder path=new StringBuilder();
			path.append(author.getPath()).append("/");
			for (StringTokenizer tokens=new StringTokenizer(title, " ,-\"?!"); tokens.hasMoreTokens();)
			{
				path.append(tokens.nextToken());
				if (tokens.hasMoreTokens()) path.append("_");
			}
			return path;
		}
	}

	private class FilesAction extends ContextAction
	{
		public FilesAction()
		{
			super("Check/Create Files");
			setEnabled(fanFic!=null);
		}

		@Override
		public void update(List objects)
		{
			setEnabled(fanFic!=null);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				SortableTableModel<FanFicPart> tmParts=partsController.getModel();
				StringBuilder buffer=new StringBuilder();
				int errors=0;
				for (int i=0; i<tmParts.getRowCount(); i++)
				{
					String fileName=((FanFicPartTableRow) tmParts.getRow(i)).getPath();
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
