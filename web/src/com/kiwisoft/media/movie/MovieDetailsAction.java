package com.kiwisoft.media.movie;

import com.kiwisoft.collection.SetMap;
import com.kiwisoft.collection.SortedSetMap;
import com.kiwisoft.media.BaseAction;
import com.kiwisoft.media.Country;
import com.kiwisoft.media.CountryManager;
import com.kiwisoft.media.files.ImageFile;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.files.MediaFileUtils;
import com.kiwisoft.media.medium.Medium;
import com.kiwisoft.media.medium.MediumManager;
import com.kiwisoft.media.person.CastCreditComparator;
import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.Credit;
import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.media.show.Summary;
import com.kiwisoft.media.show.SummaryComparator;
import com.kiwisoft.web.RecentItemManager;
import com.kiwisoft.web.RecentIdObject;

import java.util.*;

/**
 * @author Stefan Stiller
 * @since 30.09.2009
 */
public class MovieDetailsAction extends BaseAction
{
	private static final long serialVersionUID=-2266415720898928261L;

	private Long movieId;

	private Movie movie;
	private MediaFile poster;
	private ImageFile thumbnail;
	private Set<Medium> media;
	private List<Summary> summaries;
	private List<CastMember> cast;
	private SetMap<CreditType, Credit> crew;

	@Override
	public String getPageTitle()
	{
		return movie.getTitle();
	}

	@Override
	public String execute() throws Exception
	{
		if (movieId!=null) movie=MovieManager.getInstance().getMovie(movieId);
		if (movie!=null)
		{
			RecentItemManager.getInstance().addItem(new RecentIdObject<Movie>(Movie.class, movie));
			poster=movie.getPoster();
			if (poster!=null)
			{
				thumbnail=poster.getThumbnailSidebar();
				if (thumbnail==null && poster.getWidth()<=MediaFileUtils.THUMBNAIL_SIDEBAR_WIDTH) thumbnail=poster;
			}
			summaries=new ArrayList<Summary>(movie.getSummaries());
			Collections.sort(summaries, new SummaryComparator());
			cast=new ArrayList<CastMember>(movie.getCastMembers());
			Collections.sort(cast, new CastCreditComparator());
			Set<Credit> credits=movie.getCredits();
			if (!credits.isEmpty())
			{
				crew=new SortedSetMap<CreditType, Credit>(null, new Comparator<Credit>()
				{
					@Override
					public int compare(Credit o1, Credit o2)
					{
						return o1.getPerson().getName().compareToIgnoreCase(o2.getPerson().getName());
					}
				});
				for (Credit credit : credits)
				{
					crew.add(credit.getCreditType(), credit);
				}
			}
			media=MediumManager.getInstance().getMedia(movie);
		}
		return super.execute();
	}

	public Long getMovieId()
	{
		return movieId;
	}

	public void setMovieId(Long movieId)
	{
		this.movieId=movieId;
	}

	public Movie getMovie()
	{
		return movie;
	}

	public MediaFile getPoster()
	{
		return poster;
	}

	public ImageFile getThumbnail()
	{
		return thumbnail;
	}

	public Country getGermany()
	{
		return CountryManager.getInstance().getCountryBySymbol("DE");
	}

	public Set<Medium> getMedia()
	{
		return media;
	}

	public MediaFile getPicture(CastMember castMember)
	{
		MediaFile picture=castMember.getPicture();
		if (picture==null && castMember.getActor()!=null)
		{
			picture=castMember.getActor().getPicture();
		}
		return picture;
	}

	public List<Summary> getSummaries()
	{
		return summaries;
	}

	public List<CastMember> getCast()
	{
		return cast;
	}

	public SetMap<CreditType, Credit> getCrew()
	{
		return crew;
	}

	public Set<Credit> getCredits(CreditType type)
	{
		return crew.get(type); 
	}
}
