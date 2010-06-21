package com.kiwisoft.media.books;

import junit.framework.TestCase;

/**
 * @author Stefan Stiller
 * @since 31.10.2009
 */
public class IsbnTest extends TestCase
{
	public void testValueOf()
	{
		assertIsbn("0671028057", null, "0", "671", "02805", "7", "0-671-02805-7");
		assertIsbn("380252747x", null, "3", "8025", "2747", "X", "3-8025-2747-X");
		assertIsbn("9783522181655", "978", "3", "522", "18165", "5", "978-3-522-18165-5");
		assertIsbn("9789170014833", "978", "91", "7001", "483", "3", "978-91-7001-483-3");
		assertIsbn("9789165000001", "978", "91", "", "6500000", "1", "978-91-6500000-1");
	}

	private void assertIsbn(String number, String prefix, String group, String publisher, String item, String check, String format)
	{
		Isbn isbn=Isbn.valueOf(number);
		assertNotNull(isbn);
		if (prefix==null) assertNull(isbn.getPrefix());
		else assertEquals(prefix, isbn.getPrefix());
		assertEquals(group, isbn.getGroupNumber());
		assertEquals(publisher, isbn.getPublisherNumber());
		assertEquals(item, isbn.getItemNumber());
		assertEquals(check, isbn.getCheckDigit());
		assertEquals(format, isbn.toString());
	}

	public void testIsbn10to13()
	{
		assertEquals("978-3-8025-2319-9", Isbn.valueOf("3-8025-2319-9").getIsbn13().toString());
		assertEquals("978-3-8025-3269-6", Isbn.valueOf("3-8025-3269-4").getIsbn13().toString());
		assertEquals("978-3-8025-2747-0", Isbn.valueOf("3-8025-2747-X").getIsbn13().toString());
	}
}
