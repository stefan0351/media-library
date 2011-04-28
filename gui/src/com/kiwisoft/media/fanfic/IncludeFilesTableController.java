package com.kiwisoft.media.fanfic;

import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.swing.table.DefaultSortableTableModel;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.media.MediaConfiguration;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

import org.jetbrains.annotations.NonNls;

import javax.swing.*;

/**
 * @author Stefan Stiller
* @since 05.12.2010
*/
public class IncludeFilesTableController extends TableController<File>
{
	public static final String NAME="name";
	public static final String SIZE="size";
	public static final String LAST_MODIFIED="lastModified";

	public IncludeFilesTableController()
	{
		super(new DefaultSortableTableModel<File>(NAME, SIZE, LAST_MODIFIED),
			  new DefaultTableConfiguration("fanfic.includes", IncludeFilesTableController.class, "table.fanfic.includes"));
	}

	@Override
	public List<ContextAction> getToolBarActions()
	{
		List<ContextAction> actions=new ArrayList<ContextAction>();
		actions.add(new NewFileAction());
		actions.add(new RemoveRowAction());
		return actions;
	}

	public static class Row extends SortableTableRow<File>
	{
		private boolean newFile;

		public Row(File file, boolean newFile)
		{
			super(file);
			this.newFile=newFile;
		}

		@Override
		public String getCellFormat(int column, @NonNls String property)
		{
			if (SIZE.equals(property)) return "ByteCount";
			return super.getCellFormat(column, property);
		}

		@Override
		public Object getDisplayValue(int column, @NonNls String property)
		{
			if (NAME.equals(property)) return getUserObject().getName();
			if (SIZE.equals(property)) return getUserObject().length();
			if (LAST_MODIFIED.equals(property))
			{
				long millis=getUserObject().lastModified();
				return millis!=0L ? new Date(millis) : null;
			}
			return null;
		}

		public boolean isNewFile()
		{
			return newFile;
		}
	}

	public class NewFileAction extends ContextAction
	{
		public NewFileAction()
		{
			super("Select", Icons.getIcon("lookup.file"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			JFileChooser fileChooser=new JFileChooser();
			String path=MediaConfiguration.getRecentMediaPath();
			if (path==null) path=MediaConfiguration.getRootPath();
			if (path!=null) fileChooser.setCurrentDirectory(new File(path));
			if (fileChooser.showOpenDialog(getComponent())==JFileChooser.APPROVE_OPTION)
			{
				File file=fileChooser.getSelectedFile();
				MediaConfiguration.setRecentMediaPath(file.getParent());
				getModel().addRow(new Row(file, true));
			}
		}
	}

}
