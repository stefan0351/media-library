/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 31, 2003
 * Time: 7:27:21 PM
 * To change this template use Options | File Templates.
 */
package com.kiwisoft.media;

import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.CollectionChangeSupport;
import com.kiwisoft.utils.db.DBLoader;

public class CharacterManager
{
	private static CharacterManager instance;

	public static final String CHARACTERS="characters";

	private CollectionChangeSupport collectionChangeSupport=new CollectionChangeSupport(this);

	public synchronized static CharacterManager getInstance()
	{
		if (instance==null) instance=new CharacterManager();
		return instance;
	}

	private CharacterManager()
	{
	}

//	public Set getCharacters()
//	{
//		return DBLoader.getInstance().loadSet(Person.class, null, "actor=1", null);
//	}

	public ShowCharacter createCharacter()
	{
		ShowCharacter character=new ShowCharacter();
		fireElementAdded(CHARACTERS, character);
		return character;
	}

	public void dropCharacter(ShowCharacter character)
	{
		character.delete();
		fireElementRemoved(CHARACTERS, character);
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

	public boolean isCharacterUsed(ShowCharacter character)
	{
		return DBLoader.getInstance().count(Cast.class, null, "character_id=?", character.getId())>0;
	}

//	public Person createPerson()
//	{
//		Person person=new Person();
//		fireElementAdded(PERSONS, person);
//		return person;
//	}
}

