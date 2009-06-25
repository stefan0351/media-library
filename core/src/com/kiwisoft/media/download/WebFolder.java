package com.kiwisoft.media.download;

import java.util.*;
import java.net.URL;
import java.io.IOException;

import com.kiwisoft.collection.CollectionChangeSupport;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.utils.Disposable;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.xml.XMLWriter;
import com.kiwisoft.utils.xml.XMLAdapter;
import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLObject;

/**
 * @author Stefan Stiller
 */
public class WebFolder extends XMLAdapter
{
	public static final String DOCUMENTS="documents";
	public static final String SUB_FOLDERS="subFolders";

	private WebFolder parent;
	private String name;
	private Set<WebDocument> documents=new HashSet<WebDocument>();
	private Map<String, WebFolder> folders=new Hashtable<String, WebFolder>();

	private CollectionChangeSupport collectionSupport=new CollectionChangeSupport(this);

	public WebFolder(String name)
	{
		this.name=name;
	}

	private void setParent(WebFolder parent)
	{
		this.parent=parent;
	}

	public WebFolder getParent()
	{
		return parent;
	}

	public String getPath()
	{
		if (parent!=null) return parent.getPath()+name+"/";
		else return name+"/";
	}

	public String[] getPathElements()
	{
		if (parent==null) return new String[]{name};
		else
		{
			String[] parentElements=parent.getPathElements();
			String[] elements=new String[parentElements.length+1];
			System.arraycopy(parentElements, 0, elements, 0, parentElements.length);
			elements[parentElements.length]=name;
			return elements;
		}
	}

	public WebFolder createSubFolder(String name)
	{
		WebFolder folder=new WebFolder(name);
		folder.setParent(this);
		folders.put(name, folder);
		collectionSupport.fireElementAdded(SUB_FOLDERS, folder);
		return folder;
	}

	public String getDocumentName(WebDocument document)
	{
		URL url=document.getURL();
		String path=url.getHost()+url.getPath()+(url.getQuery()!=null ? "?"+url.getQuery() : "");
		String[] pathElements=path.split("/");
		String[] folderPath=getPathElements();
		int j=-1;
		for (int i=0; i<pathElements.length; i++)
		{
			if (i<folderPath.length && pathElements[i].equals(folderPath[i])) continue;
			j=i;
			break;
		}
		if (j==-1) return "/";
		String documentName=null;
		for (int i=j;i<pathElements.length;i++)
		{
			if (documentName==null) documentName=pathElements[i];
			else documentName=documentName+"/"+pathElements[i];
		}
		return documentName;
	}

	public String getName()
	{
		return name;
	}

	public WebFolder getFolder(String pathElement)
	{
		return folders.get(pathElement);
	}

	public void addDocument(WebDocument document)
	{
		documents.add(document);
		collectionSupport.fireElementAdded(DOCUMENTS, document);
	}

	public Collection<WebFolder> getFolders()
	{
		return Collections.unmodifiableCollection(folders.values());
	}

	public Set<WebDocument> getDocuments()
	{
		return Collections.unmodifiableSet(documents);
	}

	public void getDocuments(Set<WebDocument> set)
	{
		set.addAll(documents);
		for (Iterator it=getFolders().iterator(); it.hasNext();)
		{
			WebFolder folder=(WebFolder)it.next();
			folder.getDocuments(set);
		}
	}

	public Disposable addListener(CollectionChangeListener listener)
	{
		return collectionSupport.addListener(listener);
	}

	public void removeListener(CollectionChangeListener listener)
	{
		collectionSupport.removeListener(listener);
	}

	public WebFolder getFolder(String name, boolean create)
	{
		WebFolder folder=getFolder(name);
		if (folder==null && create) folder=createSubFolder(name);
		return folder;
	}

	// XML Interface methods 

	public WebFolder(XMLContext context, String aName)
	{
		super(context, aName);
	}

	@Override
	public void setXMLAttribute(XMLContext context, String uri, String name, String value)
	{
		if ("name".equalsIgnoreCase(name)) this.name=value;
	}

	@Override
	public void addXMLElement(XMLContext context, XMLObject element)
	{
		if (element instanceof WebFolder)
		{
			WebFolder folder=(WebFolder)element;
			if (!StringUtils.isEmpty(folder.getName()))
			{
				folder.setParent(this);
				folders.put(folder.getName(), folder);
			}
		}
		else if (element instanceof WebDocument)
		{
			documents.add((WebDocument)element);
		}
	}

	public void writeXML(XMLWriter writer) throws IOException
	{
		writer.startElement("folder");
		writer.setAttribute("name", name);
		for (WebFolder folder : folders.values()) folder.writeXML(writer);
		for (WebDocument document : documents) document.writeXML(writer);
		writer.closeElement("folder");
	}
}
