package com.kiwisoft.media.tvdata;

/**
 * @author Stefan Stiller
 * @since 06.03.11
 */
public class ChannelGroup
{
	private String serviceId;
	private String id;
	private String name;
	private String description;
	private String providername;
	private String[] mirrors;

	public ChannelGroup(String serviceId, String id, String name, String description, String providername, String[] mirrors)
	{
		this.serviceId=serviceId;
		this.id=id;
		this.name=name;
		this.description=description;
		this.providername=providername;
		this.mirrors=mirrors;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb=new StringBuilder();
		sb.append("ChannelGroup");
		sb.append("{name='").append(name).append('\'');
		sb.append('}');
		return sb.toString();
	}

	public String getDescription()
	{
		return description;
	}

	public String getId()
	{
		return id;
	}

	public String[] getMirrors()
	{
		return mirrors;
	}

	public String getName()
	{
		return name;
	}

	public String getProvidername()
	{
		return providername;
	}

	public String getServiceId()
	{
		return serviceId;
	}
}
