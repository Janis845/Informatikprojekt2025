package org.rapla.plugin.tableview.internal;

import org.rapla.components.util.ParseDateException;
import org.rapla.components.util.SerializableDateTimeFormat;
import org.rapla.components.util.xml.XMLWriter;
import org.rapla.entities.IllegalAnnotationException;
import org.rapla.entities.MultiLanguageName;
import org.rapla.entities.Named;
import org.rapla.entities.User;
import org.rapla.entities.dynamictype.Classification;
import org.rapla.entities.dynamictype.DynamicTypeAnnotations;
import org.rapla.entities.dynamictype.internal.DynamicTypeImpl;
import org.rapla.entities.dynamictype.internal.DynamicTypeImpl.DynamicTypeParseContext;
import org.rapla.entities.dynamictype.internal.EvalContext;
import org.rapla.entities.dynamictype.internal.ParsedText;
import org.rapla.facade.RaplaFacade;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;
import org.rapla.plugin.tableview.RaplaTableColumn;
import org.rapla.plugin.tableview.TableViewPlugin;
import org.rapla.plugin.tableview.internal.TableConfig.TableColumnConfig;
import org.rapla.plugin.tableview.TableColumnType;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class DefaultRaplaTableColumn<T> implements RaplaTableColumn<T>
{

    protected final TableColumnConfig column;
    protected RaplaLocale raplaLocale;
    final RaplaFacade facade;
    User user;

    public DefaultRaplaTableColumn(TableColumnConfig column, RaplaLocale raplaLocale, RaplaFacade facade, User user)
    {
        this.column = column;
        this.raplaLocale = raplaLocale;
        this.facade = facade;
        this.user = user;
    }

    @Override
    public TableColumnType getType()
    {
        final Class<?> columnClass = getColumnClass();
        if ( columnClass.equals(Date.class))
        {
            return TableColumnType.DATE;
        }
        if ( columnClass.equals(Long.class) || columnClass.equals(Integer.class))
        {
            return TableColumnType.INTEGER;
        }
        return TableColumnType.STRING;
    }

    protected Locale getLocale()
    {
        return getRaplaLocale().getLocale();
    }

    protected RaplaLocale getRaplaLocale()
    {
        return raplaLocale;
    }

    public String getColumnName()
    {
        final Locale locale = getLocale();
        return column.getName(locale);
    }

    public Object getValue(T object, String contextAnnotationName)
    {
        return format(object, contextAnnotationName);
    }

    public String getHtmlValue(T object)
    {
        Object value = format(object, DynamicTypeAnnotations.KEY_NAME_FORMAT_EXPORT);
        return formatHtml(value);
    }

    protected Object format(Object object, String contextAnnotationName)
    {
        final Locale locale = getLocale();
        final String annotationName = getAnnotationName();
        final Classification classification = ParsedText.guessClassification(object);
        if (classification == null) {
            throw new IllegalStateException("object " + object + " is not a classifiable object");
        }
        final DynamicTypeImpl type = (DynamicTypeImpl) classification.getType();
        ParsedText parsedAnnotation = type.getParsedAnnotation(annotationName);
        final EvalContext context = type.createEvalContext(user,locale, contextAnnotationName, Collections.singletonList(object));
        if (parsedAnnotation == null)
        {
            final String defaultValue = column.getDefaultValue();
            parsedAnnotation = new ParsedText(defaultValue);
            final DynamicTypeParseContext parseContext = type.getParseContext();
            try
            {
                parsedAnnotation.init(parseContext);
            }
            catch (IllegalAnnotationException ex)
            {
                return null;
            }
        }
        String format = parsedAnnotation.formatName(context);
        if (isDate() || isDatetime())
        {
            java.util.Date date;
            try
            {
                if (isDatetime())
                {
                    date = SerializableDateTimeFormat.INSTANCE.parseTimestamp(format);
                }
                else
                {
                    boolean fillDate = false;
                    date = SerializableDateTimeFormat.INSTANCE.parseDate(format, fillDate);
                }
                return date;
            }
            catch (ParseDateException e)
            {
                return null;
            }
        }
        return format;
    }

    protected String getAnnotationName()
    {
        return TableViewPlugin.COLUMN_ANNOTATION + column.getKey();
    }

    public Class<?> getColumnClass()
    {
        if (isDate() || isDatetime())
        {
            return Date.class;
        }
        return String.class;
    }

    protected boolean isDatetime()
    {
        String type = column.getType();
        final boolean isDate = type.equals("datetime");
        return isDate;
    }

    protected boolean isDate()
    {
        String type = column.getType();
        final boolean isDate = type.equals("date");
        return isDate;
    }

    protected String formatHtml(Object value)
    {
        if (value == null)
        {
            return "";
        }
        if (isDate() || isDatetime())
        {
            RaplaLocale raplaLocale = getRaplaLocale();
            if (!(value instanceof Date))
            {
                value = "invalid date";
            }
            else
            {
                Date date = (Date) value;
                if (isDatetime())
                {
                    value = raplaLocale.formatDateLong(date) + " " + raplaLocale.formatTime(date);
                }
                else
                {
                    value = raplaLocale.formatDateLong(date);
                }

            }
        }
        return XMLWriter.encode(value.toString());
    }

}