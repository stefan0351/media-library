package com.kiwisoft.media.pics;

import java.io.File;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.kiwisoft.utils.DateUtils;
import junit.framework.TestCase;

/**
 * @author Stefan Stiller
 */
public class PictureUtilsTest extends TestCase
{
	public void testGetImageFormat1() throws URISyntaxException
	{
		PictureDetails format=PictureUtils.getImageFormat(new File(PictureUtilsTest.class.getResource("sample1.png").toURI()));
		assertNotNull(format);
		assertEquals("PNG", format.getType());
		assertEquals(1280, format.getWidth());
		assertEquals(1024, format.getHeight());
		assertEquals(200, (int)format.getXResolution());
		assertEquals(200, (int)format.getYResolution());
		assertEquals(8, format.getColorDepth());
	}

	public void testGetImageFormat2() throws URISyntaxException, ParseException
	{
		PictureDetails format=PictureUtils.getImageFormat(new File(PictureUtilsTest.class.getResource("sample2.jpg").toURI()));
		assertNotNull(format);
		assertEquals("JPEG", format.getType());
		assertEquals(1920, format.getWidth());
		assertEquals(2560, format.getHeight());
		assertEquals(72, (int)format.getXResolution());
		assertEquals(72, (int)format.getYResolution());
		assertEquals(8, format.getColorDepth());
		assertEquals("MEDION AG", format.getCameraMake());
		assertNull(format.getCameraModel());
		assertEquals(getDate("09.12.2006 19:03:33"), format.getDate());
		assertEquals(2.269824, format.getExposureTime());
		assertEquals(5.578125, format.getFNumber());
		assertEquals(100, (int)format.getIsoSpeed());
	}

	public void testGetImageFormat3() throws URISyntaxException, ParseException
	{
		PictureDetails format=PictureUtils.getImageFormat(new File(PictureUtilsTest.class.getResource("sample3.jpg").toURI()));
		assertNotNull(format);
		assertEquals("JPEG", format.getType());
		assertEquals(2048, format.getWidth());
		assertEquals(1536, format.getHeight());
		assertEquals(72, (int)format.getXResolution());
		assertEquals(72, (int)format.getYResolution());
		assertEquals(8, format.getColorDepth());
		assertEquals("Minolta Co., Ltd.", format.getCameraMake());
		assertEquals("DiMAGE S304", format.getCameraModel());
		assertEquals(getDate("01.01.2001 13:36:58"), format.getDate());
		assertEquals(7.3125, format.getFocalLength());
		assertEquals(100, (int)format.getIsoSpeed());
	}

	public void testGetImageFormat4() throws URISyntaxException, ParseException
	{
		PictureDetails format=PictureUtils.getImageFormat(new File(PictureUtilsTest.class.getResource("sample4.jpg").toURI()));
		assertNotNull(format);
		assertEquals("JPEG", format.getType());
		assertEquals(1704, format.getWidth());
		assertEquals(2272, format.getHeight());
		assertEquals(72, (int)format.getXResolution());
		assertEquals(72, (int)format.getYResolution());
		assertEquals(8, format.getColorDepth());
		assertEquals("Konica Minolta Camera, Inc.", format.getCameraMake());
		assertEquals("DiMAGE Z2", format.getCameraModel());
		assertEquals(getDate("18.05.2007 09:46:00"), format.getDate());
		assertEquals(0.01, format.getExposureTime());
		assertEquals(3.2, format.getFNumber());
		assertEquals(15.6, format.getFocalLength());
		assertEquals(50, (int)format.getIsoSpeed());
	}

	private Date getDate(String text) throws ParseException
	{
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		dateFormat.setTimeZone(DateUtils.GMT);
		return dateFormat.parse(text);
	}
}
