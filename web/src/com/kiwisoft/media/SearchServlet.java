package com.kiwisoft.media;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.collection.SetMap;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBLoader;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2004/07/16 23:29:18 $
 */
public class SearchServlet extends HttpServlet
{
	public SearchServlet()
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

		try
		{
			String type=request.getParameter("type");
			String text=request.getParameter("text");
			text=StringUtils.trimString(text);
			if (!StringUtils.isEmpty(text) && text.length()>1)
			{
				String searchText;
				request.setAttribute("searchText", text);
				if (text.contains("*") || text.contains("?")) searchText=text.replace("*", "%").replace("?", "_");
				else searchText="%"+text+"%";
				if ("shows".equals(type) || "all".equals(type))
				{
					Set<Show> shows=new HashSet<Show>();
					shows.addAll(DBLoader.getInstance().loadSet(Show.class, null, "title like ? or german_title like ?", searchText, searchText));
					shows.addAll(DBLoader.getInstance().loadSet(Show.class, "names", "names.type=? and names.ref_id=shows.id and names.name like ?",
																Name.SHOW, searchText));
					request.setAttribute("shows", shows);
				}
				if ("episodes".equals(type) || "all".equals(type))
				{
					SetMap<Show, Episode> episodes=new SetMap<Show, Episode>();
					for (Episode episode : DBLoader.getInstance().loadSet(Episode.class, null, "title like ? or german_title like ?", searchText, searchText))
					{
						episodes.add(episode.getShow(), episode);
					}
					for (Episode episode : DBLoader.getInstance().loadSet(Episode.class, "names",
																		  "names.type=? and names.ref_id=episodes.id and names.name like ?",
																		  Name.EPISODE, searchText))
					{
						episodes.add(episode.getShow(), episode);
					}
					request.setAttribute("episodes", episodes);
				}
				if ("movies".equals(type) || "all".equals(type))
				{
					Set<Movie> movies=new HashSet<Movie>();
					movies.addAll(DBLoader.getInstance().loadSet(Movie.class, null, "title like ? or german_title like ?", searchText, searchText));
					movies.addAll(DBLoader.getInstance().loadSet(Movie.class, "names", "names.type=? and names.ref_id=movies.id and names.name like ?",
																 Name.MOVIE, searchText));
					request.setAttribute("movies", movies);
				}
				if ("persons".equals(type) || "all".equals(type))
				{
					Set<Person> persons=new HashSet<Person>();
					persons.addAll(DBLoader.getInstance().loadSet(Person.class, null, "name like ?", searchText));
					persons.addAll(DBLoader.getInstance().loadSet(Person.class, "names", "names.type=? and names.ref_id=persons.id and names.name like ?",
																 Name.PERSON, searchText));
					request.setAttribute("persons", persons);
				}
			}
			servletContext.getRequestDispatcher("/search_result.jsp").forward(request, response);
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
}
