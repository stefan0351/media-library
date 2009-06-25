package com.kiwisoft.media.download;

import java.net.URL;

import com.kiwisoft.swing.tree.GenericTreeNode;

public class URLNode extends GenericTreeNode<URL>
{
	public URLNode(URL url)
	{
		super(url);
	}

	@Override
	public int getSortPriority()
	{
		return 1;
	}

	@Override
	public boolean isLeaf()
	{
		return true;
	}

	@Override
	public String getToolTip()
	{
		return getUserObject().toString();
	}
}
