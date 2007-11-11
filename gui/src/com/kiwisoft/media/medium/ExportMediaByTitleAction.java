package com.kiwisoft.media.medium;

import java.awt.event.ActionEvent;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TreeSet;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 */
public class ExportMediaByTitleAction extends ContextAction<Object>
{
	private ApplicationFrame frame;

	public ExportMediaByTitleAction(ApplicationFrame frame)
	{
		super("Export Media by Title");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		JFileChooser fileChooser=new JFileChooser();
		FileNameExtensionFilter filter=new FileNameExtensionFilter("Excel Dateien", "xls");
		fileChooser.addChoosableFileFilter(filter);
		fileChooser.setFileFilter(filter);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		String recentExportPath=MediaConfiguration.getRecentExportPath();
		if (!StringUtils.isEmpty(recentExportPath) && new File(recentExportPath).exists())
			fileChooser.setCurrentDirectory(new File(recentExportPath));
		if (fileChooser.showSaveDialog(frame)==JFileChooser.APPROVE_OPTION)
		{
			File file=fileChooser.getSelectedFile();
			if (file!=null)
			{
				MediaConfiguration.setRecentExportPath(file.getParentFile().getAbsolutePath());
				try
				{
					HSSFWorkbook workbook=createWorkbook();
					writeWorkbook(workbook, file);
				}
				catch (IOException e1)
				{
					GuiUtils.handleThrowable(frame, e1);
				}
			}
		}
	}

	private HSSFWorkbook createWorkbook()
	{
		HSSFWorkbook workbook=new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("sheet1");

		HSSFPalette palette=workbook.getCustomPalette();
		palette.setColorAtIndex((short)8, (byte)200, (byte)200, (byte)255);

		TreeSet<Track> tracks=new TreeSet<Track>(new TracksByTitleComparator());
		tracks.addAll(MediumManager.getInstance().getMovieTracks());
		int rowIndex=0;

		HSSFCellStyle headerStyle=workbook.createCellStyle();
		headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headerStyle.setFillForegroundColor((short)8);

		HSSFRow row=sheet.createRow(rowIndex++);
		HSSFCell cell=row.createCell((short)0);
		cell.setCellValue("Name");
		cell.setCellStyle(headerStyle);

		for (Track track : tracks)
		{
			row=sheet.createRow(rowIndex++);
			cell=row.createCell((short)0);
			cell.setCellValue(track.getName());

			cell=row.createCell((short)1);
			cell.setCellValue(track.getLanguage().getSymbol());
		}
		sheet.autoSizeColumn((short)0);
		sheet.autoSizeColumn((short)1);

		return workbook;
	}

	private void writeWorkbook(HSSFWorkbook wb, File file) throws IOException
	{
		FileOutputStream outputStream=new FileOutputStream(file);
		wb.write(outputStream);
		outputStream.close();
	}
}
