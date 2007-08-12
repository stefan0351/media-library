package com.kiwisoft.media.books;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Calendar;
import java.util.Collection;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageLookup;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.media.pics.PictureLookup;
import com.kiwisoft.media.pics.PictureLookupHandler;
import com.kiwisoft.media.pics.PicturePreviewUpdater;
import com.kiwisoft.utils.gui.GuiUtils;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.*;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.table.ObjectTableModel;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsFrame;

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
	private JTextField publisherField;
	private JTextField editionField;
	private JTextField bindingField;
	private JFormattedTextField pageCountField;
	private JFormattedTextField publishedYearField;
	private LookupField<Language> languageField;
	private LookupField<Picture> coverField;
	private JTextField isbn10Field;
	private JTextField isbn13Field;

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
		publisherField=new JTextField(40);
		editionField=new JTextField(20);
		bindingField=new JTextField(20);
		isbn10Field=new JTextField(15);
		isbn13Field=new JTextField(20);
		pageCountField=GuiUtils.createNumberField(Integer.class, 5, 0, null);
		publishedYearField=GuiUtils.createNumberField(Integer.class, 5, 1000, Calendar.getInstance().get(Calendar.YEAR));
		languageField=new LookupField<Language>(new LanguageLookup());
		coverField=new LookupField<Picture>(new PictureLookup(), new MyPictureLookupHandler());
		ImagePanel coverPreview=new ImagePanel(new Dimension(150, 200));
		coverPreview.setBorder(new EtchedBorder());

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
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(authorsField,
			new GridBagConstraints(2, row, 1, 1, 0.5, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(new JLabel("Translator(s):"),
			new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(translatorsField,
			new GridBagConstraints(4, row, 1, 1, 0.5, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

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

		titleField.getDocument().addDocumentListener(new BookDetailsView.FrameTitleUpdater());
		new PicturePreviewUpdater(coverField, coverPreview);
	}

	private void setBook(Book book)
	{
		this.book=book;

		if (book!=null)
		{
			titleField.setText(book.getTitle());
			pageCountField.setValue(book.getPageCount());
			publisherField.setText(book.getPublisher());
			editionField.setText(book.getEdition());
			bindingField.setText(book.getBinding());
			int publishedYear=book.getPublishedYear();
			if (publishedYear>0) publishedYearField.setValue(publishedYear);
			coverField.setValue(book.getCover());
			isbn10Field.setText(book.getIsbn10());
			isbn13Field.setText(book.getIsbn13());
			languageField.setValue(book.getLanguage());
			authorsModel.setObjects(book.getAuthors());
			authorsModel.sort();
			translatorsModel.setObjects(book.getTranslators());
			translatorsModel.sort();
		}
	}

	public boolean apply()
	{
		try
		{
			final String title=titleField.getText();
			if (StringUtils.isEmpty(title)) throw new InvalidDataException("Title is missing!", titleField);
			final Collection<Person> authors=authorsModel.getObjects();
			final Collection<Person> translators=translatorsModel.getObjects();
			final String publisher=publisherField.getText();
			final String edition=editionField.getText();
			final String binding=bindingField.getText();
			final String isbn10=isbn10Field.getText();
			final String isbn13=isbn13Field.getText();
			final Language language=languageField.getValue();
			final Integer publishedYear=(Integer)publishedYearField.getValue();
			final Integer pageCount=(Integer)pageCountField.getValue();
			final Picture cover=coverField.getValue();

			return DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					if (book==null) book=BookManager.getInstance().createBook();
					book.setTitle(title);
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
				}

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

	private class MyPictureLookupHandler extends PictureLookupHandler
	{
		@Override
		public String getDefaultName()
		{
			return titleField.getText();
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
			if (ancestor instanceof Window) ((Window)ancestor).pack();
		}
	}
}
