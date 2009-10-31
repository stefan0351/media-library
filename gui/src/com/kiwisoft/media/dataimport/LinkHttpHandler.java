package com.kiwisoft.media.dataimport;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.kiwisoft.media.MediaManagerFrame;
import com.kiwisoft.media.links.LinkDetailsView;
import com.kiwisoft.utils.Utils;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.swing.GuiUtils;

/**
 * link=javascript:window.location=%22http://localhost:50001/link?name=%22+encodeURIComponent(document.title)+%22&url=%22+encodeURIComponent(document.URL)
 *
 * @author Stefan Stiller
*/
public class LinkHttpHandler implements HttpHandler
{
	private final MediaManagerFrame frame;

	public LinkHttpHandler(MediaManagerFrame frame)
	{
		this.frame=frame;
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException
	{
		httpExchange.getResponseHeaders().set("content-type", "text/html; charset=iso-8859-1");
		httpExchange.sendResponseHeaders(200, 0);
		PrintStream responseBody=new PrintStream(httpExchange.getResponseBody());
		try
		{
			Map<String, String> parameters;
			parameters=WebUtils.getParameters(httpExchange.getRequestURI());
			String name=parameters.get("name");
			String url=parameters.get("url");

			responseBody.println("<html><body>");
			responseBody.println("Adding link "+name+" for url "+url+"...done");
			responseBody.println("</body></html>");
			responseBody.close();
			httpExchange.close();
			try
			{
				LinkDetailsView.createDialog(frame, name, url);
			}
			catch (Exception e)
			{
				GuiUtils.handleThrowable(frame, e);
			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			responseBody.println(Utils.toString(e1));
			responseBody.close();
			httpExchange.close();
			return;
		}
	}
}
