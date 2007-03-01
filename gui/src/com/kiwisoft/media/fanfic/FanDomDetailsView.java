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
import com.kiwisoft.media.fanfic.FanDom;
import com.kiwisoft.media.fanfic.FanFicManager;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.movie.MovieLookup;
import com.kiwisoft.media.show.ShowLookup;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.lookup.LookupEvent;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.utils.gui.lookup.LookupSelectionListener;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.DetailsFrame;

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

	public boolean apply()
	{
		String name=tfName.getText();
		if (StringUtils.isEmpty(name))
		{
			JOptionPane.showMessageDialog(this, "Name fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfName.requestFocus();
			return false;
		}
		Show show=tfShow.getValue();
		Movie movie=tfMovie.getValue();
		if (show!=null && movie!=null)
		{
			JOptionPane.showMessageDialog(this, "Nur entweder Serie oder Film erlaubt!", "Fehler", JOptionPane.ERROR_MESSAGE);
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
		add(new JLabel("Serie:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfShow, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Film:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfMovie, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		tfName.getDocument().addDocumentListener(new FrameTitleUpdater());
		ReferenceListener referenceListener=new ReferenceListener();
		tfShow.addSelectionListener(referenceListener);
		tfShow.addSelectionListener(referenceListener);
	}

	public JComponent getDefaultFocusComponent()
	{
		return tfName;
	}

	private class ReferenceListener implements LookupSelectionListener
	{
		public void selectionChanged(LookupEvent event)
		{
			if (event.getSource()==tfShow)
			{
				Show show=tfShow.getValue();
				if (show!=null)
				{
					tfMovie.setValue(null);
					if (StringUtils.isEmpty(tfName.getText())) tfName.setText(show.getName());
				}
			}
			else if (event.getSource()==tfMovie)
			{
				Movie movie=tfMovie.getValue();
				if (movie!=null)
				{
					tfMovie.setValue(null);
					if (StringUtils.isEmpty(tfName.getText())) tfName.setText(movie.getName());
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

		public void changedUpdate(DocumentEvent e)
		{
			String name=tfName.getText();
			if (StringUtils.isEmpty(name)) name="<unbekannt>";
			setTitle("FanDom: "+name);
		}
	}
}
