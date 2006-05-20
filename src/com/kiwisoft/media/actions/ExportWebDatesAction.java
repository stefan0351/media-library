/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: May 17, 2003
 * Time: 3:01:23 PM
 */
package com.kiwisoft.media.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.AirdateComparator;
import com.kiwisoft.media.Channel;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.show.WebDatesExport;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.progress.ProgressDialog;
import com.kiwisoft.utils.xml.XMLUtils;

public class ExportWebDatesAction extends AbstractAction
{
	public ExportWebDatesAction()
	{
		super("Exportiere Internet-Termine");
	}

	public void actionPerformed(ActionEvent e)
	{
		new ProgressDialog(null, new WebDatesExport()).show();
	}

}
