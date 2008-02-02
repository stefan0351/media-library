package com.kiwisoft.media.dataImport;

import java.io.IOException;
import java.io.PrintStream;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.kiwisoft.media.dataImport.BookData;
import com.kiwisoft.media.dataImport.AmazonDeLoader;
import com.kiwisoft.media.books.BookDataDetailsView;
import com.kiwisoft.media.MediaManagerFrame;
import com.kiwisoft.utils.Utils;
import com.kiwisoft.swing.GuiUtils;

/**
 * link=javascript:window.location=%22http://localhost:50001/amazon?%22+encodeURIComponent(document.URL)
 * 
 * @author Stefan Stiller
*/
public class AmazonHttpHandler implements HttpHandler
{
	private final MediaManagerFrame frame;

	public AmazonHttpHandler(MediaManagerFrame frame)
	{
		this.frame=frame;
	}

	public void handle(HttpExchange httpExchange) throws IOException
	{
		BookData bookData;
		httpExchange.getResponseHeaders().set("content-type", "text/html; charset=iso-8859-1");
		httpExchange.sendResponseHeaders(200, 0);
		PrintStream responseBody=new PrintStream(httpExchange.getResponseBody());
		try
		{
			String url=httpExchange.getRequestURI().getQuery();
			responseBody.println("<html><body>");
			responseBody.println("Loading url <a href=\""+url+"\">"+url+"</a>...");
			AmazonDeLoader loader=new AmazonDeLoader(url);
			bookData=loader.load();
			responseBody.println("done");
			responseBody.println("</body></html>");
			responseBody.close();
			httpExchange.close();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			responseBody.println(Utils.toString(e1));
			responseBody.close();
			httpExchange.close();
			return;
		}
		try
		{
			BookDataDetailsView.createDialog(frame, bookData);
		}
		catch (Exception e)
		{
			GuiUtils.handleThrowable(frame, e);
		}
	}
}
