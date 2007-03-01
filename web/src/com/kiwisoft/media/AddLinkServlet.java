package com.kiwisoft.media;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.fanfic.FanFicManager;
import com.kiwisoft.media.fanfic.FanDom;
import com.kiwisoft.media.*;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2004/07/16 23:29:18 $
 */
public class AddLinkServlet extends HttpServlet
{
	public AddLinkServlet()
	{
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		process(request, response);
	}

	private void process(HttpServletRequest request, HttpServletResponse response)
	{
		HttpSession session=request.getSession();
		ServletContext servletContext=session.getServletContext();
		MediaManagerApp.getInstance(servletContext);

		try
		{
			Linkable linkable=null;
			try
			{
				Long id=new Long(request.getParameter("show"));
				linkable=ShowManager.getInstance().getShow(id);
			}
			catch (NumberFormatException e)
			{
			}
			if (linkable==null)
			{
				try
				{
					Long id=new Long(request.getParameter("fanDom"));
					linkable=FanFicManager.getInstance().getDomain(id);
				}
				catch (NumberFormatException e)
				{
				}
			}
			String name=request.getParameter("name");
			String url=request.getParameter("url");
			Language language=LanguageManager.getInstance().getLanguageBySymbol(request.getParameter("language"));
			if (StringUtils.isEmpty(name) || StringUtils.isEmpty(url) || linkable==null)
			{
				RequestDispatcher requestDispatcher=servletContext.getRequestDispatcher(request.getRequestURI());
				requestDispatcher.forward(request, response);
			}
			else
			{
				createLink(linkable, name, url, language);
				RequestDispatcher requestDispatcher;
				if (linkable instanceof Show)
					requestDispatcher=servletContext.getRequestDispatcher("/shows/links.jsp?show="+((Show)linkable).getId());
				else
					requestDispatcher=servletContext.getRequestDispatcher("/fanfic/fanfics.jsp?fandom="+((FanDom)linkable).getId());
				requestDispatcher.forward(request, response);
			}
		}
		catch (Throwable e)
		{
			request.setAttribute("error", e);
			RequestDispatcher requestDispatcher=servletContext.getRequestDispatcher("/error.jsp");
			try
			{
				requestDispatcher.forward(request, response);
			}
			catch (Exception e1)
			{
			}
		}
	}

	private void createLink(Linkable linkable, String name, String url, Language language) throws Exception
	{
		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			Link link=linkable.createLink();
			link.setName(name);
			link.setUrl(url);
			link.setLanguage(language);
			transaction.close();
		}
		catch (Exception e)
		{
			if (transaction!=null)
			{
				try
				{
					transaction.rollback();
				}
				catch (SQLException e1)
				{
					e1.printStackTrace();
				}
			}
			throw e;
		}
	}
}