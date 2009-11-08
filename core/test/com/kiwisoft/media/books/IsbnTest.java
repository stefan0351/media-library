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
	}

	private void assertIsbn(String number, String prefix, String group, String publisher, String item, String check, String format)
	{
		Isbn isbn=Isbn.valueOf(number);
		assertNotNull(isbn);
		if (prefix==null) assertNull(prefix, isbn.getPrefix());
		else assertEquals(prefix, isbn.getPrefix());
		assertEquals(group, isbn.getGroupNumber());
		assertEquals(publisher, isbn.getPublisherNumber());
		assertEquals(item, isbn.getItemNumber());
		assertEquals(check, isbn.getCheckDigit());
		assertEquals(format, isbn.toString());
	}

	public void testIsbn10to13()
	{
		assertEquals("978-3-8025-2319-1", Isbn.valueOf("3-8025-2319-0").getIsbn13().toString());
		assertEquals("978-3-8025-3269-4", Isbn.valueOf("3-8025-3269-4").getIsbn13().toString());
		assertEquals("978-3-8025-2747-0", Isbn.valueOf("3-8025-2747-X").getIsbn13().toString());
	}
}
