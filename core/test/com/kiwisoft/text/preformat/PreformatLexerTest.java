package com.kiwisoft.text.preformat;

import com.kiwisoft.utils.parser.Token;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringReader;

/**
 * @author Stefan Stiller
 * @since 15.11.2009
 */
public class PreformatLexerTest extends TestCase
{
	public void testLexer() throws IOException
	{
		assertTokens("abc[b]def[/b]",
			new Token(PreformatLexer.TEXT, "abc"),
			new Token(PreformatLexer.OPENING_TAG, "[b]"),
			new Token(PreformatLexer.TEXT, "def"),
			new Token(PreformatLexer.CLOSING_TAG, "[/b]")
		);
		assertTokens("[[b]]",
			new Token(PreformatLexer.TEXT, "["),
			new Token(PreformatLexer.OPENING_TAG, "[b]"),
			new Token(PreformatLexer.TEXT, "]")
		);
		assertTokens("a[br/]",
			new Token(PreformatLexer.TEXT, "a"),
			new Token(PreformatLexer.EMPTY_TAG, "[br/]")
		);
		assertTokens("a[BR/]",
			new Token(PreformatLexer.TEXT, "a"),
			new Token(PreformatLexer.EMPTY_TAG, "[BR/]")
		);
		assertTokens("a\nb  c",
			new Token(PreformatLexer.TEXT, "a"),
			new Token(PreformatLexer.WHITE_SPACE, "\n"),
			new Token(PreformatLexer.TEXT, "b"),
			new Token(PreformatLexer.WHITE_SPACE, "  "),
			new Token(PreformatLexer.TEXT, "c")
		);
	}

	private void assertTokens(String text, Token... tokens) throws IOException
	{
		PreformatLexer lexer=new PreformatLexer(new StringReader(text));
		int t=0;
		Token token;
		while ((token=lexer.getNextToken())!=null)
		{
			assertTrue("No more tokens expected", t<tokens.length);
			System.out.println(token.getId()+": "+token.getContents());
			assertEquals(tokens[t].getId(), token.getId());
			assertEquals(tokens[t].getContents(), token.getContents());
			t++;
		}
		assertEquals("More tokens expected", t, tokens.length);
	}
}
