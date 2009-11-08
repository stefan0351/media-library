package com.kiwisoft.media.books;

import com.kiwisoft.media.IndexByUtils;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.Summary;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.utils.StringUtils;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Set;

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
	public static final String SHOW="show";
	public static final String SERIES_NAME="seriesName";
	public static final String SERIES_NUMBER="seriesNumber";
	public static final String INDEX_BY="indexBy";

	private String title;
	private String publisher;
	private String edition;
	private Integer publishedYear;
	private Integer pageCount;
	private String isbn10;
	private String isbn13;
	private String binding;
	private String seriesName;
	private Integer seriesNumber;
	private String indexBy;

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

	public String getSeriesName()
	{
		return seriesName;
	}

	public void setSeriesName(String seriesName)
	{
		String oldName=this.seriesName;
		this.seriesName=seriesName;
		setModified(SERIES_NAME, oldName, this.seriesName);
	}

	public Integer getSeriesNumber()
	{
		return seriesNumber;
	}

	public void setSeriesNumber(Integer seriesNumber)
	{
		Integer oldNumber=this.seriesNumber;
		this.seriesNumber=seriesNumber;
		setModified(SERIES_NUMBER, oldNumber, this.seriesNumber);
	}

	public String getIndexBy()
	{
		return indexBy;
	}

	public void setIndexBy(String indexBy)
	{
		String oldIndexBy=this.indexBy;
		this.indexBy=indexBy;
		setModified(INDEX_BY, oldIndexBy, this.indexBy);
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
		return (Language) getReference(LANGUAGE);
	}

	public void setLanguage(Language language)
	{
		setReference(LANGUAGE, language);
	}

	public MediaFile getCover()
	{
		return (MediaFile) getReference(COVER);
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

	public Show getShow()
	{
		return (Show) getReference(SHOW);
	}

	public void setShow(Show show)
	{
		Show oldShow=getShow();
		setReference(SHOW, show);
		if (oldShow!=null) oldShow.removeBook(this);
		if (show!=null) show.addBook(this);
	}

	public String getFullTitle()
	{
		String title=getTitle();
		if (!StringUtils.isEmpty(title))
		{
			StringBuilder buffer=new StringBuilder(title);
			if (!StringUtils.isEmpty(seriesName) && buffer.indexOf(seriesName)==-1)
			{
				buffer.append(" (");
				buffer.append(seriesName);
				if (seriesNumber!=null) buffer.append(" #").append(seriesNumber);
				buffer.append(")");
			}
			return buffer.toString();
		}
		return null;
	}

	public String getSeriesTitle()
	{
		if (!StringUtils.isEmpty(seriesName))
		{
			if (seriesNumber!=null) return seriesName+" #"+seriesNumber;
		}
		return seriesName;
	}

	public static String createIndexBy(String title, String series, Integer number, Language language)
	{
		if (!StringUtils.isEmpty(title))
		{
			StringBuilder buffer=new StringBuilder();
			if (LanguageManager.GERMAN.equals(language))
				buffer.append(IndexByUtils.createGermanIndexBy(title));
			else
				buffer.append(IndexByUtils.createIndexBy(title));
			if (!StringUtils.isEmpty(series))
			{
				buffer.append(" - ");
				if (LanguageManager.GERMAN.equals(language))
					buffer.append(IndexByUtils.createGermanIndexBy(series));
				else
					buffer.append(IndexByUtils.createIndexBy(series));
				if (number!=null) buffer.append(" ").append(new DecimalFormat("0000").format(number));
			}
			return buffer.toString();
		}
		return null;
	}
}
