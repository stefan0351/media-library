package com.kiwisoft.media.dataimport;

import com.amazonaws.a2s.AmazonA2S;
import com.amazonaws.a2s.AmazonA2SClient;
import com.amazonaws.a2s.AmazonA2SException;
import com.amazonaws.a2s.AmazonA2SLocale;
import com.amazonaws.a2s.model.*;
import static com.kiwisoft.media.dataimport.ImportUtils.replaceHtmlFormatTags;
import com.kiwisoft.media.books.BookManager;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.WebUtils;
import static com.kiwisoft.utils.xml.XMLUtils.removeTags;
import static com.kiwisoft.utils.xml.XMLUtils.unescapeHtml;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stefan Stiller
 *
 * http://docs.amazonwebservices.com/AWSECommerceService/2008-08-19/DG/
 */
public class AmazonDeLoader
{
    private static final String ASSOCIATE_TAG="nurbjoern";
    private static final String AWS_ACCESS_KEY_ID="0701HTDSBK2MTTZ94W82";
	private static final String SECRET_ACCESS_KEY="h3XZzVMKNTbBnla4u/PbIs/zw2Xv0qrUEs8gkUTo";

    private String isbn;

    public AmazonDeLoader(String isbn)
    {
        this.isbn=isbn;
    }

    public BookData load() throws IOException, AmazonA2SException
    {
        AmazonA2S service=new AmazonA2SClient(AWS_ACCESS_KEY_ID, ASSOCIATE_TAG, AmazonA2SLocale.DE);

        String isbn=BookManager.filterIsbn(this.isbn);

        ItemLookupRequest request=new ItemLookupRequest();
        request.setSearchIndex("Books");
        if (isbn.length()==13) request.setIdType("EAN");
        else request.setIdType("ISBN");
        request.setItemId(Collections.singletonList(isbn));
        request.setResponseGroup(Arrays.asList("Medium"));

        ItemLookupResponse response;
        try
        {
            response=service.itemLookup(request);
        }
        catch (AmazonA2SException e)
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e1)
            {
                e1.printStackTrace();
            }
            response=service.itemLookup(request.withSearchIndex("ForeignBooks"));
        }

        for (Items items : response.getItems())
        {
            for (Item item : items.getItem())
            {
                ItemAttributes itemAttributes=item.getItemAttributes();

                BookData bookData=new BookData();
                bookData.setTitle(itemAttributes.getTitle());
                List<Creator> creators=itemAttributes.getCreator();
                if (creators!=null)
                {
                    for (Creator creator : creators)
                    {
                        if ("Autor".equals(creator.getRole())) bookData.addAuthor(creator.getValue());
                        else
                            if ("\u00dcbersetzer".equals(creator.getRole())) bookData.addTranslator(creator.getValue());
                    }
                }
                bookData.addAuthors(itemAttributes.getAuthor());
                bookData.setPublisher(itemAttributes.getPublisher());
                String publicationDate=itemAttributes.getPublicationDate();
                if (!StringUtils.isEmpty(publicationDate)) bookData.setPublishedYear(getYear(publicationDate));
                bookData.setBinding(itemAttributes.getBinding());
                bookData.setEdition(itemAttributes.getEdition());
                bookData.setIsbn10(itemAttributes.getISBN());
                bookData.setIsbn13(itemAttributes.getEAN());
                if (itemAttributes.getNumberOfPages()!=null) bookData.setPageCount(itemAttributes.getNumberOfPages().intValue());
                if (itemAttributes.getLanguages()!=null)
                {
                    for (com.amazonaws.a2s.model.Language language : itemAttributes.getLanguages().getLanguage())
                    {
                        System.out.println("AmazonDeLoader.load: language = "+language.getName());
                    }
                }
                if (item.getLargeImage()!=null)
                {
                    byte[] coverData=WebUtils.loadBytesFromURL(item.getLargeImage().getURL());
                    File tempDir=new File("tmp", "books");
                    tempDir.mkdirs();
                    File coverFile=File.createTempFile("cover", ".jpg", tempDir);
                    coverFile.deleteOnExit();
                    FileUtils.saveToFile(coverData, coverFile);
                    bookData.setImageFile(coverFile);
                }
                if (item.getEditorialReviews()!=null)
                {
                    for (EditorialReview review : item.getEditorialReviews().getEditorialReview())
                    {
                        if ("Aus der Amazon.de-Redaktion".equalsIgnoreCase(review.getSource())
                                || "Amazon.co.uk".equalsIgnoreCase(review.getSource()))
                        {
                            String summary=trimLines(unescapeHtml(removeTags(replaceHtmlFormatTags(review.getContent()))));
                            bookData.setSummary(summary);
                        }
                    }
                }
                if (StringUtils.isEmpty(bookData.getSummary()))
                {
                    String detailPage=WebUtils.loadURL(item.getDetailPageURL());
                    bookData.setSummary(getDescription(detailPage, "Kurzbeschreibung"));
                }
                return bookData;
            }
        }
        return null;
    }

    private int getYear(String dateString)
    {
        for (String pattern : new String[]{"yyyy-MM-dd", "yyyy-MM"})
        {
            try
            {
                Date date=new SimpleDateFormat(pattern).parse(dateString);
                Calendar calendar=Calendar.getInstance();
                calendar.setTime(date);
                return calendar.get(Calendar.YEAR);
            }
            catch (ParseException e)
            {
            }
        }
        return 0;
    }

    private String getDescription(String detailPage, String title)
    {
        Matcher matcher=Pattern.compile("<div[^>]*id=\"productDescription\"").matcher(detailPage);
        if (matcher.find())
        {
            String header="<b>"+title+"</b><br />";
            int start=detailPage.indexOf(header, matcher.start());
            if (start>0)
            {
                start+=header.length();
                int end=detailPage.indexOf("<br /><br />", start);
                int end2=detailPage.indexOf("</div>", start);
                if (end<0 || (end2>0 && end2<end)) end=end2;
                String description=detailPage.substring(start, end);
                description=trimLines(unescapeHtml(removeTags(replaceHtmlFormatTags(description))));
                return description;
            }
        }
        return null;
    }

    private static String trimLines(String text)
    {
        if (text==null) return text;
        String[] lines=text.split("\\[br/\\]");
        StringBuilder builder=new StringBuilder();
        for (String line : lines)
        {
            line=line.trim();
            if (builder.length()>0) builder.append("[br/]\n");
            builder.append(line);
        }
        return builder.toString();
    }
}
