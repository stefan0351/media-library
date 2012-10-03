package com.kiwisoft.media.tools;

import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.format.FormatManager;
import com.kiwisoft.format.FormatUtils;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.books.*;
import com.kiwisoft.media.dataimport.ImportUtils;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.PersonManager;
import com.kiwisoft.persistence.*;
import com.kiwisoft.utils.CSVReader;
import com.kiwisoft.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 09.05.2010
 */
public class CreateForeignKeys
{
	private CreateForeignKeys()
	{
	}

	public static void main(String[] args) throws IOException
	{
		ImportUtils.USE_CACHE=true;
		Locale.setDefault(Locale.UK);
		SimpleConfiguration configuration=new SimpleConfiguration();
		configuration.loadDefaultsFromFile(new File("conf", "config-dev.xml"));

		for (DatabaseTypeMapping mapping : DBMapping.getInstance().getMappings())
		{
			for (ColumnMapping fieldMapping : mapping.getPersistentFieldMappings())
			{
				if (fieldMapping instanceof ReferenceMapping)
				{
					ReferenceMapping referenceMapping=(ReferenceMapping) fieldMapping;
					if (referenceMapping.getType() instanceof DatabaseTypeMapping)
					{
						DatabaseTypeMapping targetMapping=(DatabaseTypeMapping) referenceMapping.getType();
						StringBuilder statement=new StringBuilder();
						statement.append("alter table ").append(mapping.getTableName());
						statement.append(" add constraint fk_").append(mapping.getTableName().toLowerCase()).append("_").append(referenceMapping.getName().toLowerCase());
						statement.append(" foreign key (").append(referenceMapping.getColumnName()).append(")");
						statement.append(" references ").append(targetMapping.getTableName()).append("(").append(targetMapping.getPrimaryKeyMapping().getColumnName()).append(")");
						System.out.println(statement);
					}
				}
			}
			System.out.println();
		}
		for (ElementMapping mapping : DBMapping.getInstance().getAssociations())
		{
			StringBuilder statement=new StringBuilder();
			statement.append("alter table ").append(mapping.getTable());
			statement.append(" add constraint fk_").append(mapping.getTable().toLowerCase()).append("_").append(mapping.getName().toLowerCase());
			statement.append(" foreign key (").append(mapping.getColumn()).append(")");
			DatabaseTypeMapping targetMapping=mapping.getType();
			statement.append(" references ").append(targetMapping.getTableName()).append("(").append(targetMapping.getPrimaryKeyMapping().getColumnName()).append(")");
			System.out.println(statement);
		}
	}

}
