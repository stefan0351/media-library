/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media.ui;

import java.util.Collection;
import java.util.Collections;

import com.kiwisoft.media.Person;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.gui.lookup.ListLookup;

public class ActorLookup extends ListLookup<Person>
{
	public Collection<Person> getValues(String text, Person currentValue, boolean lookup)
	{
		if (text==null) return Collections.emptySet();
		else
		{
			if (text.indexOf('*')>=0) text=text.replace('*', '%');
			else text=text+"%";
			return DBLoader.getInstance().loadSet(Person.class, null,
					"actor=1 and (concat(surname, ', ', firstname) like ? or concat(firstname, ' ', surname) like ?)", text, text);
		}
	}

}
