/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 31, 2003
 * Time: 7:27:21 PM
 * To change this template use Options | File Templates.
 */
package com.kiwisoft.media;

import java.util.Set;

import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.CollectionChangeSupport;
import com.kiwisoft.utils.db.DBLoader;

public class PersonManager
{
	private static PersonManager instance;

	public static final String PERSONS="persons";

	private CollectionChangeSupport collectionChangeSupport=new CollectionChangeSupport(this);

	public synchronized static PersonManager getInstance()
	{
		if (instance==null) instance=new PersonManager();
		return instance;
	}

	private PersonManager()
	{
	}

	public Set getActors()
	{
		return DBLoader.getInstance().loadSet(Person.class, null, "actor=1");
	}

	public void dropPerson(Person person)
	{
		person.delete();
		fireElementRemoved(PERSONS, person);
	}

	public void addCollectionChangeListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.addListener(listener);
	}

	public void removeCollectionListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.removeListener(listener);
	}

	protected void fireElementAdded(String propertyName, Object element)
	{
		collectionChangeSupport.fireElementAdded(propertyName, element);
	}

	protected void fireElementRemoved(String propertyName, Object element)
	{
		collectionChangeSupport.fireElementRemoved(propertyName, element);
	}

	public boolean isPersonUsed(Person person)
	{
		return DBLoader.getInstance().count(CastMember.class, null, "actor_id=?", person.getId())>0;
	}

	public Person createPerson()
	{
		Person person=new Person();
		fireElementAdded(PERSONS, person);
		return person;
	}

	public Person getPersonByName(String name)
	{
		return DBLoader.getInstance().load(Person.class, null, "name=?", name);
	}
}

