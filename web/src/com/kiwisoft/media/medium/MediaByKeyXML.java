package com.kiwisoft.media.medium;

import java.util.TreeSet;
import java.util.Comparator;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.kiwisoft.media.XMLSource;
import com.kiwisoft.utils.xml.XMLWriter;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.utils.Utils;

/**
 * @author Stefan Stiller
 */
public class MediaByKeyXML implements XMLSource
{
	@Override
	public void createXML(HttpServletRequest request, XMLWriter xmlWriter) throws IOException
	{
		TreeSet<Medium> mediums=new TreeSet<Medium>(new MediumComparator());
		mediums.addAll(DBLoader.getInstance().loadSet(Medium.class, null, "userkey is not null and ifnull(obsolete, 0)=0"));

		xmlWriter.startElement("media");
		for (Medium medium : mediums)
		{
			xmlWriter.startElement("medium");
			xmlWriter.setAttribute("key", medium.getFullKey());
			xmlWriter.setAttribute("name", medium.getName());
			xmlWriter.setAttribute("length", medium.getLength());
			xmlWriter.setAttribute("remaining", medium.getRemainingLength());
			xmlWriter.setAttribute("storage", medium.getStorage());
			xmlWriter.closeElement("medium");
		}
		xmlWriter.closeElement("media");
	}

	private static class MediumComparator implements Comparator<Medium>
	{
		@Override
		public int compare(Medium o1, Medium o2)
		{
			return Utils.compareNullSafe(o1.getUserKey(), o2.getUserKey(), false);
		}
	}
}
