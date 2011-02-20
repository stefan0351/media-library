package com.kiwisoft.utils.websearch;

import com.kiwisoft.swing.ComponentUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @author Stefan Stiller
 * @since 19.02.11
 */
public class WebSearchResultView extends JPanel
{
	public WebSearchResultView()
	{
	}

	public void setResults(List<WebSearchResult> results)
	{
		removeAll();

		setLayout(new GridBagLayout());
		int row=0;
		for (int i=0; i<results.size(); i++)
		{
			final WebSearchResult result=results.get(i);
			add(new JLabel(String.valueOf(i+1)+"."),
				new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
			add(ComponentUtils.createBoldLabel(result.getTitle()),
				new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 10), 0, 0));
			row++;
			JLabel linkLabel=new JLabel("<html><u>"+result.getUrl()+"</u></html>");
			linkLabel.setForeground(Color.BLUE);
			linkLabel.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					if (e.getButton()==MouseEvent.BUTTON1)
					{
						linkSelected(result);
					}
				}
			});
			add(linkLabel,
				new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 10), 0, 0));
			row++;
			JLabel label=new JLabel("<html>"+result.getDescription()+"</html>")
			{
				@Override
				public Dimension getPreferredSize()
				{
					Insets insets=getInsets();
					int dx=insets.left+insets.right;
					int dy=insets.top+insets.bottom;

					Rectangle viewR=new Rectangle(0, 0, 100, Short.MAX_VALUE);
					Rectangle iconR=new Rectangle();
					Rectangle textR=new Rectangle();
					SwingUtilities.layoutCompoundLabel(this, getFontMetrics(getFont()), getText(), getIcon(),
													   getVerticalAlignment(), getHorizontalAlignment(),
													   getVerticalTextPosition(), getHorizontalTextPosition(),
													   viewR, iconR, textR,
													   getIconTextGap());
					int x1=Math.min(iconR.x, textR.x);
					int x2=Math.max(iconR.x+iconR.width, textR.x+textR.width);
					int y1=Math.min(iconR.y, textR.y);
					int y2=Math.max(iconR.y+iconR.height, textR.y+textR.height);
					return new Dimension(x2-x1+dx, y2-y1+dy);
				}
			};
			add(label,
				new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 10), 0, 0));
			row++;
		}
	}

	public void linkSelected(WebSearchResult result)
	{
	}
}
