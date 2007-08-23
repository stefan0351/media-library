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

	private static final DBAssociation<FanFic, Author> ASSOCIATIONS_AUTHORS=DBAssociation.getAssociation(AUTHORS, FanFic.class, Author.class);
	private static final DBAssociation<FanFic, Pairing> ASSOCIATIONS_PAIRINGS=DBAssociation.getAssociation(PAIRINGS, FanFic.class, Pairing.class);
	private static final DBAssociation<FanFic, FanDom> ASSOCIATIONS_FANDOMS=DBAssociation.getAssociation(FANDOMS, FanFic.class, FanDom.class);

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
		this.title=title;
		setModified();
	}

	public String getRating()
	{
		return rating;
	}

	public void setRating(String rating)
	{
		this.rating=rating;
		setModified();
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description=description;
		setModified();
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
		this.finished=finished;
		setModified();
	}

	public String getSpoiler()
	{
		return spoiler;
	}

	public void setSpoiler(String spoiler)
	{
		this.spoiler=spoiler;
		setModified();
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url=url;
		setModified();
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
		return ASSOCIATIONS_AUTHORS.getAssociations(this);
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
		ASSOCIATIONS_AUTHORS.addAssociation(this, author);
	}

	public void removeAuthor(Author author)
	{
		ASSOCIATIONS_AUTHORS.removeAssociation(this, author);
	}

	public Collection<FanDom> getFanDoms()
	{
		return ASSOCIATIONS_FANDOMS.getAssociations(this);
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
		ASSOCIATIONS_FANDOMS.addAssociation(this, fanDom);
	}

	public void removeFanDom(FanDom fanDom)
	{
		ASSOCIATIONS_FANDOMS.removeAssociation(this, fanDom);
	}

	public Collection<Pairing> getPairings()
	{
		return ASSOCIATIONS_PAIRINGS.getAssociations(this);
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
		ASSOCIATIONS_PAIRINGS.addAssociation(this, pairing);
	}

	public void removePairing(Pairing pairing)
	{
		ASSOCIATIONS_PAIRINGS.removeAssociation(this, pairing);
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
