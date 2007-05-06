package com.kiwisoft.media.dataImport;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ResourceBundle;
import java.net.URLEncoder;

import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.utils.xml.XMLUtils;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;

/**
 * @author Stefan Stiller
 */
public class AmazonDeLoader
{
	private String url;

	public AmazonDeLoader(String url)
	{
		this.url=url;
	}

//	http://www.amazon.de/Winterschmied-Terry-Pratchett/dp/3442546192/ref=sr_1_1/303-2943436-3629057?ie=UTF8&s=books&qid=1175865960&sr=1-1
//	http://www.amazon.de/Kleine-freie-M%C3%A4nner-M%C3%A4rchen-Scheibenwelt/dp/3442463092/ref=sr_1_2/303-2943436-3629057?ie=UTF8&s=books&qid=1175868777&sr=1-2
//	http://www.amazon.de/Gevatter-Tod-Roman-bizarren-Scheibenwelt/dp/349228504X/ref=sr_1_3/303-2943436-3629057?ie=UTF8&s=books&qid=1175868777&sr=1-3
//	http://www.amazon.de/Wer-Angst-Beowulf-Roman-Fantasy/dp/3453065867/ref=sr_1_2/303-2943436-3629057?ie=UTF8&s=books&qid=1175869217&sr=1-2
//	http://www.amazon.de/Wintersmith-Terry-Pratchett/dp/0060890312/ref=sr_1_1/303-2943436-3629057?ie=UTF8&s=books-intl-de&qid=1175960540&sr=1-1
//	http://www.amazon.de/Good-Omens-Accurate-Prophecies-Nutter/dp/0060853980/ref=sr_1_3/303-2943436-3629057?ie=UTF8&s=books-intl-de&qid=1175960540&sr=1-3

	public BookData load() throws IOException
	{
		BookData bookData=new BookData();

		System.out.print("Loading main page...");
		String page=WebUtils.loadURL(url);
//		FileUtils.saveToFile(page, new File(tempPath, "page.html"));
		System.out.println("done");

		System.out.print("Analyzing main page...");
		String imagePageURL=extractImagePageURL(page);
		extractTitleAndAuthor(page, bookData);
		extractProductInformation(page, bookData);
		System.out.println("done");

		if (imagePageURL!=null)
		{
			System.out.print("Loading image page...");
			String imagePage=WebUtils.loadURL(imagePageURL);
//			FileUtils.saveToFile(imagePage, new File(tempPath, "imagePage.html"));
			System.out.println("done");

			System.out.print("Analyzing image page...");
			String imageURL=extractImageURL(imagePage);
			String extension=imageURL.substring(imageURL.lastIndexOf("."));
			System.out.println("done");

			System.out.print("Loading image...");
			byte[] image=WebUtils.loadBytesFromURL(imageURL);
			File file;
			int index=0;
			do
			{
				file=new File(MediaConfiguration.getBookCoverPath(), createFileName(bookData.getTitle(), index++, extension));
			}
			while (file.exists());
			file.getParentFile().mkdirs();
			FileUtils.saveToFile(image, file);
			bookData.setImageFile(file);
			System.out.println("done");
		}

		return bookData;
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
		//noinspection deprecation
		title=URLEncoder.encode(title);
		if (index==0) return title+extension;
		else return title+"_"+index+extension;
	}

	private static void extractProductInformation(String page, BookData bookData)
	{
		int ulStart=page.indexOf("<b class=\"h1\">Produktinformation</b>");
		ulStart=page.indexOf("<ul>", ulStart);
		int ulEnd=page.indexOf("</ul>", ulStart);

		int liEnd=ulStart;
		while (true)
		{
			int liStart=page.indexOf("<li>", liEnd)+4;
			if (liStart<0 || liStart>ulEnd) break;
			liEnd=page.indexOf("</li>", liStart);
			if (liEnd<0 || liEnd>ulEnd) liEnd=ulEnd;
			String liContent=page.substring(liStart, liEnd).trim();
			Matcher matcher=Pattern.compile("<b>(.+):</b> (\\d+) Seiten").matcher(liContent);
			if (matcher.matches())
			{
				bookData.setBinding(matcher.group(1));
				bookData.setPageCount(Integer.parseInt(matcher.group(2)));
				continue;
			}
			matcher=Pattern.compile("<b>Verlag:</b> (.+); Auflage: (.*) \\([a-zA-Z\\.]*\\s*(\\d+)\\)").matcher(liContent);
			if (matcher.matches())
			{
				bookData.setPublisher(matcher.group(1));
				bookData.setEdition(matcher.group(2));
				bookData.setPublishedYear(Integer.parseInt(matcher.group(3)));
				continue;
			}
			matcher=Pattern.compile("<b>Verlag:</b> (.+) \\([a-zA-Z\\.]*\\s*(\\d+)\\)").matcher(liContent);
			if (matcher.matches())
			{
				bookData.setPublisher(matcher.group(1));
				bookData.setPublishedYear(Integer.parseInt(matcher.group(2)));
				continue;
			}
			matcher=Pattern.compile("<b>Sprache:</b> (.+)").matcher(liContent);
			if (matcher.matches())
			{
				String languageName=matcher.group(1);
				Language language=mapLanguage(languageName);
				bookData.setLanguage(language);
				continue;
			}
			matcher=Pattern.compile("<b>ISBN-10:</b> (.+)").matcher(liContent);
			if (matcher.matches())
			{
				bookData.setIsbn10(matcher.group(1));
				continue;
			}
			matcher=Pattern.compile("<b>ISBN-13:</b> (.+)").matcher(liContent);
			if (matcher.matches())
			{
				bookData.setIsbn13(matcher.group(1));
				continue;
			}
			if (liContent.startsWith("<b>\nProduktma&szlig;e: \n</b>")) continue;
			if (liContent.startsWith("<b>Durchschnittliche Kundenbewertung:</b>")) continue;
			if (liContent.startsWith("<b>Amazon.de Verkaufsrang:</b>")) continue;
			if (liContent.startsWith("<b>Weitere Ausgaben:</b>")) continue;
			System.err.println("Unhandled product information: "+liContent);
		}
	}

	private static Language mapLanguage(String languageName)
	{
		String code=ResourceBundle.getBundle(AmazonDeLoader.class.getName()).getString(languageName);
		return LanguageManager.getInstance().getLanguageBySymbol(code);
	}

	private static void extractTitleAndAuthor(String page, BookData bookData)
	{
		int index=page.indexOf("<b class=\"sans\">")+16;
		int index2=page.indexOf("</b>", index);
		String title=page.substring(index, index2).trim();
		title=removeTitleSuffix(title).trim();
		bookData.setTitle(title);

		index=page.indexOf("<br />", index)+6;
		index2=page.indexOf("<br />", index);
		String authorString=page.substring(index, index2).trim();
		authorString=XMLUtils.removeTags(authorString);
		if (authorString.startsWith("von ")) authorString=authorString.substring(4);
		index=0;
		while ((index2=authorString.indexOf("),", index))>0)
		{
			addAuthor(authorString.substring(index, index2+1).trim(), bookData);
			index=index2+2;
		}
		addAuthor(authorString.substring(index).trim(), bookData);
	}

	private static void addAuthor(String author, BookData bookData)
	{
		Matcher matcher=Pattern.compile("(.*)\\s*\\((Autor|\u00DCbersetzer)\\)").matcher(author);
		if (matcher.matches())
		{
			String name=matcher.group(1).trim();
			String type=matcher.group(2);
			if ("Autor".equals(type)) bookData.addAuthor(name);
			else if ("\u00DCbersetzer".equals(type)) bookData.addTranslator(name);
		}
		else bookData.addAuthor(author);
	}

	private static String removeTitleSuffix(String title)
	{
		Pattern pattern=Pattern.compile("(.+)\\s*\\((Gebundene Ausgabe|Taschenbuch|Broschiert)\\)\\s*");
		Matcher matcher=pattern.matcher(title);
		if (matcher.matches()) title=matcher.group(1);
		pattern=Pattern.compile("(.+)\\s*\\((\\s*Fantasy)\\).\\s*");
		matcher=pattern.matcher(title);
		if (matcher.matches()) title=matcher.group(1);
		pattern=Pattern.compile("(.+)\\s*(Roman).\\s*");
		matcher=pattern.matcher(title);
		if (matcher.matches()) title=matcher.group(1);
		return title;
	}

	private static String extractImageURL(String page)
	{
		int index=page.indexOf("<div id=\"imageViewerDiv\">");
		index=page.indexOf("<img", index);
		int index2=page.indexOf(">", index);
		return XMLUtils.getAttribute(page.substring(index, index2), "src");
	}

	private static String extractImagePageURL(String page)
	{
		int index=page.indexOf("Gr\u00F6\u00DFeres Bild");
		if (index<0) return null;
		index=page.lastIndexOf("http://www.amazon.de/gp/product/images", index);
		int index2=page.indexOf("'", index);
		if (index2<0) return null;
		return page.substring(index, index2);
	}
}
