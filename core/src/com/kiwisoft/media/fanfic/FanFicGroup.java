/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 24, 2003
 * Time: 10:13:42 PM
 */
package com.kiwisoft.media.fanfic;

import java.util.Set;
import java.util.SortedSet;

public interface FanFicGroup
{
	public Long getId();

	public String getFanFicGroupName();

	public Set<FanFic> getFanFics();

	public int getFanFicCount();

	public boolean contains(FanFic fanFic);

	public SortedSet<Character> getFanFicLetters();

	public Set<FanFic> getFanFics(char ch);

	public String getHttpParameter();
}
