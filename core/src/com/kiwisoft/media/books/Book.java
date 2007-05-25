package com.kiwisoft.media.books;

import java.util.Set;
import java.util.Collection;

import com.kiwisoft.media.Language;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.utils.db.DBAssociation;

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

	public static final DBAssociation<Book, Person> AUTHORS_ASSOCIATION=DBAssociation.getAssociation(AUTHORS, Book.class, Person.class);
	public static final DBAssociation<Book, Person> TRANSLATORS_ASSOCIATION=DBAssociation.getAssociation(TRANSLATORS, Book.class, Person.class);

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
		setModified();
		firePropertyChange(TITLE, oldTitle, title);
	}

	public String getPublisher()
	{
		return publisher;
	}

	public void setPublisher(String publisher)
	{
		String oldPublisher=this.publisher;
		this.publisher=publisher;
		setModified();
		firePropertyChange(PUBLISHER, oldPublisher, publisher);
	}

	public String getEdition()
	{
		return edition;
	}

	public void setEdition(String edition)
	{
		String oldEdition=this.edition;
		this.edition=edition;
		setModified();
		firePropertyChange(EDITION, oldEdition, edition);
	}

	public Integer getPublishedYear()
	{
		return publishedYear;
	}

	public void setPublishedYear(Integer publishedYear)
	{
		Integer oldPublishedYear=this.publishedYear;
		this.publishedYear=publishedYear;
		setModified();
		firePropertyChange(PUBLISHED_YEAR, oldPublishedYear, publishedYear);
	}

	public Integer getPageCount()
	{
		return pageCount;
	}

	public void setPageCount(Integer pageCount)
	{
		Integer oldPageCount=this.pageCount;
		this.pageCount=pageCount;
		setModified();
		firePropertyChange(PAGE_COUNT, oldPageCount, pageCount);
	}

	public String getIsbn10()
	{
		return isbn10;
	}

	public void setIsbn10(String isbn10)
	{
		String oldIsbn10=this.isbn10;
		this.isbn10=isbn10;
		setModified();
		firePropertyChange(ISBN_10, oldIsbn10, isbn10);
	}

	public String getIsbn13()
	{
		return isbn13;
	}

	public void setIsbn13(String isbn13)
	{
		String oldIsbn13=this.isbn13;
		this.isbn13=isbn13;
		setModified();
		firePropertyChange(ISBN_13, oldIsbn13, isbn13);
	}

	public String getBinding()
	{
		return binding;
	}

	public void setBinding(String binding)
	{
		String oldBinding=this.binding;
		this.binding=binding;
		setModified();
		firePropertyChange(BINDING, oldBinding, binding);
	}

	public Language getLanguage()
	{
		return (Language)getReference(LANGUAGE);
	}

	public void setLanguage(Language language)
	{
		setReference(LANGUAGE, language);
	}

	public Picture getCover()
	{
		return (Picture)getReference(COVER);
	}

	public void setCover(Picture cover)
	{
		setReference(COVER, cover);
	}

	public void addAuthor(Person author)
	{
		AUTHORS_ASSOCIATION.addAssociation(this, author);
	}

	public void removeAuthor(Person author)
	{
		AUTHORS_ASSOCIATION.removeAssociation(this, author);
	}

	public Set<Person> getAuthors()
	{
		return AUTHORS_ASSOCIATION.getAssociations(this);
	}

	public void setAuthors(Collection<Person> authors)
	{
		AUTHORS_ASSOCIATION.setAssociations(this, authors);
	}

	public void addTranslator(Person translator)
	{
		TRANSLATORS_ASSOCIATION.addAssociation(this, translator);
	}

	public void removeTranslator(Person translator)
	{
		TRANSLATORS_ASSOCIATION.removeAssociation(this, translator);
	}

	public Set<Person> getTranslators()
	{
		return TRANSLATORS_ASSOCIATION.getAssociations(this);
	}

	public void setTranslators(Collection<Person> translators)
	{
		TRANSLATORS_ASSOCIATION.setAssociations(this, translators);
	}


	@Override
	public void delete()
	{
		AUTHORS_ASSOCIATION.setAssociations(this, null);
		TRANSLATORS_ASSOCIATION.setAssociations(this, null);
		super.delete();
	}
}