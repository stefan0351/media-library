package com.kiwisoft.media.dataimport.movie;

import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.dataimport.*;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieLookup;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.swing.ComponentUtils;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.style.ObjectStyle;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.text.preformat.PreformatTextController;
import com.kiwisoft.utils.CollectionPropertyChangeEvent;
import com.kiwisoft.utils.NamedValue;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Utils;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

import static java.awt.GridBagConstraints.*;

/**
 * @author Stefan Stiller
 * @since 21.02.11
 */
public class MovieDataDetailsView extends DetailsView
{
	private MovieData movieData;

	private JComboBox summarySelectionField;
	private JTextField imdbKeyField;
	private JTextField titleField;
	private JTextField germanTitleField;
	private PreformatTextController summaryController;
	private JFormattedTextField yearField;
	private JFormattedTextField runtimeField;
	private TableController<CastData> castController;
	private TableController<CrewData> crewController;
	private TableController<LanguageData> languageController;
	private TableController<CountryData> countryController;
	private MatchingPanel<Movie> matchPanel;

	public static void create(Window owner, MovieData movieData)
	{
		MovieDataDetailsView view=new MovieDataDetailsView(movieData);
		DetailsDialog dialog=new DetailsDialog(owner, view, DetailsDialog.OK_CANCEL_ACTIONS);
		dialog.show();
	}

	private MovieDataDetailsView(MovieData movieData)
	{
		this.movieData=movieData;
		initializeComponents();
		initializeData();
	}

	protected void initializeComponents()
	{
		titleField=new JTextField();
		germanTitleField=new JTextField();
		imdbKeyField=new JTextField(5);
		imdbKeyField.setMinimumSize(new Dimension(100, imdbKeyField.getPreferredSize().height));

		JTabbedPane tabbedPane=new JTabbedPane();
		tabbedPane.setTabPlacement(JTabbedPane.LEFT);
		tabbedPane.addTab("Summary", createSummaryPanel());
		tabbedPane.addTab("Info", createInfoPanel());
		tabbedPane.addTab("Cast", createCastPanel());
		tabbedPane.addTab("Crew", createCrewPanel());

		matchPanel=new MatchingPanel<Movie>(new MovieLookup());

		setLayout(new GridBagLayout());
		int row=0;
		add(new JLabel("Key:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(imdbKeyField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Title:", Icons.getIcon("language.en"), SwingConstants.RIGHT),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(titleField, new GridBagConstraints(1, row, 5, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Title:", Icons.getIcon("language.de"), SwingConstants.RIGHT),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 0, 0, 0), 0, 0));
		add(germanTitleField, new GridBagConstraints(1, row, 5, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

		row++;
		add(tabbedPane, new GridBagConstraints(0, row, 6, 1, 1.0, 1.0, CENTER, BOTH, new Insets(10, 0, 0, 0), 0, 0));

		row++;
		add(matchPanel, new GridBagConstraints(0, row, 6, 1, 1.0, 0.0, CENTER, HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));

		titleField.getDocument().addDocumentListener(new FrameTitleUpdater());
		summarySelectionField.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				NamedValue<String> summary=Utils.cast(summarySelectionField.getSelectedItem());
				summaryController.setText(summary!=null ? summary.getValue() : "");
			}
		});
		castController.installListeners();
		crewController.installListeners();
		languageController.installListeners();
		countryController.installListeners();
	}

	private Component createCrewPanel()
	{
		crewController=new TableController<CrewData>(new DefaultSortableTableModel<CrewData>("name", "type", "status"),
													 new DefaultTableConfiguration("MovieDataDetailsView.crewTable", MovieDataDetailsView.class, "crewTable"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>(1);
				actions.add(new CrewDataDetailsAction(MovieDataDetailsView.this));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new CrewDataDetailsAction(MovieDataDetailsView.this);
			}
		};
		crewController.getComponent().setPreferredSize(new Dimension(600, 400));
		JPanel panel=new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel.add(crewController.getComponent(), BorderLayout.CENTER);
		return panel;
	}

	private Component createCastPanel()
	{
		castController=new TableController<CastData>(new DefaultSortableTableModel<CastData>("name", "role", "status"),
													 new DefaultTableConfiguration("MovieDataDetailsView.castTable", MovieDataDetailsView.class, "castTable"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>(1);
				actions.add(new CastDataDetailsAction(MovieDataDetailsView.this));
				actions.add(new DeleteCastDataAction(movieData));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new CastDataDetailsAction(MovieDataDetailsView.this);
			}
		};

		JPanel panel=new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel.add(castController.getComponent(), BorderLayout.CENTER);
		return panel;
	}

	private JPanel createInfoPanel()
	{
		yearField=ComponentUtils.createNumberField(Integer.class, 4, 1900, 3000);
		runtimeField=ComponentUtils.createNumberField(Integer.class, 4, 0, 1000);
		languageController=new TableController<LanguageData>(new DefaultSortableTableModel<LanguageData>("language", "status"),
															 new DefaultTableConfiguration("MovieDataDetailsView.languageTable", MovieDataDetailsView.class, "languageTable"))
		{
			@Override
			public ContextAction getDoubleClickAction()
			{
				return new LanguageDataDetailsAction(MovieDataDetailsView.this);
			}
		};
		languageController.getComponent().setPreferredSize(new Dimension(200, 100));
		countryController=new TableController<CountryData>(new DefaultSortableTableModel<CountryData>("country", "status"),
														   new DefaultTableConfiguration("MovieDataDetailsView.countryTable", MovieDataDetailsView.class, "countryTable"))
		{
			@Override
			public ContextAction getDoubleClickAction()
			{
				return new CountryDataDetailsAction(MovieDataDetailsView.this);
			}
		};
		countryController.getComponent().setPreferredSize(new Dimension(200, 100));

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel.add(new JLabel("Year:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		panel.add(yearField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		panel.add(new JLabel("Runtime:"), new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 10, 0, 0), 0, 0));
		panel.add(runtimeField, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		panel.add(new JLabel("Languages:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		panel.add(languageController.getComponent(), new GridBagConstraints(1, 1, 3, 1, 0.5, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(new JLabel("Countries:"), new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(countryController.getComponent(), new GridBagConstraints(5, 1, 3, 1, 0.5, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(Box.createGlue(), new GridBagConstraints(0, 10, 1, 1, 0.0, 1.0, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
		return panel;
	}

	private JPanel createSummaryPanel()
	{
		summaryController=new PreformatTextController();
		summaryController.getComponent().setPreferredSize(new Dimension(400, 150));
		summarySelectionField=new JComboBox(new DefaultComboBoxModel());

		JPanel summaryPanel=new JPanel(new GridBagLayout());
		summaryPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		summaryPanel.add(summarySelectionField, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		summaryPanel.add(summaryController.getComponent(), new GridBagConstraints(0, 1, 1, 1, 1.0, 0.5, CENTER, BOTH, new Insets(5, 0, 0, 0), 0, 0));
		return summaryPanel;
	}

	private void initializeData()
	{
		getListenerList().installPropertyChangeListener(movieData, new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt instanceof CollectionPropertyChangeEvent)
				{
					CollectionPropertyChangeEvent event=(CollectionPropertyChangeEvent) evt;
					if ("cast".equals(event.getPropertyName()))
					{
						if (event.getType()==CollectionPropertyChangeEvent.REMOVED)
						{
							castController.getModel().removeObject(event.getElement());
						}
					}
				}
			}
		});

		imdbKeyField.setText(movieData.getImdbKey());
		titleField.setText(movieData.getTitle());
		germanTitleField.setText(movieData.getGermanTitle());
		yearField.setValue(movieData.getYear());
		runtimeField.setValue(movieData.getRuntime());

		DefaultComboBoxModel summaryModel=(DefaultComboBoxModel) summarySelectionField.getModel();
		if (!StringUtils.isEmpty(movieData.getSummary())) summaryModel.addElement(new NamedValue<String>("Summary", movieData.getSummary()));
		if (!StringUtils.isEmpty(movieData.getOutline())) summaryModel.addElement(new NamedValue<String>("Outline", movieData.getOutline()));
		summaryModel.addElement(new NamedValue<String>("None", ""));
		summarySelectionField.setSelectedIndex(0);
		NamedValue<String> summary=Utils.cast(summarySelectionField.getSelectedItem());
		summaryController.setText(summary!=null ? summary.getValue() : "");

		ImportUtils.matchMovie(movieData);
		matchPanel.setMatches(movieData.getMovies());

		SortableTableModel<CastData> castModel=castController.getModel();
		for (CastData castData : movieData.getCast())
		{
			ImportUtils.matchPerson(castData, ImportUtils.KeyType.IMDB);
			castModel.addRow(new CastRow(castData));
		}
		SortableTableModel<CrewData> crewModel=crewController.getModel();
		for (CrewData crewData : movieData.getCrew())
		{
			ImportUtils.matchPerson(crewData, ImportUtils.KeyType.IMDB);
			crewModel.addRow(new CrewRow(crewData));
		}
		SortableTableModel<LanguageData> languageModel=languageController.getModel();
		for (LanguageData languageData : movieData.getLanguages())
		{
			ImportUtils.matchLanguage(languageData);
			languageModel.addRow(new LanguageRow(languageData));
		}
		SortableTableModel<CountryData> countryModel=countryController.getModel();
		for (CountryData countryData : movieData.getCountries())
		{
			ImportUtils.matchCountry(countryData);
			countryModel.addRow(new CountryRow(countryData));
		}
	}

	@Override
	public boolean apply() throws InvalidDataException
	{
		Set<Movie> matches=matchPanel.getMatches();
		if (matches==null) throw new InvalidDataException("No matching for movie defined!", matchPanel);

		String title=titleField.getText();
		if (StringUtils.isEmpty(title)) throw new InvalidDataException("Title must not be empty!", titleField);
		String userKey=imdbKeyField.getText();
		if (StringUtils.isEmpty(userKey)) throw new InvalidDataException("Number must not be empty!", imdbKeyField);

		movieData.setTitle(title);
		movieData.setGermanTitle(germanTitleField.getText());
		movieData.setMovies(matches);
		movieData.setSummary(summaryController.getText());

		return DBSession.execute(new SaveMovieDataTransactional(movieData));
	}

	@Override
	public JComponent getDefaultFocusComponent()
	{
		return imdbKeyField;
	}


	private class FrameTitleUpdater extends DocumentAdapter
	{
		@Override
		public void changedUpdate(DocumentEvent e)
		{
			String name=titleField.getText();
			if (StringUtils.isEmpty(name)) name="<unknown>";
			setTitle("Movie: "+name);
		}
	}

	private static final ObjectStyle ERROR_STYLE=new ObjectStyle(new Color(200, 0, 0), null);
	private static final ObjectStyle WARNING_STYLE=new ObjectStyle(new Color(255, 155, 0), null);
	private static final ObjectStyle OK_STYLE=new ObjectStyle(new Color(0, 155, 0), null);

	public static ObjectStyle getStyle(Set matches)
	{
		if (matches==null) return null;
		if (matches.size()==1) return OK_STYLE;
		if (matches.isEmpty()) return WARNING_STYLE;
		if (matches.size()>1) return ERROR_STYLE;
		return null;
	}

	public static String getMatchStatus(Set matches)
	{
		if (matches==null) return "Not matched";
		if (matches.size()==1) return "Match found";
		if (matches.isEmpty()) return "No match found";
		if (matches.size()>1) return "Multiple matches found";
		return null;
	}


	private static class CreditRow<T extends CreditData> extends SortableTableRow<T>
	{
		private CreditRow(T userObject)
		{
			super(userObject);
		}

		@Override
		public Object getDisplayValue(int column, @NonNls String property)
		{
			if ("name".equals(property))
			{
				if (getUserObject().getListedAs()!=null) return getUserObject().getName()+" (as "+getUserObject().getListedAs()+")";
				else return getUserObject().getName();
			}
			else if ("status".equals(property))
			{
				return getMatchStatus(getUserObject().getPersons());
			}
			return null;
		}

		@Override
		public ObjectStyle getCellStyle(int column, @NonNls String property)
		{
			if ("status".equals(property)) return getStyle(getUserObject().getPersons());
			return super.getCellStyle(column, property);
		}
	}

	private static class CastRow extends CreditRow<CastData>
	{
		private CastRow(CastData userObject)
		{
			super(userObject);
		}

		@Override
		public Object getDisplayValue(int column, @NonNls String property)
		{
			if ("role".equals(property)) return getUserObject().getRole();
			return super.getDisplayValue(column, property);
		}
	}

	private static class CrewRow extends CreditRow<CrewData>
	{
		private CrewRow(CrewData userObject)
		{
			super(userObject);
		}

		@Override
		public Object getDisplayValue(int column, @NonNls String property)
		{
			if ("type".equals(property))
			{
				if (!StringUtils.isEmpty(getUserObject().getSubType())) return getUserObject().getType()+" ("+getUserObject().getSubType()+")";
				return getUserObject().getType();
			}
			return super.getDisplayValue(column, property);
		}
	}

	private static class LanguageRow extends SortableTableRow<LanguageData>
	{
		private LanguageRow(LanguageData userObject)
		{
			super(userObject);
		}

		@Override
		public Object getDisplayValue(int column, @NonNls String property)
		{
			if ("language".equals(property)) return getUserObject().getName();
			if ("status".equals(property)) return getMatchStatus(getUserObject().getLanguages());
			return null;
		}

		@Override
		public ObjectStyle getCellStyle(int column, @NonNls String property)
		{
			if ("status".equals(property)) return getStyle(getUserObject().getLanguages());
			return super.getCellStyle(column, property);
		}
	}

	private static class CountryRow extends SortableTableRow<CountryData>
	{
		private CountryRow(CountryData userObject)
		{
			super(userObject);
		}

		@Override
		public Object getDisplayValue(int column, @NonNls String property)
		{
			if ("country".equals(property)) return getUserObject().getName();
			if ("status".equals(property)) return getMatchStatus(getUserObject().getCountries());
			return null;
		}

		@Override
		public ObjectStyle getCellStyle(int column, @NonNls String property)
		{
			if ("status".equals(property)) return getStyle(getUserObject().getCountries());
			return super.getCellStyle(column, property);
		}
	}
}
