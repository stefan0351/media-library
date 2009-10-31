package com.kiwisoft.media.fanfic;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.movie.MovieLookup;
import com.kiwisoft.media.show.ShowLookup;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.swing.lookup.LookupEvent;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.lookup.LookupSelectionListener;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.persistence.DBSession;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2004/08/28 21:19:44 $
 */
public class FanDomDetailsView extends DetailsView
{
	public static void create(FanDom fanDom)
	{
		new DetailsFrame(new FanDomDetailsView(fanDom)).show();
	}

	private FanDom fanDom;

	// Konfigurations Panel
	private LookupField<Show> tfShow;
	private LookupField<Movie> tfMovie;
	private JTextField tfName;

	private FanDomDetailsView(FanDom fanDom)
	{
		this.fanDom=fanDom;
		createContentPanel();
		initializeData();
	}

	private void initializeData()
	{
		if (fanDom!=null)
		{
			tfName.setText(fanDom.getName());
			tfShow.setValue(fanDom.getShow());
			tfMovie.setValue(fanDom.getMovie());
		}
	}

	@Override
	public boolean apply()
	{
		String name=tfName.getText();
		if (StringUtils.isEmpty(name))
		{
			JOptionPane.showMessageDialog(this, "Name is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			tfName.requestFocus();
			return false;
		}
		Show show=tfShow.getValue();
		Movie movie=tfMovie.getValue();
		if (show!=null && movie!=null)
		{
			JOptionPane.showMessageDialog(this, "Only one of Show or Movie must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
			tfShow.requestFocus();
			return false;
		}

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (fanDom==null) fanDom=FanFicManager.getInstance().createDomain();
			fanDom.setName(name);
			fanDom.setShow(show);
			fanDom.setMovie(movie);
			transaction.close();
			return true;
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			try
			{
				if (transaction!=null) transaction.rollback();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			JOptionPane.showMessageDialog(this, t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	protected void createContentPanel()
	{
		tfShow=new LookupField<Show>(new ShowLookup());
		tfMovie=new LookupField<Movie>(new MovieLookup());
		tfName=new JTextField();

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 150));
		int row=0;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfName, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Show:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfShow, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Movie:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfMovie, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		tfName.getDocument().addDocumentListener(new FrameTitleUpdater());
		ReferenceListener referenceListener=new ReferenceListener();
		tfShow.addSelectionListener(referenceListener);
		tfShow.addSelectionListener(referenceListener);
	}

	@Override
	public JComponent getDefaultFocusComponent()
	{
		return tfName;
	}

	private class ReferenceListener implements LookupSelectionListener
	{
		@Override
		public void selectionChanged(LookupEvent event)
		{
			if (event.getSource()==tfShow)
			{
				Show show=tfShow.getValue();
				if (show!=null)
				{
					tfMovie.setValue(null);
					if (StringUtils.isEmpty(tfName.getText())) tfName.setText(show.getTitle());
				}
			}
			else if (event.getSource()==tfMovie)
			{
				Movie movie=tfMovie.getValue();
				if (movie!=null)
				{
					tfMovie.setValue(null);
					if (StringUtils.isEmpty(tfName.getText())) tfName.setText(movie.getTitle());
				}
			}
		}
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		public FrameTitleUpdater()
		{
			changedUpdate(null);
		}

		@Override
		public void changedUpdate(DocumentEvent e)
		{
			String name=tfName.getText();
			if (StringUtils.isEmpty(name)) name="<unknown>";
			setTitle("Domain: "+name);
		}
	}
}
