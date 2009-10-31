package com.kiwisoft.media.dataimport;

import java.util.Set;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieManager;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;

/**
 * @author Stefan Stiller
*/
class TvTvDeMovieHandler extends TvTvDeHandler<Movie>
{
	TvTvDeMovieHandler(TVTVDeLoader loader, Movie object)
	{
		super(loader, object);
	}

	@Override
	protected String getName()
	{
		return getObject().getTitle();
	}

	@Override
	protected Set<SearchPattern> getSearchPatterns()
	{
		return SearchManager.getInstance().getSearchPattern(SearchPattern.TVTV, getObject());
	}

	@Override
	protected boolean deleteAirdates()
	{
		return DBSession.execute(new DeleteAirdatesTransaction());
	}

	@Override
	protected boolean preCheck(TvTvDeAirdateData airdate)
	{
		if (super.preCheck(airdate))
		{
			Set<Movie> movies=MovieManager.getInstance().getMoviesByTitle(airdate.getTitle());
			if (movies.contains(getObject())) return true;
		}
		return false;
	}

	@Override
	protected boolean analyze(TvTvDeAirdateData airdate)
	{
		if (super.analyze(airdate))
		{
			if (airdate.getMovie()==getObject()) return true;
		}
		return false;
	}

	private class DeleteAirdatesTransaction implements Transactional
	{
		@Override
		public void run() throws Exception
		{
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("delete from airdates where movie_id=? and source_id=? and viewdate>=now()");
			try
			{
				statement.setLong(1, getObject().getId());
				statement.setLong(2, DataSource.TVTV.getId());
				int updateCount=statement.executeUpdate();
				getProgressSupport().info("Deleted "+updateCount+" old airdates for movie "+getName());
			}
			finally
			{
				statement.close();
			}
			DBSession.getInstance().getCurrentTransaction().forceCommit();
		}

		@Override
		public void handleError(Throwable throwable, boolean rollback)
		{
			getProgressSupport().error(throwable);
		}
	}
}
