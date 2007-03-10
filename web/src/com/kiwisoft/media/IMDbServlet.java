package com.kiwisoft.media;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;

import com.kiwisoft.media.dataImport.IMDbComLoader;
import com.kiwisoft.media.dataImport.MovieData;
import com.kiwisoft.media.dataImport.CreateMovieTx;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieManager;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.StringUtils;

/**
 * URL: javascript:window.location=%22http://localhost:8080/import_imdb?url=%22+encodeURI(document.URL)
 *
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2004/07/16 23:29:18 $
 */
public class IMDbServlet extends HttpServlet
{
	private Pattern urlPattern;

	public IMDbServlet()
	{
		urlPattern=Pattern.compile("http://(german|www).imdb.com/title/(\\w+)/.*");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		process(request, response);
	}

	private void process(final HttpServletRequest request, HttpServletResponse response)
	{
		HttpSession session=request.getSession();
		ServletContext servletContext=session.getServletContext();
		MediaManagerApp.getInstance(servletContext);

		try
		{
			String action=request.getParameter("action");
			if ("add".equals(action))
			{
				MovieData movieData=(MovieData)session.getAttribute("movie");
				if (movieData!=null)
				{
					if (Boolean.parseBoolean(request.getParameter("force_new"))) movieData.setMovie(null);
					CreateMovieTx createMovieTx=new CreateMovieTx(movieData)
					{
						public void handleError(Throwable throwable)
						{
							request.setAttribute("error", throwable);
						}
					};
					boolean success=DBSession.execute(createMovieTx);
					if (success) forward(request, response, "/movies/movie.jsp?movie="+createMovieTx.getMovie().getId());
					else forward(request, response, "/error.jsp");
				}
				else forward(request, response, "/movies/import.jsp");
			}
			else
			{
				String url=request.getParameter("url");
				Matcher matcher=urlPattern.matcher(url);
				if (matcher.matches())
				{
					url="http://www.imdb.com/title/"+matcher.group(2)+"/";
					MovieData movieData=new IMDbComLoader(url).load();
					MovieManager movieManager=MovieManager.getInstance();
					Movie movie=movieManager.getMovieByTitle(movieData.getTitle());
					if (movie==null && !StringUtils.isEmpty(movieData.getGermanTitle())) movie=movieManager.getMovieByTitle(movieData.getGermanTitle());
					movieData.setMovie(movie);
					session.setAttribute("movie", movieData);
					RequestDispatcher requestDispatcher=servletContext.getRequestDispatcher("/movies/import.jsp");
					requestDispatcher.forward(request, response);
				}
				else throw new IllegalArgumentException("Invalid url.");
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

	private void forward(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException
	{
		HttpSession session=request.getSession();
		ServletContext servletContext=session.getServletContext();
		RequestDispatcher requestDispatcher=servletContext.getRequestDispatcher(page);
		requestDispatcher.forward(request, response);
	}

}
