package org.rapla.plugin.examplecolumn;

import org.rapla.entities.MultiLanguageName;
import org.rapla.framework.RaplaLocale;
import org.rapla.inject.Extension;
import org.rapla.plugin.tableview.extensionpoints.TableColumnDefinitionExtension;
import org.rapla.plugin.tableview.internal.TableConfig;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

@Extension(provides = TableColumnDefinitionExtension.class,id=ExampleColumnPlugin.PLUGIN_ID)
public class ExampleColumnDefinitionExtension implements TableColumnDefinitionExtension
{
    private  final ExampleColumnResources i18n;
    private final RaplaLocale raplaLocale;

    @Inject
    public ExampleColumnDefinitionExtension(ExampleColumnResources i18n, RaplaLocale raplaLocale)
    {
        this.i18n = i18n;
        this.raplaLocale = raplaLocale;
    }

    @Override public Collection<TableConfig.TableColumnConfig> getColumns(Set<String> languages)
    {
        TableConfig.TableColumnConfig column = new TableConfig.TableColumnConfig();
        MultiLanguageName name = new MultiLanguageName();
        column.setName(name);
        column.setKey("ExampleColumnKey");
        
        // Der Standardwert, der in der Spalte gezeigt werden soll (hier der Name der Veranstaltung). 
        // In der Definition von Veranstaltungstypen können eigene Attribute definiert werden, die bspw. 
        // in dieser Spalte angezeigt werden können.
        column.setDefaultValue("{p->name(p)}"); 
        
        column.setType("string");
        for (String lang:languages)
        {
            Locale locale = raplaLocale.newLocale(lang, null);
            name.setName(lang, i18n.getString("columnName", locale)); // Findet den Spaltennamen in der gewünschten Sprache
        }
        return Collections.singletonList(column);
    }
}

