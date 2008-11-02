package com.kiwisoft.media.download;

import java.io.*;
import java.net.*;
import java.util.*;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import com.kiwisoft.utils.*;
import com.kiwisoft.utils.xml.*;
import com.kiwisoft.cfg.Configuration;
import com.kiwisoft.collection.CollectionChangeSupport;
import com.kiwisoft.collection.CollectionChangeListener;

public class WebDocument implements XMLObject, XMLWritable, PropertyChangeSource
{
	private int id;
	private URL url;
	private File file;

	private List<WebDocument> containedDocuments=new ArrayList<WebDocument>();
	private List<WebDocument> linkedDocuments=new ArrayList<WebDocument>();

	private String contentType;
	private long expiration=-1;
	private long lastModified=-1;
	private long size=-1;
	private State state=NEW;

	private String error;

	private final static State NEW=new NewState();
	public final static State DOWNLOADING=new DownloadingState();
	private final static State DOWNLOADED=new DownloadedState();
	public final static State PARSING=new ParsingState();
	private final static State PARSED=new ParsedState();
	private final static State COMPLETED=new CompletedState();
	private final static State FAILED=new FailedState();

	private PropertyChangeSupport changeSupport=new PropertyChangeSupport(this);
	private CollectionChangeSupport collectionSupport=new CollectionChangeSupport(this);

	public static final String STATE="state";
	public static final String CONTAINED_DOCUMENTS="containedDocuments";
	public static final String LINKED_DOCUMENTS="linkedDocuments";

	private DownloadProject project;

	public WebDocument(DownloadProject project, URL aURL)
	{
		this.project=project;
		id=project.getId();
		url=aURL;
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public WebDocument(XMLContext context, String name)
	{
	}

	public void setXMLAttribute(XMLContext context, String name, String value)
	{
		if ("url".equalsIgnoreCase(name))
		{
			try
			{
				url=new URL(value);
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
		}
		else if ("id".equalsIgnoreCase(name))
		{
			try
			{
				id=Integer.parseInt(value);
				project.initId(id);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		else if ("file".equalsIgnoreCase(name))
		{
			file=new File(Configuration.getInstance().getString("path.downloads"), value);
		}
		else if ("state".equalsIgnoreCase(name))
		{
			if (value.equals(NEW.toString())) state=NEW;
			else if (value.equals(DOWNLOADED.toString())) state=DOWNLOADED;
			else if (value.equals(DOWNLOADING.toString())) state=DOWNLOADING;
			else if (value.equals(PARSED.toString())) state=PARSED;
			else if (value.equals(PARSING.toString())) state=PARSING;
			else if (value.equals(COMPLETED.toString())) state=COMPLETED;
			else if (value.equals(FAILED.toString())) state=FAILED;
		}
	}

	public void setXMLReference(XMLContext context, String name, Object value)
	{
		if ("links".equalsIgnoreCase(name)) addLinkedDocument((WebDocument)value);
		else if ("objects".equalsIgnoreCase(name)) addContainedDocument((WebDocument)value);
	}

	public void setXMLContent(XMLContext context, String value)
	{
	}

	public void addXMLElement(XMLContext context, XMLObject element)
	{
	}

	public void writeXML(XMLWriter writer) throws IOException
	{
		writer.startElement("document");
		writer.setAttribute("id", Integer.toString(id));
		writer.setAttribute("url", getURL().toString());
		if (file!=null)
		{
			try
			{
				String relativePath=FileUtils.getRelativePath(Configuration.getInstance().getString("path.downloads"), file.getAbsolutePath());
				writer.setAttribute("file", relativePath);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		writer.setAttribute("state", state.toString());
		Iterator it=getLinkedDocuments().iterator();
		if (it.hasNext())
		{
			StringBuilder buffer=new StringBuilder();
			while (it.hasNext())
			{
				WebDocument document=(WebDocument)it.next();
				buffer.append(document.getId());
				if (it.hasNext()) buffer.append(",");
			}
			writer.setAttribute("links", buffer.toString());
		}
		it=getContainedDocuments().iterator();
		if (it.hasNext())
		{
			StringBuilder buffer=new StringBuilder();
			while (it.hasNext())
			{
				WebDocument document=(WebDocument)it.next();
				buffer.append(document.getId());
				if (it.hasNext()) buffer.append(",");
			}
			writer.setAttribute("objects", buffer.toString());
		}
		writer.closeElement("document");
	}

	public int getId()
	{
		return id;
	}

	public URL getURL()
	{
		return url;
	}

	public void setURL(URL newURL)
	{
		URL oldURL=url;
		url=newURL;
		project.changeDocumentURL(this, oldURL, newURL);
	}

	public String[] getPathElements()
	{
		String path=url.getHost()+url.getPath();
		return path.split("/");
	}

	public void setFile(File file)
	{
		this.file=file;
	}

	public File getFile()
	{
		return file;
	}

	public State getState()
	{
		return state;
	}

	public void setState(State newState)
	{
		State oldState=getState();
		state=newState;
		changeSupport.firePropertyChange(STATE, oldState, state);
	}

	public boolean isEditable()
	{
		return getState().isEditable();
	}

	public void enqueueForDownload()
	{
		getState().enqueueForDownload(this);
	}

	public void download()
	{
		getState().download(this);
	}

	public void enqueueForParsing()
	{
		getState().enqueueForParsing(this);
	}

	public void parse()
	{
		getState().parse(this);
	}

	public void setSize(long value)
	{
		size=value;
	}

	public long getSize()
	{
		if (size>0)
			return size;
		else
		{
			if (file!=null && file.exists()) return file.length();
		}
		return -1;
	}

	public String getError()
	{
		return error;
	}

	public void setError(String value)
	{
		error=value;
		System.out.println("Error: "+url+" "+error);
	}

	public String getContentType()
	{
		return contentType;
	}

	public void setContentType(String value)
	{
		contentType=value;
	}

	public void setExpiration(long value)
	{
		expiration=value;
	}

	public long getExpiration()
	{
		return expiration;
	}

	public void setLastModified(long value)
	{
		lastModified=value;
	}

	public long getLastModified()
	{
		return lastModified;
	}

	public boolean needsParsing()
	{
		return ParserFactory.isParsable(contentType);
	}

	public boolean parse(List<URL> contained, List<URL> linked)
	{
		Parser parser=ParserFactory.getParser(contentType);
		if (parser!=null)
		{
			try
			{
				parser.parse(file, url, contained, linked);
				setState(PARSED);
				return true;
			}
			catch (Exception e)
			{
				setState(FAILED);
				setError("Exception: "+e.getClass()+": "+e.getMessage());
				return false;
			}
		}
		else
		{
			setError("No parser found.");
			setState(FAILED);
			return false;
		}
	}

	public void open()
	{
		WebUtils.openURL(url);
	}

	public void addContainedDocument(WebDocument document)
	{
		if (!containedDocuments.contains(document))
		{
			containedDocuments.add(document);
			collectionSupport.fireElementAdded(CONTAINED_DOCUMENTS, document);
		}
	}

	public List<WebDocument> getContainedDocuments()
	{
		return Collections.unmodifiableList(containedDocuments);
	}

	public void addLinkedDocument(WebDocument document)
	{
		if (!linkedDocuments.contains(document))
		{
			linkedDocuments.add(document);
			collectionSupport.fireElementAdded(LINKED_DOCUMENTS, document);
			project.addRootDocument(document);
		}
	}

	public List getLinkedDocuments()
	{
		return Collections.unmodifiableList(linkedDocuments);
	}

	public DownloadProject getProject()
	{
		return project;
	}

	public boolean isDownloable()
	{
		return getState().isDownloadable();
	}

	public abstract static class State
	{
		public boolean isEditable()
		{
			return false;
		}

		public boolean isDownloadable()
		{
			return false;
		}

		public void enqueueForDownload(WebDocument document)
		{
			throw new UnsupportedOperationException("Downloading "+document+" not allowed in state "+this);
		}

		public void download(WebDocument document)
		{
			throw new UnsupportedOperationException();
		}

		public void enqueueForParsing(WebDocument document)
		{
			throw new UnsupportedOperationException("Parsing "+document+" not allowed in state "+this);
		}

		public boolean isDownloaded()
		{
			return false;
		}

		public void parse(WebDocument document)
		{
			throw new UnsupportedOperationException();
		}

		public abstract String getIcon();
	}

	private static class NewState extends State
	{
		public boolean isEditable()
		{
			return true;
		}

		public boolean isDownloadable()
		{
			return true;
		}

		public void enqueueForDownload(WebDocument document)
		{
			document.getProject().addDocumentToQueue(DOWNLOADING, document);
			document.setState(DOWNLOADING);
		}

		public String toString()
		{
			return "New";
		}

		public String getIcon()
		{
			return "webpage.new";
		}
	}

	private static File buildFile(URL url) throws UnsupportedEncodingException
	{
		String host=url.getHost();
		String path=url.getPath();
		String query=url.getQuery();
		if ("".equals(path)) path="/index.html";
		if (path.endsWith("/")) path=path+"index.html";
		if (query!=null)
		{
			int indexName=path.lastIndexOf("/");
			int indexExt=path.lastIndexOf(".");
			if (indexExt>0 && indexExt>indexName)
			{
				path=path.substring(0, indexExt)+"_"+URLEncoder.encode(query, "UTF-8")+path.substring(indexExt, path.length());
				path=path.replace('%', '_');
			}
		}
		return new File(Configuration.getInstance().getString("path.downloads"), host+File.separator+path);
	}

	public void checkCache()
	{
		try
		{
			File oldFile=buildFile(url);
			if (oldFile.exists() && oldFile.isFile())
			{
				file=oldFile;
				String fileName=file.getName().toLowerCase();
				if (fileName.endsWith(".html") || fileName.endsWith(".htm")) contentType="text/html";
				else if (fileName.endsWith(".txt")) contentType="text";
				else contentType="unknown";
				state=DOWNLOADED;
				size=file.length();
				enqueueForParsing();
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	private static class DownloadingState extends State
	{
		public void download(WebDocument document)
		{
			try
			{
				HttpURLConnection connection=(HttpURLConnection)document.getURL().openConnection();
				connection.connect();
				URL url=connection.getURL();
				document.setURL(url);
				int responseCode=connection.getResponseCode();
				if (responseCode<400)
				{
					document.setContentType(connection.getContentType());
					document.setSize(connection.getContentLength());
					document.setExpiration(connection.getExpiration());
					document.setLastModified(connection.getLastModified());

					// Build name for local file
					File file=buildFile(url);
					document.setFile(file);
					file.getParentFile().mkdirs();

					// Download document
//					long time1=System.currentTimeMillis();
					InputStream is=connection.getInputStream();
					FileOutputStream fos=new FileOutputStream(file);
					byte[] buffer=new byte[Configuration.getInstance().getLong("buffer.download", 4096L).intValue()];
					int bytesRead;
					while ((bytesRead=is.read(buffer))!=-1)
					{
						fos.write(buffer, 0, bytesRead);
					}
					if ("text/html".equals(document.getContentType())) fos.write(("<!-- saved form url="+url+"-->").getBytes());
					fos.flush();
					fos.close();
					is.close();
//					long time2=System.currentTimeMillis();
//					System.out.println("\tDownload Time: "+(time2-time1));
					document.setState(DOWNLOADED);
					document.enqueueForParsing();

					// Close connection
					connection.disconnect();
					return;
				}
				else
				{
					document.setState(FAILED);
					document.setError("HTTP Error: "+responseCode);
					connection.disconnect();
					return;
				}
			}
			catch (Exception e)
			{
				document.setState(FAILED);
				document.setError("Exception: "+e.getClass()+": "+e.getMessage());
				return;
			}
		}

		public String toString()
		{
			return "Downloading";
		}

		public String getIcon()
		{
			return "webpage.processing";
		}
	}

	private static class DownloadedState extends State
	{
		public boolean isDownloadable()
		{
			return true;
		}

		public void enqueueForDownload(WebDocument document)
		{
			document.getProject().addDocumentToQueue(DOWNLOADING, document);
			document.setState(DOWNLOADING);
		}

		public boolean isDownloaded()
		{
			return true;
		}

		public void enqueueForParsing(WebDocument document)
		{
			document.getProject().addDocumentToQueue(PARSING, document);
			document.setState(PARSING);
		}

		public String toString()
		{
			return "Downloaded";
		}

		public String getIcon()
		{
			return "webpage.queued";
		}
	}

	private static class ParsingState extends State
	{
		public void parse(WebDocument document)
		{
			if (document.needsParsing())
			{
				List<URL> containedURLs=new ArrayList<URL>();
				List<URL> linkedURLs=new ArrayList<URL>();
				boolean result;
				Parser parser=ParserFactory.getParser(document.contentType);
				if (parser!=null)
				{
					try
					{
						parser.parse(document.file, document.url, containedURLs, linkedURLs);
						document.setState(COMPLETED);
						result=true;
					}
					catch (Exception e)
					{
						document.setState(FAILED);
						document.setError("Exception: "+e.getClass()+": "+e.getMessage());
						result=false;
					}
				}
				else
				{
					document.setError("No parser found.");
					document.setState(FAILED);
					result=false;
				}
				if (result)
				{
					WebDocument newDocument;
					DownloadProject project=document.getProject();
					for (URL containedURL : containedURLs)
					{
						newDocument=project.getDocumentForURL(containedURL);
						if (newDocument==null)
						{
							newDocument=new WebDocument(project, containedURL);
							project.addNewDocument(newDocument);
							if (newDocument.getState()==NEW) newDocument.enqueueForDownload();
						}
						document.addContainedDocument(newDocument);
					}
					for (URL linkedURL : linkedURLs)
					{
						newDocument=project.getDocumentForURL(linkedURL);
						if (newDocument==null)
						{
							newDocument=new WebDocument(project, linkedURL);
							project.addNewDocument(newDocument);
						}
						document.addLinkedDocument(newDocument);
					}
				}
			}
			else
			{
				document.setState(COMPLETED);
			}
		}

		public String toString()
		{
			return "Parsing";
		}

		public String getIcon()
		{
			return "webpage.processing";
		}
	}

	private static class ParsedState extends State
	{
		public boolean isDownloadable()
		{
			return true;
		}

		public void enqueueForDownload(WebDocument document)
		{
			document.getProject().addDocumentToQueue(DOWNLOADING, document);
			document.setState(DOWNLOADING);
		}

		public boolean isDownloaded()
		{
			return true;
		}

		public void enqueueForParsing(WebDocument document)
		{
			document.getProject().addDocumentToQueue(PARSING, document);
			document.setState(PARSING);
		}

		public String toString()
		{
			return "Parsed";
		}

		public String getIcon()
		{
			return "webpage.queued";
		}
	}

	private static class CompletedState extends State
	{
		public boolean isDownloadable()
		{
			return true;
		}

		public void enqueueForDownload(WebDocument document)
		{
			document.getProject().addDocumentToQueue(DOWNLOADING, document);
			document.setState(DOWNLOADING);
		}

		public boolean isDownloaded()
		{
			return true;
		}

		public void enqueueForParsing(WebDocument document)
		{
			document.getProject().addDocumentToQueue(PARSING, document);
			document.setState(PARSING);
		}

		public String toString()
		{
			return "Completed";
		}

		public String getIcon()
		{
			return "webpage.finished";
		}
	}

	private static class FailedState extends State
	{
		public boolean isEditable()
		{
			return true;
		}

		public boolean isDownloadable()
		{
			return true;
		}

		public void enqueueForDownload(WebDocument document)
		{
			document.getProject().addDocumentToQueue(DOWNLOADING, document);
			document.setState(DOWNLOADING);
		}

		public String toString()
		{
			return "Failed";
		}

		public String getIcon()
		{
			return "webpage.error";
		}
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

	public Disposable addCollectionListener(CollectionChangeListener listener)
	{
		return collectionSupport.addListener(listener);
	}

	public void removeCollectionListener(CollectionChangeListener listener)
	{
		collectionSupport.removeListener(listener);
	}
}