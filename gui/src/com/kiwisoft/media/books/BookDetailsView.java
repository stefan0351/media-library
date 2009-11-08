package com.kiwisoft.media.books;

import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageLookup;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.files.*;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowLookup;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.ComponentUtils;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.swing.ImagePanel;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.lookup.DialogLookup;
import com.kiwisoft.swing.lookup.DialogLookupField;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.table.ObjectTableModel;
import com.kiwisoft.swing.table.SortableTable;
import com.kiwisoft.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Calendar;
import java.util.Collection;

public class BookDetailsView extends DetailsView
{
	public static void create(Book book)
	{
		new DetailsFrame(new BookDetailsView(book)).show();
	}

	private Book book;

	// Konfigurations Panel
	private JTextField titleField;
	private ObjectTableModel<Person> authorsModel;
	private ObjectTableModel<Person> translatorsModel;
	private LookupField<String> publisherField;
	private JTextField editionField;
	private LookupField<String> bindingField;
	private JFormattedTextField pageCountField;
	private JFormattedTextField publishedYearField;
	private LookupField<Language> languageField;
	private LookupField<MediaFile> coverField;
	private JTextField isbn10Field;
	private JTextField isbn13Field;
	private JTextPane germanSummaryField;
	private JTextPane englishSummaryField;
	private LookupField<Show> showField;
	private LookupField<String> seriesNameField;
	private JFormattedTextField seriesNumberField;
	private DialogLookupField indexByField;

	private BookDetailsView(Book book)
	{
		createContentPanel();
		setBook(book);
	}

	protected void createContentPanel()
	{
		authorsModel=new ObjectTableModel<Person>("name", Person.class, null);
		final SortableTable authorsField=new SortableTable(authorsModel);
		authorsField.setBorder(new LineBorder(Color.BLACK));
		authorsField.addComponentListener(new WindowResizeListener(authorsField));

		translatorsModel=new ObjectTableModel<Person>("name", Person.class, null);
		SortableTable translatorsField=new SortableTable(translatorsModel);
		translatorsField.setBorder(new LineBorder(Color.BLACK));
		translatorsField.addComponentListener(new WindowResizeListener(authorsField));

		titleField=new JTextField(40);
		indexByField=new DialogLookupField(new IndexByLookup());
		publisherField=new LookupField<String>(new PublisherLookup());
		editionField=new JTextField(20);
		bindingField=new LookupField<String>(new BindingLookup());
		isbn10Field=new JTextField(15);
		isbn13Field=new JTextField(20);
		pageCountField=ComponentUtils.createNumberField(Integer.class, 5, 0, null);
		publishedYearField=ComponentUtils.createNumberField(Integer.class, 5, 1000, Calendar.getInstance().get(Calendar.YEAR));
		languageField=new LookupField<Language>(new LanguageLookup());
		coverField=new LookupField<MediaFile>(new MediaFileLookup(MediaType.IMAGE), new MyImageLookupHandler());
		ImagePanel coverPreview=new ImagePanel(new Dimension(150, 200));
		coverPreview.setBorder(new EtchedBorder());
		germanSummaryField=new JTextPane();
		englishSummaryField=new JTextPane();
		JTabbedPane summaryField=new JTabbedPane(JTabbedPane.BOTTOM);
		summaryField.setPreferredSize(new Dimension(400, 100));
		summaryField.addTab("German", new JScrollPane(germanSummaryField));
		summaryField.addTab("English", new JScrollPane(englishSummaryField));
		showField=new LookupField<Show>(new ShowLookup());
		seriesNameField=new LookupField<String>(new SeriesNameLookup());
		seriesNumberField=ComponentUtils.createNumberField(Integer.class, 5, 1, 1000);

		setLayout(new GridBagLayout());
		int row=0;
		add(coverPreview,
			new GridBagConstraints(0, row, 1, 6, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(new JLabel("Title:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(titleField,
			new GridBagConstraints(2, row, 3, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Index by:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(indexByField,
			new GridBagConstraints(2, row, 3, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Series Name:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(seriesNameField,
			new GridBagConstraints(2, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Series Number:"),
			new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(seriesNumberField,
			new GridBagConstraints(4, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Author(s):"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(authorsField,
			new GridBagConstraints(2, row, 1, 1, 0.5, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Translator(s):"),
			new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(translatorsField,
			new GridBagConstraints(4, row, 1, 1, 0.5, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Summary:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(summaryField,
			new GridBagConstraints(2, row, 3, 1, 0.5, 0.5, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Binding:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(bindingField,
			new GridBagConstraints(2, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Pages:"),
			new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(pageCountField,
			new GridBagConstraints(4, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Language:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(languageField,
			new GridBagConstraints(2, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Publisher:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(publisherField,
			new GridBagConstraints(2, row, 3, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Edition:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(editionField,
			new GridBagConstraints(2, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Publ. Year:"),
			new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(publishedYearField,
			new GridBagConstraints(4, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("ISBN-10:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(isbn10Field,
			new GridBagConstraints(2, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("ISBN-13:"),
			new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(isbn13Field,
			new GridBagConstraints(4, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Cover:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(coverField,
			new GridBagConstraints(2, row, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Show:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(showField,
			new GridBagConstraints(2, row, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		titleField.getDocument().addDocumentListener(new BookDetailsView.FrameTitleUpdater());
		new PicturePreviewUpdater(coverField, coverPreview);
	}

	private void setBook(Book book)
	{
		this.book=book;

		if (book!=null)
		{
			titleField.setText(book.getTitle());
			indexByField.setText(book.getIndexBy());
			seriesNameField.setValue(book.getSeriesName());
			seriesNumberField.setValue(book.getSeriesNumber());
			pageCountField.setValue(book.getPageCount());
			publisherField.setValue(book.getPublisher());
			editionField.setText(book.getEdition());
			bindingField.setValue(book.getBinding());
			if (book.getPublishedYear()!=null)
			{
				publishedYearField.setValue(book.getPublishedYear());
			}
			coverField.setValue(book.getCover());
			isbn10Field.setText(book.getIsbn10());
			isbn13Field.setText(book.getIsbn13());
			languageField.setValue(book.getLanguage());
			authorsModel.setObjects(book.getAuthors());
			authorsModel.sort();
			translatorsModel.setObjects(book.getTranslators());
			translatorsModel.sort();
			germanSummaryField.setText(book.getSummaryText(LanguageManager.GERMAN));
			englishSummaryField.setText(book.getSummaryText(LanguageManager.ENGLISH));
			showField.setValue(book.getShow());
		}
	}

	@Override
	public boolean apply()
	{
		try
		{
			final String title=titleField.getText();
			if (StringUtils.isEmpty(title)) throw new InvalidDataException("Title is missing!", titleField);
			final String indexBy=indexByField.getText();
			if (StringUtils.isEmpty(indexBy)) throw new InvalidDataException("Index by is missing!", indexByField);
			final String seriesName=seriesNameField.getText();
			final Integer seriesNumber=(Integer) seriesNumberField.getValue();
			final Collection<Person> authors=authorsModel.getObjects();
			final Collection<Person> translators=translatorsModel.getObjects();
			final String publisher=publisherField.getText();
			final String edition=editionField.getText();
			final String binding=bindingField.getText();
			final String isbn10=Isbn.format(isbn10Field.getText());
			isbn10Field.setText(isbn10);
			final String isbn13=Isbn.format(isbn13Field.getText());
			isbn13Field.setText(isbn13);
			final Language language=languageField.getValue();
			final Integer publishedYear=(Integer) publishedYearField.getValue();
			final Integer pageCount=(Integer) pageCountField.getValue();
			final MediaFile cover=coverField.getValue();
			final Show show=showField.getValue();

			return DBSession.execute(new Transactional()
			{
				@Override
				public void run() throws Exception
				{
					if (book==null) book=BookManager.getInstance().createBook();
					book.setTitle(title);
					book.setIndexBy(indexBy);
					book.setSeriesName(seriesName);
					book.setSeriesNumber(seriesNumber);
					book.setAuthors(authors);
					book.setTranslators(translators);
					book.setBinding(binding);
					book.setPublisher(publisher);
					book.setEdition(edition);
					book.setPublishedYear(publishedYear);
					book.setPageCount(pageCount);
					book.setIsbn10(isbn10);
					book.setIsbn13(isbn13);
					book.setLanguage(language);
					book.setCover(cover);
					book.setShow(show);
				}

				@Override
				public void handleError(Throwable throwable, boolean rollback)
				{
					JOptionPane.showMessageDialog(BookDetailsView.this, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		catch (InvalidDataException e)
		{
			e.handle();
		}
		return false;
	}

	@Override
	public JComponent getDefaultFocusComponent()
	{
		return titleField;
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		@Override
		public void changedUpdate(DocumentEvent e)
		{
			String title=titleField.getText();
			if (StringUtils.isEmpty(title)) title="<unknown>";
			setTitle("Book: "+title);
		}
	}

	private class MyImageLookupHandler extends ImageLookupHandler
	{
		@Override
		public String getDefaultName()
		{
			return titleField.getText()+" - Cover";
		}
	}

	private static class WindowResizeListener extends ComponentAdapter
	{
		private final SortableTable authorsField;

		public WindowResizeListener(SortableTable authorsField)
		{
			this.authorsField=authorsField;
		}

		@Override
		public void componentResized(ComponentEvent e)
		{
			Container ancestor=authorsField.getTopLevelAncestor();
			if (ancestor instanceof Window) ((Window) ancestor).pack();
		}
	}

	private class IndexByLookup implements DialogLookup
	{
		@Override
		public void open(JTextField field)
		{
			field.setText(Book.createIndexBy(titleField.getText(), seriesNameField.getText(),
											 (Integer) seriesNumberField.getValue(), languageField.getValue()));
		}

		@Override
		public Icon getIcon()
		{
			return Icons.getIcon("lookup.create");
		}
	}
}
