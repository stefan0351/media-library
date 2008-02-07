package com.kiwisoft.media.download;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.showInputDialog;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.kiwisoft.cfg.Configuration;
import com.kiwisoft.swing.WindowManager;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.tree.TreeController;
import com.kiwisoft.utils.WebUtils;

public class DownloadFrame extends JFrame
{
	private TreeController treeController;

	private DownloadProject project;

	public DownloadFrame(DownloadProject project)
	{
		this.project=project;
		setSize(700, 500);
		setTitle("Download Manager");

		createComponents();

		WindowManager.registerFrame(this);
		WindowManager.arrange(null, this);
	}

	private void createComponents()
	{
		treeController=new TreeController()
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new SaveProjectAction());
				actions.add(new StartDownloadAction(project));
				actions.add(new StopDownloadAction(project));
				actions.add(new AddURLAction());
//				actions.add(new ImportAction());
//				actions.add(new ClearDocumentsAction());
				return actions;
			}

			@Override
			protected void selectionChanged(List objects)
			{
				super.selectionChanged(objects);
			}
		};

		setLayout(new BorderLayout());
		add(treeController.createComponent(), BorderLayout.CENTER);

		treeController.getTree().setRoot(new DownloadProjectNode(project));
		treeController.getTree().setRootVisible(false);
		treeController.getTree().setShowsRootHandles(true);
		treeController.installListeners();
	}

	@Override
	public void dispose()
	{
		treeController.dispose();
		WindowManager.deregisterFrame(this);
		super.dispose();
	}

//	private JPopupMenu getPopupMenu()
//	{
//		JPopupMenu menu=new JPopupMenu();
//		TreePath[] paths=treeDocuments.getSelectionPaths();
//		if (paths!=null && paths.length==1)
//		{
//			Object node=paths[0].getLastPathComponent();
//			if (node instanceof GenericTreeNode)
//			{
//				JComponent nodeMenuItems[]=((GenericTreeNode)node).getPopupMenu();
//				if (nodeMenuItems!=null)
//				{
//					menu.addSeparator();
//					for (int i=0; i<nodeMenuItems.length; i++) menu.add(nodeMenuItems[i]);
//				}
//			}
//		}
//		else if (paths!=null)
//		{
//			Set<WebDocument> documents=new HashSet<WebDocument>();
//			for (int i=0; i<paths.length; i++)
//			{
//				Object node=paths[i].getLastPathComponent();
//				if (node instanceof DocumentNode)
//				{
//					documents.add(((DocumentNode)node).getUserObject());
//				}
//			}
//			menu.add(new StartDownloadAction(documents));
//		}
//		return menu;
//	}

	private boolean selectDocumentNode(DefaultMutableTreeNode node, WebDocument document)
	{
		if (node.getUserObject()==document)
		{
			TreePath path=new TreePath(node.getPath());
			treeController.getTree().expandPath(path);
			treeController.getTree().setSelectionPath(path);
			return true;
		}
		for (int i=0; i<node.getChildCount(); i++)
		{
			if (selectDocumentNode((DefaultMutableTreeNode)node.getChildAt(i), document)) return true;
		}
		return false;
	}

//	private class LoadProjectAction extends AbstractAction
//	{
//		public LoadProjectAction()
//		{
//			super("Load...", Icons.getIcon("open.file"));
//		}
//
//		public void actionPerformed(ActionEvent event)
//		{
//			FileDialog fd=new FileDialog(DownloadFrame.this, "Load Project", FileDialog.LOAD);
//			fd.setDirectory(Configuration.getInstance().getString("path.downloads"));
//			fd.setFile("*.*");
//			fd.setVisible(true);
//			if (fd.getFile()!=null)
//			{
//				try
//				{
//					project.load(new File(fd.getDirectory(), fd.getFile()));
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//					JOptionPane.showMessageDialog(DownloadFrame.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//				}
//			}
//		}
//	}

	private class SaveProjectAction extends ContextAction
	{
		public SaveProjectAction()
		{
			super("Save");
		}

		public void actionPerformed(ActionEvent event)
		{
			FileDialog fd=new FileDialog(DownloadFrame.this, "Save Project", FileDialog.SAVE);
			fd.setDirectory(Configuration.getInstance().getString("path.downloads"));
			fd.setFile("*.*");
			fd.setVisible(true);
			if (fd.getFile()!=null)
			{
				try
				{
					project.save(new File(fd.getDirectory(), fd.getFile()));
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(DownloadFrame.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			return;
		}
	}

	private class AddURLAction extends ContextAction
	{
		public AddURLAction()
		{
			super("Add Address", Icons.getIcon("add"));
		}

		public void actionPerformed(ActionEvent e)
		{
			String urlString=showInputDialog(DownloadFrame.this, "Address:", "Add Address", QUESTION_MESSAGE);
			if (urlString!=null)
			{
				URL url=WebUtils.getValidURL(urlString);
				if (url!=null)
				{
					if (!project.containsURL(url))
					{
						WebDocument document=new WebDocument(project, url);
						project.addNewDocument(document);
						project.addRootDocument(document);
						selectDocumentNode((DefaultMutableTreeNode)treeController.getTree().getModel().getRoot(), document);
					}
					else
						JOptionPane.showMessageDialog(DownloadFrame.this, "Diese URL existiert bereits.", "Fehler", JOptionPane.ERROR_MESSAGE);
				}
				else
					JOptionPane.showMessageDialog(DownloadFrame.this, "Diese Zeichenkette repräsentiert keine URL.", "Fehler", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

//	private class ImportAction extends AbstractAction
//	{
//		public ImportAction()
//		{
//			super("Importiere URLs...");
//		}
//
//		public void actionPerformed(ActionEvent event)
//		{
//			FileDialog fd=new FileDialog(DownloadFrame.this, "Importiere URLs", FileDialog.LOAD);
//			fd.setDirectory("d:\\temp");
//			fd.setFile("*.*");
//			fd.setVisible(true);
//			if (fd.getFile()!=null)
//			{
//				List<String> malformedURLs=new ArrayList<String>();
//				List<URL> ignoredURLs=new ArrayList<URL>();
//				try
//				{
//					BufferedReader reader=new BufferedReader(new FileReader(fd.getDirectory()+fd.getFile()));
////					int lineNumber=0;
//					String line;
//					while ((line=reader.readLine())!=null)
//					{
////						lineNumber++;
//						URL url=WebUtils.getValidURL(line);
//						if (url!=null)
//						{
//							if (!project.containsURL(url))
//								project.addNewDocument(new WebDocument(url));
//							else
//								ignoredURLs.add(url);
//						}
//						else
//							malformedURLs.add(line);
//					}
//					reader.close();
//				}
//				catch (Exception e)
//				{
//					JOptionPane
//						.showMessageDialog(DownloadFrame.this, "Datei '"+fd.getFile()+"' konnte nicht geöffnet werden.", "Fehler", JOptionPane.ERROR_MESSAGE);
//				}
//				if (!malformedURLs.isEmpty())
//				{
//					StringBuffer message=new StringBuffer();
//					message.append(malformedURLs.size()).append(" fehlerhafte URL's gefunden:\n");
//					Iterator it=malformedURLs.iterator();
//					while (it.hasNext())
//					{
//						message.append("   ").append(it.next());
//						if (it.hasNext()) message.append("\n");
//					}
//					JOptionPane.showMessageDialog(DownloadFrame.this, message, "Error", JOptionPane.ERROR_MESSAGE);
//				}
//				if (!ignoredURLs.isEmpty())
//				{
//					StringBuffer message=new StringBuffer();
//					message.append(ignoredURLs.size()).append(" URLs ignoriert:\n");
//					Iterator it=ignoredURLs.iterator();
//					while (it.hasNext())
//					{
//						message.append("   ").append(it.next());
//						if (it.hasNext()) message.append("\n");
//					}
//					JOptionPane.showMessageDialog(DownloadFrame.this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
//				}
//			}
//		}
//	}

}
