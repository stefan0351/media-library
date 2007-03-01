package com.kiwisoft.media.video;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
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
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.MediaManagerFrame;
import com.kiwisoft.media.video.MediumType;
import com.kiwisoft.media.video.Video;
import com.kiwisoft.media.video.VideoManager;
import com.kiwisoft.media.video.Recording;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.Chain;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.DetailsFrame;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.table.DynamicTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.TableConfiguration;

public class VideoDetailsView extends DetailsView
{
	private EpisodesTableModel tmEpisodes;
	private DynamicTable tblEpisodes;

	public static void create(Video video)
	{
		new DetailsFrame(new VideoDetailsView(video)).show();
	}

	public static void create(MediumType type)
	{
		new DetailsFrame(new VideoDetailsView(type)).show();
	}

	public static void create(Set<Episode> episodes)
	{
		new DetailsFrame(new VideoDetailsView(episodes)).show();
	}

	private Chain<EpisodeTableRow> episodes;
	private Video video;
	private MediumType type;

	private JTextField tfUserKey;
	private JTextField tfName;
	private JTextField tfLength;
	private JTextField tfRemaining;
	private LookupField<MediumType> tfType;

	private VideoDetailsView(Set<Episode> episodes)
	{
		this.episodes=new Chain<EpisodeTableRow>();
		for (Episode episode : episodes)
		{
			this.episodes.addNew(new EpisodeTableRow(episode));
		}
		createContentPanel();
		setVideo(null);
	}

	private VideoDetailsView(Video video)
	{
		createContentPanel();
		setVideo(video);
	}

	private VideoDetailsView(MediumType type)
	{
		this.type=type;
		createContentPanel();
		setVideo(null);
	}

	protected void createContentPanel()
	{
		tfUserKey=new JTextField();
		tfName=new JTextField();
		tfLength=new JTextField();
		tfLength.setHorizontalAlignment(JTextField.TRAILING);
		tfRemaining=new JTextField();
		tfRemaining.setHorizontalAlignment(JTextField.TRAILING);
		tfType=new LookupField<MediumType>(new MediumTypeLookup());

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 120));
		add(new JLabel("Schlüssel:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfUserKey, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		add(new JLabel("Type:"), new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
		add(tfType, new GridBagConstraints(3, 0, 1, 1, 0.5, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		add(new JLabel("Name:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfName, new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Länge:"), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfLength, new GridBagConstraints(1, 2, 1, 1, 0.5, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Restzeit:"), new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(tfRemaining, new GridBagConstraints(3, 2, 1, 1, 0.5, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		if (episodes!=null)
		{
			setPreferredSize(new Dimension(600, 300));
			tmEpisodes=new EpisodesTableModel();
			tblEpisodes=new DynamicTable(tmEpisodes);
			tblEpisodes.initializeColumns(new TableConfiguration(Configurator.getInstance(), MediaManagerFrame.class, "table.video.episodes"));
			add(new JLabel("Episoden:"), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
			add(new JScrollPane(tblEpisodes), new GridBagConstraints(1, 3, 3, 1, 1.0, 1.0,
					GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));
			tblEpisodes.addMouseListener(new EpisodesMouseListener());
		}

		tfName.getDocument().addDocumentListener(new FrameTitleUpdater());
		tfLength.addMouseListener(new LengthFieldListener());
	}

	private void setVideo(Video video)
	{
		this.video=video;
		if (video!=null)
		{
			tfUserKey.setText(video.getUserKey());
			tfName.setText(video.getName());
			tfLength.setText(String.valueOf(video.getLength()));
			tfLength.setEditable(false);
			tfRemaining.setText(String.valueOf(video.getRemainingLength()));
			tfType.setValue(video.getType());
		}
		else
		{
			tfType.setValue(type);
			tfLength.setEditable(true);
			tfRemaining.setEditable(false);
			tfLength.getDocument().addDocumentListener(new RemainingUpdater());
			tfLength.setText("300");
		}
	}

	public boolean apply()
	{
		String key=tfUserKey.getText();
		if (StringUtils.isEmpty(key))
		{
			JOptionPane.showMessageDialog(this, "Schlüssel fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfUserKey.requestFocus();
			return false;
		}
		String name=tfName.getText();
		if (StringUtils.isEmpty(name))
		{
			JOptionPane.showMessageDialog(this, "Name fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfName.requestFocus();
			return false;
		}
		MediumType type=tfType.getValue();
		if (type==null)
		{
			JOptionPane.showMessageDialog(this, "Typ fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfType.requestFocus();
			return false;
		}
		int length=0;
		int remain=0;
		if (type.isRewritable())
		{
			try
			{
				length=Integer.parseInt(tfLength.getText());
				if (length<=0 || length>500) throw new NumberFormatException();
			}
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(this, "Fehlerhafte Längenangabe!", "Fehler", JOptionPane.ERROR_MESSAGE);
				tfLength.requestFocus();
				return false;
			}
			try
			{
				remain=Integer.parseInt(tfRemaining.getText());
				if (remain<0 || remain>length) throw new NumberFormatException();
			}
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(this, "Fehlerhafte Restlängenangabe!", "Fehler", JOptionPane.ERROR_MESSAGE);
				tfRemaining.requestFocus();
				return false;
			}
		}

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (video==null) video=VideoManager.getInstance().createVideo();
			video.setLength(length);
			video.setRemainingLength(remain);
			video.setName(name);
			video.setUserKey(key);
			video.setType(type);
			if (episodes!=null)
			{
				for (Iterator it=episodes.iterator(); it.hasNext();)
				{
					EpisodeTableRow row=(EpisodeTableRow)it.next();
					Recording recording=video.createRecording();
					recording.setShow(row.getUserObject().getShow());
					recording.setEpisode(row.getUserObject());
					recording.setLength(row.getLength());
					recording.setLanguage(row.getLanguage());
				}
			}
			transaction.close();
			VideoManager.getInstance().fireElementChanged(video);
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
			if (e.getClickCount()>1) tfLength.setEditable(true);
		}
	}

	private class RemainingUpdater extends DocumentAdapter
	{
		public void changedUpdate(DocumentEvent e)
		{
			try
			{
				int length=Integer.parseInt(tfLength.getText());
				tfRemaining.setText(Integer.toString(length));
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
				int[] rowIndexes=tblEpisodes.getSelectedRows();
				Set<EpisodeTableRow> rows=new LinkedHashSet<EpisodeTableRow>();
				for (int i=0; i<rowIndexes.length; i++)
				{
					rows.add(tmEpisodes.getRow(rowIndexes[i]));
				}
				JPopupMenu popupMenu=new JPopupMenu();
				popupMenu.add(new MoveUpAction(rows));
				popupMenu.add(new MoveDownAction(rows));
				popupMenu.show(tblEpisodes, e.getX(), e.getY());
				e.consume();
			}
			super.mouseClicked(e);
		}
	}

	private void restoreSelection(Set<EpisodeTableRow> rows)
	{
		tblEpisodes.clearSelection();
		for (EpisodeTableRow row : rows)
		{
			int index=tmEpisodes.indexOfRow(row);
			tblEpisodes.getSelectionModel().addSelectionInterval(index, index);
		}
	}

	private class MoveUpAction extends AbstractAction
	{
		private Set<EpisodeTableRow> rows;

		public MoveUpAction(Set<EpisodeTableRow> rows)
		{
			super("Nach oben");
			this.rows=rows;
		}

		public void actionPerformed(ActionEvent e)
		{
			for (EpisodeTableRow row : rows) episodes.moveUp(row);
			tmEpisodes.sort();
			restoreSelection(rows);
		}
	}

	private class MoveDownAction extends AbstractAction
	{
		private Set<EpisodeTableRow> rows;

		public MoveDownAction(Set<EpisodeTableRow> rows)
		{
			super("Nach unten");
			this.rows=rows;
		}

		public void actionPerformed(ActionEvent e)
		{
			for (EpisodeTableRow row : rows) episodes.moveDown(row);
			tmEpisodes.sort();
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
			String name=tfName.getText();
			if (StringUtils.isEmpty(name)) name="<unbekannt>";
			setTitle("Video: "+name);
		}
	}

	private class EpisodesTableModel extends SortableTableModel<EpisodeTableRow>
	{
		private final String[] COLUMNS={"event", "length", "language"};

		public EpisodesTableModel()
		{
			for (Iterator it=episodes.iterator(); it.hasNext();) addRow((EpisodeTableRow)it.next());
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

		public void sort()
		{
			Collections.sort(rows, new Chain.ChainComparator<EpisodeTableRow>());
			fireTableDataChanged();
		}
	}

	private static class EpisodeTableRow extends SortableTableRow<Episode> implements Chain.ChainLink
	{
		private int length;
		private int index;
		private Language language;

		public EpisodeTableRow(Episode episode)
		{
			super(episode);
			length=episode.getShow().getDefaultEpisodeLength();
			language=LanguageManager.getInstance().getLanguageBySymbol("de");
		}

		public Object getDisplayValue(int column, String property)
		{
			switch (column)
			{
				case 0:
					Episode episode=getUserObject();
					Show show=episode.getShow();
					return show.getName(language)+" - "+episode.getNameWithKey(language);
				case 1:
					return new Integer(length);
				case 2:
					return language;
			}
			return "";
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
	}
}
