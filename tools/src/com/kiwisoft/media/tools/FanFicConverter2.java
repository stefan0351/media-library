package com.kiwisoft.media.tools;

import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.fanfic.FanFic;
import com.kiwisoft.media.fanfic.FanFicPart;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.filestore.FileStore;
import com.kiwisoft.utils.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.htmlparser.util.ParserException;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.nio.charset.Charset;

/**
 * @author Stefan Stiller
 * @since 24.11.2010
 */
public class FanFicConverter2
{
	public static boolean devMode=false;

	public FanFicConverter2()
	{
	}

	public static void main(String[] args) throws IOException, ParserException
	{
		Locale.setDefault(Locale.UK);
		SimpleConfiguration configuration=new SimpleConfiguration();
		File configFile=new File("conf", "config.xml");
		configuration.loadDefaultsFromFile(configFile);
		if (devMode) configuration.loadUserValues("media"+File.separator+"dev-profile.xml");
		else configuration.loadUserValues("media"+File.separator+"profile.xml");

		new FanFicConverter2().convert();
	}

	private void convert() throws IOException, ParserException
	{
		List<Object> fanficIds=DBLoader.getInstance().loadKeys(FanFic.class, null, null);
		for (Object fanficId : fanficIds)
		{
			convertFanfic(DBLoader.getInstance().load(FanFic.class, fanficId));
		}
	}

	private Pattern imagePattern=Pattern.compile("<img[^>]+src=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE+Pattern.MULTILINE);
	private Pattern linkPattern=Pattern.compile("<a[^>]+href=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE+Pattern.MULTILINE);

	private void convertFanfic(final FanFic fanfic) throws IOException, ParserException
	{
		final List<File> additionalFiles=new ArrayList<File>();
		for (final FanFicPart part : fanfic.getParts())
		{
			final File file=new File(MediaConfiguration.getFanFicPath(), part.getOldSource());
			if (file.exists())
			{
				additionalFiles.clear();
				final String extension=FilenameUtils.getExtension(file.getName());
				final String type=getType(extension);
				InputStream inputStream;
				if ("html".equals(type))
				{
					String content=FileUtils.loadFile(file);
					content=replaceReferences(part, content, imagePattern, file.getParentFile(), additionalFiles);
					content=replaceReferences(part, content, linkPattern, file.getParentFile(), additionalFiles);
					inputStream=new ByteArrayInputStream(content.getBytes());
				}
				else inputStream=new FileInputStream(file);

				final InputStream inputStream1=inputStream;
				DBSession.execute(new Transactional()
				{
					@Override
					public void run() throws Exception
					{
						part.setType(type);
						part.putContent(inputStream1, extension, System.getProperty("file.encoding"));
						for (File additionalFile : additionalFiles)
						{
							FileStore.getInstance().putFile(part, additionalFile.getName(), new FileInputStream(additionalFile));
						}
					}

					@Override
					public void handleError(Throwable throwable, boolean rollback)
					{
						throw new RuntimeException(throwable);
					}
				});

			}
			else throw new RuntimeException("File "+file.getAbsolutePath()+" doesn't exist.");
		}
	}

	private String replaceReferences(FanFicPart part, String content, Pattern pattern, File directory, List<File> additionalFiles)
	{
		Matcher matcher=pattern.matcher(content);
		int findIndex=0;
		while (matcher.find(findIndex))
		{
			findIndex=matcher.end();
			String reference=matcher.group(1);
			File additionalFile;
			if (reference.startsWith("#")) continue;
			if (reference.startsWith("res/")) additionalFile=FileUtils.getFile(MediaConfiguration.getRootPath(), reference.substring(4));
			else additionalFile=new File(directory, reference);
			if (additionalFile.exists())
			{
				additionalFiles.add(additionalFile);
				String replacement="/media/files/"+part.getClass().getName()+"/"+part.getId()+"/"+additionalFile.getName();
				content=content.substring(0, matcher.start(1))+replacement+content.substring(matcher.end(1));
				findIndex=matcher.start(1)+replacement.length();
				matcher=pattern.matcher(content);
			}
			else System.err.println("Referenced file "+reference+" not found at "+additionalFile.getAbsolutePath());
		}
		return content;
	}

	private String getType(String extension)
	{
		if ("html".equals(extension)) return "html";
		if ("jpg".equals(extension)) return "image";
		if ("gif".equals(extension)) return "image";
		throw new IllegalArgumentException(extension);
	}
}