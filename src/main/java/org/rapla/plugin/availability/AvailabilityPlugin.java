package org.rapla.plugin.availability;

import org.rapla.entities.configuration.CalendarModelConfiguration;
import org.rapla.entities.configuration.RaplaMap;
import org.rapla.framework.TypedComponentRole;

public class AvailabilityPlugin
{
    public static final TypedComponentRole<RaplaMap<CalendarModelConfiguration>> PLUGIN_ENTRY = CalendarModelConfiguration.EXPORT_ENTRY;
    public static final String HTML_EXPORT= PLUGIN_ENTRY + ".selected";
    public static final String PLUGIN_ID ="org.rapla.plugin.availability";
    public static final TypedComponentRole<String> SERVER_DOMAIN = new TypedComponentRole<>(PLUGIN_ID + "." + "ServerDomain");
    public static final TypedComponentRole<String> URLS = new TypedComponentRole<>(PLUGIN_ID + "."+ "Urls");

}
