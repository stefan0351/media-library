/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 21, 2003
 * Time: 6:56:46 PM
 */
package com.kiwisoft.media.fanfic;

import java.util.Collection;
import java.util.Iterator;
import java.util.HashSet;

import com.kiwisoft.persistence.*;
import com.kiwisoft.collection.Chain;

public class FanFic extends IDObject
{
	public static final String AUTHORS="authors";
	public static final String PAIRINGS="pairings";
	public static final String FANDOMS="fandoms";
	public static final String SEQUEL="sequel";
	public static final String PREQUEL="prequel";
	public static final String PARTS = "parts";

	private String title;
	private String rating;
	private String description;
	private boolean finished;
	private String spoiler;
	private String url;
	private Chain<FanFicPart> parts;

	public FanFic()
	{
		super(SequenceManager.getSequence("fanfic").next());
	}

	public FanFic(long id)
	{
		super(id);
	}

	public FanFic(DBDummy dummy)
	{
		super(dummy);
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		String oldTitle=this.title;
		this.title=title;
		setModified("title", oldTitle, this.title);
	}

	public String getRating()
	{
		return rating;
	}

	public void setRating(String rating)
	{
		String oldRating=this.rating;
		this.rating=rating;
		setModified("rating", oldRating, this.rating);
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		String oldDescription=this.description;
		this.description=description;
		setModified("description", oldDescription, this.description);
	}

	public String getSource()
	{
		Iterator<FanFicPart> parts=getParts().iterator();
		if (parts.hasNext()) return parts.next().getSource();
		return null;
	}

	public boolean isFinished()
	{
		return finished;
	}

	public void setFinished(boolean finished)
	{
		boolean oldFinished=this.finished;
		this.finished=finished;
		setModified("finished", oldFinished, this.finished);
	}

	public String getSpoiler()
	{
		return spoiler;
	}

	public void setSpoiler(String spoiler)
	{
		String oldSpoiler=this.spoiler;
		this.spoiler=spoiler;
		setModified("spoiler", oldSpoiler, this.spoiler);
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		String oldUrl=this.url;
		this.url=url;
		setModified("url", oldUrl, this.url);
	}

	public FanFic getSequel()
	{
		return (FanFic)getReference(SEQUEL);
	}

	public void setSequel(FanFic value)
	{
		setReference(SEQUEL, value);
	}

	public FanFic getPrequel()
	{
		return (FanFic)getReference(PREQUEL);
	}

	public void setPrequel(FanFic value)
	{
		setReference(PREQUEL, value);
	}

	public Collection<Author> getAuthors()
	{
		return getAssociations(AUTHORS);
	}

	public void setAuthors(Collection<Author> authors)
	{
		for (Author author : new HashSet<Author>(getAuthors()))
		{
			if (authors.contains(author)) authors.remove(author);
			else removeAuthor(author);
		}
		for (Author author : authors) addAuthor(author);
	}

	public void addAuthor(Author author)
	{
		createAssociation(AUTHORS, author);
	}

	public void removeAuthor(Author author)
	{
		dropAssociation(AUTHORS, author);
	}

	@SuppressWarnings({"unchecked"})
	public Collection<FanDom> getFanDoms()
	{
		return (Collection<FanDom>)DBAssociation.getAssociation(FanFic.class, FANDOMS).getAssociations(this);
	}

	public void setFanDoms(Collection<FanDom> fanDoms)
	{
		for (Iterator<FanDom> it=new HashSet<FanDom>(getFanDoms()).iterator(); it.hasNext();)
		{
			FanDom fanDom=it.next();
			if (fanDoms.contains(fanDom)) fanDoms.remove(fanDom);
			else removeFanDom(fanDom);
		}
		for (Iterator<FanDom> it=fanDoms.iterator(); it.hasNext();)
		{
			FanDom fanDom=it.next();
			addFanDom(fanDom);
		}
	}

	public void addFanDom(FanDom fanDom)
	{
		DBAssociation.getAssociation(FanFic.class, FANDOMS).addAssociation(this, fanDom);
	}

	public void removeFanDom(FanDom fanDom)
	{
		DBAssociation.getAssociation(FanFic.class, FANDOMS).removeAssociation(this, fanDom);
	}

	@SuppressWarnings({"unchecked"})
	public Collection<Pairing> getPairings()
	{
		return (Collection<Pairing>)DBAssociation.getAssociation(FanFic.class, PAIRINGS).getAssociations(this);
	}

	public void setPairings(Collection<Pairing> pairings)
	{
		for (Iterator<Pairing> it=new HashSet<Pairing>(getPairings()).iterator(); it.hasNext();)
		{
			Pairing	pairing=it.next();
			if (pairings.contains(pairing)) pairings.remove(pairing);
			else removePairing(pairing);
		}
		for (Pairing pairing : pairings) addPairing(pairing);
	}

	public void addPairing(Pairing pairing)
	{
		DBAssociation.getAssociation(FanFic.class, PAIRINGS).addAssociation(this, pairing);
	}

	public void removePairing(Pairing pairing)
	{
		DBAssociation.getAssociation(FanFic.class, PAIRINGS).removeAssociation(this, pairing);
	}

	public String toString()
	{
		return getTitle();
	}

	public void notifyChanged()
	{
		FanFicManager.getInstance().notifyFanFicChanged(this);
	}

	public FanFicPart createPart()
	{
		FanFicPart part=new FanFicPart(this);
		getParts().addNew(part);
		fireElementAdded(PARTS, part);
		return part;
	}

	public void dropPart(FanFicPart part)
	{
		getParts().remove(part);
		part.delete();
		fireElementRemoved(PARTS, part);
	}

	public Chain<FanFicPart> getParts()
	{
		if (parts==null)
			parts=new Chain<FanFicPart>(DBLoader.getInstance().loadSet(FanFicPart.class, null, "fanfic_id=?", getId()));
		return parts;
	}
}
