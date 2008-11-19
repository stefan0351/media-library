package com.kiwisoft.media.files;

import java.awt.*;
import java.net.URL;
import java.io.File;
import java.io.StringReader;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Properties;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FileUtils;

import com.kiwisoft.utils.Utils;
import com.kiwisoft.utils.StringUtils;

public class MediaFileUtils
{
	private static Component mediaTracker=new JLabel();

	private MediaFileUtils()
	{
	}

	public static ImageIcon loadIcon(String fileName)
	{
		Image image=loadImage(fileName);
		if (image!=null)
			return new ImageIcon(image);
		else
			return null;
	}

	public static ImageIcon loadIcon(URL url)
	{
		Image image=loadImage(url);
		if (image!=null)
			return new ImageIcon(image);
		else
			return null;
	}

	public static Image loadImage(String fileName)
	{
		if (fileName.startsWith("/")) fileName=fileName.substring(1, fileName.length());
		MediaTracker tracker=new MediaTracker(mediaTracker);
		Image image=Toolkit.getDefaultToolkit().getImage(fileName);
		tracker.addImage(image, 1);
		try
		{
			tracker.waitForID(1);
		}
		catch (InterruptedException e)
		{
			System.out.println("Interrupted while loading image: "+fileName);
			return null;
		}
		if (tracker.isErrorID(1))
		{
			return null;
		}
		return image;
	}

	public static Image loadImage(URL url)
	{
		MediaTracker tracker=new MediaTracker(mediaTracker);
		Image image=Toolkit.getDefaultToolkit().createImage(url);
		tracker.addImage(image, 1);
		try
		{
			tracker.waitForID(1);
		}
		catch (InterruptedException e)
		{
			System.out.println("Interrupted while loading image: "+url);
			return null;
		}
		if (tracker.isErrorID(1))
		{
			return null;
		}
		return image;
	}

	public static Dimension getImageSize(File file)
	{
		try
		{
			String result=Utils.run("cmd /c identify -format \"%wx%h|\" \""+file.getAbsolutePath()+"\"", new StringBuilder(), null);
			if (result!=null)
			{
				result=result.split("\\|", 2)[0];
				Matcher matcher=Pattern.compile("(\\d+)x(\\d+)").matcher(result.trim());
				if (matcher.matches())
				{
					return new Dimension(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
				}
			}
			return null;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static PhotoFileInfo getPhotoFileInfo(File file)
	{
		try
		{
			String result=Utils.run("cmd /c identify -units PixelsPerInch -format "+
									"width=%w\\n"+
									"height=%h\\n"+
									"type=%m\\n"+
									"xresolution=%x\\n"+
									"yresolution=%y\\n"+
									"colordepth=%z\\n"+
									"make=%[exif:make]\\n"+
									"model=%[exif:model]\\n"+
									"date=%[exif:datetime]\\n"+
									"fnumber=%[exif:fnumber]\\n"+
									"focallength=%[exif:focallength]\\n"+
									"exposuretime=%[exif:exposuretime]\\n"+
									"iso=%[exif:isospeedratings]"+
									" \""+file.getAbsolutePath()+"\"", new StringBuilder(), null);
			if (result!=null)
			{
				String[] lines=result.split(System.getProperty("line.separator"));
				PhotoFileInfo imageDescriptor=new PhotoFileInfo(file);
				for (String line : lines)
				{
					String[] keyValue=line.split("=", 2);
					String key=keyValue[0];
					String value=keyValue[1].trim();
					if (!StringUtils.isEmpty(value))
					{
						if ("width".equals(key)) imageDescriptor.setWidth(Integer.parseInt(value));
						else if ("height".equals(key)) imageDescriptor.setHeight(Integer.parseInt(value));
						else if ("type".equals(key)) imageDescriptor.setType(value);
						else if ("colordepth".equals(key)) imageDescriptor.setColorDepth(Integer.parseInt(value));
						else if ("make".equals(key)) imageDescriptor.setCameraMake(value);
						else if ("model".equals(key)) imageDescriptor.setCameraModel(value);
						else if ("date".equals(key))
						{
							SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
							imageDescriptor.setDate(dateFormat.parse(value));
						}
						else if ("exposuretime".equals(key)) imageDescriptor.setExposureTime(convertFractionToNumber(value));
						else if ("fnumber".equals(key)) imageDescriptor.setFNumber(convertFractionToNumber(value));
						else if ("focallength".equals(key)) imageDescriptor.setFocalLength(convertFractionToNumber(value));
						else if ("iso".equals(key)) imageDescriptor.setIsoSpeed(Integer.parseInt(value));
						else if ("xresolution".equals(key)) imageDescriptor.setXResolution(convertDPI(value));
						else if ("yresolution".equals(key)) imageDescriptor.setYResolution(convertDPI(value));
					}
				}
				return imageDescriptor;
			}
			return null;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private static Double convertFractionToNumber(String value)
	{
		Matcher matcher=Pattern.compile("(\\d+)/(\\d+)").matcher(value);
		if (matcher.matches()) return (double)Integer.parseInt(matcher.group(1))/Integer.parseInt(matcher.group(2));
		try
		{
			return new Double(value);
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static Integer convertDPI(String value)
	{
		Matcher matcher=Pattern.compile("(\\d+) PixelsPerInch").matcher(value);
		if (matcher.matches()) return new Integer(matcher.group(1));
		try
		{
			return new Integer(value);
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			return null;
		}
	}


	public static void resize(File source, int width, int height, File target)
	{
		StringBuilder command=new StringBuilder("cmd /c convert");
		command.append(" \"").append(source.getAbsolutePath()).append("\"");
		command.append(" -resize ");
		if (width>0) command.append(width);
		if (height>0) command.append("x").append(height);
		command.append(" \"").append(target.getAbsolutePath()).append("\"");
		try
		{
			Utils.run(command.toString());
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void rotateAndResize(File source, int angle, int width, int height, File target)
	{
		try
		{
			Utils.run("cmd /c convert \""+source.getAbsolutePath()+"\" -rotate "+angle+" -resize "+width+"x"+height+" \""+target.getAbsolutePath()+"\"");
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void rotate(File source, int angle, File target)
	{
		try
		{
			Utils.run("cmd /c convert \""+source.getAbsolutePath()+"\" -rotate "+angle+" \""+target.getAbsolutePath()+"\"");
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void convert(File source, File target)
	{
		StringBuilder command=new StringBuilder("cmd /c convert");
		command.append(" \"").append(source.getAbsolutePath()).append("\"");
		command.append(" \"").append(target.getAbsolutePath()).append("\"");
		try
		{
			Utils.run(command.toString());
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static MediaFileInfo getMediaFileInfo(File file)
	{
		try
		{
			String output=Utils.run("bin"+File.separator+"mediainfo --inform=file://bin/MediaInfo.format"
									+" \""+file.getAbsolutePath()+"\"", new StringBuilder(), null);
			Properties properties=new Properties();
			properties.load(new StringReader(output));
			MediaFileInfo fileInfo=new MediaFileInfo();
			fileInfo.setFormat(properties.getProperty("general.format.info"));
			if (StringUtils.isEmpty(fileInfo.getFormat())) fileInfo.setFormat(properties.getProperty("general.format"));
			fileInfo.setVideoFormat(properties.getProperty("general.videoFormat"));
			fileInfo.setAudioFormat(properties.getProperty("general.audioFormat"));
			fileInfo.setDuration(Utils.parseLong(properties.getProperty("general.duration")));
			fileInfo.setWidth(Utils.parseInteger(properties.getProperty("video.width")));
			fileInfo.setHeight(Utils.parseInteger(properties.getProperty("video.height")));
			Integer count=Utils.parseInteger(properties.getProperty("video.streams"));
			if (count!=null) fileInfo.setVideoStreamCount(count);
			count=Utils.parseInteger(properties.getProperty("audio.streams"));
			if (count!=null) fileInfo.setAudioStreamCount(count);
			count=Utils.parseInteger(properties.getProperty("image.streams"));
			if (count!=null) fileInfo.setImageStreamCount(count);
			return fileInfo;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static void createVideoThumbnail(File videoFile, int width, int height, File thumbnailFile)
	{
		if (videoFile!=null && videoFile.exists())
		{
			try
			{
				File directory=new File("tmp", "vlc");
				directory.mkdirs();
				long counter=new Random().nextLong()&0x7fffffff;
				String prefix;
				do
				{
					counter++;
					prefix="snap-"+Long.toString(counter, 36)+"-";
				}
				while (new File(directory, prefix+"00001.png").exists());
				System.out.println("MediaFileManager.createThumbnail: directory = "+directory.getAbsolutePath());
				System.out.println("MediaFileManager.createThumbnail: prefix = "+prefix);
				String command="C:\\Programme\\VideoLAN\\VLC\\vlc.exe"
							   +" --no-video-title-show"
							   +" --snapshot-format png --snapshot-sequential"+
							   " --snapshot-path \""+directory.getAbsolutePath()+"\" --snapshot-prefix "+prefix
							   +" \""+videoFile.getAbsolutePath()+"\"";
				Utils.run(command);
				File[] files=directory.listFiles(new PrefixFilter(prefix));
				if (files!=null && files.length>0)
				{
					File file=files[files.length-1];
					MediaFileUtils.resize(file, width, height, thumbnailFile);
				}
				FileUtils.cleanDirectory(directory);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private static class PrefixFilter implements FilenameFilter
	{
		private String prefix;

		public PrefixFilter(String prefix)
		{
			this.prefix=prefix;
		}

		public boolean accept(File dir, String name)
		{
			if (name.startsWith(prefix)) return true;
			return false;
		}
	}

	public final static String[] THUMBNAIL_SUFFIXES={"mini", "small", "sb"};

	public static Map<String, ImageFileInfo> getThumbnails(File imageFile)
	{
		Map<String, ImageFileInfo> map=new HashMap<String, ImageFileInfo>();
		String extension=com.kiwisoft.utils.FileUtils.getExtension(imageFile);
		String basePath=imageFile.getAbsolutePath();
		basePath=basePath.substring(0, basePath.length()-extension.length()-1);
		for (String suffix : THUMBNAIL_SUFFIXES)
		{
			File file=new File(basePath+"_"+suffix+".jpg");
			if (file.exists())
			{
				Dimension size=MediaFileUtils.getImageSize(file);
				if (size!=null)
				{
					if (size.width==50 && size.height==50)
					{
						map.put(MediaFile.THUMBNAIL_50x50, new ImageFileInfo(file, size));
					}
					else if (size.width<=170)
					{
						ImageFileInfo currentData=map.get(MediaFile.THUMBNAIL_SIDEBAR);
						if (currentData==null || currentData.getSize().width<size.width)
						{
							map.put(MediaFile.THUMBNAIL_SIDEBAR, new ImageFileInfo(file, size));
						}
					}
				}
			}
		}
		return map;
	}

	public static FileFilter getVideoFileFilter()
	{
		return new FileNameExtensionFilter("Video Files", "avi", "mp4", "mpg", "mpeg", "mov", "flv", "mkv", "wmv");
	}

	public static FileFilter getAudioFileFilter() 
	{
		return new FileNameExtensionFilter("Video Files", "mp3", "wma", "wav");
	}
}
