package com.kiwisoft.media.files;

import java.awt.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import javax.swing.*;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 */
public class AudioDetailsView extends DetailsView
{
	public static void create(MediaFile audio)
	{
		new DetailsFrame(new AudioDetailsView(audio)).show();
	}

	public static MediaFile createDialog(Window owner, MediaFile audio)
	{
		AudioDetailsView view=new AudioDetailsView(audio);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.audio;
		return null;
	}

	public static MediaFile createDialog(Window owner, String name, String root, String path)
	{
		AudioDetailsView view=new AudioDetailsView(null);
		view.nameField.setText(name);
		view.audioField.setFile(root, path);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.audio;
		return null;
	}

	private MediaFile audio;

	// Konfigurations Panel
	private JTextField nameField;
	private JTextPane descriptionField;
	private AudioField audioField;
	private MediaFileReferencesController referencesController;
	private LookupField<ContentType> contentTypeField;

	private AudioDetailsView(MediaFile picture)
	{
		this.audio=picture;
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		nameField=new JTextField(40);
		contentTypeField=new LookupField<ContentType>(new ContentTypeLookup(MediaType.AUDIO));
		audioField=new AudioField("Audio");
		descriptionField=new JTextPane();
		JScrollPane descriptionPane=new JScrollPane(descriptionField);
		descriptionPane.setPreferredSize(new Dimension(400, 50));
		referencesController=new MediaFileReferencesController();
		JComponent referenceField=referencesController.getComponent();
		referenceField.setPreferredSize(new Dimension(200, 150));

		setLayout(new GridBagLayout());
		int row=0;
		add(new JLabel("Name:"),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(nameField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Content Type:"),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(contentTypeField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Description:"),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(descriptionPane,
			new GridBagConstraints(1, row, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("File:"),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(audioField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(referenceField, new GridBagConstraints(1, row, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

		nameField.getDocument().addDocumentListener(new FrameTitleUpdater());
		referencesController.installListeners();
	}

	private void initializeData()
	{
		if (audio!=null)
		{
			nameField.setText(audio.getName());
			audioField.setFile(audio.getRoot(), audio.getFile());
			descriptionField.setText(audio.getDescription());
			referencesController.addReferences(audio.getReferences());
			contentTypeField.setValue(audio.getContentType());
		}
	}

	@Override
	public void dispose()
	{
		referencesController.dispose();
		super.dispose();
	}

	@Override
	public boolean apply() throws InvalidDataException
	{
		Set<File> filesToBeDeleted=new HashSet<File>();
		filesToBeDeleted.addAll(audioField.getFilesToBeDeleted());
		final String name=nameField.getText();
		if (StringUtils.isEmpty(name)) throw new InvalidDataException("Name is missing!", nameField);
		File file=audioField.getFile();
		filesToBeDeleted.remove(file);
		if (file==null) throw new InvalidDataException("No audio is specified!", audioField);
		if (!file.exists()) throw new InvalidDataException("File '"+file.getAbsolutePath()+"' doesn't exist!", audioField);
		final String root=audioField.getRoot();
		if (root==null) throw new InvalidDataException("File is not located in a configured directory.", audioField);
		final String path=audioField.getPath();
		final Collection<IDObject> references=referencesController.getReferences();

		try
		{
			return DBSession.execute(new Transactional()
			{
				@Override
				public void run() throws Exception
				{
					if (audio==null) audio=MediaFileManager.getInstance().createAudio(root);
					else audio.setRoot(root);
					audio.setName(name);
					audio.setContentType(contentTypeField.getValue());
					audio.setDescription(descriptionField.getText());
					audio.setFile(path);
					audio.setDuration(audioField.getDuration());
					audio.setReferences(references);
				}

				@Override
				public void handleError(Throwable throwable, boolean rollback)
				{
					JOptionPane.showMessageDialog(AudioDetailsView.this, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		finally
		{
			try
			{
				for (File f : filesToBeDeleted)
				{
					if (f.exists() && f.isFile()) f.delete();
				}
				filesToBeDeleted.clear();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		@Override
		public void changedUpdate(DocumentEvent e)
		{
			String name=nameField.getText();
			if (StringUtils.isEmpty(name)) setTitle("Video: <unknown>");
			else setTitle("Video: "+name);
		}
	}
}
