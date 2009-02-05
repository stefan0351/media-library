package com.kiwisoft.utils;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.awt.Dimension;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.commons.io.*;
import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.media.files.MediaFileUtils;

/**
 * @author Stefan Stiller
 */
public class ODFTemplate
{
	private URL resource;
	private Map<String, Object> variables=new HashMap<String, Object>();
	private Map<String, File> pictures=new HashMap<String, File>();
	public double maxWidth=12.7;
	public double maxHeight=18.0;

	public static void main(String[] args) throws Exception
	{
		ODFTemplate template=new ODFTemplate(ClassLoader.getSystemResource("com/kiwisoft/media/covers/dvd_movie.odtt"));
		template.setVariable("title", "The Last Mimzy");
		template.setPicture("poster", new File("D:\\Webpages\\local\\movies\\posters\\last_mimzy.jpg"));
		template.createDocument(new File("d:\\temp\\test.odt"));
	}

	public ODFTemplate(URL resource)
	{
		this.resource=resource;
	}

	public void setPicture(String name, File file)
	{
		pictures.put(name, file);
		Map<String, Object> pictureVariables=new HashMap<String, Object>();
		String fileName=file.getName();
		pictureVariables.put("filename", fileName);
		String mimeType=FileUtils.getMimeType(file);
		if (!StringUtils.isEmpty(mimeType)) pictureVariables.put("mimetype", mimeType);
		Dimension size=MediaFileUtils.getImageSize(file);
		double scale=Math.min(maxWidth/size.width, maxHeight/size.height);
		pictureVariables.put("width", size.width*scale);
		pictureVariables.put("height", size.height*scale);
		variables.put(name, pictureVariables);
	}

	public void setVariable(String name, Object value)
	{
		if (value!=null) value=StringEscapeUtils.escapeXml(value.toString());
		variables.put(name, value);
	}

	public void createDocument(File file) throws Exception
	{
//		System.out.println("variables = "+variables);
		VelocityEngine engine=new VelocityEngine();
		engine.init();
		VelocityContext context=new VelocityContext(variables);

		ZipEntry templateEntry;
		ZipInputStream zipTemplate=new ZipInputStream(resource.openStream());
		ZipOutputStream zipOutput=new ZipOutputStream(new FileOutputStream(file));
		while ((templateEntry=zipTemplate.getNextEntry())!=null)
		{
			if ("mimetype".equals(templateEntry.getName()))
			{
//				System.out.println(templateEntry.getName());
				int size=(int)templateEntry.getSize();
				byte[] mimetype=new byte[size];
				zipTemplate.read(mimetype, 0, size);
				ZipEntry mimeEntry=new ZipEntry(templateEntry);
				zipOutput.putNextEntry(mimeEntry);
				zipOutput.write(mimetype);
			}
		}
		zipTemplate.close();

		zipTemplate=new ZipInputStream(resource.openStream());
		while ((templateEntry=zipTemplate.getNextEntry())!=null)
		{
			if (!"mimetype".equals(templateEntry.getName()))
			{
//				System.out.println(templateEntry.getName());
				ZipEntry outputEntry=new ZipEntry(templateEntry.getName());
				if (!templateEntry.isDirectory() && templateEntry.getSize()>0)
				{
					int size=(int)templateEntry.getSize();
					byte[] buffer=new byte[size];
					int position=0;
					int bytesRead;
					while (true)
					{
						bytesRead=zipTemplate.read(buffer, position, size-position);
						if (bytesRead==-1) break;
						position+=bytesRead;
						if (position>=size) break;
					}
//					System.out.println(new String(buffer));
					ByteArrayOutputStream output=new ByteArrayOutputStream();
					OutputStreamWriter writer=new OutputStreamWriter(output);
					engine.evaluate(context, writer, "ODFTemplate", new InputStreamReader(new ByteArrayInputStream(buffer)));
					writer.close();
					byte[] bytes=output.toByteArray();
//					System.out.println("bytes.length = "+bytes.length);
					outputEntry.setSize(bytes.length);
					outputEntry.setMethod(ZipEntry.DEFLATED);
					zipOutput.putNextEntry(outputEntry);
					zipOutput.write(bytes);
					zipOutput.closeEntry();
				}
				else
				{
					outputEntry.setSize(0);
					zipOutput.putNextEntry(outputEntry);
				}
			}
		}
		zipTemplate.close();

		for (Map.Entry<String, File> entry : pictures.entrySet())
		{
			ZipEntry pictureEntry=new ZipEntry("Pictures/"+entry.getValue().getName());
			pictureEntry.setSize(entry.getValue().length());
			pictureEntry.setMethod(ZipEntry.DEFLATED);
			zipOutput.putNextEntry(pictureEntry);
			IOUtils.copy(new FileInputStream(entry.getValue()), zipOutput);
		}
		zipOutput.close();
	}
}
