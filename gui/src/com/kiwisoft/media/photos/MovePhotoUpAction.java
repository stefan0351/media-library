package com.kiwisoft.media.photos;

import java.util.List;
import java.util.Collections;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;

import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.collection.Chain;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;

/**
 * @author Stefan Stiller
*/
public class MovePhotoUpAction extends MultiContextAction
{
	private ThumbnailPanel thumbnailPanel;
	private Chain<Photo> chain;

	public MovePhotoUpAction(ThumbnailPanel thumbnailPanel, Chain<Photo> chain)
	{
		super(Photo.class, "Move Up", Icons.getIcon("move.up"));
		this.thumbnailPanel=thumbnailPanel;
		this.chain=chain;
	}

	@Override
	protected boolean isValid(Object object)
	{
		return super.isValid(object) && object!=chain.getFirst();
	}

	@Override
	@SuppressWarnings({"unchecked"})
	public void actionPerformed(ActionEvent e)
	{
		final List<Photo> photos=getObjects();
		Collections.sort(photos, new Chain.ChainComparator());
		thumbnailPanel.clearSelection();
		DBSession.execute(new Transactional()
		{
			@Override
			public void run() throws Exception
			{
				for (Photo photo : photos) chain.moveUp(photo);
			}

			@Override
			public void handleError(Throwable throwable, boolean rollback)
			{
				GuiUtils.handleThrowable(thumbnailPanel, throwable);
			}
		});
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				for (Photo photo : photos)
				{
					int rowIndex=thumbnailPanel.indexOf(photo);
					if (rowIndex>=0) thumbnailPanel.addSelectionInterval(rowIndex, rowIndex);
				}
			}
		});
	}
}
