/*
 * Created by IntelliJ IDEA.
 * User: stefan
 * Date: Oct 11, 2002
 * Time: 8:28:32 PM
 */
package com.kiwisoft.xp;

import java.util.Collection;
import java.util.List;

import com.kiwisoft.utils.xml.XMLObject;

public interface XPBean extends XMLObject
{
	String getName();

	Object getValue(String name);

	Collection getValues(String name);

	List getChildren();
}
