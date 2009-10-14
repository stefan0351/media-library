package com.kiwisoft.media.fanfics;

import com.kiwisoft.media.BaseAction;
import com.kiwisoft.media.IndexByUtils;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.fanfic.*;
import com.kiwisoft.utils.StringUtils;

import java.util.Set;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Stefan Stiller
 * @since 03.10.2009
 */
public class ListFanFicsAction extends BaseAction
{
	private Long fanDomId;
	private Long pairingId;
	private Long authorId;
	private Long showId;
	private String letter;

	private FanFicGroup container;
	private Show show;
	private FanDom fanDom;
	private Set<Character> letters;
	private Set<Character> visibleLetters;
	private Pairing pairing;
	private Author author;
	private Comparator comparator;
	private Movie movie;

	@Override
	public String getPageTitle()
	{
		return "Fan Fiction";
	}

	@Override
	public String execute() throws Exception
	{
		if (fanDomId!=null)
		{
			fanDom=FanFicManager.getInstance().getDomain(fanDomId);
			container=fanDom;
			show=fanDom.getShow();
			movie=fanDom.getMovie();
		}
		else if (pairingId!=null)
		{
			pairing=FanFicManager.getInstance().getPairing(pairingId);
			container=pairing;
		}
		else if (authorId!=null)
		{
			author=FanFicManager.getInstance().getAuthor(authorId);
			container=author;
		}
		else if (showId!=null)
		{
			show=ShowManager.getInstance().getShow(showId);
			container=show;
		}

		if (container!=null) letters=container.getFanFicLetters();
		else letters=Collections.emptySet();

		if ("all".equalsIgnoreCase(letter)) visibleLetters=letters;
		else if (!StringUtils.isEmpty(letter)) visibleLetters=Collections.singleton(new Character(letter.charAt(0)));
		else if (!letters.isEmpty()) visibleLetters=Collections.singleton(letters.iterator().next());
		else visibleLetters=Collections.singleton(new Character('A'));

		comparator=new Comparator()
		{
			@Override
			public int compare(Object o1, Object o2)
			{
				String title1=IndexByUtils.createIndexBy(((FanFic)o1).getTitle());
				String title2=IndexByUtils.createIndexBy(((FanFic)o2).getTitle());
				return title1.compareToIgnoreCase(title2);
			}
		};

		return super.execute();
	}

	public Long getFanDomId()
	{
		return fanDomId;
	}

	public void setFanDomId(Long fanDomId)
	{
		this.fanDomId=fanDomId;
	}

	public Long getPairingId()
	{
		return pairingId;
	}

	public void setPairingId(Long pairingId)
	{
		this.pairingId=pairingId;
	}

	public Long getAuthorId()
	{
		return authorId;
	}

	public void setAuthorId(Long authorId)
	{
		this.authorId=authorId;
	}

	public Long getShowId()
	{
		return showId;
	}

	public void setShowId(Long showId)
	{
		this.showId=showId;
	}

	public String getLetter()
	{
		return letter;
	}

	public void setLetter(String letter)
	{
		this.letter=letter;
	}

	public FanFicGroup getContainer()
	{
		return container;
	}

	public Show getShow()
	{
		return show;
	}

	public Movie getMovie()
	{
		return movie;
	}

	public Set<Character> getLetters()
	{
		return letters;
	}

	public Set<Character> getVisibleLetters()
	{
		return visibleLetters;
	}

	public FanDom getFanDom()
	{
		return fanDom;
	}

	public Pairing getPairing()
	{
		return pairing;
	}

	public Author getAuthor()
	{
		return author;
	}

	public Comparator getComparator()
	{
		return comparator;
	}
}
