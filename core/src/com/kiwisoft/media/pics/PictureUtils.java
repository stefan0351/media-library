package com.kiwisoft.media.pics;

import java.awt.*;
import java.net.URL;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import com.kiwisoft.utils.Utils;
import com.kiwisoft.utils.StringUtils;

public class PictureUtils
{
	private static Component mediaTracker=new JLabel();

	private PictureUtils()
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
		Image image=Toolkit.getDefaultToolkit().getImage(url);
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

	public static PictureDetails getImageFormat(File file)
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
				PictureDetails imageDescriptor=new PictureDetails(file);
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
			Utils.run(command.toString(), null, null);
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
			Utils.run("cmd /c convert \""+source.getAbsolutePath()+"\" -rotate "+angle+" -resize "+width+"x"+height+" \""+target.getAbsolutePath()+"\"", null,
					  null);
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
			Utils.run("cmd /c convert \""+source.getAbsolutePath()+"\" -rotate "+angle+" \""+target.getAbsolutePath()+"\"", null,
					  null);
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
			Utils.run(command.toString(), null, null);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
