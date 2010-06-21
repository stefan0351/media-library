package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.books.Book;
import com.kiwisoft.media.files.MediaFileUtils;
import com.kiwisoft.swing.ButtonDialog;
import com.kiwisoft.swing.ComponentUtils;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.ImagePanel;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Utils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;

/**
 * @author Stefan Stiller
 * @since 13.05.2010
 */
public class BookSyncDialog extends ButtonDialog
{
	private BookData currentData;
	private BookData onlineData;
	private Map<String, Object> syncData;

	private TextFieldPair titleFields;
	private TextFieldPair originalTitleFields;
	private TextFieldPair publisherFields;
	private TextFieldPair editionFields;
	private TextFieldPair bindingFields;
	private IntegerFieldPair pageCountFields;
	private IntegerFieldPair publishedYearFields;
	private TextPanePair germanSummaryFields;
	private TextPanePair englishSummaryFields;
	private ImageFieldPair coverFields;
	private List<TextFieldPair> authorFields;
	private List<TextFieldPair> translatorFields;

	public BookSyncDialog(Window window, BookData onlineData, BookData currentData)
	{
		super(window, "Synchronize Book", true);
		this.onlineData=onlineData;
		this.currentData=currentData;
		init();
	}

	@Override
	protected JComponent createContentPane()
	{
		JPanel panel=new JPanel(new GridBagLayout());

		int row=0;
		panel.add(ComponentUtils.createBoldLabel("Amazon.de"),
				  new GridBagConstraints(1, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		panel.add(ComponentUtils.createBoldLabel("MediaLib"),
				  new GridBagConstraints(3, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		if (!StringUtils.isEmpty(onlineData.getTitle()) && !onlineData.getTitle().equals(currentData.getTitle()))
		{
			titleFields=new TextFieldPair(onlineData.getTitle(), currentData.getTitle(), 30);
			addFieldPair(panel, ++row, "Title:", titleFields, GridBagConstraints.HORIZONTAL, 10);
		}

		if (!StringUtils.isEmpty(onlineData.getOriginalTitle()) && !onlineData.getOriginalTitle().equals(currentData.getOriginalTitle()))
		{
			originalTitleFields=new TextFieldPair(onlineData.getOriginalTitle(), currentData.getOriginalTitle(), 30);
			addFieldPair(panel, ++row, "Original Title:", originalTitleFields, GridBagConstraints.HORIZONTAL, 10);
		}

		String newSummary=onlineData.getSummary(LanguageManager.GERMAN);
		String oldSummary=currentData.getSummary(LanguageManager.GERMAN);
		if (!StringUtils.isEmpty(newSummary) && !newSummary.equals(oldSummary))
		{
			germanSummaryFields=new TextPanePair(newSummary, oldSummary);
			addFieldPair(panel, ++row, "German Summary", germanSummaryFields, GridBagConstraints.BOTH, 10);
		}

		newSummary=onlineData.getSummary(LanguageManager.ENGLISH);
		oldSummary=currentData.getSummary(LanguageManager.ENGLISH);
		if (!StringUtils.isEmpty(newSummary) && !newSummary.equals(oldSummary))
		{
			englishSummaryFields=new TextPanePair(newSummary, oldSummary);
			addFieldPair(panel, ++row, "English Summary", englishSummaryFields, GridBagConstraints.BOTH, 10);
		}

		if (!onlineData.getAuthors().isEmpty() && !onlineData.getAuthors().equals(currentData.getAuthors()))
		{
			authorFields=new ArrayList<TextFieldPair>();
			List<String[]> pairs=createPairs(onlineData.getAuthors(), currentData.getAuthors());
			boolean label=true;
			for (String[] pair : pairs)
			{
				TextFieldPair authorFields=new TextFieldPair(pair[0], pair[1], 30);
				this.authorFields.add(authorFields);
				addFieldPair(panel, ++row, label ? "Author(s)" : null, authorFields, GridBagConstraints.HORIZONTAL, label ? 10 : 5);
				label=false;
			}
		}

		if (!onlineData.getTranslators().isEmpty() && !onlineData.getTranslators().equals(currentData.getTranslators()))
		{
			translatorFields=new ArrayList<TextFieldPair>();
			List<String[]> pairs=createPairs(onlineData.getTranslators(), currentData.getTranslators());
			boolean label=true;
			for (String[] pair : pairs)
			{
				TextFieldPair translatorFields=new TextFieldPair(pair[0], pair[1], 30);
				this.translatorFields.add(translatorFields);
				addFieldPair(panel, ++row, label ? "Translator(s)" : null, translatorFields, GridBagConstraints.HORIZONTAL, label ? 10 : 5);
				label=false;
			}
		}

		if (!StringUtils.isEmpty(onlineData.getBinding()) && !onlineData.getBinding().equals(currentData.getBinding()))
		{
			bindingFields=new TextFieldPair(onlineData.getBinding(), currentData.getBinding(), 30);
			addFieldPair(panel, ++row, "Binding:", bindingFields, GridBagConstraints.HORIZONTAL, 10);
		}

		if (onlineData.getPageCount()!=null && !onlineData.getPageCount().equals(currentData.getPageCount()))
		{
			pageCountFields=new IntegerFieldPair(onlineData.getPageCount(), currentData.getPageCount(), 5);
			addFieldPair(panel, ++row, "Pages:", pageCountFields, GridBagConstraints.NONE, 10);
		}

		if (!StringUtils.isEmpty(onlineData.getPublisher()) && !onlineData.getPublisher().equals(currentData.getPublisher()))
		{
			publisherFields=new TextFieldPair(onlineData.getPublisher(), currentData.getPublisher(), 30);
			addFieldPair(panel, ++row, "Publisher:", publisherFields, GridBagConstraints.HORIZONTAL, 10);
		}

		if (!StringUtils.isEmpty(onlineData.getEdition()) && !onlineData.getEdition().equals(currentData.getEdition()))
		{
			editionFields=new TextFieldPair(onlineData.getEdition(), currentData.getEdition(), 30);
			addFieldPair(panel, ++row, "Edition:", editionFields, GridBagConstraints.HORIZONTAL, 10);
		}

		if (onlineData.getPublishedYear()!=null && !onlineData.getPublishedYear().equals(currentData.getPublishedYear()))
		{
			publishedYearFields=new IntegerFieldPair(onlineData.getPublishedYear(), currentData.getPublishedYear(), 5);
			addFieldPair(panel, ++row, "Published Year:", publishedYearFields, GridBagConstraints.NONE, 10);
		}

		if (onlineData.getImageFile()!=null)
		{
			coverFields=new ImageFieldPair(onlineData.getImageFile(), currentData.getImageFile());
			addFieldPair(panel, ++row, "Cover:", coverFields, GridBagConstraints.BOTH, 10);
		}

		return panel;
	}

	private List<String[]> createPairs(Set<String> set1, Set<String> set2)
	{
		Set<String> persons1=new LinkedHashSet<String>(set1);
		List<String[]> pairs=new ArrayList<String[]>();
		for (String person : set2)
		{
			if (persons1.contains(person))
			{
				pairs.add(new String[]{person, person});
				persons1.remove(person);
			}
			else pairs.add(new String[]{null, person});
		}
		for (String person : persons1) pairs.add(new String[]{person, null});
		return pairs;
	}

	private void addFieldPair(JPanel panel, int row, String label, FieldPair fields, int fill, int marginTop)
	{
		if (label!=null)
			panel.add(new JLabel(label),
					  new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(marginTop, 0, 0, 0), 0, 0));
		panel.add(fields.getLeftComponent(),
				  new GridBagConstraints(1, row, 1, 1, 0.5, 0.0, GridBagConstraints.NORTHWEST, fill, new Insets(marginTop, 5, 0, 0), 0, 0));
		panel.add(fields.getSyncButton(),
				  new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(marginTop, 15, 0, 15), 0, 0));
		panel.add(fields.getRightComponent(),
				  new GridBagConstraints(3, row, 1, 1, 0.5, 0.0, GridBagConstraints.NORTHWEST, fill, new Insets(marginTop, 0, 0, 0), 0, 0));
	}

	@Override
	protected boolean apply()
	{
		if (super.apply())
		{
			syncData=new HashMap<String, Object>();
			if (titleFields!=null && titleFields.isSynchronize()) syncData.put(Book.TITLE, onlineData.getTitle());
			if (originalTitleFields!=null && originalTitleFields.isSynchronize()) syncData.put(Book.ORIGINAL_TITLE, onlineData.getOriginalTitle());
			if (germanSummaryFields!=null && germanSummaryFields.isSynchronize())
				syncData.put(Book.SUMMARIES+"."+LanguageManager.GERMAN.getSymbol(), onlineData.getSummary(LanguageManager.GERMAN));
			if (englishSummaryFields!=null && englishSummaryFields.isSynchronize())
				syncData.put(Book.SUMMARIES+"."+LanguageManager.ENGLISH.getSymbol(), onlineData.getSummary(LanguageManager.ENGLISH));
			if (bindingFields!=null && bindingFields.isSynchronize()) syncData.put(Book.BINDING, onlineData.getBinding());
			if (publishedYearFields!=null && publishedYearFields.isSynchronize()) syncData.put(Book.PUBLISHED_YEAR, onlineData.getPublishedYear());
			if (editionFields!=null && editionFields.isSynchronize()) syncData.put(Book.EDITION, onlineData.getEdition());
			if (publisherFields!=null && publisherFields.isSynchronize()) syncData.put(Book.PUBLISHER, onlineData.getPublisher());
			if (pageCountFields!=null && pageCountFields.isSynchronize()) syncData.put(Book.PAGE_COUNT, onlineData.getPageCount());
			if (coverFields!=null && coverFields.isSynchronize()) syncData.put(Book.COVER, onlineData.getImageFile());
			if (authorFields!=null)
			{
				List<String[]> pairs=new ArrayList<String[]>();
				for (TextFieldPair authorField : authorFields)
				{
					if (authorField.isSynchronize()) pairs.add(new String[]{authorField.getSourceValue(), authorField.getTargetValue()});
				}
				syncData.put(Book.AUTHORS, pairs);
			}
			if (translatorFields!=null)
			{
				List<String[]> pairs=new ArrayList<String[]>();
				for (TextFieldPair translatorField : translatorFields)
				{
					if (translatorField.isSynchronize()) pairs.add(new String[]{translatorField.getSourceValue(), translatorField.getTargetValue()});
				}
				syncData.put(Book.TRANSLATORS, pairs);
			}
			return true;
		}
		return false;
	}

	public Map<String, Object> getSyncData()
	{
		return syncData;
	}

	private static abstract class FieldPair implements ActionListener
	{
		private JButton syncButton;
		private boolean synchronize;

		private FieldPair()
		{
			syncButton=new JButton(Icons.getIcon("sync.right-active"));
			syncButton.addActionListener(this);
			syncButton.setMargin(new Insets(0, 0, 0, 0));
		}

		protected abstract JComponent getLeftComponent();

		protected abstract JComponent getRightComponent();

		public JButton getSyncButton()
		{
			return syncButton;
		}

		public boolean isSynchronize()
		{
			return synchronize;
		}

		public void setSynchronize(boolean synchronize)
		{
			this.synchronize=synchronize;
			if (synchronize) syncButton.setIcon(Icons.getIcon("sync.left-active"));
			else syncButton.setIcon(Icons.getIcon("sync.right-active"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			setSynchronize(!isSynchronize());
		}
	}

	private static class AbstractTextFieldPair extends FieldPair
	{
		protected JTextComponent sourceField;
		protected JTextComponent targetField;

		@Override
		public void setSynchronize(boolean synchronize)
		{
			super.setSynchronize(synchronize);
			if (isSynchronize())
			{
				sourceField.setFont(UIManager.getFont("TextField.font").deriveFont(Font.BOLD));
				targetField.setFont(UIManager.getFont("TextField.font"));
			}
			else
			{
				sourceField.setFont(UIManager.getFont("TextField.font"));
				targetField.setFont(UIManager.getFont("TextField.font").deriveFont(Font.BOLD));
			}
		}

		@Override
		protected JComponent getLeftComponent()
		{
			return sourceField;
		}

		@Override
		protected JComponent getRightComponent()
		{
			return targetField;
		}
	}

	private static class TextFieldPair extends AbstractTextFieldPair
	{
		private TextFieldPair(String sourceValue, String targetValue, int columns)
		{
			sourceField=new JTextField(columns);
			sourceField.setEditable(false);
			sourceField.setText(sourceValue);
			sourceField.setCaretPosition(0);

			targetField=new JTextField(columns);
			targetField.setEditable(false);
			targetField.setText(targetValue);
			targetField.setCaretPosition(0);

			setSynchronize(StringUtils.isEmpty(targetValue));
		}

		protected String getSourceValue()
		{
			return sourceField.getText();
		}

		protected String getTargetValue()
		{
			return targetField.getText();
		}
	}

	private static class TextPanePair extends AbstractTextFieldPair
	{
		private JScrollPane sourcePanel;
		private JScrollPane targetPanel;

		private TextPanePair(String sourceValue, String targetValue)
		{
			sourceField=new JTextPane();
			sourceField.setEditable(false);
			sourceField.setText(sourceValue);
			sourceField.setCaretPosition(0);
			sourcePanel=new JScrollPane(sourceField);
			sourcePanel.setPreferredSize(new Dimension(200, 100));

			targetField=new JTextPane();
			targetField.setEditable(false);
			targetField.setText(targetValue);
			targetField.setCaretPosition(0);
			targetPanel=new JScrollPane(targetField);
			targetPanel.setPreferredSize(new Dimension(200, 100));

			setSynchronize(StringUtils.isEmpty(targetValue));
		}

		@Override
		protected JComponent getLeftComponent()
		{
			return sourcePanel;
		}

		@Override
		protected JComponent getRightComponent()
		{
			return targetPanel;
		}
	}

	private static class IntegerFieldPair extends AbstractTextFieldPair
	{
		private IntegerFieldPair(Integer sourceValue, Integer targetValue, int columns)
		{
			sourceField=ComponentUtils.createNumberField(Integer.class, columns, null, null);
			sourceField.setMinimumSize(sourceField.getPreferredSize());
			sourceField.setEditable(false);
			((JFormattedTextField) sourceField).setValue(sourceValue);

			targetField=ComponentUtils.createNumberField(Integer.class, columns, null, null);
			targetField.setMinimumSize(targetField.getPreferredSize());
			targetField.setEditable(false);
			((JFormattedTextField) targetField).setValue(targetValue);

			setSynchronize(targetValue==null);
		}
	}

	private static class ImageFieldPair extends FieldPair
	{
		private ImagePanel sourceField;
		private ImagePanel targetField;
		private File sourceValue;
		private JPanel sourcePanel;

		private ImageFieldPair(File sourceValue, File targetValue)
		{
			this.sourceValue=sourceValue;

			sourceField=new ImagePanel(new Dimension(150, 200));
			setImageFile(sourceField, sourceValue);

			JToolBar toolBar=new JToolBar()
			{
				@Override
				protected JButton createActionComponent(Action a)
				{
					JButton button=super.createActionComponent(a);
					button.setMargin(new Insets(2, 2, 2, 2));
					return button;
				}
			};
			toolBar.setOrientation(SwingConstants.VERTICAL);
			toolBar.setFloatable(false);
			toolBar.setMargin(null);
			toolBar.add(new EditAction());


			sourcePanel=new JPanel(new BorderLayout());
			sourcePanel.setBorder(new LineBorder(Color.GRAY, 1));
			sourcePanel.add(toolBar, BorderLayout.WEST);
			sourcePanel.add(sourceField, BorderLayout.CENTER);

			targetField=new ImagePanel(new Dimension(150, 200));
			targetField.setBorder(new LineBorder(Color.BLACK, 1));
			setImageFile(targetField, targetValue);

			setSynchronize(targetValue==null);
		}

		@Override
		protected JComponent getLeftComponent()
		{
			return sourcePanel;
		}

		@Override
		protected JComponent getRightComponent()
		{
			return targetField;
		}

		private void setImageFile(ImagePanel imagePanel, File file)
		{
			if (file!=null && file.exists())
			{
				try
				{
					imagePanel.setImage(MediaFileUtils.loadIcon(file.toURI().toURL()));
					imagePanel.setToolTipText(file.getAbsolutePath());
				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				imagePanel.setImage(null);
				imagePanel.setToolTipText(null);
			}
		}

		@Override
		public void setSynchronize(boolean synchronize)
		{
			super.setSynchronize(synchronize);
			if (isSynchronize())
			{
				sourcePanel.setBorder(new LineBorder(Color.BLACK, 1));
				targetField.setBorder(new LineBorder(Color.GRAY, 1));
			}
			else
			{
				sourcePanel.setBorder(new LineBorder(Color.GRAY, 1));
				targetField.setBorder(new LineBorder(Color.BLACK, 1));
			}
		}

		private class EditAction extends AbstractAction
		{
			private EditAction()
			{
				super("Edit", Icons.getIcon("edit"));
			}

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					Utils.run("\""+MediaConfiguration.getImageEditorPath()+"\" \""+sourceValue.getAbsolutePath()+"\"");
					setImageFile(sourceField, sourceValue);
				}
				catch (Exception e1)
				{
					GuiUtils.handleThrowable(sourceField, e1);
				}
			}
		}
	}
}
