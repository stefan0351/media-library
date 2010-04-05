package com.kiwisoft.media.channel;

import com.kiwisoft.media.Channel;
import com.kiwisoft.swing.table.BeanTableRow;
import org.jetbrains.annotations.NonNls;

/**
 * @author Stefan Stiller
* @since 05.04.2010
*/
public class ChannelRow extends BeanTableRow<Channel>
{
	public ChannelRow(Channel channel)
	{
		super(channel);
	}

	@Override
	public String getCellFormat(int column, @NonNls String property)
	{
		if (Channel.LOGO.equals(property)) return "icon";
		return super.getCellFormat(column, property);
	}
}
