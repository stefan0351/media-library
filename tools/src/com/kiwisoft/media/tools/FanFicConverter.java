package com.kiwisoft.media.tools;

import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.html.HtmlUtils;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.fanfic.FanFic;
import com.kiwisoft.media.fanfic.FanFicPart;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.xp.XPBean;
import com.kiwisoft.xp.XPLoader;
import org.apache.commons.io.FilenameUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Stefan Stiller
 * @since 24.11.2010
 */
public class FanFicConverter
{
	public static boolean testMode=true;
	public static boolean devMode=true;

	public FanFicConverter()
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

		new FanFicConverter().convert();
	}

	private void convert() throws IOException, ParserException
	{
		List<Object> fanficIds=DBLoader.getInstance().loadKeys(FanFic.class, null, null);
		for (Object fanficId : fanficIds)
		{
			convertFanfic(DBLoader.getInstance().load(FanFic.class, fanficId));
		}
	}

	private void convertFanfic(final FanFic fanfic) throws IOException, ParserException
	{
		final List<Part> parts=new ArrayList<Part>();
		boolean converted=false;
		List<File> convertedFiles=new ArrayList<File>();
		for (FanFicPart part : fanfic.getParts())
		{
			File file=new File(MediaConfiguration.getFanFicPath(), part.getSource());
			File baseFile=new File(MediaConfiguration.getFanFicPath()+"/"+getBaseFile(part.getSource()));
			if (!file.exists())
			{
				throw new RuntimeException("File '"+file.getAbsolutePath()+"' not found.");
			}
			String extension=FilenameUtils.getExtension(part.getSource());
			if ("jpg".equals(extension))
			{
				parts.add(new Part(parts.size()+1, part.getName(), baseFile, file));
			}
			else if ("html".equals(extension))
			{
				String fileContent=FileUtils.loadFile(file);
				if (fileContent.contains("<html>"))
				{
					parts.addAll(convertHtml(fanfic.getTitle(), baseFile, parts.size()+1, file));
					convertedFiles.add(file);
					converted=true;
				}
				else parts.add(new Part(parts.size()+1, part.getName(), baseFile, file));
			}
			else if ("xp".equals(extension))
			{
				parts.addAll(convertXp(fanfic.getTitle(), baseFile, parts.size()+1, file));
				convertedFiles.add(file);
				converted=true;
			}
			else
			{
				System.err.println("Unknown extension: "+extension);
				parts.add(new Part(parts.size()+1, part.getName(), baseFile, file));
			}
		}
		if (converted)
		{
			System.out.println(fanfic.getTitle()+" ["+fanfic.getId()+"]");
			for (File file : convertedFiles) backupFile(file);
			adjustSinglePartName(parts, convertedFiles);
			saveConvertedParts(fanfic, parts);
		}
	}

	private void adjustSinglePartName(List<Part> parts, List<File> convertedFiles)
	{
		if (parts.size()==1)
		{
			Part part=parts.get(0);
			File singleFile=new File(part.baseFile.getAbsolutePath()+".html");
			System.out.println("Rename "+part.file.getAbsolutePath()+" to "+singleFile.getAbsolutePath());
			if (singleFile.exists())
			{
				if (testMode && convertedFiles.contains(singleFile)) part.file=singleFile;
				else System.err.println("File "+singleFile.getAbsolutePath()+" already exists.");
			}
			else
			{
				if (!testMode) part.file.renameTo(singleFile);
				part.file=singleFile;

			}
		}
	}

	private void saveConvertedParts(final FanFic fanfic, final List<Part> parts)
	{
		DBSession.execute(new Transactional()
		{
			@Override
			public void run() throws Exception
			{
				Iterator<FanFicPart> oldParts=new ArrayList<FanFicPart>(fanfic.getParts().elements()).iterator();
				for (Part part : parts)
				{
					String source=FileUtils.getRelativePath(MediaConfiguration.getFanFicPath(), part.file.getAbsolutePath());
					source=StringUtils.replaceStrings(source, "\\", "/");
					System.out.println(part+": "+source);

					FanFicPart newPart=oldParts.hasNext() ? oldParts.next() : null;
					if (!testMode)
					{
						if (newPart==null) newPart=fanfic.createPart();
						newPart.setName(part.name);
						newPart.setSource(source);
					}
				}
				if (oldParts.hasNext()) throw new RuntimeException("Number of new parts smaller than number of old parts");
			}

			@Override
			public void handleError(Throwable throwable, boolean rollback)
			{
				throw new RuntimeException(throwable);
			}
		});
	}

	private void backupFile(File file) throws IOException
	{
		String path=file.getCanonicalPath().replace("\\fanfic\\authors\\", "\\fanfic\\backup\\");
		File backupFile=new File(path);
		backupFile.getParentFile().mkdirs();
		System.out.println("Move file from "+file.getAbsolutePath()+" to "+backupFile.getAbsolutePath());
		if (!testMode)
		{
			file.renameTo(backupFile);
			deleteEmptyDirectory(file.getParentFile());
		}
	}

	private void deleteEmptyDirectory(File file)
	{
		if (file.isDirectory())
		{
			if (file.listFiles().length==0)
			{
				System.out.println("Delete empty directory "+file.getAbsolutePath());
				file.delete();
			}
		}
	}

	private String getBaseFile(String source)
	{
		String[] parts=source.split("/", 3);
		if (parts.length==2) return parts[0]+"/"+FilenameUtils.getBaseName(parts[1]);
		else return parts[0]+"/"+parts[1];
	}

	private List<Part> convertHtml(String title, File baseFile, int partNumber, File file) throws IOException, ParserException
	{
		List<Part> parts=new ArrayList<Part>();
		String html=FileUtils.loadFile(file);

		Parser parser=new Parser();
		parser.setInputHTML(html);

		Node bodyNode=HtmlUtils.findFirst(parser, "body");
		NodeList h2Nodes=HtmlUtils.findAll((CompositeTag) bodyNode, "td.h2");
		Node startNode=null;
		String partName=null;
		for (NodeIterator it=h2Nodes.elements(); it.hasMoreNodes();)
		{
			TableColumn h2Node=(TableColumn) it.nextNode();
			String heading=h2Node.toPlainTextString();
			heading=HtmlUtils.trimUnescape(heading);
			if ("Credits".equals(heading)) continue;
			Node rowNode=h2Node.getParent();
			Node tableNode=rowNode.getParent();
			if (startNode!=null)
			{
				String partHtml=html.substring(startNode.getNextSibling().getStartPosition(), tableNode.getStartPosition());
				partHtml=trimPart(partHtml, title+" \\ "+partName);
				File partFile=newFile(baseFile, partNumber, "html");
				if (!testMode) FileUtils.saveToFile(partHtml, partFile);
				parts.add(new Part(partNumber, partName, baseFile, partFile));
				partNumber++;
			}
			startNode=tableNode;
			partName=heading;
		}
		if (startNode!=null)
		{
			String partHtml=html.substring(startNode.getNextSibling().getStartPosition(), bodyNode.getLastChild().getEndPosition());
			partHtml=trimPart(partHtml, title+" \\ "+partName);
			File partFile=newFile(baseFile, partNumber, "html");
			if (!testMode) FileUtils.saveToFile(partHtml, partFile);
			parts.add(new Part(partNumber, partName, baseFile, partFile));
		}
		else throw new RuntimeException("Heading not found.");
		return parts;
	}

	private List<Part> convertXp(String title, File baseFile, int partNumber, File file) throws IOException
	{
		List<Part> parts=new ArrayList<Part>();

		XPBean xp=XPLoader.loadXMLFile(null, file);
		for (XPBean chapter : (Collection<XPBean>) xp.getValues("chapter"))
		{
			String partName=(String) chapter.getValue("title");
			String partHtml="<!-- "+title+" \\ "+partName+" -->\n\n"+chapter.toString().trim();
			File partFile=newFile(baseFile, partNumber, "html");
			if (!testMode) FileUtils.saveToFile(partHtml, partFile);
			parts.add(new Part(partNumber, partName, baseFile, partFile));
			partNumber++;
		}

		return parts;
	}

	private File newFile(File baseFile, int partNumber, String extension)
	{
		while (true)
		{
			File partFile=new File(baseFile.getParentFile(), baseFile.getName()+"_"+partNumber+"."+extension);
			if (partFile.exists())
			{
				System.err.println("Part already exists. "+partFile.getAbsolutePath());
				partNumber++;
			}
			else return partFile;
		}
	}

	private String trimPart(String html, String comment)
	{
		int pos=html.lastIndexOf("<p align=center><b>[</b> ");
		if (pos>0)
		{
			if (pos<html.length()-300) throw new RuntimeException("Error trimming part: "+(html.length()-pos)+"\n"+html.substring(pos));
			html=html.substring(0, pos);
		}
		pos=html.lastIndexOf("<p align=right><a class=link href=\"#top\">Top</a></p>");
		if (pos>0)
		{
			if (pos<html.length()-100) throw new RuntimeException("Error trimming part: "+(html.length()-pos)+"\n"+html.substring(pos));
			html=html.substring(0, pos);
		}
		return "<!-- "+comment+" -->\n\n"+html.trim();
	}

	private static class Part
	{
		private int number;
		private String name;
		private File baseFile;
		private File file;

		private Part(int number, String name, File baseFile, File file)
		{
			this.number=number;
			this.name=name;
			this.baseFile=baseFile;
			this.file=file;
		}

		@Override
		public String toString()
		{
			return number+": "+name+" ("+file.getAbsolutePath()+")";
		}
	}
}
