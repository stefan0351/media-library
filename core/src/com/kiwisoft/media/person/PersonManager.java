/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 31, 2003
 * Time: 7:27:21 PM
 * To change this template use Options | File Templates.
 */
package com.kiwisoft.media.person;

import com.kiwisoft.media.Name;
import com.kiwisoft.media.books.Book;
import com.kiwisoft.persistence.DBAssociation;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.utils.Bean;
import static com.kiwisoft.utils.StringUtils.isEmpty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PersonManager extends Bean
{
	private static PersonManager instance;

	public static final String PERSONS="persons";

	public synchronized static PersonManager getInstance()
	{
		if (instance==null) instance=new PersonManager();
		return instance;
	}

	private PersonManager()
	{
	}

	public Set<Person> getPersons()
	{
		return DBLoader.getInstance().loadSet(Person.class);
	}

	public Set<Person> getActors()
	{
		return DBLoader.getInstance().loadSet(Person.class, null, "actor=1");
	}

	public void dropPerson(Person person)
	{
		person.delete();
		fireElementRemoved(PERSONS, person);
	}

	public boolean isPersonUsed(Person person)
	{
		return DBLoader.getInstance().count(CastMember.class, null, "actor_id=?", person.getId())>0
			   || DBLoader.getInstance().count(Credit.class, null, "person_id=?", person.getId())>0
			   || DBLoader.getInstance().count(Book.class, "_ join map_book_author m on m.book_id=books.id", "m.author_id=?", person.getId())>0
			   || DBLoader.getInstance().count(Book.class, "_ join map_book_translator m on m.book_id=books.id", "m.translator_id=?", person.getId())>0;
	}

	public Person createPerson()
	{
		Person person=new Person();
		fireElementAdded(PERSONS, person);
		return person;
	}

	public Person getPersonByName(String name)
	{
		Person person=DBLoader.getInstance().load(Person.class, null, "name=?", name);
		if (person==null)
		{
			person=DBLoader.getInstance().load(Person.class, "names", "names.type=? and names.ref_id=persons.id and names.name=?", Name.PERSON, name);
		}
		return person;
	}

	public Set<Person> getPersonsByName(String name)
	{
		Set<Person> persons=new HashSet<Person>();
		persons.addAll(DBLoader.getInstance().loadSet(Person.class, null, "name=?", name));
		persons.addAll(DBLoader.getInstance().loadSet(Person.class, "names", "names.type=? and names.ref_id=persons.id and names.name=?", Name.PERSON, name));
		return persons;
	}

	public Person getPerson(Long id)
	{
		return DBLoader.getInstance().load(Person.class, id);
	}

	public Person getPersonByIMDbKey(String imdbKey)
	{
		return DBLoader.getInstance().load(Person.class, null, "binary imdb_key=?", imdbKey);
	}

	public Person getPersonByTVcomKey(String key)
	{
		return DBLoader.getInstance().load(Person.class, null, "binary tvcom_key=?", key);
	}

	public void mergePersons(Person basePerson, Collection<Person> persons)
	{
		for (Person person : persons)
		{
			if (person!=basePerson) mergePersons(basePerson, person);
		}
	}

	private void mergePersons(Person basePerson, Person person)
	{
		if (basePerson.getGender()==null && person.getGender()!=null) basePerson.setGender(person.getGender());
		if (basePerson.getPicture()==null && person.getPicture()!=null) basePerson.setPicture(person.getPicture());
		if (isEmpty(basePerson.getImdbKey()) && !isEmpty(person.getImdbKey())) basePerson.setImdbKey(person.getImdbKey());
		if (isEmpty(basePerson.getTvcomKey()) && !isEmpty(person.getTvcomKey())) basePerson.setTvcomKey(person.getTvcomKey());
		if (!basePerson.getName().equals(person.getName())) basePerson.addAltName(person.getName());
		for (Name altName : person.getAltNames())
		{
			if (!basePerson.getName().equals(altName.getName())) basePerson.addAltName(altName.getName());
		}
		for (CastMember castMember : person.getActingCredits()) castMember.setActor(basePerson);
		for (Credit credit : person.getCrewCredits()) credit.setPerson(basePerson);
		DBAssociation association=DBAssociation.getAssociation(Person.class, "writtenBooks");
		//noinspection unchecked
		Set<Book> books=(Set<Book>) association.getAssociations(person);
		for (Book book : books)
		{
			association.removeAssociation(person, book);
			association.addAssociation(basePerson, book);
		}
		association=DBAssociation.getAssociation(Person.class, "translatedBooks");
		//noinspection unchecked
		books=(Set<Book>) association.getAssociations(person);
		for (Book book : books)
		{
			association.removeAssociation(person, book);
			association.addAssociation(basePerson, book);
		}
		dropPerson(person);
	}
}

