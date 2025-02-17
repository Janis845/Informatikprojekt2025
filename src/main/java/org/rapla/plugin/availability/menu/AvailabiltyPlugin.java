package org.rapla.plugin.availability.menu;

import org.rapla.entities.configuration.CalendarModelConfiguration;
import org.rapla.entities.configuration.RaplaMap;
import org.rapla.framework.TypedComponentRole;

public class AvailabiltyPlugin
{
	public static final String CALENDAR_GENERATOR = "calendar";
    public static final TypedComponentRole<RaplaMap<CalendarModelConfiguration>> PLUGIN_ENTRY = CalendarModelConfiguration.EXPORT_ENTRY;
    public static final String HTML_EXPORT= PLUGIN_ENTRY + ".selected";
    public static final String PLUGIN_ID ="org.rapla.plugin.availability";
    public static final TypedComponentRole<Boolean> SHOW_CALENDAR_LIST_IN_HTML_MENU = new TypedComponentRole<>(PLUGIN_ID + "." + "AvailabiltyPluginTest");
    public static final TypedComponentRole<Boolean> SHOW_TOOLTIP_IN_EXPORT_CONFIG_ENTRY = new TypedComponentRole<>(PLUGIN_ID + "."+ "AvailabiltyPluginTest");

}
