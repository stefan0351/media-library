/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 12, 2003
 * Time: 6:51:02 PM
 */
package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.Country;
import com.kiwisoft.media.CountryManager;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieManager;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.PersonManager;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.xml.XMLUtils;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.html.HtmlUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.File;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.Map;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImportUtils
{
	private final static Log log=LogFactory.getLog(ImportUtils.class);

	public static final DateFormat DATE_FORMAT=new SimpleDateFormat("d.M.yyyy H:mm");
	public static boolean USE_CACHE=Boolean.getBoolean("kiwisoft.media.downloadCache");

	public static enum KeyType {IMDB, TV_COM}

	private ImportUtils()
	{
	}

	public static String toPreformattedText(String text, boolean ignoreParagraphs)
	{
		if (text==null) return null;
		if (!ignoreParagraphs)
		{
			text=text.replaceAll("<p *>", "[br/][br/]");
			text=text.replaceAll("</p>", "");
		}
		text=replaceTags(text, "i");
		text=replaceTags(text, "u");
		text=replaceTags(text, "b");
		text=replaceTags(text, "sup");
		text=replaceTags(text, "sub");
		text=replaceTags(text, "em");
		text=replaceTags(text, "br");
		return HtmlUtils.trimUnescape(XMLUtils.removeTags(text));
	}

	public static String toPreformattedText(String text)
	{
		return toPreformattedText(text, false);
	}

	private static String replaceTags(String line, String name)
	{
		line=Pattern.compile("<"+name+"[^>]*>", Pattern.CASE_INSENSITIVE).matcher(line).replaceAll("["+name+"]");
		line=Pattern.compile("</"+name+" *>", Pattern.CASE_INSENSITIVE).matcher(line).replaceAll("[/"+name+"]");
		line=Pattern.compile("<"+name+"[^>]*/>", Pattern.CASE_INSENSITIVE).matcher(line).replaceAll("["+name+"/]");
		return line;
	}

	public static String loadUrl(String url) throws IOException
	{
		return loadUrl(url, null, null);
	}

	public static String loadUrl(String url, String charSetName) throws IOException
	{
		return loadUrl(url, null, charSetName);
	}

	public static String loadUrl(String url, Map<String, String> properties) throws IOException
	{
		return loadUrl(url, properties, null);
	}

	public static String loadUrl(String url, Map<String, String> properties, String charSetName) throws IOException
	{
		if (charSetName==null) charSetName=Charset.defaultCharset().name();
		if (USE_CACHE)
		{
			File file=new File("tmp"+File.separator+"web", URLEncoder.encode(url, "UTF-8")+".html");
			if (file.exists())
			{
				log.debug("Loading cached data from "+file.getAbsolutePath());
				return FileUtils.loadFile(file, charSetName);
			}
		}
		int tries=0;
		while (true)
		{
			try
			{
				String page=WebUtils.loadURL(url, properties, charSetName);
				if (USE_CACHE)
				{
					File file=new File("tmp"+File.separator+"web", URLEncoder.encode(url, "UTF-8")+".html");
					file.getParentFile().mkdirs();
					FileUtils.saveToFile(page, file, charSetName);
				}
				return page;
			}
			catch (IOException e)
			{
				tries++;
				if (tries>=3) throw e;
				else
				{
					try
					{
						System.err.println("Failed to load URL "+url+". Trying again in 1 second.");
						Thread.sleep(1000);
					}
					catch (InterruptedException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public static byte[] loadUrlBinary(String url) throws IOException
	{
		if (USE_CACHE)
		{
			File file=new File("tmp"+File.separator+"web", URLEncoder.encode(url, "UTF-8"));
			if (file.exists())
			{
				log.debug("Loading cached data from "+file.getAbsolutePath());
				return FileUtils.loadBinaryFile(file);
			}
		}
		int tries=0;
		while (true)
		{
			try
			{
				byte[] data=WebUtils.loadBytesFromURL(url);
				if (USE_CACHE)
				{
					File file=new File("tmp"+File.separator+"web", URLEncoder.encode(url, "UTF-8"));
					file.getParentFile().mkdirs();
					FileUtils.saveToFile(data, file);
				}
				return data;
			}
			catch (IOException e)
			{
				tries++;
				if (tries>=3) throw e;
			}
		}
	}

	public static void matchMovie(MovieData movieData)
	{
		Movie movie=MovieManager.getInstance().getMovieByIMDbKey(movieData.getImdbKey());
		if (movie!=null)
		{
			movieData.setMovies(Collections.singleton(movie));
		}
		else
		{
			Set<Movie> movies=new HashSet<Movie>();
			movies.addAll(MovieManager.getInstance().getMoviesByTitle(movieData.getTitle()));
			if (!StringUtils.isEmpty(movieData.getGermanTitle())) movies.addAll(MovieManager.getInstance().getMoviesByTitle(movieData.getGermanTitle()));
			movieData.setMovies(movies);
		}
	}


	public static void matchPerson(CreditData creditData, KeyType keyType)
	{
		Set<Person> persons=findPerson(creditData.getKey(), keyType, creditData.getName(), creditData.getListedAs());
		creditData.setPersons(persons);
	}

	private static Set<Person> findPerson(String key, KeyType keyType, String... names)
	{
		if (!StringUtils.isEmpty(key))
		{
			Person person=null;
			if (keyType==KeyType.IMDB) person=PersonManager.getInstance().getPersonByIMDbKey(key);
			else if (keyType==KeyType.TV_COM) person=PersonManager.getInstance().getPersonByTVcomKey(key);
			if (person!=null) return Collections.singleton(person);
		}
		Set<Person> persons=new HashSet<Person>();
		for (String name : names)
		{
			if (!StringUtils.isEmpty(name))
			{
				Set<Person> personsByName=PersonManager.getInstance().getPersonsByName(name);
				if (StringUtils.isEmpty(key)) persons.addAll(personsByName);
				else
				{
					for (Person person : personsByName)
					{
						if (!key.equals(person.getImdbKey())) persons.add(person);
					}
				}
			}
		}
		return persons;
	}

	public static void matchLanguage(LanguageData languageData)
	{
		Language language=LanguageManager.getInstance().getLanguageByName(languageData.getName());
		if (language!=null)
		{
			languageData.setLanguages(Collections.singleton(language));
			languageData.setSymbol(language.getSymbol());
		}
		else languageData.setLanguages(Collections.<Language>emptySet());
	}

	public static void matchCountry(CountryData countryData)
	{
		if ("UK".equalsIgnoreCase(countryData.getName())) countryData.setName("Great Britain");
		Country country=CountryManager.getInstance().getCountryByName(countryData.getName());
		if (country!=null)
		{
			countryData.setCountries(Collections.singleton(country));
			countryData.setSymbol(country.getSymbol());
		}
		else countryData.setCountries(Collections.<Country>emptySet());

	}
}
