package com.kiwisoft.media.dataimport;

import java.util.Set;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieManager;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;

/**
 * @author Stefan Stiller
*/
class TvTvDeShowHandler extends TvTvDeHandler<Show>
{
	TvTvDeShowHandler(TVTVDeLoader loader, Show object)
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
			Set<Show> shows=ShowManager.getInstance().getShowsByName(airdate.getTitle());
			if (shows.contains(getObject())) return true;
			Set<Movie> movies=MovieManager.getInstance().getMoviesByTitle(airdate.getTitle());
			for (Movie movie : movies)
			{
				if (movie.getShow()==getObject()) return true;
			}
		}
		return false;
	}

	@Override
	protected boolean analyze(TvTvDeAirdateData airdate)
	{
		if (super.analyze(airdate))
		{
			if (airdate.getShow()==getObject()) return true;
		}
		return false;
	}

	private class DeleteAirdatesTransaction implements Transactional
	{
		@Override
		public void run() throws Exception
		{
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("delete from airdate_persons where airdate_id in (select id from airdates where show_id=? and source_id=? and viewdate>=now())");
			try
			{
				statement.setLong(1, getObject().getId());
				statement.setLong(2, DataSource.TVTV.getId());
				statement.executeUpdate();
			}
			finally
			{
				statement.close();
			}
			statement=connection.prepareStatement("delete from airdates where show_id=? and source_id=? and viewdate>=now()");
			try
			{
				statement.setLong(1, getObject().getId());
				statement.setLong(2, DataSource.TVTV.getId());
				int updateCount=statement.executeUpdate();
				getProgressSupport().info("Deleted "+updateCount+" old airdates for show "+getName());
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
