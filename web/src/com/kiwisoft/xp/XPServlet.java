package com.kiwisoft.xp;

import java.io.IOException;
import java.io.File;
import java.util.ResourceBundle;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.media.MediaWebApplication;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2004/07/16 23:29:18 $
 */
public class XPServlet extends HttpServlet
{
	private ResourceBundle templates;

	public XPServlet()
	{
		templates=ResourceBundle.getBundle("com.kiwisoft.xp.templates");
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
		String fileName=request.getServletPath();
		HttpSession session=request.getSession();
		ServletContext servletContext=session.getServletContext();
		String realPath=servletContext.getRealPath(fileName);

		try
		{
			Object bean=XPLoader.loadXMLFile(request, new File(realPath));
			XPBean xmlBean=(XPBean) bean;
			request.setAttribute("xp", xmlBean);
			String templateFile=(String)xmlBean.getValue("template");
			if (templateFile==null)
			{
				Object variant=xmlBean.getValue("variant");
	            if (variant!=null) templateFile=templates.getString(xmlBean.getName()+"."+variant);
				else templateFile=templates.getString(xmlBean.getName());
			}
			if (StringUtils.isEmpty(templateFile)) throw new RuntimeException("No template defined for name="+xmlBean.getName()
					+"; variant="+xmlBean.getValue("variant"));
			RequestDispatcher requestDispatcher=servletContext.getRequestDispatcher(templateFile);
			requestDispatcher.forward(request, response);
		}
		catch (Throwable e)
		{
			request.setAttribute("error", e);
			RequestDispatcher requestDispatcher=servletContext.getRequestDispatcher(templates.getString("error"));
			try
			{
				requestDispatcher.forward(request, response);
			}
			catch (Exception e1)
			{
//				System.out.println(e1.getMessage());
			}
		}
	}
}