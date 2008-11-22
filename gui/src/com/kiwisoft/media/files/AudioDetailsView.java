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
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.utils.FileUtils;
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

	public static MediaFile createDialog(Window owner, String name, File file)
	{
		AudioDetailsView view=new AudioDetailsView(null);
		view.nameField.setText(name);
		view.audioField.setFile(file);
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
		JComponent referenceField=referencesController.createComponent();
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
			audioField.setFileName(audio.getFile());
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

	public boolean apply() throws InvalidDataException
	{
		Set<File> filesToBeDeleted=new HashSet<File>();
		filesToBeDeleted.addAll(audioField.getFilesToBeDeleted());
		final String name=nameField.getText();
		if (StringUtils.isEmpty(name)) throw new InvalidDataException("Name is missing!", nameField);
		File file=audioField.getFile();
		if (file==null) throw new InvalidDataException("No audio is specified!", audioField);
		filesToBeDeleted.remove(file);
		if (!file.exists()) throw new InvalidDataException("File '"+file.getAbsolutePath()+"' doesn't exist!", audioField);
		final String videoPath=FileUtils.getRelativePath(MediaConfiguration.getRootPath(), file.getAbsolutePath());
		final Collection<IDObject> references=referencesController.getReferences();

		try
		{
			return DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					if (audio==null) audio=MediaFileManager.getInstance().createAudio(MediaConfiguration.PATH_ROOT);
					audio.setName(name);
					audio.setContentType(contentTypeField.getValue());
					audio.setDescription(descriptionField.getText());
					audio.setFile(videoPath);
					audio.setDuration(audioField.getDuration());
					audio.setReferences(references);
				}

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
		public void changedUpdate(DocumentEvent e)
		{
			String name=nameField.getText();
			if (StringUtils.isEmpty(name)) setTitle("Video: <unknown>");
			else setTitle("Video: "+name);
		}
	}
}
