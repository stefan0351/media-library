package com.kiwisoft.media.download;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.showInputDialog;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreePath;

import com.kiwisoft.app.Application;
import com.kiwisoft.cfg.Configuration;
import com.kiwisoft.media.MediaApplication;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.PanelController;
import com.kiwisoft.swing.SwingListenerSupport;
import com.kiwisoft.swing.WindowManager;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.swing.tree.TreeController;
import com.kiwisoft.swing.tree.TreeUtils;
import com.kiwisoft.utils.JobQueue;
import com.kiwisoft.utils.JobQueueListener;
import com.kiwisoft.utils.WebUtils;

/**
 * @author Stefan Stiller
 */
public class GrabberFrame extends JFrame
{
	private TreeController treeController;

	private GrabberProject project;
	private PanelController statusController;

	public GrabberFrame(GrabberProject project)
	{
		this.project=project;
		if (this.project==null) this.project=new GrabberProject();
		setSize(900, 700);
		setTitle("Webpage Grabber");

		createComponents();

		WindowManager.registerFrame(this);
		GuiUtils.centerWindow(null, this);
	}

	private void createComponents()
	{
		treeController=new TreeController()
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new NewProjectAction());
				actions.add(new LoadProjectAction());
				actions.add(new SaveProjectAction());
				actions.add(new DownloadAction(GrabberFrame.this));
				actions.add(new StartQueuesAction());
				actions.add(new StopQueuesAction());
				actions.add(new AddURLAction());
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions(GenericTreeNode node)
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				if (node instanceof DocumentNode || node instanceof URLNode)
					actions.add(new DownloadAction(GrabberFrame.this));
				return actions;
			}
		};

		JLabel downloadLabel=new JLabel();
		JLabel parsedLabel=new JLabel();

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel.add(downloadLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		panel.add(parsedLabel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));

		statusController=new PanelController("Status", panel);

		setLayout(new BorderLayout());
		add(treeController.createComponent(), BorderLayout.CENTER);
		add(statusController.createComponent(), BorderLayout.SOUTH);

		treeController.getTree().setRoot(new GrabberProjectNode(project));
		treeController.getTree().setRootVisible(false);
		treeController.getTree().setShowsRootHandles(true);
		treeController.installListeners();

		SwingListenerSupport listeners=statusController.getListeners();
		MyJobQueueListener queueListener;
		queueListener=new MyJobQueueListener(downloadLabel, "Downloading ''{0}'' of {1} documents", "Downloading stopped, {0} documents in queue");
		listeners.installJobQueueListener(GrabberUtils.getDownloadQueue(), queueListener);
		queueListener.jobAdded(GrabberUtils.getDownloadQueue(), null);
		queueListener=new MyJobQueueListener(parsedLabel, "Parsing ''{0}'' of {1} documents", "Parsing stopped, {0} documents in queue");
		listeners.installJobQueueListener(GrabberUtils.getParserQueue(), queueListener);
		queueListener.jobAdded(GrabberUtils.getParserQueue(), null);
	}

	@Override
	public void dispose()
	{
		statusController.dispose();
		treeController.dispose();
		WindowManager.deregisterFrame(this);
		super.dispose();
	}

	private boolean selectDocumentNode(GenericTreeNode node, WebDocument document)
	{
		if (node.getUserObject()==document)
		{
			TreePath path=TreeUtils.getPathToRoot(node);
			treeController.getTree().expandPath(path);
			treeController.getTree().setSelectionPath(path);
			return true;
		}
		for (int i=0; i<node.getChildCount(); i++)
		{
			if (selectDocumentNode(node.getChildAt(i), document)) return true;
		}
		return false;
	}

	public GrabberProject getProject()
	{
		return project;
	}

	private class SaveProjectAction extends ContextAction
	{
		public SaveProjectAction()
		{
			super("Save");
		}

		@Override
		public void actionPerformed(ActionEvent event)
		{
			GrabberUtils.stopAllQueues();
			JFileChooser fileChooser=new JFileChooser();
			fileChooser.setCurrentDirectory(new File(Configuration.getInstance().getString("path.downloads")));
			fileChooser.setAcceptAllFileFilterUsed(true);
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML Files", "xml"));
			if (fileChooser.showSaveDialog(GrabberFrame.this)==JFileChooser.APPROVE_OPTION)
			{
				try
				{
					project.save(fileChooser.getSelectedFile());
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(GrabberFrame.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		}
	}

	private class NewProjectAction extends ContextAction
	{
		public NewProjectAction()
		{
			super("New");
		}

		@Override
		public void actionPerformed(ActionEvent event)
		{
			if (project!=null && !project.getFolders().isEmpty())
			{
				int option=JOptionPane.showConfirmDialog(GrabberFrame.this, "Do you really want to overwrite the current project?", "Confirmation",
														 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (option==JOptionPane.NO_OPTION) return;
			}
			GrabberUtils.stopAllQueues();
			project=new GrabberProject();
			treeController.getTree().setRoot(new GrabberProjectNode(project));
		}
	}

	private class LoadProjectAction extends ContextAction
	{
		public LoadProjectAction()
		{
			super("Load", Icons.getIcon("open.file"));
		}

		@Override
		public void actionPerformed(ActionEvent event)
		{
			if (project!=null && !project.getFolders().isEmpty())
			{
				int option=JOptionPane.showConfirmDialog(GrabberFrame.this, "Do you really want to overwrite the current project?", "Confirmation",
														 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (option==JOptionPane.NO_OPTION) return;
			}
			GrabberUtils.stopAllQueues();
			JFileChooser fileChooser=new JFileChooser();
			fileChooser.setCurrentDirectory(new File(Configuration.getInstance().getString("path.downloads")));
			fileChooser.setAcceptAllFileFilterUsed(true);
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML Files", "xml"));
			if (fileChooser.showOpenDialog(GrabberFrame.this)==JFileChooser.APPROVE_OPTION)
			{
				project=GrabberProject.load(fileChooser.getSelectedFile());
				if (project!=null) treeController.getTree().setRoot(new GrabberProjectNode(project));
			}
		}
	}

	private class AddURLAction extends ContextAction
	{
		public AddURLAction()
		{
			super("Add Address", Icons.getIcon("add"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				String urlString=showInputDialog(GrabberFrame.this, "Address:", "Add Address", QUESTION_MESSAGE);
				if (urlString!=null)
				{
					URL url=WebUtils.getValidURL(urlString);
					if (url!=null)
					{
						url=GrabberUtils.getRealURL(url);
						if (!project.containsURL(url))
						{
							WebDocument document=project.createDocument(url);
							selectDocumentNode((GenericTreeNode)treeController.getTree().getModel().getRoot(), document);
						}
						else
							JOptionPane.showMessageDialog(GrabberFrame.this, "This URL already exists.", "Error", JOptionPane.ERROR_MESSAGE);
					}
					else
						JOptionPane.showMessageDialog(GrabberFrame.this, "This string is no valid URL.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			catch (Throwable throwable)
			{
				GuiUtils.handleThrowable(GrabberFrame.this, throwable);
			}
		}
	}

	public static void main(String[] args) throws IOException
	{
		Locale.setDefault(Locale.UK);
		Application application=new MediaApplication();
		application.configureXML();
		application.initialize();

		GrabberFrame downloadFrame=new GrabberFrame(null);
		downloadFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		downloadFrame.setVisible(true);
	}

	private static class MyJobQueueListener implements JobQueueListener
	{
		private MessageFormat format;
		private MessageFormat idleFormat;
		private JLabel label;

		public MyJobQueueListener(JLabel label, String pattern, String idlePattern)
		{
			this.label=label;
			format=new MessageFormat(pattern);
			idleFormat=new MessageFormat(idlePattern);
		}

		@Override
		public void jobAdded(JobQueue jobQueue, Runnable runnable)
		{
			DocumentJob job=(DocumentJob)jobQueue.getCurrentJob();
			if (job!=null)
				updateLabel(format.format(new Object[]{job.getDocument().getURL(), jobQueue.getJobCount()}));
			else
				updateLabel(idleFormat.format(new Object[]{jobQueue.getJobCount()}));
		}

		public void updateLabel(final String text)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					label.setText(text);
				}
			});
		}

		@Override
		public void jobStarted(JobQueue jobQueue, Runnable runnable)
		{
			jobAdded(jobQueue, runnable);
		}

		@Override
		public void jobFinished(JobQueue jobQueue, Runnable runnable)
		{
			jobAdded(jobQueue, runnable);
		}
	}
}
