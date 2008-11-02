package com.kiwisoft.media.download;

import java.io.*;
import java.net.URL;
import java.util.List;

public interface Parser
{
	public void parse(File file, URL url, List<URL> contained, List<URL> linked) throws IOException;
}