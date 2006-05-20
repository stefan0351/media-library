/*
 * Created by IntelliJ IDEA.
 * User: stefan
 * Date: Oct 11, 2002
 * Time: 8:34:11 PM
 */
package com.kiwisoft.xp;

import java.util.Collection;
import java.util.List;

import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLObject;

public class IncludedXPBean implements XPBean
{
	private XPBean include;
	private String path;
	private String urlPath;

	public IncludedXPBean(XPBean include, String path, String urlPath)
	{
		this.include=include;
		this.path=path;
		this.urlPath=urlPath;
	}

	public void reload()
	{
		include=(XPBean)XPLoader.loadXMLFile(path, urlPath);
	}

	public String getName()
	{
		return include.getName();
	}

	public Object getValue(String name)
	{
		return include.getValue(name);
	}

	public Collection getValues(String name)
	{
		return include.getValues(name);
	}

	public String getBasePath()
	{
		return include.getBasePath();
	}

	public List getChildren()
	{
		return include.getChildren();
	}

	/**
	 * Method called by the XMLHandler to set the attributes
	 * of an element.
	 *
	 * @param name The name of the attribute.
	 * @param value The value of the attribute.
	 */
	public void setXMLAttribute(XMLContext context, String name, String value)
	{
	}

	public void setXMLReference(XMLContext context, String name, Object value)
	{
	}

	/**
	 * Method called by the XMLHandler to set the content of
	 * an element.
	 *
	 * @param value The content of the element.
	 */
	public void setXMLContent(XMLContext context, String value)
	{
	}

	/**
	 * Method called by the XMLHandler to add child elements
	 * to an element.
	 *
	 * @param element The new child element.
	 */
	public void addXMLElement(XMLContext context, XMLObject element)
	{
	}

}
