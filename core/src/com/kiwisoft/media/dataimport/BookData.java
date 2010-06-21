package com.kiwisoft.media.dataimport;

import java.util.*;
import java.io.File;

import com.kiwisoft.media.Language;

/**
 * @author Stefan Stiller
 */
public class BookData
{
	private String title;
	private Set<String> authors=new LinkedHashSet<String>();
	private Set<String> translators=new LinkedHashSet<String>();
	private String binding;
	private Integer pageCount;
	private String publisher;
	private Integer publishedYear;
	private Language language;
	private String isbn10;
	private String isbn13;
	private String edition;
	private File imageFile;
    private Map<Language, String> summaries=new HashMap<Language, String>();
	private String originalTitle;

	public void setTitle(String title)
	{
		this.title=title;
	}

	public String getTitle()
	{
		return title;
	}

	public void addAuthor(String author)
	{
		authors.add(author);
	}

	public void addAuthors(Collection<String> authors)
	{
		authors.addAll(authors);
	}

	public void addTranslator(String translator)
	{
		translators.add(translator);
	}

	public void setBinding(String binding)
	{
		this.binding=binding;
	}

	public void setPageCount(Integer pageCount)
	{
		this.pageCount=pageCount;
	}

	public void setPublisher(String publisher)
	{
		this.publisher=publisher;
	}

	public void setPublishedYear(Integer publishedYear)
	{
		this.publishedYear=publishedYear;
	}

	@Override
	public String toString()
	{
		return "Book [\n" +
			   "\ttitle="+title+"\n"+
			   "\toriginalTitle="+originalTitle+"\n"+
			   "\tauthors="+authors+"\n"+
			   "\ttranslators="+translators+"\n"+
			   "\tpages="+pageCount+"\n"+
			   "\tbinding="+binding+"\n"+
			   "\tpublisher="+publisher+"\n"+
			   "\tpublished="+publishedYear+"\n"+
			   "\tedition="+edition+"\n"+
			   "\tlanguage="+language+"\n"+
			   "\tisbn-10="+isbn10+"\n"+
			   "\tisbn-13="+isbn13+"\n"+
			   "\timage="+(imageFile!=null ? imageFile.getAbsolutePath() : null)+"\n"+
			   "\tsummaries="+summaries+"\n"+
			   "]";
	}

	public void setLanguage(Language language)
	{
		this.language=language;
	}

	public void setIsbn10(String isbn10)
	{
		this.isbn10=isbn10;
	}

	public void setIsbn13(String isbn13)
	{
		this.isbn13=isbn13;
	}

	public void setEdition(String edition)
	{
		this.edition=edition;
	}

	public void setImageFile(File imageFile)
	{
		this.imageFile=imageFile;
	}

	public Set<String> getAuthors()
	{
		return authors;
	}

	public Set<String> getTranslators()
	{
		return translators;
	}

	public String getBinding()
	{
		return binding;
	}

	public Integer getPageCount()
	{
		return pageCount;
	}

	public String getPublisher()
	{
		return publisher;
	}

	public Integer getPublishedYear()
	{
		return publishedYear;
	}

	public Language getLanguage()
	{
		return language;
	}

	public String getIsbn10()
	{
		return isbn10;
	}

	public String getIsbn13()
	{
		return isbn13;
	}

	public String getEdition()
	{
		return edition;
	}

	public File getImageFile()
	{
		return imageFile;
	}

    public void setSummary(Language language, String summary)
    {
        this.summaries.put(language, summary);
    }

    public String getSummary(Language language)
    {
        return summaries.get(language);
    }

	public void setOriginalTitle(String originalTitle)
	{
		this.originalTitle=originalTitle;
	}

	public String getOriginalTitle()
	{
		return originalTitle;
	}
}
