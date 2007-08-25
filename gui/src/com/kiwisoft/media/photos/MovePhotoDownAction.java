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
public class MovePhotoDownAction extends MultiContextAction<Photo>
{
	private ThumbnailPanel thumbnailPanel;
	private Chain<Photo> chain;

	public MovePhotoDownAction(ThumbnailPanel thumbnailPanel, Chain<Photo> chain)
	{
		super("Move Down", Icons.getIcon("move.down"));
		this.thumbnailPanel=thumbnailPanel;
		this.chain=chain;
	}

	@Override
	public void update(List<? extends Photo> objects)
	{
		super.update(objects);
		if (!objects.isEmpty())
		{
			if (objects.contains(chain.getLast())) setEnabled(false);
		}
	}

	@SuppressWarnings({"unchecked"})
	public void actionPerformed(ActionEvent e)
	{
		final List<Photo> photos=getObjects();
		Collections.sort(photos, Collections.reverseOrder(new Chain.ChainComparator()));
		thumbnailPanel.clearSelection();
		DBSession.execute(new Transactional()
		{
			public void run() throws Exception
			{
				for (Photo photo : photos) chain.moveDown(photo);
			}

			public void handleError(Throwable throwable, boolean rollback)
			{
				GuiUtils.handleThrowable(thumbnailPanel, throwable);
			}
		});
		SwingUtilities.invokeLater(new Runnable()
		{
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
