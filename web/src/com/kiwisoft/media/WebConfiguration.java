package com.kiwisoft.media;

import com.kiwisoft.cfg.Configuration;
import com.kiwisoft.utils.xml.DefaultXMLObject;
import com.kiwisoft.utils.xml.XMLHandler;
import com.kiwisoft.utils.xml.XMLObject;
import com.kiwisoft.utils.xml.XMLUtils;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.*;

/**
 * @author Stefan Stiller
 * @since 08.12.13
 */
public class WebConfiguration extends Configuration
{
	private final static String USER_HOME=System.getProperty("user.home")+File.separator+".kiwisoft";

	private ServletContext context;
	private Map<String, String> userValues=new TreeMap<String, String>();
	private String userFile;

	public WebConfiguration(ServletContext context)
	{
		this.context=context;
	}

	public void loadUserValues(String fileName)
	{
		userFile=fileName;
		File file=new File(USER_HOME, fileName);
		if (file.exists())
		{
			try
			{
				InputStream is=new FileInputStream(file);
				load(is, userValues);
				is.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void load(InputStream is, Map<String, String> map)
	{
		XMLHandler handler=new XMLHandler();
		XMLObject root=handler.loadStream(is);
		if (root instanceof DefaultXMLObject)
		{
			DefaultXMLObject xmlRoot=(DefaultXMLObject) root;
			if ("configuration".equals(xmlRoot.getName())) // NON-NLS
			{
				Iterator it=xmlRoot.getElements().iterator();
				while (it.hasNext())
				{
					DefaultXMLObject xmlParameter=(DefaultXMLObject) it.next();
					if ("parameter".equalsIgnoreCase(xmlParameter.getName()))
						map.put(xmlParameter.getAttribute("name"), xmlParameter.getContent()); // NON-NLS
				}
			}
		}
	}

	@Override
	public void saveUserValues()
	{
		if (userFile!=null)
		{
			try
			{
				File file=new File(USER_HOME, userFile);
				file.getParentFile().mkdirs();
				save(file, userValues);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void save(File file, Map<String, String> map) throws FileNotFoundException
	{
		PrintStream ps=new PrintStream(new FileOutputStream(file));
		ps.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>"); // NON-NLS
		ps.println("<configuration>"); // NON-NLS
		Iterator<String> it=map.keySet().iterator();
		while (it.hasNext())
		{
			String name=it.next();
			String value=map.get(name);
			if (value!=null) ps.println("<parameter name=\""+XMLUtils.toXMLString(name)+"\">"+XMLUtils.toXMLString(value)+"</parameter>"); // NON-NLS
		}
		ps.println("</configuration>");  // NON-NLS
		ps.flush();
		ps.close();
	}

	private String getValue(String name)
	{
		String value=userValues.get(name);
		if (value==null) value=context.getInitParameter(name);
		return value;
	}

	@Override
	public String getString(String name) throws MissingResourceException
	{
		return getValue(name);
	}

	@Override
	public void setString(String name, String value)
	{
		// We have to save null values because they may overwrite default values
		userValues.put(name, value);
	}

	@Override
	public Long getLong(String name) throws MissingResourceException
	{
		String value=getValue(name);
		if (value==null) return null;
		return new Long(value);
	}

	@Override
	public void setLong(String name, Long value)
	{
		// We have to save null values because they may overwrite default values
		userValues.put(name, String.valueOf(value));
	}

	@Override
	public Double getDouble(String name) throws MissingResourceException
	{
		String value=getValue(name);
		if (value==null) return null;
		return new Double(value);
	}

	@Override
	public void setDouble(String name, Double value)
	{
		// We have to save null values because they may overwrite default values
		userValues.put(name, String.valueOf(value));
	}

	@Override
	public Boolean getBoolean(String name) throws MissingResourceException
	{
		String value=getValue(name);
		if (value==null) return null;
		return new Boolean(value);
	}

	@Override
	public void setBoolean(String name, Boolean value)
	{
		// We have to save null values because they may overwrite default values
		userValues.put(name, String.valueOf(value));
	}

	@Override
	public Object getObject(String property)
	{
		return getString(property);
	}

	@Override
	public List getList(String property)
	{
		throw new UnsupportedOperationException();
	}
}
