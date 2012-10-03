package com.kiwisoft.media.tools;

import com.kiwisoft.media.files.MediaFileUtils;
import com.kiwisoft.media.files.PhotoFileInfo;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.Utils;
import org.apache.commons.io.FilenameUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stefan Stiller
 * @since 22.04.12
 */
public class Photos
{
	public static final String NS_GPX="http://www.topografix.com/GPX/1/1";
	public static final String EXIF_TOOL="c:\\PortableApps\\GeoSetter\\tools\\exiftool.exe";

	private Photos()
	{
		// exiftool -c "%.8f" -GPSLatitude=54.08618617
	}

	public static void main(String[] args) throws Exception
	{
		copyExif(new File("c:\\Users\\Stefan\\Pictures\\Photos\\2012-06-Daenemark\\2012-06-16\\Stefan"),
				 new File("c:\\Users\\Stefan\\Pictures\\Photos\\2012-06-Daenemark\\2012-06-16\\Stefan3d"));
	}

	private static void copyExif(File dir2d, File dir3d) throws IOException, InterruptedException
	{
		File[] files=dir2d.listFiles(new PhotoFileFilter());
		for (File file2d : files)
		{
			if (!file2d.isDirectory())
			{
				PhotoFileInfo fileInfo=MediaFileUtils.getPhotoFileInfo(file2d);
				if (fileInfo!=null)
				{
					File file3d=new File(dir3d, FilenameUtils.getBaseName(file2d.getName())+".mpo");
					Utils.run("cmd /c \""+EXIF_TOOL+"\" -c \"%8.f\" ");
					System.out.println(file2d.getName()+" "+fileInfo);
				}


			}
		}
	}

	private static void renameFiles(File dir)
	{
		File[] files=dir.listFiles(new PhotoFileFilter());
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		int fileCount=0;
		int renamedCount=0;
		for (File file : files)
		{
			if (file.isDirectory()) renameFiles(file);
			else
			{
				fileCount++;
				File metaFile=getMetaFile(file);
				if (metaFile.exists())
				{
					Properties metaData=loadMetaData(file);
					if (metaData.containsKey("time"))
					{
						Date date=new Date(Long.parseLong(metaData.getProperty("time")));
						String extension=FilenameUtils.getExtension(file.getName());
						String newBaseName=dateFormat.format(date);
						String baseName=FilenameUtils.getBaseName(file.getName());
						if (!newBaseName.equals(baseName))
						{
							File dest=new File(dir, newBaseName+"."+extension.toLowerCase());
							int index=0;
							while (dest.exists())
							{
								newBaseName=dateFormat.format(date)+"_"+new DecimalFormat("00").format(index);
								dest=new File(dir, newBaseName+"."+extension.toLowerCase());
							}
							boolean renamed=file.renameTo(dest);
							if (renamed)
							{
								renamedCount++;
								metaFile.renameTo(new File(dir, newBaseName+".meta"));
							}
							else System.err.println("Rename of "+file.getAbsolutePath()+" to "+dest.getAbsolutePath()+" failed");
						}
					}
				}
				else
				{
					Properties metaData=new Properties();
				}
			}
		}
		if (fileCount>0) System.out.println(renamedCount+" of "+fileCount+" files renamed");
	}

	private static void addGPSDataToPhotos(File dir, List<TrackPoint> trackPoints)
	{
		File[] files=dir.listFiles(new PhotoFileFilter());
		int photos=0;
		int positions=0;
		for (File file : files)
		{
			if (file.isDirectory()) addGPSDataToPhotos(file, trackPoints);
			else
			{
				photos++;
				Properties properties=loadMetaData(file);
				if (properties.containsKey("time"))
				{
					long photoTime=Long.parseLong(properties.getProperty("time"));
					TrackPoint trackPoint=getNearestTrackPoint(trackPoints, photoTime);
					if (trackPoint!=null)
					{
						properties.setProperty("longitude", String.valueOf(trackPoint.longitude));
						properties.setProperty("latitude", String.valueOf(trackPoint.latitude));
						properties.setProperty("elevation", String.valueOf(trackPoint.elevation));
						saveMetaData(file, properties);
						positions++;
					}
				}
			}
		}
		if (photos>0) System.out.println(dir.getAbsolutePath()+"... "+photos+" photos found... "+positions+" geo positions added");
	}

	private static TrackPoint getNearestTrackPoint(List<TrackPoint> trackPoints, long time)
	{
		TrackPoint nearestPoint=null;
		long minDiff=DateUtils.MINUTE*5;
		for (TrackPoint trackPoint : trackPoints)
		{
			long diff=Math.abs(trackPoint.date.getTime()-time);
			if (diff<minDiff)
			{
				nearestPoint=trackPoint;
				minDiff=diff;
			}
		}
		return nearestPoint;
	}

	private static List<TrackPoint> readTrackData(File dir) throws Exception
	{
		List<TrackPoint> trackPoints=new ArrayList<TrackPoint>();
		File[] files=dir.listFiles(new TrackFileFilter());
		for (File file : files)
		{
			if (file.isDirectory()) readTrackData(dir);
			else
			{
				System.out.println("Read track from "+file.getAbsolutePath());
				SAXParserFactory factory=SAXParserFactory.newInstance();
				factory.setNamespaceAware(true);

				SAXParser parser=factory.newSAXParser();
				parser.parse(file, new GpxHandler(trackPoints));
			}
		}
		System.out.println(trackPoints.size()+" track points found");
		return trackPoints;
	}

	private static void updateFileDates() throws ParseException
	{
		File dir=new File("c:\\Users\\Stefan\\Pictures\\Photos\\Schottland 2011\\Arend\\3");
		long offset=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("08.09.2011 16:37:20").getTime()-getPhotoDate(new File(dir, "FILE1335.JPG")).getTime();
		//long offset=DateUtils.DAY;
		System.out.println("offset = "+offset);
		updateFileDates(dir, offset);
	}

	private static void updateFileDates(File dir, long offset)
	{
		File[] files=dir.listFiles(new PhotoFileFilter());
		DateFormat dateFormat=DateFormat.getDateTimeInstance();
		for (File file : files)
		{
			if (file.isDirectory())
			{
				updateFileDates(file, offset);
			}
			else
			{
				Date date=getPhotoDate(file);
				if (date!=null)
				{
					long time=date.getTime()+offset;
					file.setLastModified(time);
					Properties metaData=loadMetaData(file);
					metaData.setProperty("time", String.valueOf(time));
					saveMetaData(file, metaData);
					System.out.println("Set file date of "+file.getAbsolutePath()+" to "+dateFormat.format(new Date(time)));
				}
			}
		}
	}

	private static Date getPhotoDate(File file)
	{
		Date date=MediaFileUtils.getPhotoFileInfo(file).getDate();
		if (date==null)
		{
			try
			{
				byte[] buffer=new byte[5000];
				FileInputStream inputStream=new FileInputStream(file);
				int size=inputStream.read(buffer);
				inputStream.close();
				String text=new String(buffer, 0, size);
				Matcher matcher=Pattern.compile("d(\\d{4}:\\d{2}:\\d{2} \\d{2}:\\d{2}:\\d{2})").matcher(text);
				if (matcher.find())
				{
					date=new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").parse(matcher.group(1));
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}
		return date;
	}

	private static class PhotoFileFilter implements FileFilter
	{
		@Override
		public boolean accept(File file)
		{
			if (file.isDirectory()) return true;
			String fileName=file.getName().toLowerCase();
			return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
		}
	}

	private static class TrackFileFilter implements FileFilter
	{
		@Override
		public boolean accept(File file)
		{
			if (file.isDirectory()) return true;
			String fileName=file.getName().toLowerCase();
			return fileName.endsWith(".gpx");
		}
	}

	private static Properties loadMetaData(File file)
	{
		File metaFile=getMetaFile(file);
		Properties properties=new Properties();
		if (metaFile.exists())
		{
			try
			{
				FileInputStream stream=new FileInputStream(metaFile);
				properties.load(stream);
				stream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return properties;
	}

	private static void saveMetaData(File file, Properties properties)
	{
		File metaFile=getMetaFile(file);
		try
		{
			FileOutputStream stream=new FileOutputStream(metaFile);
			properties.store(stream, null);
			stream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private static File getMetaFile(File file)
	{
		return new File(file.getParentFile(), FilenameUtils.getBaseName(file.getName())+".meta");
	}

	private static class GpxHandler extends DefaultHandler2
	{
		private TrackPoint trackPoint;
		private boolean inTime;
		private boolean inElevation;
		private List<TrackPoint> trackPoints;
		private SimpleDateFormat timeFormat;

		public GpxHandler(List<TrackPoint> trackPoints)
		{
			this.trackPoints=trackPoints;
			timeFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
//						<trkpt lat="52.621964" lon="13.303767"><ele>70.79</ele><time>2011-08-26T05:01:04Z</time></trkpt>
			if (NS_GPX.equals(uri))
			{
				if ("trkpt".equals(localName))
				{
					trackPoint=new TrackPoint();
					trackPoint.latitude=Double.valueOf(attributes.getValue("lat"));
					trackPoint.longitude=Double.valueOf(attributes.getValue("lon"));
				}
				else if ("time".equals(localName)) inTime=true;
				else if ("ele".equals(localName)) inElevation=true;
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			if (NS_GPX.equals(uri))
			{
				if ("trkpt".equals(localName))
				{
					if (trackPoint!=null)
					{
						trackPoints.add(trackPoint);
						trackPoint=null;
					}
				}
				else if ("time".equals(localName)) inTime=false;
				else if ("ele".equals(localName)) inElevation=false;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			if (trackPoint!=null)
			{
				if (inTime)
				{
					try
					{
						trackPoint.date=timeFormat.parse(new String(ch, start, length));
					}
					catch (ParseException e)
					{
						e.printStackTrace();
					}
				}
				else if (inElevation)
				{
					trackPoint.elevation=Double.valueOf(new String(ch, start, length));
				}
			}
		}
	}

	public static class TrackPoint
	{
		private Date date;
		private Double latitude;
		private Double longitude;
		private Double elevation;

		@Override
		public String toString()
		{
			final StringBuilder sb=new StringBuilder();
			sb.append("TrackPoint");
			sb.append("{date=").append(date);
			sb.append(", latitude=").append(latitude);
			sb.append(", longitude=").append(longitude);
			sb.append(", elevation=").append(elevation);
			sb.append('}');
			return sb.toString();
		}
	}
}
