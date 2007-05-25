package com.kiwisoft.media.photos;

import java.util.List;
import java.util.Collections;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;

import com.kiwisoft.utils.gui.actions.MultiContextAction;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.db.Chain;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.media.utils.GuiUtils;

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

			public void handleError(Throwable throwable)
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
