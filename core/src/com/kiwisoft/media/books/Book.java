package com.kiwisoft.media.books;

import java.util.Set;
import java.util.Collection;

import com.kiwisoft.media.Language;
import com.kiwisoft.media.show.Summary;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 */
public class Book extends IDObject
{
	public static final String TITLE="title";
	public static final String PUBLISHER="publisher";
	public static final String EDITION="edition";
	public static final String PUBLISHED_YEAR="publishedYear";
	public static final String PAGE_COUNT="pageCount";
	public static final String ISBN_10="isbn10";
	public static final String ISBN_13="isbn13";
	public static final String BINDING="binding";
	public static final String LANGUAGE="language";
	public static final String COVER="cover";
	public static final String AUTHORS="authors";
	public static final String TRANSLATORS="translators";

	private String title;
	private String publisher;
	private String edition;
	private Integer publishedYear;
	private Integer pageCount;
	private String isbn10;
	private String isbn13;
	private String binding;

	public Book()
	{
	}

	public Book(DBDummy dummy)
	{
		super(dummy);
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		String oldTitle=this.title;
		this.title=title;
		setModified(TITLE, oldTitle, title);
	}

	public String getPublisher()
	{
		return publisher;
	}

	public void setPublisher(String publisher)
	{
		String oldPublisher=this.publisher;
		this.publisher=publisher;
		setModified(PUBLISHER, oldPublisher, publisher);
	}

	public String getEdition()
	{
		return edition;
	}

	public void setEdition(String edition)
	{
		String oldEdition=this.edition;
		this.edition=edition;
		setModified(EDITION, oldEdition, edition);
	}

	public Integer getPublishedYear()
	{
		return publishedYear;
	}

	public void setPublishedYear(Integer publishedYear)
	{
		Integer oldPublishedYear=this.publishedYear;
		this.publishedYear=publishedYear;
		setModified(PUBLISHED_YEAR, oldPublishedYear, publishedYear);
	}

	public Integer getPageCount()
	{
		return pageCount;
	}

	public void setPageCount(Integer pageCount)
	{
		Integer oldPageCount=this.pageCount;
		this.pageCount=pageCount;
		setModified(PAGE_COUNT, oldPageCount, pageCount);
	}

	public String getIsbn10()
	{
		return isbn10;
	}

	public void setIsbn10(String isbn10)
	{
		String oldIsbn10=this.isbn10;
		this.isbn10=isbn10;
		setModified(ISBN_10, oldIsbn10, isbn10);
	}

	public String getIsbn13()
	{
		return isbn13;
	}

	public void setIsbn13(String isbn13)
	{
		String oldIsbn13=this.isbn13;
		this.isbn13=isbn13;
		setModified(ISBN_13, oldIsbn13, isbn13);
	}

	public String getIsbn()
	{
		if (!StringUtils.isEmpty(isbn13)) return isbn13;
		if (!StringUtils.isEmpty(isbn10)) return isbn10;
		return null;
	}

	public String getBinding()
	{
		return binding;
	}

	public void setBinding(String binding)
	{
		String oldBinding=this.binding;
		this.binding=binding;
		setModified(BINDING, oldBinding, binding);
	}

	public Language getLanguage()
	{
		return (Language)getReference(LANGUAGE);
	}

	public void setLanguage(Language language)
	{
		setReference(LANGUAGE, language);
	}

	public MediaFile getCover()
	{
		return (MediaFile)getReference(COVER);
	}

	public void setCover(MediaFile cover)
	{
		setReference(COVER, cover);
	}

	public void addAuthor(Person author)
	{
		createAssociation(AUTHORS, author);
	}

	public void removeAuthor(Person author)
	{
		dropAssociation(AUTHORS, author);
	}

	public Set<Person> getAuthors()
	{
		return getAssociations(AUTHORS);
	}

	public void setAuthors(Collection<Person> authors)
	{
		setAssociations(AUTHORS, authors);
	}

	public void addTranslator(Person translator)
	{
		createAssociation(TRANSLATORS, translator);
	}

	public void removeTranslator(Person translator)
	{
		dropAssociation(TRANSLATORS, translator);
	}

	public Set<Person> getTranslators()
	{
		return getAssociations(TRANSLATORS);
	}

	public void setTranslators(Collection<Person> translators)
	{
		setAssociations(TRANSLATORS, translators);
	}

    public void setSummaryText(Language language, String text)
    {
        Summary summary=getSummary(language);
        if (!StringUtils.isEmpty(text))
        {
            if (summary==null)
            {
                summary=new Summary();
                summary.setBook(this);
                summary.setLanguage(language);
            }
            summary.setSummary(text);
        }
        else
        {
            if (summary!=null) summary.delete();
        }
    }

    public String getSummaryText(Language language)
    {
        Summary summary=getSummary(language);
        return summary!=null ? summary.getSummary() : null;
    }

    private Summary getSummary(Language language)
    {
        return DBLoader.getInstance().load(Summary.class, null, "book_id=? and language_id=?", getId(), language.getId());
    }

}
