package com.kiwisoft.media.tools;

import com.kiwisoft.media.tvdata.Channel;
import com.kiwisoft.media.tvdata.ChannelGroup;
import com.kiwisoft.utils.CSVReader;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * @author Stefan Stiller
 * @since 06.03.11
 */
public class TvBrowserDataService implements TvDataService
{
	private final static Logger LOGGER=Logger.getLogger(TvBrowserDataService.class.getName());

	private static final String CHANNEL_GROUPS_FILENAME="groups.txt";
	public static final String CHANNEL_LIST_FILENAME = "channellist.gz";
	/**
	 * Contains the mirror urls useable for receiving the groups.txt from.
	 */
	private static final String[] DEFAULT_CHANNEL_GROUPS_MIRRORS=
			{
					"http://tvbrowser.dyndns.tv",
					"http://daten.wannawork.de",
					"http://www.gfx-software.de/tvbrowserorg",
					"http://tvbrowser1.sam-schwedler.de",
					"http://tvbrowser.nicht-langweilig.de/data"
			};
	private static final String DEFAULT_CHANNEL_GROUPS_URL="http://tvbrowser.org/listings";

	private File dataDir=new File("tmp", TvBrowserDataService.class.getName());

	public TvBrowserDataService()
	{
	}

	@Override
	public void refreshChannelGroups() throws TvDataException
	{
		dataDir.mkdirs();
		String url=getChannelGroupsMirror().getUrl();

		try
		{
			String name=CHANNEL_GROUPS_FILENAME.substring(0, CHANNEL_GROUPS_FILENAME.indexOf("."))+"_"+Mirror.MIRROR_LIST_FILE_NAME;
			IOUtilities.download(getRelativeUrl(url, name), new File(dataDir, name));
		}
		catch (Exception ee)
		{
		}

		try
		{
			try
			{
				IOUtilities.download(getRelativeUrl(url, CHANNEL_GROUPS_FILENAME), new File(dataDir, CHANNEL_GROUPS_FILENAME));
			}
			catch (Exception ex)
			{
				IOUtilities.download(getRelativeUrl(DEFAULT_CHANNEL_GROUPS_URL, CHANNEL_GROUPS_FILENAME), new File(dataDir, CHANNEL_GROUPS_FILENAME));
			}
		}
		catch (MalformedURLException e)
		{
			throw new TvDataException("Invalid URL: "+url, e);
		}
		catch (IOException e)
		{
			throw new TvDataException("Could not download group file "+url, e);
		}
	}

	private URL getRelativeUrl(String url, String name) throws MalformedURLException
	{
		return new URL(url+(url.endsWith("/") ? "" : "/")+name);
	}

	@Override
	public Set<ChannelGroup> getChannelGroups()
	{
		File groupFile=new File(dataDir, CHANNEL_GROUPS_FILENAME);
		if (!groupFile.exists())
		{
			LOGGER.info("Group file '"+CHANNEL_GROUPS_FILENAME+"' does not exist");
			return Collections.emptySet();
		}
		BufferedReader in=null;
		Set<ChannelGroup> list=new HashSet<ChannelGroup>();

		try
		{
			in=new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(groupFile), 0x1000), "utf-8"));
			String line=in.readLine();
			while (line!=null)
			{
				String[] s=line.split(";");
				if (s.length>=5)
				{
					String groupId=s[0];
					String name=s[1];
					String providername=s[2];
					String description=s[3];

					int n=s.length-4;

					String[] mirrors=new String[n];

					System.arraycopy(s, 4, mirrors, 0, n);

					ChannelGroup group=new ChannelGroup(getId(), groupId, name, description, providername, mirrors);
					list.add(group);
				}
				line=in.readLine();
			}
			in.close();
		}
		catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, "Could not read group list "+CHANNEL_GROUPS_FILENAME, e);
		}
		finally
		{
			if (in!=null)
				try
				{
					in.close();
				}
				catch (Exception ee)
				{
				}
		}

		return list;

	}

	private Mirror getChannelGroupsMirror()
	{
		File file=new File(dataDir, CHANNEL_GROUPS_FILENAME.substring(0, CHANNEL_GROUPS_FILENAME.indexOf("."))+"_"+Mirror.MIRROR_LIST_FILE_NAME);

		if (file.isFile())
		{
			try
			{
				return Mirror.chooseUpToDateMirror(Mirror.readMirrorListFromFile(file), "groups", "  Please inform the TV-Browser team.");
			}
			catch (Exception exc)
			{
			}
		}

		return getDefaultChannelGroupsMirror();
	}


	private static Mirror getDefaultChannelGroupsMirror()
	{
		try
		{
			if (DEFAULT_CHANNEL_GROUPS_MIRRORS.length>0)
			{
				Mirror[] mirr=new Mirror[DEFAULT_CHANNEL_GROUPS_MIRRORS.length];

				for (int i=0; i<DEFAULT_CHANNEL_GROUPS_MIRRORS.length; i++)
				{
					mirr[i]=new Mirror(DEFAULT_CHANNEL_GROUPS_MIRRORS[i]);
				}

				Mirror choosenMirror=Mirror.chooseUpToDateMirror(mirr, "groups", " Please inform the TV-Browser team.");

				if (choosenMirror!=null)
				{
					return choosenMirror;
				}
			}
		}
		catch (Exception exc2)
		{
		}

		return new Mirror(DEFAULT_CHANNEL_GROUPS_URL);
	}

	@Override
	public String getId()
	{
		return "TvBrowserDataService";
	}

	private Mirror[] getServerDefinedMirrors(ChannelGroup channelGroup)
	{
		File groupFile=new File(dataDir, TvBrowserDataService.CHANNEL_GROUPS_FILENAME);
		Mirror[] mirrorArr=null;

		if (!groupFile.exists())
		{
			LOGGER.info("Group file '"+TvBrowserDataService.CHANNEL_GROUPS_FILENAME+"' does not exist");
		}
		else
		{
			BufferedReader in=null;

			try
			{
				in=new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(groupFile), 0x1000), "utf-8"));
				String line=in.readLine();
				while (line!=null)
				{
					String[] s=line.split(";");

					if (s.length>=5 && s[0].compareTo(channelGroup.getId())==0)
					{
						int n=s.length-4;
						mirrorArr=new Mirror[n];

						for (int i=0; i<n; i++)
						{
							mirrorArr[i]=new Mirror(s[i+4], 1);
						}

						break;
					}

					line=in.readLine();
				}
				in.close();
			}
			catch (IOException e)
			{
				LOGGER.log(Level.SEVERE, "Could not read group list "+TvBrowserDataService.CHANNEL_GROUPS_FILENAME, e);
			}
			finally
			{
				if (in!=null)
				{
					try
					{
						in.close();
					}
					catch (Exception ee)
					{
					}
				}
			}
		}

		return mirrorArr;
	}


	/**
	 * Checks and returns the available channels of this group.
	 *
	 * @return The available channel array.
	 */
	@Override
	public void refreshChannels(ChannelGroup channelGroup) throws TvDataException
	{
		// load the mirror list
		Mirror[] serverDefindeMirros=getServerDefinedMirrors(channelGroup);
		Mirror[] mirrorArr=Mirror.loadMirrorList(new File(dataDir, channelGroup.getId()+"_"+Mirror.MIRROR_LIST_FILE_NAME), channelGroup.getMirrors(), serverDefindeMirros);

		// Get a random Mirror that is up to date
		Mirror mirror=Mirror.chooseUpToDateMirror(mirrorArr, channelGroup.getId(), " Please contact the TV data provider for help.");
		LOGGER.info("Using mirror "+mirror.getUrl());

		// Update the mirrorlist (for the next time)
		updateMetaFile(mirror.getUrl(), channelGroup.getId()+"_"+Mirror.MIRROR_LIST_FILE_NAME);

		// Update the groupname file
		updateMetaFile(mirror.getUrl(), channelGroup.getId()+"_info");

		// Update the channel list
		updateChannelList(channelGroup, mirror, true);
	}

	@Override
	public Set<Channel> getChannels(ChannelGroup channelGroup)
	{
		String fileName=channelGroup.getId()+"_"+CHANNEL_LIST_FILENAME;
		try
		{
			return readChannelsFromFile(channelGroup, new File(dataDir, fileName));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return Collections.emptySet();
		}
	}

	private void updateChannelList(ChannelGroup channelGroup, Mirror mirror, boolean forceUpdate) throws TvDataException
	{
		String fileName=channelGroup.getId()+"_"+CHANNEL_LIST_FILENAME;
		File file=new File(dataDir, fileName+".new");
		if (forceUpdate || needsUpdate(file))
		{
			String url=mirror.getUrl()+(mirror.getUrl().endsWith("/") ? "" : "/")+fileName;
			try
			{
				IOUtilities.download(new URL(url), file);
				if (file.canRead() && file.length()>0)
				{
					// try reading the file
					readChannelsFromFile(channelGroup, file);
					// ok, we can read it, so use this new file instead of the old
					File oldFile=new File(dataDir, fileName);
					oldFile.delete();
					file.renameTo(oldFile);
				}
			}
			catch (Exception exc)
			{
				throw new TvDataException("Server has no channel list: "+mirror.getUrl(), exc);
			}
		}
	}

	private Set<Channel> readChannelsFromFile(ChannelGroup channelGroup, File file) throws IOException, FileFormatException
	{
		BufferedInputStream stream=null;
		try
		{
			stream=new BufferedInputStream(new FileInputStream(file), 0x2000);

			return readChannelsFromStream(channelGroup, stream, true);
		}
		finally
		{
			if (stream!=null)
			{
				try
				{
					stream.close();
				}
				catch (IOException exc)
				{
				}
			}
		}
	}

	private Set<Channel> readChannelsFromStream(ChannelGroup channelGroup, InputStream stream, boolean compressed) throws IOException, FileFormatException
	{
		CSVReader reader;

		if (compressed)
		{
			GZIPInputStream gIn=new GZIPInputStream(stream);
			reader=new CSVReader(new InputStreamReader(gIn, "ISO-8859-15"));
		}
		else
		{
			reader=new CSVReader(new InputStreamReader(stream, "ISO-8859-15"));
		}
		reader.setDelimiter(';');

		int lineCount=1;

		Set<Channel> channels=new HashSet<Channel>();
		/**
		 * ChannelList.readFromStream is called by both MirrorUpdater and
		 * TvBrowserDataService. The MirrorUpdater calls this method without
		 * DataService and doesn't need the IconLoader
		 */
		//IconLoader iconLoader=new IconLoader(channelGroup.getId(), dataDir);

		List<String> tokens;
		while ((tokens=reader.read())!=null)
		{
			if (tokens.size()<4)
			{
				throw new FileFormatException("Syntax error in mirror file line "+lineCount+": column count is '"+tokens.size()+" < 4' : "+tokens.get(0));
			}

			String country=null, timezone=null, channelId=null, name=null, copyright=null, webpage=null, iconUrl=null, categoryStr=null;
			try
			{
				country=tokens.get(0);
				timezone=tokens.get(1);
				channelId=tokens.get(2);
				name=tokens.get(3);
				copyright=tokens.get(4);
				webpage=tokens.get(5);
				iconUrl=tokens.get(6);
				categoryStr=tokens.get(7);

				if (tokens.size()>8)
				{
					name=StringEscapeUtils.unescapeHtml(tokens.get(8));
				}

			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				// ignore
			}

			int categories=Channel.CATEGORY_NONE;
			if (categoryStr!=null)
			{
				try
				{
					categories=Integer.parseInt(categoryStr);
				}
				catch (NumberFormatException e)
				{
					categories=Channel.CATEGORY_NONE;
				}
			}
			Channel channel=new Channel(name, channelId, TimeZone.getTimeZone(timezone), country, copyright, webpage, channelGroup, categories);
			channel.setIconUrl(iconUrl);
//			if (iconLoader!=null && iconUrl!=null && iconUrl.length()>0)
//			{
//				Icon icon=iconLoader.getIcon(channelId, iconUrl);
//				if (icon!=null)
//				{
//					channel.setDefaultIcon(icon);
//				}
//			}
			channels.add(channel);
			lineCount++;
		}

		reader.close();
//		if (iconLoader!=null)
//		{
//			iconLoader.close();
//			iconLoader=null;
//		}
		return channels;
	}


	private static final int MAX_META_DATA_AGE=2;

	private boolean needsUpdate(File file)
	{
		if (!file.exists())
		{
			return true;
		}
		else
		{
			long minLastModified=System.currentTimeMillis()-(MAX_META_DATA_AGE*24L*60L*60L*1000L);
			return file.lastModified()<minLastModified;
		}
	}

	private void updateMetaFile(String serverUrl, String metaFileName) throws TvDataException
	{
		File file=new File(dataDir, metaFileName);

		// Download the new file if needed
		if (needsUpdate(file))
		{
			String url=serverUrl+"/"+metaFileName;
			LOGGER.fine("Updating Metafile "+url);
			try
			{
				IOUtilities.download(new URL(url), file);
			}
			catch (IOException exc)
			{
				throw new TvDataException("Downloading file from '"+url+"' to '"+file.getAbsolutePath()+"' failed", exc);
			}
		}
	}

}
