/*
 * Created by IntelliJ IDEA.
 * User: stefan
 * Date: Oct 11, 2002
 * Time: 8:34:11 PM
 */
package com.kiwisoft.xp;

import java.util.Collection;
import java.util.List;
import java.io.File;

import javax.servlet.http.HttpServletRequest;

import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLObject;

public class IncludedXPBean implements XPBean
{
	private XPBean include;
	private File file;

	public IncludedXPBean(XPBean include, File file)
	{
		this.include=include;
		this.file=file;
	}

	public void reload(HttpServletRequest request)
	{
		include=XPLoader.loadXMLFile(request, file);
	}

	@Override
	public String getName()
	{
		return include.getName();
	}

	@Override
	public Object getString(String name)
	{
		return include.getString(name);
	}

	@Override
	public Object getValue(String name)
	{
		return include.getValue(name);
	}

	@Override
	public Collection getValues(String name)
	{
		return include.getValues(name);
	}

	@Override
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
	@Override
	public void setXMLAttribute(XMLContext context, String uri, String name, String value)
	{
	}

	@Override
	public void setXMLReference(XMLContext context, String uri, String name, Object value)
	{
	}

	/**
	 * Method called by the XMLHandler to set the content of
	 * an element.
	 *
	 * @param value The content of the element.
	 */
	@Override
	public void setXMLContent(XMLContext context, String value)
	{
	}

	/**
	 * Method called by the XMLHandler to add child elements
	 * to an element.
	 *
	 * @param element The new child element.
	 */
	@Override
	public void addXMLElement(XMLContext context, XMLObject element)
	{
	}
}
