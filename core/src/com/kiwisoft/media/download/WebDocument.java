package com.kiwisoft.media.download;

import java.io.*;
import java.net.*;
import java.util.*;
import java.beans.PropertyChangeListener;

import com.kiwisoft.utils.*;
import com.kiwisoft.utils.xml.*;
import com.kiwisoft.cfg.Configuration;

public class WebDocument extends XMLAdapter implements PropertyChangeSource
{
	public final static State NEW=new NewState();
	public final static State DOWNLOADED=new DownloadedState();
	public final static State PARSED=new ParsedState();
	public final static State COMPLETED=new CompletedState();
	public final static State FAILED=new FailedState();

	public static final String STATE="state";
	public static final String ELEMENTS="elements";
	public static final String LINKS="links";
	public static final String CONTENT_TYPE="contentType";

	private URL url;
	private File file;
	private String contentType;
	private long expiration=-1;
	private long lastModified=-1;
	private long size=-1;
	private State state=NEW;
	private String error;
	private GrabberProject project;
	private String fileName;
	private List<URL> elements=new ArrayList<URL>();
	private List<URL> links=new ArrayList<URL>();
	private boolean queued;

	private BeanChangeSupport changeSupport=new BeanChangeSupport(this);

	public WebDocument(GrabberProject project, String fileName, URL aURL)
	{
		this.project=project;
		this.fileName=fileName;
		url=aURL;
	}

	public URL getURL()
	{
		return url;
	}

	public void setFile(File file)
	{
		this.file=file;
	}

	public File getFile()
	{
		return file;
	}

	public String getFileName()
	{
		return fileName;
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

	public boolean isQueued()
	{
		return queued;
	}

	public void setQueued(boolean queued)
	{
		this.queued=queued;
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
		String oldContentType=this.contentType;
		contentType=value;
		changeSupport.firePropertyChange(CONTENT_TYPE, oldContentType, contentType);
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

	public boolean isParsable()
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

	public void addElement(URL url)
	{
		if (!elements.contains(url))
		{
			elements.add(url);
			changeSupport.fireElementAdded(ELEMENTS, url);
		}
	}

	public List<URL> getElements()
	{
		return Collections.unmodifiableList(elements);
	}

	public void addLink(URL url)
	{
		if (!links.contains(url))
		{
			links.add(url);
			changeSupport.fireElementAdded(LINKS, url);
		}
	}

	public List<URL> getLinks()
	{
		return Collections.unmodifiableList(links);
	}

	public GrabberProject getProject()
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
		@Override
		public boolean isEditable()
		{
			return true;
		}

		@Override
		public boolean isDownloadable()
		{
			return true;
		}

		@Override
		public void enqueueForDownload(WebDocument document)
		{
			GrabberUtils.getDownloadQueue().addJob(new DownloadJob(document));
			document.setQueued(true);
		}

		@Override
		public String toString()
		{
			return "New";
		}

		@Override
		public String getIcon()
		{
			return "webpage.new";
		}
	}

	private static class DownloadedState extends State
	{
		@Override
		public boolean isDownloadable()
		{
			return true;
		}

		@Override
		public void enqueueForDownload(WebDocument document)
		{
			GrabberUtils.getDownloadQueue().addJob(new DownloadJob(document));
			document.setQueued(true);
		}

		@Override
		public boolean isDownloaded()
		{
			return true;
		}

		@Override
		public void enqueueForParsing(WebDocument document)
		{
			GrabberUtils.getParserQueue().addJob(new ParserJob(document));
			document.setQueued(true);
		}

		@Override
		public String toString()
		{
			return "Downloaded";
		}

		@Override
		public String getIcon()
		{
			return "webpage.queued";
		}
	}

	private static class ParsedState extends State
	{
		@Override
		public boolean isDownloadable()
		{
			return true;
		}

		@Override
		public void enqueueForDownload(WebDocument document)
		{
			GrabberUtils.getDownloadQueue().addJob(new DownloadJob(document));
			document.setQueued(true);
		}

		@Override
		public boolean isDownloaded()
		{
			return true;
		}

		@Override
		public void enqueueForParsing(WebDocument document)
		{
			GrabberUtils.getParserQueue().addJob(new ParserJob(document));
			document.setQueued(true);
		}

		@Override
		public String toString()
		{
			return "Parsed";
		}

		@Override
		public String getIcon()
		{
			return "webpage.queued";
		}
	}

	private static class CompletedState extends State
	{
		@Override
		public boolean isDownloadable()
		{
			return true;
		}

		@Override
		public void enqueueForDownload(WebDocument document)
		{
			GrabberUtils.getDownloadQueue().addJob(new DownloadJob(document));
			document.setQueued(true);
		}

		@Override
		public boolean isDownloaded()
		{
			return true;
		}

		@Override
		public void enqueueForParsing(WebDocument document)
		{
			GrabberUtils.getParserQueue().addJob(new ParserJob(document));
			document.setQueued(true);
		}

		@Override
		public String toString()
		{
			return "Completed";
		}

		@Override
		public String getIcon()
		{
			return "webpage.finished";
		}
	}

	private static class FailedState extends State
	{
		@Override
		public boolean isEditable()
		{
			return true;
		}

		@Override
		public boolean isDownloadable()
		{
			return true;
		}

		@Override
		public void enqueueForDownload(WebDocument document)
		{
			GrabberUtils.getDownloadQueue().addJob(new DownloadJob(document));
			document.setQueued(true);
		}

		@Override
		public String toString()
		{
			return "Failed";
		}

		@Override
		public String getIcon()
		{
			return "webpage.error";
		}
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

	// XML interface methods

	public WebDocument(XMLContext context, String name)
	{
		super(context, name);
		project=(GrabberProject)context.getAttribute("project");
	}

	@Override
	public void setXMLAttribute(XMLContext context, String uri, String name, String value)
	{
		if ("fileName".equalsIgnoreCase(name)) this.fileName=value;
		else if ("error".equalsIgnoreCase(name)) this.error=value;
		else if ("file".equalsIgnoreCase(name)) file=new File(Configuration.getInstance().getString("path.downloads"), value);
		else if ("size".equalsIgnoreCase(name)) size=Long.parseLong(value);
		else if ("expiration".equalsIgnoreCase(name)) expiration=Long.parseLong(value);
		else if ("lastModified".equalsIgnoreCase(name)) lastModified=Long.parseLong(value);
		else if ("url".equalsIgnoreCase(name))
		{
			try
			{
				url=new URL(value);
				project.registerDocument(this);
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
		}
		else if ("state".equalsIgnoreCase(name))
		{
			if (value.equals(NEW.toString())) state=NEW;
			else if (value.equals(DOWNLOADED.toString())) state=DOWNLOADED;
			else if (value.equals(PARSED.toString())) state=PARSED;
			else if (value.equals(COMPLETED.toString())) state=COMPLETED;
			else if (value.equals(FAILED.toString())) state=FAILED;
		}
	}

	@Override
	public void addXMLElement(XMLContext context, XMLObject element)
	{
		if (element instanceof DefaultXMLObject)
		{
			DefaultXMLObject xmlObject=(DefaultXMLObject)element;
			if ("element".equalsIgnoreCase(xmlObject.getName()))
			{
				try
				{
					elements.add(new URL(xmlObject.getContent()));
				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
			}
			else if ("link".equalsIgnoreCase(xmlObject.getName()))
			{
				try
				{
					links.add(new URL(xmlObject.getContent()));
				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void writeXML(XMLWriter writer) throws IOException
	{
		writer.startElement("document");
		writer.setAttribute("fileName", fileName);
		writer.setAttribute("url", getURL().toString());
		writer.setAttribute(STATE, state.toString());
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
		writer.setAttribute(CONTENT_TYPE, contentType);
		if (size!=-1) writer.setAttribute("size", size);
		if (expiration!=-1) writer.setAttribute("expiration", expiration);
		if (lastModified!=-1) writer.setAttribute("lastModified", lastModified);
		writer.setAttribute("error", error);
		for (URL element : elements) writer.addElement("element", element.toString());
		for (URL link : links) writer.addElement("link", link.toString());
		writer.closeElement("document");
	}
}