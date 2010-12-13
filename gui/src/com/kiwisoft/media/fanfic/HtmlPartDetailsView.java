package com.kiwisoft.media.fanfic;

import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.persistence.filestore.FileStore;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.utils.Disposable;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.text.html.HtmlSourceTextController;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// todo: search in preview and source
// todo: reformat source html

// todo use stylesheet for preview
public class HtmlPartDetailsView extends DetailsView
{
	public static void create(FanFicPart part)
	{
		new DetailsFrame(new HtmlPartDetailsView(part)).show();
	}

	private FanFicPart part;

	private HtmlSourceTextController htmlSourceController;
	private JTextField titleField;
	private JTabbedPane tabs;
	private JTextPane htmlPreviewField;
	private TableController<File> includesController;

	private HtmlPartDetailsView(FanFicPart part)
	{
		this.part=part;
		createContentPanel();
		initializeData();
		setTitle(part.getFanFic().getTitle()+" - Part "+(part.getFanFic().getParts().indexOf(part)+1));
	}

	private void initializeData()
	{
		titleField.setText(part.getName());
		try
		{
			InputStream contentStream=part.getContent();
			if (contentStream!=null)
			{
				try
				{
					String content=IOUtils.toString(contentStream, part.getEncoding()!=null ? part.getEncoding() : "UTF-8");
					htmlSourceController.setText(content);
				}
				finally
				{
					contentStream.close();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		List<File> files=FileStore.getInstance().getAllFiles(part);
		for (File file : files)
		{
			if (!file.getName().equals("content."+part.getExtension()))
				includesController.getModel().addRow(new IncludeFilesTableController.Row(file, false));
		}
	}

	@Override
	public boolean apply() throws InvalidDataException
	{
		final String title=titleField.getText();
		final Set<String> currentFiles=new HashSet<String>();
		for (File file : FileStore.getInstance().getAllFiles(part)) currentFiles.add(file.getName());
		StringBuilder text=new StringBuilder(htmlSourceController.getText());
		String comment="<!-- "+part.getFanFic().getTitle()+(!StringUtils.isEmpty(title) ? " \\ "+title : "")+"-->";
		if (text.indexOf(comment)<0)
		{
			text.insert(0, comment+"\n");
			setText(text.toString(), htmlSourceController.getTextField());
		}

		final byte[] content;
		try
		{
			content=text.toString().getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InvalidDataException(e.getMessage(), htmlSourceController.getTextField());
		}

		return DBSession.execute(new Transactional()
		{
			@Override
			public void run() throws Exception
			{
				part.setName(title);
				part.putContent(new ByteArrayInputStream(content), "html", "UTF-8");
				currentFiles.remove("content.html");
				for (int i=0; i<includesController.getModel().getRowCount(); i++)
				{
					IncludeFilesTableController.Row row=(IncludeFilesTableController.Row) includesController.getModel().getRow(i);
					String fileName=row.getUserObject().getName();
					if (row.isNewFile() && row.getUserObject().exists())
					{
						FileStore.getInstance().putFile(part, fileName, new FileInputStream(row.getUserObject()));
					}
					currentFiles.remove(fileName);
				}
				for (String file : currentFiles) FileStore.getInstance().removeFile(part, file);
			}

			@Override
			public void handleError(Throwable throwable, boolean rollback)
			{
				GuiUtils.handleThrowable(HtmlPartDetailsView.this, throwable);
			}
		});
	}

	protected void createContentPanel()
	{
		titleField=new JTextField();
		htmlSourceController=new HtmlSourceTextController();
		htmlPreviewField=new JTextPane();
		htmlPreviewField.setEditable(false);
		htmlPreviewField.setEditorKit(new MyHTMLEditorKit());
		includesController=new IncludeFilesTableController();

		tabs=new JTabbedPane();
		tabs.setPreferredSize(new Dimension(700, 400));
		tabs.addTab("Source Code", htmlSourceController.getComponent());
		tabs.addTab("Includes", includesController.getComponent());
		final int htmlIndex=tabs.getTabCount();
		tabs.addTab("Preview", new JScrollPane(htmlPreviewField));

		setLayout(new GridBagLayout());
		int row=0;
		add(new JLabel("Title:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(titleField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(tabs, new GridBagConstraints(0, row, 2, 1, 1.0, 1.0, WEST, BOTH, new Insets(10, 0, 0, 0), 0, 0));

		getListenerList().installChangeListener(tabs, new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (tabs.getSelectedIndex()==htmlIndex)
				{
					setText(htmlSourceController.getText(), htmlPreviewField);
				}
			}
		});
		includesController.installListeners();
		getListenerList().addDisposable(new Disposable()
		{
			@Override
			public void dispose()
			{
				includesController.removeListeners();
			}
		});
	}

	private void setText(String text, JEditorPane textPane)
	{
		int position=textPane.getCaretPosition();
		textPane.setText(text);
		textPane.setCaretPosition(position);
	}

	@Override
	public JComponent getDefaultFocusComponent()
	{
		return titleField;
	}

	private static class MyHTMLEditorKit extends HTMLEditorKit
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
		@Override
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

			if (src.startsWith("/media/files/"))
			{
				File file=FileStore.getInstance().getFile(src.substring("/media/files".length()));
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