package com.kiwisoft.media.fanfic;

import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.editor.SourceCodeDocument;
import com.kiwisoft.editor.lexer.HTMLLexer;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.lookup.DialogLookupField;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class FanFicPartDetailsView extends DetailsView
{
	public static void create(FanFicPart part)
	{
		new DetailsFrame(new FanFicPartDetailsView(part)).show();
	}

	private FanFicPart part;

	private JTextField titleField;
	private DialogLookupField fileField;
	private JTabbedPane tabs;
	private JTextPane htmlSourceField;
	private JTextPane htmlPreviewField;

	private FanFicPartDetailsView(FanFicPart part)
	{
		this.part=part;
		createContentPanel();
		initializeData();
		setTitle(part.getFanFic().getTitle()+" - Part "+(part.getFanFic().getParts().indexOf(part)+1));
	}

	private void initializeData()
	{
		titleField.setText(part.getName());
		String source=part.getSource();
		if (!StringUtils.isEmpty(source))
		{
			File file=FileUtils.getFile(MediaConfiguration.getFanFicPath(), source);
			fileField.setText(file.getAbsolutePath());
			if (file.exists())
			{
				try
				{
					String content=FileUtils.loadFile(file);
					htmlSourceField.setText(content);
					htmlPreviewField.setText(content);
				}
				catch (IOException e)
				{
					GuiUtils.handleThrowable(this, e);
				}
			}
		}
	}

	@Override
	public boolean apply()
	{
		final String title=titleField.getText();
		String source=null;
		String path=fileField.getText();
		if (!StringUtils.isEmpty(path)) source=FileUtils.getRelativePath(MediaConfiguration.getFanFicPath(), path);

		final String finalSource=source;
		return DBSession.execute(new Transactional()
		{
			@Override
			public void run() throws Exception
			{
				part.setName(title);
				part.setSource(finalSource);
				// todo save content
				// todo create file store
			}

			@Override
			public void handleError(Throwable throwable, boolean rollback)
			{
				GuiUtils.handleThrowable(FanFicPartDetailsView.this, throwable);
			}
		});
	}

	protected void createContentPanel()
	{
		titleField=new JTextField();
		fileField=new DialogLookupField(new FanFicPartLookup());
		SourceCodeDocument document=new SourceCodeDocument();
		document.setHighlightStyle(HTMLLexer.class);
		htmlSourceField=new JTextPane(document);
		htmlPreviewField=new JTextPane();
		htmlPreviewField.setEditable(false);
		htmlPreviewField.setEditorKit(new MyHTMLEditorKit());
		tabs=new JTabbedPane();
		tabs.setPreferredSize(new Dimension(700, 400));
		tabs.addTab("Source Code", new JScrollPane(htmlSourceField));
		tabs.addTab("Preview", new JScrollPane(htmlPreviewField));

		setLayout(new GridBagLayout());
		int row=0;
		add(new JLabel("Title:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(titleField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("File:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(fileField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(tabs, new GridBagConstraints(0, row, 2, 1, 1.0, 1.0, WEST, BOTH, new Insets(10, 0, 0, 0), 0, 0));

		getListenerList().installChangeListener(tabs, new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (tabs.getSelectedIndex()==1)
				{
					htmlPreviewField.setText(htmlSourceField.getText());
				}
			}
		});
	}


	@Override
	public JComponent getDefaultFocusComponent()
	{
		return titleField;
	}

	private class MyHTMLEditorKit extends HTMLEditorKit
	{
		private ViewFactory viewFactory;

		private MyHTMLEditorKit()
		{
			viewFactory=new MyHTMLFactory();
		}

		@Override
		public ViewFactory getViewFactory()
		{
			return viewFactory;
		}
	}

	public static class MyHTMLFactory extends HTMLEditorKit.HTMLFactory
	{
		/**
		 * Creates a view from an element.
		 *
		 * @param elem the element
		 * @return the view
		 */
		public View create(Element elem)
		{
			AttributeSet attrs=elem.getAttributes();
			Object elementName=attrs.getAttribute(AbstractDocument.ElementNameAttribute);
			Object o=(elementName!=null) ? null : attrs.getAttribute(StyleConstants.NameAttribute);
			if (o instanceof HTML.Tag)
			{
				HTML.Tag kind=(HTML.Tag) o;
				if (kind==HTML.Tag.IMG)
				{
					return new MyImageView(elem);
				}
			}
			return super.create(elem);
		}
	}

	private static class MyImageView extends ImageView
	{
		public MyImageView(Element elem)
		{
			super(elem);
		}

		@Override
		public URL getImageURL()
		{
			String src=(String) getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
			if (src==null) return null;

			if (src.startsWith("res/"))
			{
				File file=FileUtils.getFile(MediaConfiguration.getRootPath()+src.substring(3));
				try
				{
					return file.toURI().toURL();
				}
				catch (MalformedURLException e)
				{
					return null;
				}
			}
			return super.getImageURL();
		}
	}
}