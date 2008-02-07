package com.kiwisoft.media.download;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import com.kiwisoft.utils.xml.*;
import com.kiwisoft.utils.Disposable;
import com.kiwisoft.utils.PropertyChangeSource;
import com.kiwisoft.collection.CollectionChangeSupport;
import com.kiwisoft.collection.CollectionChangeListener;

public class DownloadProject extends XMLAdapter implements PropertyChangeSource
{
	public static final String STATE="state";
	public static final String FOLDERS="folders";

	private List<WebDocument> documents;
	private Map<String, WebFolder> folders;
	private Hashtable<String, WebDocument> urlMap;

	private DownloadThread downloader;
	private ParserThread parser;

	private Map<WebDocument.State, List<WebDocument>> queues;
	private int id=1;

	private CollectionChangeSupport collectionSupport=new CollectionChangeSupport(this);
	private PropertyChangeSupport changeSupport=new PropertyChangeSupport(this);

	public DownloadProject()
	{
		documents=new ArrayList<WebDocument>();
		folders=new HashMap<String, WebFolder>();
		urlMap=new Hashtable<String, WebDocument>();
		queues=new HashMap<WebDocument.State, List<WebDocument>>();
		queues.put(WebDocument.DOWNLOADING, new ArrayList<WebDocument>());
		queues.put(WebDocument.PARSING, new ArrayList<WebDocument>());
		try
		{
			addRootDocument(new WebDocument(this, new URL("http://java.sstiller.de")));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}

	private void addFolder(WebFolder folder)
	{
		folders.put(folder.getName(), folder);
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
		queues.get(WebDocument.DOWNLOADING).clear();
		queues.get(WebDocument.PARSING).clear();
		changeSupport.firePropertyChange(STATE, null, "cleared");
	}

	public void load(File file)
	{
		clear();
		XMLHandler handler=new XMLHandler();
		handler.addTagMapping("document", WebDocument.class);
		handler.addTagMapping("documents", this);
		String documentIds="document";
		handler.addIdAttribute("document", "id", documentIds);
		handler.addRefAttribute("document", "links", documentIds);
		handler.addRefAttribute("document", "objects", documentIds);
		handler.addRefAttribute("documents", "root", documentIds);
		handler.loadFile(file);
	}

	public void save(File file) throws IOException
	{
		XMLWriter writer=new XMLWriter(new FileWriter(file), null);
		writer.start();
		writer.startElement("documents");
		Iterator it=getRootDocuments().iterator();
		if (it.hasNext())
		{
			StringBuilder buffer=new StringBuilder();
			while (it.hasNext())
			{
				WebDocument document=(WebDocument)it.next();
				buffer.append(String.valueOf(document.getId()));
				if (it.hasNext()) buffer.append(",");
			}
			writer.setAttribute("root", buffer.toString());
		}
		it=documents.iterator();
		while (it.hasNext()) ((XMLWritable)it.next()).writeXML(writer);
		writer.closeElement("documents");
		writer.close();
	}

	public boolean canSave()
	{
		return queues.get(WebDocument.DOWNLOADING).isEmpty()
		    && queues.get(WebDocument.PARSING).isEmpty();
	}

	public void setXMLReference(XMLContext context, String name, Object value)
	{
		if ("root".equalsIgnoreCase(name)) addRootDocument((WebDocument)value);
	}

	public void addXMLElement(XMLContext context, XMLObject element)
	{
		if (element instanceof WebDocument)
		{
			WebDocument document=(WebDocument)element;
			addNewDocument(document);
		}
	}

	public String toXML()
	{
		return null;
	}

	public WebDocument getDocumentFromQueue(WebDocument.State state)
	{
		List<WebDocument> queue=queues.get(state);
		synchronized (queue)
		{
			if (!queue.isEmpty()) return queue.remove(0);
			else return null;
		}
	}

	public void addDocumentToQueue(WebDocument.State state, WebDocument document)
	{
		List<WebDocument> queue=queues.get(state);
		synchronized (queue)
		{
			queue.add(document);
		}
	}

	private String createKey(URL url)
	{
		String ref=url.getRef();
		String s=url.toString();
		if (ref!=null && ref.length()>0)
		{
			return s.substring(0, s.lastIndexOf("#"));
		}
		return s;
	}

	public void addNewDocument(WebDocument document)
	{
		synchronized (urlMap)
		{
			if (!documents.contains(document))
			{
				documents.add(document);
				urlMap.put(createKey(document.getURL()), document);
				collectionSupport.fireElementAdded("documents", document);
//				document.checkCache();
			}
		}
	}

	public void deleteDocument(WebDocument document)
	{
		synchronized (urlMap)
		{
			documents.remove(document);
			urlMap.remove(createKey(document.getURL()));
		}
	}

	public void setDocumentFailed(WebDocument document)
	{
		collectionSupport.fireElementChanged("documents", document);
	}

	public void setDocumentCompleted(WebDocument document)
	{
		collectionSupport.fireElementChanged("documents", document);
	}

	public WebDocument getDocumentForURL(URL url)
	{
		synchronized (urlMap)
		{
			return urlMap.get(createKey(url));
		}
	}

	public void mapURLToDocument(URL url, WebDocument document)
	{
		synchronized (urlMap)
		{
			urlMap.put(createKey(url), document);
		}
	}

	public void changeDocumentURL(WebDocument document, URL oldURL, URL newURL)
	{
		synchronized (urlMap)
		{
			urlMap.remove(createKey(oldURL));
			urlMap.put(createKey(newURL), document);
		}
		collectionSupport.fireElementChanged("documents", document);
	}

	public boolean containsURL(URL url)
	{
		synchronized (urlMap)
		{
			return urlMap.containsKey(createKey(url));
		}
	}

	public void addRootDocument(WebDocument document)
	{
		try
		{
			String[] pathElements=document.getPathElements();
			WebFolder folder=null;
			for (int i=0; i<pathElements.length-1; i++)
			{
				String pathElement=pathElements[i];
				if (folder==null)
				{
					WebFolder child=getFolder(pathElement);
					if (child==null) child=createFolder(pathElement);
					folder=child;
				}
				else
				{
					WebFolder child=folder.getFolder(pathElement);
					if (child==null) child=folder.createSubFolder(pathElement);
					folder=child;
				}
			}
			if (folder==null)
			{
				if (pathElements.length==1)
				folder=getFolder(pathElements[0]);
				if (folder==null) folder=createFolder(pathElements[0]);
			}
			folder.addDocument(document);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private WebFolder createFolder(String pathElement)
	{
		WebFolder child=new WebFolder(null, pathElement);
		addFolder(child);
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

	public int getId()
	{
		return id++;
	}

	public void initId(int id)
	{
		if (this.id<=id) this.id=id+1;
	}

	public Set<WebDocument> getRootDocuments()
	{
		Set<WebDocument> set=new HashSet<WebDocument>();
		for (Iterator it=getFolders().iterator(); it.hasNext();)
		{
			WebFolder folder=(WebFolder)it.next();
			folder.getDocuments(set);
		}
		return set;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		changeSupport.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

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

	public void start()
	{
		if (parser==null || !parser.isAlive())
		{
			parser=new ParserThread(this);
			parser.start();
		}
		if (downloader==null || !downloader.isAlive())
		{
			downloader=new DownloadThread(this);
			downloader.start();
		}
	}

	public void stop()
	{
		if (downloader!=null) downloader.setStopped();
		if (parser!=null) parser.setStopped();
	}

}
