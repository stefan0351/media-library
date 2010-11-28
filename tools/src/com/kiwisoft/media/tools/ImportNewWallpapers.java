package com.kiwisoft.media.tools;

import java.awt.Dimension;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.collection.CountingMap;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.files.ContentType;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.files.MediaFileManager;
import com.kiwisoft.media.files.MediaFileUtils;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.PersonManager;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringNumberComparator;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;

/**
 * @author Stefan Stiller
 */
public class ImportNewWallpapers
{
	private ImportNewWallpapers()
	{
	}

	public static void main(String[] args)
	{
		Map<String, Person> personMap=new HashMap<String, Person>();
		CountingMap<Person> wallpaperMap=new CountingMap<Person>();

		Locale.setDefault(Locale.UK);
		SimpleConfiguration configuration=new SimpleConfiguration();
		File configFile=new File("conf", "config.xml");
		configuration.loadDefaultsFromFile(configFile);

		File folder=new File("D:\\Webpages\\local\\wallpapers");
		Pattern pattern=Pattern.compile("(.*)-\\d+x\\d+-\\d+\\.(jpg|png|gif|jpeg)");
		File[] files=folder.listFiles();
		Arrays.sort(files, new Comparator<File>()
		{
			private StringNumberComparator comparator=new StringNumberComparator();

			@Override
			public int compare(File o1, File o2)
			{
				return comparator.compare(o1.getName(), o2.getName());
			}
		});
		for (File file : files)
		{
			Matcher matcher=pattern.matcher(file.getName());
			if (!matcher.matches()) continue;
			Dimension size=MediaFileUtils.getImageSize(file);
			if (size==null) continue;
			String path=FileUtils.getRelativePath(MediaConfiguration.getRootPath(), file.getAbsolutePath());
			Set<MediaFile> mediaFile=MediaFileManager.getInstance().getMediaFileByFile(MediaConfiguration.PATH_ROOT, path);
			if (mediaFile==null || mediaFile.isEmpty())
			{
				String name=matcher.group(1);
				name=name.replace("-", " ");
				Person person;
				if (personMap.containsKey(name)) person=personMap.get(name);
				else
				{
					person=PersonManager.getInstance().getPersonByName(name);
					personMap.put(name, person);
				}
				System.out.println(name+" -> "+person+": "+path);
				if (person!=null)
				{
					int wallpaperCount;
					if (!wallpaperMap.containsKey(person))
					{
						wallpaperCount=MediaFileManager.getInstance().getNumberOfMediaFiles(person, ContentType.WALLPAPER);
						wallpaperMap.set(person, wallpaperCount);
					}
					wallpaperCount=wallpaperMap.increase(person);
					createWallpaper(size, person, path, wallpaperCount);
				}
			}
		}
	}

	private static void createWallpaper(final Dimension size, final Person person, final String path, final int wallpaperCount)
	{
		final String thumbnailPath=MediaFileUtils.createThumbnail(MediaConfiguration.PATH_ROOT, path, MediaFileUtils.THUMBNAIL_WIDTH, MediaFileUtils.THUMBNAIL_HEIGHT, "thb");
		final Dimension thumbnailSize=MediaFileUtils.getImageSize(FileUtils.getFile(MediaConfiguration.getRootPath(), thumbnailPath));
		DBSession.execute(new Transactional()
		{
			@Override
			public void run() throws Exception
			{
				MediaFile wallpaper=MediaFileManager.getInstance().createImage(MediaConfiguration.PATH_ROOT);
				wallpaper.setName(person.getName()+" - Wallpaper "+wallpaperCount);
				wallpaper.setContentType(ContentType.WALLPAPER);
				wallpaper.setWidth(size.width);
				wallpaper.setHeight(size.height);
				wallpaper.setFile(path);
				wallpaper.addPerson(person);
				wallpaper.setThumbnail(MediaConfiguration.PATH_ROOT, thumbnailPath, thumbnailSize.width, thumbnailSize.height);
			}

			@Override
			public void handleError(Throwable throwable, boolean rollback)
			{
				throwable.printStackTrace();
				System.exit(0);
			}
		});
	}
}
