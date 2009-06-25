package com.kiwisoft.media.books;

import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageLookup;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.dataimport.BookData;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.files.MediaFileManager;
import com.kiwisoft.media.files.MediaFileUtils;
import com.kiwisoft.media.files.ContentType;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.PersonManager;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.*;
import com.kiwisoft.swing.lookup.DialogLookupField;
import com.kiwisoft.swing.lookup.FileLookup;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Utils;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookDataDetailsView extends DetailsView
{
    private File targetFile;

    public static Book createDialog(Window owner, BookData bookData, Book book)
    {
        BookDataDetailsView view=new BookDataDetailsView(bookData, book);
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
    private JTextPane summaryField;
    private LookupField<Language> summaryLanguageField;

    private BookDataDetailsView(BookData bookData, Book book)
    {
        this.book=book;
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
        summaryField=new JTextPane();
        JScrollPane summaryPane=new JScrollPane(summaryField);
        summaryPane.setPreferredSize(new Dimension(400, 100));
        summaryLanguageField=new LookupField<Language>(new LanguageLookup());

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
        add(new JLabel("Summary:"),
                new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
        add(summaryPane,
                new GridBagConstraints(2, row, 3, 1, 0.5, 0.5, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));
        row++;
        add(summaryLanguageField,
                new GridBagConstraints(2, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

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
        summaryField.setText(bookData.getSummary());
    }

    @Override
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
            final Integer publishedYear=(Integer) publishedYearField.getValue();
            final Integer pageCount=(Integer) pageCountField.getValue();
            String coverPath=coverFileField.getText();
            String thumbnailPath=null;
            Dimension coverSize=null;
            Dimension thumbnailSize=null;
            if (!StringUtils.isEmpty(coverPath))
            {
                File coverFile=new File(coverPath);
                if (!coverFile.exists()) throw new InvalidDataException("Cover file doesn't exist!", coverFileField);
                coverSize=MediaFileUtils.getImageSize(coverFile);
                if (coverSize==null) throw new InvalidDataException("Cover file is no image!", coverFileField);
                int fileIndex=0;
                String extension="."+FileUtils.getExtension(coverFile);
                File dir=FileUtils.getFile(MediaConfiguration.getRootPath(), "books", "covers");
                while (targetFile==null || targetFile.exists())
                {
                    targetFile=new File(dir, createFileName(title, fileIndex++, extension));
                }
                targetFile.getParentFile().mkdirs();
                FileUtils.copyFile(coverFile, targetFile, false);
                coverPath=FileUtils.getRelativePath(MediaConfiguration.getRootPath(), targetFile.getAbsolutePath());
                if (coverSize.getWidth()>170)
                {
                    thumbnailPath=MediaFileUtils.createThumbnail(MediaConfiguration.PATH_ROOT, coverPath, MediaFileUtils.THUMBNAIL_SIDEBAR_WIDTH, -1, "sb");
                    thumbnailSize=MediaFileUtils.getImageSize(FileUtils.getFile(MediaConfiguration.getRootPath(), thumbnailPath));
                }
            }
            final String summary=summaryField.getText();
            final Language summaryLanguage=summaryLanguageField.getValue();
            if (!StringUtils.isEmpty(summary) && summaryLanguage==null)
                throw new InvalidDataException("No summary language specified!", summaryLanguageField);


            final Dimension finalCoverSize=coverSize;
            final String finalCoverPath=coverPath;
            final String finalThumbnailPath=thumbnailPath;
            final Dimension finalThumbnailSize=thumbnailSize;
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
                    if (!StringUtils.isEmpty(summary)) book.setSummaryText(summaryLanguage, summary);
                    if (finalCoverSize!=null && !StringUtils.isEmpty(finalCoverPath))
                    {
                        MediaFile cover=MediaFileManager.getInstance().createImage(MediaConfiguration.PATH_ROOT);
                        cover.setName(title+" - Cover");
                        cover.setContentType(ContentType.COVER);
                        cover.setFile(finalCoverPath);
                        cover.setWidth(finalCoverSize.width);
                        cover.setHeight(finalCoverSize.height);
                        if (finalThumbnailSize!=null && !StringUtils.isEmpty(finalThumbnailPath))
                        {
                            cover.setThumbnailSidebar(MediaConfiguration.PATH_ROOT, finalThumbnailPath,
                                    finalThumbnailSize.width, finalThumbnailSize.height);
                        }
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

    @Override
	public JComponent getDefaultFocusComponent()
    {
        return titleField;
    }

    private static String createFileName(String title, int index, String extension)
    {
        title=title.toLowerCase();
        title=title.replaceAll("[\\.\\?\\!\\:\\,]", "");
        title=title.replaceAll("\\s+", "_");
        title=title.replaceAll("\u00E4", "ae");
        title=title.replaceAll("\u00FC", "ue");
        title=title.replaceAll("\u00F6", "oe");
        title=title.replaceAll("\u00DF", "ss");
        while (title.endsWith("_")) title=title.substring(0, title.length()-1);
        while (title.startsWith("_")) title=title.substring(1);
        try
        {
            title=URLEncoder.encode(title, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
        if (index==0) return title+extension;
        else return title+"_"+index+extension;
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

    private class CoverMouseListener extends MouseAdapter
    {
        @Override
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
