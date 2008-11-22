package com.kiwisoft.media.books;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageLookup;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.files.MediaFileUtils;
import com.kiwisoft.media.files.MediaFileManager;
import com.kiwisoft.media.person.PersonManager;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.dataimport.BookData;
import com.kiwisoft.utils.*;
import com.kiwisoft.swing.lookup.DialogLookupField;
import com.kiwisoft.swing.lookup.FileLookup;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.*;
import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;

public class BookDataDetailsView extends DetailsView
{
	public static Book createDialog(Window owner, BookData bookData)
	{
		BookDataDetailsView view=new BookDataDetailsView(bookData);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.book;
		return null;
	}

	private Book book;

	// Konfigurations Panel
	private JTextField titleField;
	private JTextField authorsField;
	private JTextField translatorsField;
	private JTextField publisherField;
	private JTextField editionField;
	private JTextField bindingField;
	private JFormattedTextField pageCountField;
	private JFormattedTextField publishedYearField;
	private LookupField<Language> languageField;
	private DialogLookupField coverFileField;
	private ImagePanel coverPreview;
	private JTextField isbn10Field;
	private JTextField isbn13Field;

	private BookDataDetailsView(BookData bookData)
	{
		createContentPanel();
		setBookData(bookData);
	}

	protected void createContentPanel()
	{
		authorsField=new JTextField(20);
		translatorsField=new JTextField(30);
		titleField=new JTextField(40);
		publisherField=new JTextField(40);
		editionField=new JTextField(20);
		bindingField=new JTextField(20);
		isbn10Field=new JTextField(15);
		isbn13Field=new JTextField(20);
		pageCountField=GuiUtils.createNumberField(Integer.class, 5, 0, null);
		publishedYearField=GuiUtils.createNumberField(Integer.class, 5, 1000, Calendar.getInstance().get(Calendar.YEAR));
		languageField=new LookupField<Language>(new LanguageLookup());
		coverFileField=new DialogLookupField(new FileLookup(JFileChooser.FILES_ONLY, true));
		coverPreview=new ImagePanel(new Dimension(150, 200));
		coverPreview.setBorder(new EtchedBorder());
		coverPreview.addMouseListener(new CoverMouseListener());

		setLayout(new GridBagLayout());
		int row=0;
		add(coverPreview,
			new GridBagConstraints(0, row, 1, 6, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(new JLabel("Title:"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(titleField,
			new GridBagConstraints(2, row, 3, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Author(s):"),
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(authorsField,
			new GridBagConstraints(2, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Translator(s):"),
			new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(translatorsField,
			new GridBagConstraints(4, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

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
		add(coverFileField,
			new GridBagConstraints(2, row, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		titleField.getDocument().addDocumentListener(new BookDataDetailsView.FrameTitleUpdater());
		new ImageUpdater(coverFileField.getTextField(), coverPreview);
	}

	private void setBookData(BookData bookData)
	{
		titleField.setText(bookData.getTitle());
		pageCountField.setValue(bookData.getPageCount());
		publisherField.setText(bookData.getPublisher());
		editionField.setText(bookData.getEdition());
		bindingField.setText(bookData.getBinding());
		int publishedYear=bookData.getPublishedYear();
		if (publishedYear>0) publishedYearField.setValue(publishedYear);
		File imageFile=bookData.getImageFile();
		if (imageFile!=null) coverFileField.setText(imageFile.getAbsolutePath());
		isbn10Field.setText(bookData.getIsbn10());
		isbn13Field.setText(bookData.getIsbn13());
		languageField.setValue(bookData.getLanguage());
		authorsField.setText(StringUtils.formatAsEnumeration(bookData.getAuthors(), "; "));
		translatorsField.setText(StringUtils.formatAsEnumeration(bookData.getTranslators(), "; "));
	}

	public boolean apply()
	{
		try
		{
			final String title=titleField.getText();
			if (StringUtils.isEmpty(title)) throw new InvalidDataException("Title is missing!", titleField);
			final String[] authors=StringUtils.splitAndTrim(authorsField.getText(), ";");
			final String[] translators=StringUtils.splitAndTrim(translatorsField.getText(), ";");
			final String publisher=publisherField.getText();
			final String edition=editionField.getText();
			final String binding=bindingField.getText();
			final String isbn10=isbn10Field.getText();
			final String isbn13=isbn13Field.getText();
			final Language language=languageField.getValue();
			final Integer publishedYear=(Integer)publishedYearField.getValue();
			final Integer pageCount=(Integer)pageCountField.getValue();
			String coverPath=coverFileField.getText();
			Dimension coverSize=null;
			if (!StringUtils.isEmpty(coverPath))
			{
				File coverFile=new File(coverPath);
				if (!coverFile.exists()) throw new InvalidDataException("Cover file doesn't exist!", coverFileField);
				coverSize=MediaFileUtils.getImageSize(coverFile);
				if (coverSize==null) throw new InvalidDataException("Cover file is no image!", coverFileField);
				coverPath=FileUtils.getRelativePath(MediaConfiguration.getRootPath(),coverPath);
			}

			final Dimension finalCoverSize=coverSize;
			final String finalCoverPath=coverPath;
			return DBSession.execute(new Transactional()
			{
				private Map<String, Person> persons=new HashMap<String, Person>();

				public void run() throws Exception
				{
					if (book==null) book=BookManager.getInstance().createBook();
					book.setTitle(title);
					for (String name : authors)
					{
						if (!StringUtils.isEmpty(name))
						{
							Person author=getPerson(name);
							book.addAuthor(author);
						}
					}
					for (String name : translators)
					{
						if (!StringUtils.isEmpty(name))
						{
							Person translator=getPerson(name);
							book.addTranslator(translator);
						}
					}
					book.setBinding(binding);
					book.setPublisher(publisher);
					book.setEdition(edition);
					book.setPublishedYear(publishedYear);
					book.setPageCount(pageCount);
					book.setIsbn10(isbn10);
					book.setIsbn13(isbn13);
					book.setLanguage(language);
					if (finalCoverSize!=null && !StringUtils.isEmpty(finalCoverPath))
					{
						MediaFile cover=MediaFileManager.getInstance().createImage(MediaConfiguration.PATH_ROOT);
						cover.setName(title);
						cover.setFile(finalCoverPath);
						cover.setWidth(finalCoverSize.width);
						cover.setHeight(finalCoverSize.height);
						book.setCover(cover);
					}
				}

				private Person getPerson(String name)
				{
					Person person=persons.get(name);
					if (person==null) person=PersonManager.getInstance().getPersonByName(name, true);
					if (person==null)
					{
						person=PersonManager.getInstance().createPerson();
						person.setName(name);
						persons.put(name, person);
					}
					return person;
				}

				public void handleError(Throwable throwable, boolean rollback)
				{
					JOptionPane.showMessageDialog(BookDataDetailsView.this, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		catch (InvalidDataException e)
		{
			e.handle();
		}
		return false;
	}

	public JComponent getDefaultFocusComponent()
	{
		return titleField;
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		public void changedUpdate(DocumentEvent e)
		{
			String title=titleField.getText();
			if (StringUtils.isEmpty(title)) title="<unknown>";
			setTitle("Book: "+title);
		}
	}

	private class CoverMouseListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()>1)
			{
				e.consume();
				String coverPath=coverFileField.getText();
				if (StringUtils.isEmpty(coverPath)) return;
				File coverFile=new File(coverPath);
				if (!coverFile.exists()) return;
				try
				{
					Utils.run("\""+MediaConfiguration.getImageEditorPath()+"\" \""+coverFile.getAbsolutePath()+"\"");
					coverPreview.setImage(new ImageIcon(coverFile.toURI().toURL()));
				}
				catch (Exception e1)
				{
					GuiUtils.handleThrowable(BookDataDetailsView.this, e1);
				}
			}
		}
	}
}
