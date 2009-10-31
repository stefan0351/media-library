package com.kiwisoft.media.dataimport;

import com.kiwisoft.html.HtmlUtils;
import com.kiwisoft.html.CssClassFilter;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.books.BookManager;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.PlainTextFilter;
import com.kiwisoft.utils.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stefan Stiller
 */
public class AmazonDeLoader
{
	private String isbn;
	private Pattern authorPattern;
	private Pattern pageCountPattern;
	private Pattern bindingPattern;
	private Pattern publisherPattern;

	public AmazonDeLoader(String isbn)
	{
		this.isbn=isbn;
	}

	public BookData load() throws Exception
	{
		Pattern titlePattern=Pattern.compile("(.*)\\((?:Gebundene Ausgabe|Taschenbuch|Broschiert)\\)");
		authorPattern=Pattern.compile("(.*) \\((Autor|Illustrator|Übersetzer)\\)");
		pageCountPattern=Pattern.compile("(\\d+) Seiten");
		bindingPattern=Pattern.compile("(Gebundene Ausgabe|Taschenbuch|Broschiert)");
		publisherPattern=Pattern.compile("(.+?)(?:; Auflage: (.+?))?(?: \\(.*(\\d{4})\\))?");

		String isbn=BookManager.filterIsbn(this.isbn);
		String html=ImportUtils.loadUrl("http://www.amazon.de/gp/search/ref=sr_adv_b/?search-alias=stripbooks&field-isbn="+isbn);
		Parser parser=new Parser();
		parser.setInputHTML(html);

		CompositeTag bodyElement=(CompositeTag) HtmlUtils.findFirst(parser, "body");

		CompositeTag titleElement=(CompositeTag) HtmlUtils.findFirst(bodyElement, "span#btAsinTitle");
		if (titleElement==null) return null;

		BookData bookData=new BookData();
		bookData.setTitle(HtmlUtils.trimUnescape(titleElement.toPlainTextString()));
		Matcher matcher=titlePattern.matcher(bookData.getTitle());
		if (matcher.matches()) bookData.setTitle(matcher.group(1).trim());
		parseAuthors(titleElement, bookData);
		parseProductInformation(bodyElement, bookData);
		parseDescription(bodyElement, bookData);
		parseImage(bodyElement, bookData);
		return bookData;
	}

	private void parseDescription(CompositeTag bodyElement, BookData bookData) throws ParserException
	{
		CompositeTag descriptionElement=(CompositeTag) HtmlUtils.findFirst(bodyElement, "div#productDescription");
		if (descriptionElement==null) return;
		NodeList contentElements=new NodeList();
		descriptionElement.collectInto(contentElements, new OrFilter(new CssClassFilter("productDescriptionSource"),
																	 new CssClassFilter("productDescriptionWrapper")));
		String source=null;
		for (NodeIterator it=contentElements.elements();it.hasMoreNodes();)
		{
			CompositeTag tag=(CompositeTag) it.nextNode();
			if (tag.getAttribute("class").contains("productDescriptionSource")) source=HtmlUtils.trimUnescape(tag.toPlainTextString());
			else
			{
				if ("Kurzbeschreibung".equals(source) || "Aus der Amazon.de-Redaktion".equals(source)
						|| "Amazon.co.uk".equals(source) || "Amazon.com".equals(source) || "Synopsis".equals(source)
						|| "Der Autor über sein Buch".equals(source)
						|| "Umschlagtext".equals(source))
				{
					String summary=ImportUtils.toPreformattedText(tag.getChildrenHTML());
					if (StringUtils.isEmpty(bookData.getSummary()) || bookData.getSummary().length()<summary.length()) bookData.setSummary(summary);
				}
				else if ("Pressestimmen".equals(source) || "Über den Autor".equals(source) || "Autorenporträt".equals(source)) continue;
				else System.err.println("Invalid description source: "+source+"\n"+tag.getChildrenHTML());
			}
		}
	}

	private void parseImage(CompositeTag bodyElement, BookData bookData) throws Exception
	{
		CompositeTag imageCell=(CompositeTag) HtmlUtils.findFirst(bodyElement, "td#prodImageCell");
		if (imageCell!=null)
		{
			CompositeTag linkElement=(CompositeTag) HtmlUtils.findFirst(imageCell, "a");
			if (linkElement!=null)
			{
				String imageUrl=linkElement.getAttribute("href");
				if (!imageUrl.startsWith("http")) imageUrl="http://www.amazon.de"+imageUrl;
				String imageHtml=ImportUtils.loadUrl(imageUrl);
				Parser parser=new Parser();
				parser.setInputHTML(imageHtml);
				CompositeTag imagePlaceHolderElement=(CompositeTag) HtmlUtils.findFirst(parser, "div#imagePlaceHolder");
				if (imagePlaceHolderElement!=null)
				{
					Tag imageElement=(Tag) HtmlUtils.findFirst(imagePlaceHolderElement, "img#prodImage");
					byte[] coverData=ImportUtils.loadUrlBinary(imageElement.getAttribute("src"));
					File tempDir=new File("tmp", "books");
					tempDir.mkdirs();
					File coverFile=File.createTempFile("cover", ".jpg", tempDir);
					coverFile.deleteOnExit();
					FileUtils.saveToFile(coverData, coverFile);
					bookData.setImageFile(coverFile);
				}
			}
		}
	}

	private void parseAuthors(CompositeTag titleElement, BookData bookData)
	{
		Matcher matcher;
		Node node=titleElement.getParent();
		StringBuilder authorHtml=new StringBuilder();
		while (node.getNextSibling()!=null)
		{
			node=node.getNextSibling();
			authorHtml.append(node.toPlainTextString());
		}
		String authorText=authorHtml.toString().trim();
		int pos=authorText.indexOf("\n");
		if (pos>0) authorText=authorText.substring(0, pos);
		authorText=HtmlUtils.trimUnescape(authorText);
		if (authorText.startsWith("von "))
		{
			authorText=authorText.substring(4).trim();
			String[] parts=StringUtils.splitAndTrim(authorText, ",");
			for (String part : parts)
			{
				matcher=authorPattern.matcher(part);
				if (matcher.matches())
				{
					String name=matcher.group(1);
					String creditType=matcher.group(2);
					if ("Autor".equals(creditType)) bookData.addAuthor(name);
					else if ("Übersetzer".equals(creditType)) bookData.addTranslator(name);
					else if (!"Illustrator".equals(creditType)) System.err.println("Invalid credit type: "+creditType);
				}
				else System.err.println("Invalid credit pattern: "+part);
			}
		}
		else System.err.println("Invalid author text: "+authorText);
	}

	private void parseProductInformation(CompositeTag bodyElement, BookData bookData)
			throws ParserException
	{
		Matcher matcher;
		NodeList nodeList=new NodeList();
		bodyElement.collectInto(nodeList, new AndFilter(new TagNameFilter("h2"), new PlainTextFilter("Produktinformation")));
		for (NodeIterator it=nodeList.elements(); it.hasMoreNodes();)
		{
			CompositeTag informationNode=(CompositeTag) it.nextNode().getParent();
			informationNode=(CompositeTag) HtmlUtils.findFirst(informationNode, "ul");
			for (NodeIterator itList=HtmlUtils.findAll(informationNode, "li").elements(); itList.hasMoreNodes();)
			{
				String[] information=itList.nextNode().toPlainTextString().split(":", 2);
				String label=information[0].trim();
				String text=information[1].trim();
				Matcher labelMatcher=bindingPattern.matcher(label);
				if (labelMatcher.matches())
				{
					bookData.setBinding(labelMatcher.group(1));
					matcher=pageCountPattern.matcher(text);
					if (matcher.matches()) bookData.setPageCount(Integer.parseInt(matcher.group(1)));
					else System.err.println("Invalid page count pattern: "+text);
				}
				else if ("Verlag".equals(label))
				{
					matcher=publisherPattern.matcher(text);
					if (matcher.matches())
					{
						bookData.setPublisher(matcher.group(1));
						bookData.setEdition(matcher.group(2));
						if (!StringUtils.isEmpty(matcher.group(3))) bookData.setPublishedYear(Integer.parseInt(matcher.group(3)));
					}
					else System.err.println("Invalid publisher pattern: "+text);
				}
				else if ("ISBN-10".equals(label)) bookData.setIsbn10(text);
				else if ("ISBN-13".equals(label)) bookData.setIsbn13(text);
				else if ("Sprache".equals(label))
				{
					if ("Deutsch".equals(text)) bookData.setLanguage(LanguageManager.GERMAN);
					else if ("Englisch".equals(text)) bookData.setLanguage(LanguageManager.ENGLISH);
					else System.err.println("Invalid language: "+text);
				}
				else if ("Originaltitel".equals(label)) bookData.setOriginalTitle(HtmlUtils.trimUnescape(text));
				else if ("Vom Hersteller empfohlenes Alter".equals(label)) continue;
				else if ("Größe und/oder Gewicht".equals(label)) continue;
				else if ("Amazon.de Verkaufsrang".equals(label)) continue;
				else if ("Durchschnittliche Kundenbewertung".equals(label)) continue;
				else System.err.println("Invalid label: "+label);
			}
		}
	}

}