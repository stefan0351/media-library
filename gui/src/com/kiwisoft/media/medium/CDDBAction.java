package com.kiwisoft.media.medium;

import static java.awt.GridBagConstraints.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.StringReader;
import javax.swing.*;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.dataImport.cddb.CDDBUtils;
import com.kiwisoft.media.dataImport.cddb.DiscInfo;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class CDDBAction extends ContextAction
{
	private ApplicationFrame frame;

	public CDDBAction(ApplicationFrame frame)
	{
		super("CDDB", Icons.getIcon("download"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		try
		{
			Map<String, List<DiscInfo>> discInfos=CDDBUtils.getDiscInfos();
			MyDialog dialog=new MyDialog(frame, discInfos);
			dialog.setVisible(true);
			if (dialog.getDiscInfo()!=null)
			{
				String details=CDDBUtils.getDiscDetails(dialog.getDiscInfo());
				System.out.println("CDDBAction.actionPerformed: details = "+details);
				BufferedReader reader=new BufferedReader(new StringReader(details));
				Pattern discTitlePattern=Pattern.compile("DTITLE=(.*)");
				Pattern discYearPattern=Pattern.compile("DYEAR=(.*)");
				Pattern trackTitlePattern=Pattern.compile("TTITLE(\\d+)=(.*)");
				String line;
				while ((line=reader.readLine())!=null)
				{
					Matcher matcher=trackTitlePattern.matcher(line);
					if (matcher.matches())
					{
						int number=Integer.parseInt(matcher.group(1));
						String title=matcher.group(2).trim();
						System.out.println("CDDBAction.actionPerformed: number = "+number);
						System.out.println("CDDBAction.actionPerformed: title = "+title);
					}
					matcher=discTitlePattern.matcher(line);
					if (matcher.matches())
					{
						String discTitle=matcher.group(1);
						System.out.println("CDDBAction.actionPerformed: discTitle = "+discTitle);
					}
					matcher=discYearPattern.matcher(line);
					if (matcher.matches())
					{
						int discYear=Integer.parseInt(matcher.group(1));
						System.out.println("CDDBAction.actionPerformed: discYear = "+discYear);
					}
				}
			}
		}
		catch (Exception e1)
		{
			GuiUtils.handleThrowable(frame, e1);
		}
	}

	public class MyDialog extends JDialog
	{
		private DiscInfo discInfo;

		public MyDialog(Window owner, Map<String, List<DiscInfo>> discInfos)
		{
			super(owner, "Select Disc", ModalityType.APPLICATION_MODAL);
			createContentPanel(discInfos);
			pack();
			GuiUtils.centerWindow(owner, this);
		}

		private void createContentPanel(Map<String, List<DiscInfo>> discInfos)
		{
			JPanel contentPanel=new JPanel(new GridBagLayout());
			int row=0;
			for (Map.Entry<String, List<DiscInfo>> entry : discInfos.entrySet())
			{
				contentPanel.add(GuiUtils.createBoldLabel("Drive "+entry.getKey()+":"),
								 new GridBagConstraints(0, row++, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(row>0 ? 10 : 0, 0, 0, 0), 0, 0));
				for (DiscInfo info : entry.getValue())
				{
					JButton button=new JButton(new SelectAction(info));
					button.setBorderPainted(false);
					button.setFocusPainted(false);
					button.setHorizontalAlignment(SwingConstants.LEADING);
					contentPanel.add(button, new GridBagConstraints(0, row++, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(5, 20, 0, 0), 0, 0));
				}
			}

			JPanel buttonsPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
			buttonsPanel.add(new JButton(new CancelAction()));

			JPanel panel=new JPanel(new GridBagLayout());
			panel.add(contentPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, CENTER, BOTH, new Insets(10, 10, 0, 10), 0, 0));
			panel.add(buttonsPanel, new GridBagConstraints(0, 4, 3, 1, 1.0, 0.0,WEST, HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
			setContentPane(panel);
		}


		public DiscInfo getDiscInfo()
		{
			return discInfo;
		}

		private class SelectAction extends AbstractAction
		{
			private DiscInfo info;

			public SelectAction(DiscInfo info)
			{
				super(info.getName()+" ("+org.apache.commons.lang.StringUtils.capitalize(info.getGenre())+")");
				this.info=info;
			}

			public void actionPerformed(ActionEvent e)
			{
				MyDialog.this.discInfo=info;
				dispose();
			}
		}

		private class CancelAction extends AbstractAction
		{
			public CancelAction()
			{
				super("Cancel", Icons.getIcon("cancel"));
			}

			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		}
	}

}
