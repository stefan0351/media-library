package com.kiwisoft.text.preformat;

import com.kiwisoft.utils.parser.Token;

%%

%public
%class PreformatLexer
%function getNextToken
%type Token

%{
	public static final int OPENING_TAG=1;
	public static final int CLOSING_TAG=2;
	public static final int EMPTY_TAG=3;
	public static final int TEXT=4;
	public static final int WHITE_SPACE=5;
%}

%line
%char
%column
%unicode
%ignorecase

TagName=("b"|"u"|"i"|"sup"|"sub"|"br"|"em")
WhiteSpace=([ \n\r\f\t])
Text=([^ \n\r\f\t\[\]]+)
OpeningTag=(\[{TagName}{WhiteSpace}*\])
ClosingTag=(\[\/{TagName}{WhiteSpace}*\])
EmptyTag=(\[{TagName}{WhiteSpace}*\/\])
Bracket=(\[|\])

%%

<YYINITIAL> {Text}
{
    String text = yytext();
    Token token = new Token(TEXT, text, yyline, yychar, yychar+text.length());
    yybegin(YYINITIAL);
    return token;
}
<YYINITIAL> {WhiteSpace}+
{
    String text = yytext();
    Token token = new Token(WHITE_SPACE, text, yyline, yychar, yychar+text.length());
    yybegin(YYINITIAL);
    return token;
}
<YYINITIAL> {OpeningTag}
{
    String text = yytext();
    Token token = new Token(OPENING_TAG, text, yyline, yychar, yychar+text.length());
    yybegin(YYINITIAL);
    return token;
}
<YYINITIAL> {ClosingTag}
{
    String text = yytext();
    Token token = new Token(CLOSING_TAG, text, yyline, yychar, yychar+text.length());
    yybegin(YYINITIAL);
    return token;
}
<YYINITIAL> {EmptyTag}
{
    String text = yytext();
    Token token = new Token(EMPTY_TAG, text, yyline, yychar, yychar+text.length());
    yybegin(YYINITIAL);
    return token;
}
<YYINITIAL> {Bracket}
{
    String text = yytext();
    Token token = new Token(TEXT, text, yyline, yychar, yychar+text.length());
    yybegin(YYINITIAL);
    return token;
}
