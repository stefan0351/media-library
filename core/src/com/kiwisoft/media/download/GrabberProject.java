package com.kiwisoft.media.download;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import com.kiwisoft.utils.xml.*;
import com.kiwisoft.utils.Disposable;
import com.kiwisoft.utils.PropertyChangeSource;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.collection.CollectionChangeSupport;
import com.kiwisoft.collection.CollectionChangeListener;

public class GrabberProject extends XMLAdapter implements PropertyChangeSource
{
	public static final String STATE="state";
	public static final String FOLDERS="folders";

	private List<WebDocument> documents=new ArrayList<WebDocument>();
	private Map<String, WebFolder> folders=new Hashtable<String, WebFolder>();
	private Map<String, WebDocument> urlMap=new Hashtable<String, WebDocument>();

	private CollectionChangeSupport collectionSupport=new CollectionChangeSupport(this);
	private PropertyChangeSupport changeSupport=new PropertyChangeSupport(this);

	public GrabberProject()
	{
	}

	public List getDocuments()
	{
		return documents;
	}

	public void clear()
	{
		documents.clear();
		folders.clear();
		urlMap.clear();
		changeSupport.firePropertyChange(STATE, null, "cleared");
	}

	public static GrabberProject load(File file)
	{
		XMLHandler handler=new XMLHandler();
		handler.addTagMapping("project", GrabberProject.class);
		handler.addTagMapping("folder", WebFolder.class);
		handler.addTagMapping("document", WebDocument.class);
		handler.loadFile(file);
		return (GrabberProject)handler.getRootElement();
	}

	public void save(File file) throws IOException
	{
		XMLWriter writer=new XMLWriter(new FileWriter(file), null);
		writer.start();
		writeXML(writer);
		writer.close();
	}

	public WebDocument getDocumentForURL(URL url)
	{
		synchronized (urlMap)
		{
			return urlMap.get(GrabberUtils.getURLWithoutRef(url));
		}
	}

	public boolean containsURL(URL url)
	{
		synchronized (urlMap)
		{
			return urlMap.containsKey(GrabberUtils.getURLWithoutRef(url));
		}
	}

	public void registerDocument(WebDocument webDocument)
	{
		urlMap.put(GrabberUtils.getURLWithoutRef(webDocument.getURL()), webDocument);
	}

	private WebFolder getFolder(String name, boolean create)
	{
		WebFolder folder=getFolder(name);
		if (folder==null && create) folder=createFolder(name);
		return folder;
	}

	public WebDocument createDocument(URL url)
	{
		try
		{
			String host=url.getHost();
			if (url.getPort()!=-1) host=host+":"+url.getPort();
			WebFolder folder=getFolder(host, true);

			String path=url.getPath();
			if (StringUtils.isEmpty(path)) path=".";
			else if (path.endsWith("/")) path=path+".";
			String[] pathElements=path.split("/");
			for (int i=1; i<pathElements.length-1; i++)
			{
				String pathElement=pathElements[i];
				folder=folder.getFolder(pathElement, true);
			}
			String fileName=pathElements[pathElements.length-1];
			if (!StringUtils.isEmpty(url.getQuery()))
			{
				fileName=fileName+"?"+url.getQuery();
			}
			WebDocument document=new WebDocument(this, fileName, url);
			folder.addDocument(document);
			registerDocument(document);
			return document;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public void addRootDocument(WebDocument document)
	{
	}

	private WebFolder createFolder(String pathElement)
	{
		WebFolder child=new WebFolder(pathElement);
		folders.put(child.getName(), child);
		collectionSupport.fireElementAdded(FOLDERS, child);
		return child;
	}

	private WebFolder getFolder(String pathElement)
	{
		return folders.get(pathElement);
	}

	public Collection<WebFolder> getFolders()
	{
		return Collections.unmodifiableCollection(folders.values());
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		changeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		changeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public Disposable addListener(CollectionChangeListener listener)
	{
		return collectionSupport.addListener(listener);
	}

	public void removeListener(CollectionChangeListener listener)
	{
		collectionSupport.removeListener(listener);
	}

	// XML interface methods

	public GrabberProject(XMLContext context, String aName)
	{
		super(context, aName);
		context.setAttribute("project", this);
	}

	@Override
	public void addXMLElement(XMLContext context, XMLObject element)
	{
		if (element instanceof WebFolder)
		{
			WebFolder folder=(WebFolder)element;
			if (!StringUtils.isEmpty(folder.getName()))
			{
				folders.put(folder.getName(), folder);
			}
		}
	}

	private void writeXML(XMLWriter writer) throws IOException
	{
		writer.startElement("project");
		for (WebFolder folder : folders.values()) folder.writeXML(writer);
		writer.closeElement("project");
	}
}
