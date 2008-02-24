package com.kiwisoft.media;

import java.awt.Color;
import java.util.ResourceBundle;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import javax.swing.JComponent;

import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.utils.*;
import com.kiwisoft.persistence.DatabaseConfiguration;
import com.kiwisoft.swing.style.ObjectStyle;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.cfg.Configuration;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;

/**
 * @author Stefan Stiller
 */
public class ConfigurationView extends ViewPanel
{
	private TableController<String> tableController;
	private ResourceBundle resources;

	private static ObjectStyle INVALID_PROPERTY_VALUE_STYLE=new ObjectStyle(Color.RED, null);

	public ConfigurationView()
	{
		resources=ResourceBundle.getBundle(ConfigurationView.class.getName());
	}

	@Override
	public String getTitle()
	{
		return "Configuration";
	}

	protected JComponent createContentPanel(ApplicationFrame frame)
	{
		DefaultSortableTableModel<String> tableModel=new DefaultSortableTableModel<String>("property", "value", "required");

		tableController=new TableController<String>(tableModel, new DefaultTableConfiguration(ConfigurationView.class, "entries"));
		return tableController.createComponent();
	}

	@Override
	protected void installComponentListeners()
	{
		super.installComponentListeners();
		tableController.installListeners();
	}

	@Override
	protected void removeComponentListeners()
	{
		tableController.removeListeners();
		super.removeComponentListeners();
	}

	@Override
	public void dispose()
	{
		tableController.dispose();
		super.dispose();
	}

	@Override
	protected void initializeData()
	{
		super.initializeData();
		SortableTableModel<String> tableModel=tableController.getModel();
		tableModel.addRow(new ConfigRow(MediaConfiguration.PATH_ROOT, String.class, "ExistingDirectory", true));
		tableModel.addRow(new ConfigRow(MediaConfiguration.PATH_PHOTOS_RECENT, String.class, "Directory", false));
		tableModel.addRow(new ConfigRow(MediaConfiguration.PATH_SCHEDULE_RECENT, String.class, "Directory", false));
		tableModel.addRow(new ConfigRow(MediaConfiguration.PATH_PICTURES_RECENT, String.class, "Directory", false));
		tableModel.addRow(new ConfigRow(MediaConfiguration.PATH_PHOTOS_THUMBNAILS, String.class, "ExistingDirectory", true));
		tableModel.addRow(new ConfigRow(MediaConfiguration.PATH_BOOKS_COVERS, String.class, "ExistingDirectory", true));
		tableModel.addRow(new ConfigRow(MediaConfiguration.PATH_FANFICS, String.class, "ExistingDirectory", true));
		tableModel.addRow(new ConfigRow(MediaConfiguration.PATH_FANFICS_RECENT, String.class, "Directory", false));
		tableModel.addRow(new ConfigRow(MediaConfiguration.PATH_LOGOS_CHANNELS, String.class, "ExistingDirectory", true));
		tableModel.addRow(new ConfigRow(MediaConfiguration.PATH_IMAGE_EDITOR, String.class, "ExistingFile", false));
		tableModel.addRow(new ConfigRow(MediaConfiguration.PATH_WEB_DATES, String.class, "ExistingFile", false));
		tableModel.addRow(new ConfigRow(MediaConfiguration.PATH_LOGOS_CHANNELS_WEB, String.class, "ExistingDirectory", false));
		tableModel.addRow(new ConfigRow(MediaConfiguration.PATH_DOWNLOADS, String.class, "ExistingDirectory", true));
		tableModel.addRow(new ConfigRow(MediaConfiguration.PATH_CDDBIDGEN_EXE, String.class, "ExistingFile", false));
		tableModel.addRow(new ConfigRow(MediaConfiguration.URL_CDDB, String.class, "URL", false));
		tableModel.addRow(new ConfigRow(DatabaseConfiguration.DB_URL, String.class, true));
		tableModel.addRow(new ConfigRow(DatabaseConfiguration.DB_USER, String.class, true));
		tableModel.addRow(new ConfigRow(DatabaseConfiguration.DB_PASSWORD, String.class, false));
		tableModel.addRow(new ConfigRow(DatabaseConfiguration.DB_DRIVER, String.class, true));
		tableModel.addRow(new ConfigRow(DatabaseConfiguration.DB_MAPPINGS, String.class, true));
		tableModel.addRow(new ConfigRow(DatabaseConfiguration.DB_LOG_CHANGES, Boolean.class, false));
		tableModel.addRow(new ConfigRow(DatabaseConfiguration.DB_CHANGE_LOG_DIR, String.class, "Directory", false));
		tableModel.sort();
	}

	private class ConfigRow extends SortableTableRow<String>
	{
		private Class type;
		private boolean required;
		private String format;

		public ConfigRow(String property, Class type, boolean required)
		{
			this(property, type, null, required);
		}

		public ConfigRow(String property, Class type, String format, boolean required)
		{
			super(property);
			this.type=type;
			this.format=format;
			this.required=required;
		}

		@Override
		public Class getCellClass(int column, String property)
		{
			if ("property".equals(property)) return String.class;
			else if ("value".equals(property)) return type;
			else if ("required".equals(property)) return Boolean.class;
			return super.getCellClass(column, property);
		}

		@Override
		public String getCellFormat(int column, String property)
		{
			if ("value".equals(property)) return format;
			return super.getCellFormat(column, property);
		}

		@Override
		public ObjectStyle getCellStyle(int column, String property)
		{
			if (required)
			{
				if (Utils.isEmpty(getPropertyValue())) return INVALID_PROPERTY_VALUE_STYLE;
			}
			if ("Directory".equals(format) || "ExistingDirectory".equals(format))
			{
				String value=(String)getPropertyValue();
				if (!StringUtils.isEmpty(value))
				{
					File file=new File(value);
					if (!file.exists()) return INVALID_PROPERTY_VALUE_STYLE;
					else if (!file.isDirectory()) return INVALID_PROPERTY_VALUE_STYLE;
				}
			}
			else if ("File".equals(format) || "ExistingFile".equals(format))
			{
				String value=(String)getPropertyValue();
				if (!StringUtils.isEmpty(value))
				{
					File file=new File(value);
					if (!file.exists()) return INVALID_PROPERTY_VALUE_STYLE;
					else if (!file.isFile()) return INVALID_PROPERTY_VALUE_STYLE;
				}
			}
			else if ("URL".equals(format))
			{
				String value=(String)getPropertyValue();
				if (!StringUtils.isEmpty(value))
				{
					try
					{
						new URL(value);
					}
					catch (MalformedURLException e)
					{
						return INVALID_PROPERTY_VALUE_STYLE;
					}
				}
			}
			return super.getCellStyle(column, property);
		}

		public Object getDisplayValue(int column, String property)
		{
			if ("property".equals(property))
			{
				try
				{
					return resources.getString(getUserObject());
				}
				catch (Exception e)
				{
					System.err.println(e.getMessage());
					return getUserObject();
				}
			}
			else if ("value".equals(property)) return getPropertyValue();
			else if ("required".equals(property)) return required;
			return null;
		}

		@Override
		public int setValue(Object value, int column, String property)
		{
			if ("value".equals(property))
			{
				setPropertyValue(value);
				return TableConstants.ROW_UPDATE;
			}
			return super.setValue(value, column, property);
		}

		private void setPropertyValue(Object value)
		{
			if (String.class==type) Configuration.getInstance().setString(getUserObject(), (String)value);
			else if (Boolean.class==type) Configuration.getInstance().setBoolean(getUserObject(), (Boolean)value);
			else if (URL.class==type) Configuration.getInstance().setString(getUserObject(), value!=null ? value.toString() : null);
			else throw new RuntimeException("Unsupported type: "+type);
		}

		private Object getPropertyValue()
		{
			if (String.class==type) return Configuration.getInstance().getString(getUserObject(), null);
			else if (Boolean.class==type) return Configuration.getInstance().getBoolean(getUserObject(), null);
			else if (URL.class==type)
			{
				String value=Configuration.getInstance().getString(getUserObject(), null);
				if (StringUtils.isEmpty(value)) return null;
				try
				{
					return new URL(value);
				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
					return null;
				}
			}
			else throw new RuntimeException("Unsupported type: "+type);
		}

		@Override
		public boolean isEditable(int column, String property)
		{
			return "value".equals(property) || super.isEditable(column, property);
		}
	}

	@Override
	public boolean isBookmarkable()
	{
		return true;
	}

	@Override
	public Bookmark getBookmark()
	{
		return new Bookmark(getTitle(), getClass());
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new ConfigurationView(), true);
	}

}
