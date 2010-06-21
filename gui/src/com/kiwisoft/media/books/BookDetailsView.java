package com.kiwisoft.media.books;

import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageLookup;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.dataimport.AmazonDeLoader;
import com.kiwisoft.media.dataimport.BookData;
import com.kiwisoft.media.dataimport.BookSyncDialog;
import com.kiwisoft.media.files.*;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.PersonLookup;
import com.kiwisoft.media.person.PersonLookupHandler;
import com.kiwisoft.media.person.PersonManager;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowLookup;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.*;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.lookup.DialogLookup;
import com.kiwisoft.swing.lookup.DialogLookupField;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.text.preformat.PreformatTextController;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Utils;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;
import java.util.List;

public class BookDetailsView extends DetailsView
{
	public static void create(Book book)
	{
		new DetailsFrame(new BookDetailsView(book)).show();
	}

	private Book book;

	private JTextField titleField;
	private JTextField originalTitleField;
	private MultiLookupField<Person> authorsField;
	private MultiLookupField<Person> translatorsField;
	private LookupField<String> publisherField;
	private JTextField editionField;
	private LookupField<String> bindingField;
	private JFormattedTextField pageCountField;
	private JFormattedTextField publishedYearField;
	private LookupField<Language> languageField;
	private LookupField<MediaFile> coverField;
	private JTextField isbn10Field;
	private JTextField isbn13Field;
	private PreformatTextController germanSummaryController;
	private PreformatTextController englishSummaryController;
	private LookupField<Show> showField;
	private LookupField<String> seriesNameField;
	private JFormattedTextField seriesNumberField;
	private DialogLookupField indexByField;
	private LookupField<String> storageField;

	private BookDetailsView(Book book)
	{
		createContentPanel();
		setBook(book);
	}

	protected void createContentPanel()
	{
		titleField=new JTextField(40);
		originalTitleField=new JTextField(40);
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
		germanSummaryController=new PreformatTextController();
		englishSummaryController=new PreformatTextController();
		JTabbedPane summaryField=new JTabbedPane(JTabbedPane.BOTTOM);
		summaryField.setPreferredSize(new Dimension(400, 200));
		summaryField.addTab("German", germanSummaryController.getComponent());
		summaryField.addTab("English", englishSummaryController.getComponent());
		showField=new LookupField<Show>(new ShowLookup());
		seriesNameField=new LookupField<String>(new SeriesNameLookup());
		seriesNumberField=ComponentUtils.createNumberField(Integer.class, 5, 1, 1000);
		storageField=new LookupField<String>(new StorageLookup());
		authorsField=new MultiLookupField<Person>(new PersonLookup(), new PersonLookupHandler());
		translatorsField=new MultiLookupField<Person>(new PersonLookup(), new PersonLookupHandler());

		setLayout(new GridBagLayout());
		int row=0;
		add(coverPreview,
			new GridBagConstraints(0, row, 1, 6, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(new JLabel("Title:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(titleField,
			new GridBagConstraints(2, row, 3, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Original Title:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(originalTitleField,
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
		add(new JLabel("Index by:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(indexByField,
			new GridBagConstraints(2, row, 3, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

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

		row++;
		add(new JLabel("Storage:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(storageField,
			new GridBagConstraints(2, row, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		titleField.getDocument().addDocumentListener(new BookDetailsView.FrameTitleUpdater());
		new PicturePreviewUpdater(coverField, coverPreview);
	}

	@Override
	public List<Action> getActions()
	{
		return Collections.<Action>singletonList(new LoadAmazonAction());
	}

	private void setBook(Book book)
	{
		this.book=book;

		if (book!=null)
		{
			titleField.setText(book.getTitle());
			originalTitleField.setText(book.getOriginalTitle());
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
			authorsField.setValues(book.getAuthors());
			translatorsField.setValues(book.getTranslators());
			germanSummaryController.setText(book.getSummaryText(LanguageManager.GERMAN));
			englishSummaryController.setText(book.getSummaryText(LanguageManager.ENGLISH));
			showField.setValue(book.getShow());
			storageField.setValue(book.getStorage());
		}
	}

	@Override
	public boolean apply()
	{
		try
		{
			final String title=titleField.getText();
			if (StringUtils.isEmpty(title)) throw new InvalidDataException("Title is missing!", titleField);
			final String originalTitle=originalTitleField.getText();
			final String indexBy=indexByField.getText();
			if (StringUtils.isEmpty(indexBy)) throw new InvalidDataException("Index by is missing!", indexByField);
			final String seriesName=seriesNameField.getText();
			final Integer seriesNumber=(Integer) seriesNumberField.getValue();
			final Collection<Person> authors=authorsField.getValues();
			final Collection<Person> translators=translatorsField.getValues();
			final String publisher=publisherField.getText();
			final String edition=editionField.getText();
			final String binding=bindingField.getText();
			Isbn isbn10;
			try
			{
				isbn10=Isbn.valueOf(isbn10Field.getText());
				if (isbn10!=null && isbn10.getPrefix()!=null) throw new InvalidDataException("ISBN-13 entered as ISBN-10!", isbn10Field);
			}
			catch (IsbnFormatException e)
			{
				throw new InvalidDataException("Invalid ISBN-10!", isbn10Field);
			}
			Isbn isbn13;
			try
			{
				isbn13=Isbn.valueOf(isbn13Field.getText());
				if (isbn13!=null && isbn13.getPrefix()==null) throw new InvalidDataException("ISBN-10 entered as ISBN-13!", isbn13Field);
			}
			catch (IsbnFormatException e)
			{
				throw new InvalidDataException("Invalid ISBN-13!", isbn13Field);
			}
			if (isbn10==null && isbn13!=null) isbn10=isbn13.getIsbn10();
			else if (isbn10!=null && isbn13==null) isbn13=isbn10.getIsbn13();
			if (isbn10!=null) isbn10Field.setText(isbn10.toString());
			if (isbn13!=null) isbn13Field.setText(isbn13.toString());
			final String isbn10String=isbn10!=null ? isbn10.toString() : null;
			final String isbn13String=isbn13!=null ? isbn13.toString() : null;
			final Language language=languageField.getValue();
			final Integer publishedYear=(Integer) publishedYearField.getValue();
			final Integer pageCount=(Integer) pageCountField.getValue();
			final MediaFile cover=coverField.getValue();
			final Show show=showField.getValue();
			final String germanSummary=germanSummaryController.getText();
			final String englishSummary=englishSummaryController.getText();
			final String storage=storageField.getText();

			return DBSession.execute(new Transactional()
			{
				@Override
				public void run() throws Exception
				{
					if (book==null) book=BookManager.getInstance().createBook();
					book.setTitle(title);
					book.setOriginalTitle(originalTitle);
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
					book.setIsbn10(isbn10String);
					book.setIsbn13(isbn13String);
					book.setLanguage(language);
					book.setCover(cover);
					book.setShow(show);
					book.setSummaryText(LanguageManager.GERMAN, germanSummary);
					book.setSummaryText(LanguageManager.ENGLISH, englishSummary);
					book.setStorage(storage);
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

	private class LoadAmazonAction extends ContextAction
	{
		private LoadAmazonAction()
		{
			super("Amazon.de", Icons.getIcon("amazon"));
			setShortDescription("Synchronize data from Amazon.de");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String isbn=isbn13Field.getText();
			if (StringUtils.isEmpty(isbn)) isbn=isbn10Field.getText();
			if (StringUtils.isEmpty(isbn))
			{
				JOptionPane.showMessageDialog(BookDetailsView.this, "No ISBN found.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			AmazonDeLoader loader=new AmazonDeLoader(isbn);
			try
			{
				BookData amazonData=loader.load();
				if (amazonData==null) JOptionPane.showMessageDialog(BookDetailsView.this, "Book not found.", "Error", JOptionPane.ERROR_MESSAGE);
				else
				{
					System.out.println("BookDetailsView$LoadAmazonAction.actionPerformed: amazonData = "+amazonData);

					BookData bookData=new BookData();
					bookData.setTitle(titleField.getText());
					bookData.setOriginalTitle(originalTitleField.getText());
					bookData.setPublisher(publisherField.getValue());
					bookData.setEdition(editionField.getText());
					bookData.setBinding(bindingField.getValue());
					bookData.setPublishedYear((Integer) publishedYearField.getValue());
					bookData.setPageCount((Integer) pageCountField.getValue());
					bookData.setSummary(LanguageManager.GERMAN, germanSummaryController.getText());
					bookData.setSummary(LanguageManager.ENGLISH, englishSummaryController.getText());
					for (Person person : authorsField.getValues()) bookData.addAuthor(person.getName());
					for (Person person : translatorsField.getValues()) bookData.addTranslator(person.getName());
					MediaFile currentCover=coverField.getValue();
					bookData.setImageFile(currentCover!=null ? currentCover.getPhysicalFile() : null);

					BookSyncDialog bookSyncDialog=new BookSyncDialog(SwingUtilities.getWindowAncestor(BookDetailsView.this), amazonData, bookData);
					if (bookSyncDialog.open())
					{
						Map<String, Object> syncData=bookSyncDialog.getSyncData();
						for (Map.Entry<String, Object> syncEntry : syncData.entrySet())
						{
							if (Book.TITLE.equals(syncEntry.getKey())) titleField.setText((String) syncEntry.getValue());
							if (Book.ORIGINAL_TITLE.equals(syncEntry.getKey())) originalTitleField.setText((String) syncEntry.getValue());
							if (Book.BINDING.equals(syncEntry.getKey())) bindingField.setValue((String) syncEntry.getValue());
							if (Book.PUBLISHER.equals(syncEntry.getKey())) publisherField.setValue((String) syncEntry.getValue());
							if (Book.EDITION.equals(syncEntry.getKey())) editionField.setText((String) syncEntry.getValue());
							if (Book.PAGE_COUNT.equals(syncEntry.getKey())) pageCountField.setValue(syncEntry.getValue());
							if (Book.PUBLISHED_YEAR.equals(syncEntry.getKey())) publishedYearField.setValue(syncEntry.getValue());
							if ((Book.SUMMARIES+"."+LanguageManager.GERMAN.getSymbol()).equals(syncEntry.getKey()))
								germanSummaryController.setText((String) syncEntry.getValue());
							if ((Book.SUMMARIES+"."+LanguageManager.ENGLISH.getSymbol()).equals(syncEntry.getKey()))
								englishSummaryController.setText((String) syncEntry.getValue());
							if (Book.COVER.equals(syncEntry.getKey()))
							{
								MediaFile cover=createCover(titleField.getText()+" - Cover", (File) syncEntry.getValue(), "books"+File.separator+"covers"+File.separator+amazonData.getIsbn13());
								if (cover!=null) coverField.setValue(cover);
							}
							if (Book.AUTHORS.equals(syncEntry.getKey())) synchronizePersons(authorsField, Utils.<List<String[]>>cast(syncEntry.getValue()));
							if (Book.TRANSLATORS.equals(syncEntry.getKey())) synchronizePersons(translatorsField, Utils.<List<String[]>>cast(syncEntry.getValue()));
						}
					}
				}
			}
			catch (Exception e1)
			{
				GuiUtils.handleThrowable(BookDetailsView.this, e1);
			}
		}

		private void synchronizePersons(MultiLookupField<Person> personsField, List<String[]> personPairs)
		{
			List<Person> personList=personsField.getValues();
			for (String[] pair : personPairs)
			{
				if (StringUtils.isEmpty(pair[1]) && !StringUtils.isEmpty(pair[0]))
				{
					Set<Person> persons=PersonManager.getInstance().getPersonsByName(pair[0]);
					if (persons.size()==1)
					{
						Person person=persons.iterator().next();
						if (!personList.contains(person)) personList.add(person);
					}
				}
				else if (StringUtils.isEmpty(pair[0]) && !StringUtils.isEmpty(pair[1]))
				{
					for (Iterator it=personList.iterator(); it.hasNext();)
					{
						Person person=(Person) it.next();
						if (pair[1].equalsIgnoreCase(person.getName())) it.remove();
					}
				}
			}
			personsField.setValues(personList);
		}

		private MediaFile createCover(final String name, File sourceFile, String targetFileName)
		{
			final MediaFileInfo fileInfo=MediaFileUtils.getMediaFileInfo(sourceFile);
			if (fileInfo.isImage())
			{
				final Dimension imageSize=MediaFileUtils.getImageSize(sourceFile);
				String rootPath=MediaConfiguration.getRootPath();
				String extension=FilenameUtils.getExtension(sourceFile.getAbsolutePath());
				File coverFile;
				int index=1;
				do
				{
					coverFile=new File(rootPath+File.separator+targetFileName+(index>1 ? "_"+index : "")+"."+extension);
					index++;
				}
				while (coverFile.exists());
				coverFile.getParentFile().mkdirs();
				sourceFile.renameTo(coverFile);

				final String relativePath=com.kiwisoft.utils.FileUtils.getRelativePath(rootPath, coverFile.getAbsolutePath());
				final String thumbnailPath=MediaFileUtils.createThumbnail(MediaConfiguration.PATH_ROOT, relativePath, MediaFileUtils.THUMBNAIL_SIDEBAR_WIDTH, MediaFileUtils.THUMBNAIL_SIDEBAR_HEIGHT, "sb");
				final Dimension thumbnailSize=MediaFileUtils.getImageSize(FileUtils.getFile(MediaConfiguration.getRootPath(), thumbnailPath));
				final MediaFile[] cover=new MediaFile[1];
				if (DBSession.execute(new Transactional()
				{
					@Override
					public void run() throws Exception
					{
						cover[0]=MediaFileManager.getInstance().createImage(MediaConfiguration.PATH_ROOT);
						cover[0].setName(name);
						cover[0].setContentType(ContentType.COVER);
						cover[0].setWidth(imageSize.width);
						cover[0].setHeight(imageSize.height);
						cover[0].setFile(relativePath);
						cover[0].setThumbnailSidebar(MediaConfiguration.PATH_ROOT, thumbnailPath, thumbnailSize.width, thumbnailSize.height);
					}

					@Override
					public void handleError(Throwable throwable, boolean rollback)
					{
						throwable.printStackTrace();
					}
				}))
				{
					return cover[0];
				}
			}
			return null;
		}

	}
}
