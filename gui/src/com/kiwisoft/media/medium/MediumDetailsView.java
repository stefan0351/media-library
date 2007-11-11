package com.kiwisoft.media.medium;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.*;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.LanguageLookup;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.collection.DoubleKeyMap;
import com.kiwisoft.collection.Chain;
import com.kiwisoft.collection.ChainLink;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.persistence.SequenceManager;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.lookup.LookupSelectionListener;
import com.kiwisoft.swing.lookup.LookupEvent;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;

public class MediumDetailsView extends DetailsView
{
	private RecordablesTableModel recordablesModel;
	private SortableTable recordablesTable;

	public static void create(Medium medium)
	{
		new DetailsFrame(new MediumDetailsView(medium)).show();
	}

	public static void create()
	{
		new DetailsFrame(new MediumDetailsView()).show();
	}

	public static void create(List<? extends Recordable> recordables)
	{
		new DetailsFrame(new MediumDetailsView(recordables)).show();
	}

	private Chain<RecordableTableRow> recordables;
	private Medium video;

	private JTextField keyField;
	private JTextField nameField;
	private JTextField lengthField;
	private JTextField remainingField;
	private JTextField storageField;
	private LookupField<Language> languageField;
	private LookupField<MediumType> typeField;
	private boolean manualName;

	private MediumDetailsView(List<? extends Recordable> recordables)
	{
		this.recordables=new Chain<RecordableTableRow>();
		for (Recordable recordable : recordables) this.recordables.addNew(new RecordableTableRow(recordable));
		createContentPanel();
		setVideo(null);
		updateName();
	}

	private MediumDetailsView(Medium video)
	{
		createContentPanel();
		setVideo(video);
		if (video!=null) manualName=true;
	}

	private MediumDetailsView()
	{
		createContentPanel();
		setVideo(null);
	}

	private void updateName()
	{
		if (!manualName && recordables!=null)
		{
			DoubleKeyMap<Object, Language, List<Episode>> map=new DoubleKeyMap<Object, Language, List<Episode>>(
				new LinkedHashMap<Object, Map<Language, List<Episode>>>());
			for (RecordableTableRow row : recordables.elements())
			{
				Recordable recordable=row.getUserObject();
				if (recordable instanceof Episode)
				{
					Episode episode=(Episode)recordable;
					List<Episode> episodes=map.get(episode.getShow(), row.getLanguage());
					if (episodes==null) map.put(episode.getShow(), row.getLanguage(), episodes=new ArrayList<Episode>(1));
					episodes.add(episode);
				}
				else map.put(recordable, row.getLanguage(), null);
			}
			List<String> names=new ArrayList<String>(map.keySet().size());
			for (Object recordable : map.keySet())
			{
				for (Language language : map.getKeys(recordable))
				{
					if (recordable instanceof Show)
					{
						Show show=(Show)recordable;
						List<Episode> episodes=map.get(show, language);
						Collections.sort(episodes);
						List<String> episodeNames=new ArrayList<String>();
						while (!episodes.isEmpty())
						{
							Episode firstEpisode=episodes.remove(0);
							Episode lastEpisode=firstEpisode;
							while (!episodes.isEmpty() && lastEpisode.getChainPosition()+1==episodes.get(0).getChainPosition())
							{
								lastEpisode=episodes.remove(0);
							}
							if (firstEpisode==lastEpisode) episodeNames.add(firstEpisode.getUserKey());
							else episodeNames.add(firstEpisode.getUserKey()+"-"+lastEpisode.getUserKey());
						}
						names.add(show.getTitle(language)+" "+StringUtils.formatAsEnumeration(episodeNames, ","));
					}
					else names.add(((Recordable)recordable).getRecordableName(language));
				}
			}
			nameField.setText(StringUtils.formatAsEnumeration(names, " / "));
		}
	}

	protected void createContentPanel()
	{
		keyField=new JTextField();
		keyField.setEditable(false);
		nameField=new JTextField();
		lengthField=new JTextField();
		lengthField.setHorizontalAlignment(JTextField.TRAILING);
		remainingField=new JTextField();
		remainingField.setHorizontalAlignment(JTextField.TRAILING);
		typeField=new LookupField<MediumType>(new MediumTypeLookup());
		storageField=new JTextField();

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 120));
		int row=0;
		add(new JLabel("Type:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(typeField, new GridBagConstraints(1, row, 1, 1, 0.5, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		add(new JLabel("Key:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 10, 0, 0), 0, 0));
		add(keyField, new GridBagConstraints(3, row, 1, 1, 0.5, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(nameField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Storage:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(storageField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Length:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(lengthField, new GridBagConstraints(1, row, 1, 1, 0.5, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Remaining:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(remainingField, new GridBagConstraints(3, row, 1, 1, 0.5, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		if (recordables!=null)
		{
			languageField=new LookupField<Language>(new LanguageLookup());
			languageField.addSelectionListener(new LookupSelectionListener()
			{
				public void selectionChanged(LookupEvent event)
				{
					if (recordables!=null)
					{
						for (RecordableTableRow recordable : recordables)
						{
							recordable.setLanguage(languageField.getValue());
							recordablesModel.fireTableDataChanged();
						}
						updateName();
					}
				}
			});

			row++;
			add(new JLabel("Language:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
			add(languageField, new GridBagConstraints(1, row, 1, 1, 0.5, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
			setPreferredSize(new Dimension(600, 300));
			recordablesModel=new RecordablesTableModel();
			recordablesTable=new SortableTable(recordablesModel);
			recordablesTable.initializeColumns(new DefaultTableConfiguration(MediumDetailsView.class, "recordables"));
			row++;
			add(new JLabel("Records:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
			add(new JScrollPane(recordablesTable), new GridBagConstraints(1, row, 3, 1, 1.0, 1.0, WEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));
			recordablesTable.addMouseListener(new EpisodesMouseListener());
		}

		nameField.getDocument().addDocumentListener(new FrameTitleUpdater());
		lengthField.addMouseListener(new LengthFieldListener());
	}

	private void setVideo(Medium video)
	{
		this.video=video;
		if (video!=null)
		{
			keyField.setText(video.getFullKey());
			nameField.setText(video.getName());
			lengthField.setText(String.valueOf(video.getLength()));
			lengthField.setEditable(false);
			remainingField.setText(String.valueOf(video.getRemainingLength()));
			typeField.setValue(video.getType());
			storageField.setText(video.getStorage());
		}
		else
		{
			typeField.setValue(MediumType.DVD);
			lengthField.setEditable(true);
			remainingField.setEditable(false);
			lengthField.getDocument().addDocumentListener(new RemainingUpdater());
			lengthField.setText("300");
		}
	}

	public boolean apply()
	{
		String name=nameField.getText();
		if (StringUtils.isEmpty(name))
		{
			JOptionPane.showMessageDialog(this, "Name fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			nameField.requestFocus();
			return false;
		}
		MediumType type=typeField.getValue();
		if (type==null)
		{
			JOptionPane.showMessageDialog(this, "Typ fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			typeField.requestFocus();
			return false;
		}
		int length=0;
		int remain=0;
		if (type.isRewritable())
		{
			try
			{
				length=Integer.parseInt(lengthField.getText());
				if (length<=0) throw new NumberFormatException();
			}
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(this, "Fehlerhafte L�ngenangabe!", "Fehler", JOptionPane.ERROR_MESSAGE);
				lengthField.requestFocus();
				return false;
			}
			try
			{
				remain=Integer.parseInt(remainingField.getText());
				if (remain<0 || remain>length) throw new NumberFormatException();
			}
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(this, "Fehlerhafte Restl�ngenangabe!", "Fehler", JOptionPane.ERROR_MESSAGE);
				remainingField.requestFocus();
				return false;
			}
		}

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (video==null) video=MediumManager.getInstance().createMedium();
			// todo remove if all videos have a key
			else if (video.getUserKey()==null) video.setUserKey((int)SequenceManager.getSequence("medium").next());
			video.setLength(length);
			video.setRemainingLength(remain);
			video.setName(name);
			video.setType(type);
			video.setStorage(storageField.getText());
			if (recordables!=null)
			{
				for (Iterator it=recordables.iterator(); it.hasNext();)
				{
					RecordableTableRow row=(RecordableTableRow)it.next();
					Track track=video.createTrack();
					row.getUserObject().initRecord(track);
					track.setLength(row.getLength());
					track.setLanguage(row.getLanguage());
				}
				recordables=null;
			}
			transaction.close();
			MediumManager.getInstance().fireElementChanged(video);
			keyField.setText(video.getFullKey());
			return true;
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
					JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	private class LengthFieldListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount()>1) lengthField.setEditable(true);
		}
	}

	private class RemainingUpdater extends DocumentAdapter
	{
		public void changedUpdate(DocumentEvent e)
		{
			try
			{
				int length=Integer.parseInt(lengthField.getText());
				remainingField.setText(Integer.toString(length));
			}
			catch (NumberFormatException e1)
			{
			}
		}
	}

	private class EpisodesMouseListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
			{
				int[] rowIndexes=recordablesTable.getSelectedRows();
				Set<RecordableTableRow> rows=new LinkedHashSet<RecordableTableRow>();
				for (int rowIndex : rowIndexes)
				{
					rows.add((RecordableTableRow)recordablesModel.getRow(rowIndex));
				}
				JPopupMenu popupMenu=new JPopupMenu();
				popupMenu.add(new MoveUpAction(rows));
				popupMenu.add(new MoveDownAction(rows));
				popupMenu.show(recordablesTable, e.getX(), e.getY());
				e.consume();
			}
			super.mouseClicked(e);
		}
	}

	private void restoreSelection(Set<RecordableTableRow> rows)
	{
		recordablesTable.clearSelection();
		for (RecordableTableRow row : rows)
		{
			int index=recordablesModel.indexOfRow(row);
			recordablesTable.getSelectionModel().addSelectionInterval(index, index);
		}
	}

	private class MoveUpAction extends AbstractAction
	{
		private Set<RecordableTableRow> rows;

		public MoveUpAction(Set<RecordableTableRow> rows)
		{
			super("Move Up", Icons.getIcon("move.up"));
			this.rows=rows;
		}

		public void actionPerformed(ActionEvent e)
		{
			for (RecordableTableRow row : rows) recordables.moveUp(row);
			recordablesModel.sort();
			restoreSelection(rows);
		}
	}

	private class MoveDownAction extends AbstractAction
	{
		private Set<RecordableTableRow> rows;

		public MoveDownAction(Set<RecordableTableRow> rows)
		{
			super("Move Down", Icons.getIcon("move.down"));
			this.rows=rows;
		}

		public void actionPerformed(ActionEvent e)
		{
			for (RecordableTableRow row : rows) recordables.moveDown(row);
			recordablesModel.sort();
			restoreSelection(rows);
		}
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		public FrameTitleUpdater()
		{
			changedUpdate(null);
		}

		public void changedUpdate(DocumentEvent e)
		{
			if (nameField.hasFocus()) manualName=true;
			String name=nameField.getText();
			if (StringUtils.isEmpty(name)) name="<unknown>";
			setTitle("Medium: "+name);
		}
	}

	private class RecordablesTableModel extends SortableTableModel<Recordable>
	{
		private final String[] COLUMNS={"event", "length", "language"};

		public RecordablesTableModel()
		{
			for (Iterator it=recordables.iterator(); it.hasNext();) addRow((RecordableTableRow)it.next());
		}

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
		}

		public boolean isResortable()
		{
			return false;
		}

		@SuppressWarnings({"unchecked"})
		public void sort()
		{
			Collections.sort(rows, new Chain.ChainComparator());
			fireTableDataChanged();
		}
	}

	private class RecordableTableRow extends SortableTableRow<Recordable> implements ChainLink
	{
		private int length;
		private int index;
		private Language language;

		public RecordableTableRow(Recordable recordable)
		{
			super(recordable);
			length=recordable.getRecordableLength();
			language=LanguageManager.getInstance().getLanguageBySymbol("de");
		}

		public Object getDisplayValue(int column, String property)
		{
			if ("event".equals(property))
				return getUserObject().getRecordableName(language);
			if ("length".equals(property))
				return new Integer(length);
			if ("language".equals(property))
				return language;
			return "";
		}

		@Override
		public int setValue(Object value, int column, String property)
		{
			if ("length".equals(property))
			{
				if (value instanceof Number) length=((Number)value).intValue();
				return TableConstants.CELL_UPDATE;
			}
			if ("language".equals(property))
			{
				language=(Language)value;
				updateName();
				return TableConstants.ROW_UPDATE;
			}
			return super.setValue(value, column, property);
		}

		@Override
		public boolean isEditable(int column, String property)
		{
			return "language".equals(property) || "length".equals(property);
		}

		public void setChainPosition(int position)
		{
			index=position;
		}

		public int getChainPosition()
		{
			return index;
		}

		public int getLength()
		{
			return length;
		}

		public Language getLanguage()
		{
			return language;
		}

		public void setLanguage(Language language)
		{
			this.language=language;
		}
	}
}
