package com.kiwisoft.media;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Hashtable;

import com.kiwisoft.persistence.DBObject;
import com.kiwisoft.persistence.DBLoader;

/**
 * @author Stefan Stiller
 */
public class MediaTransferable implements Transferable
{
	public static final DataFlavor DATA_FLAVOR=new DataFlavor(DBObject.class, "DBObject");

	private final static DataFlavor[] flavors=new DataFlavor[]{DATA_FLAVOR, DataFlavor.stringFlavor};

	private Class<? extends DBObject> objectClass;
	private Object primaryKey;
	private Map<String, Serializable> properties;

	public MediaTransferable(Class<? extends DBObject> objectClass, Object primaryKey)
	{
		this.objectClass=objectClass;
		this.primaryKey=primaryKey;
	}

	public Class<? extends DBObject> getObjectClass()
	{
		return objectClass;
	}

	public Object getPrimaryKey()
	{
		return primaryKey;
	}

	public Serializable getProperty(String property)
	{
		if (properties==null) return null;
		return properties.get(property);
	}

	public void setProperty(String property, Serializable value)
	{
		if (properties==null) properties=new Hashtable<String, Serializable>();
		properties.put(property, value);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return flavors;
	}

	/**
	 * Returns whether the requested flavor is supported by this
	 * <code>Transferable</code>.
	 *
	 * @param flavor the requested flavor for the data
	 * @return true if <code>flavor</code> is equal to
	 *         <code>DataFlavor.stringFlavor</code> or
	 *         <code>DataFlavor.plainTextFlavor</code>; false if <code>flavor</code>
	 *         is not one of the above flavors
	 * @throws NullPointerException if flavor is <code>null</code>
	 */
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		for (int i=0; i<flavors.length; i++)
		{
			if (flavor.equals(flavors[i])) return true;
		}
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if (DATA_FLAVOR.equals(flavor))
			return DBLoader.getInstance().load(objectClass, primaryKey);
		else if (DataFlavor.stringFlavor.equals(flavor))
			return objectClass.getName()+"#"+primaryKey;
		return null;
	}
}
