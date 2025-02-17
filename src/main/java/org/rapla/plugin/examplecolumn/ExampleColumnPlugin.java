package org.rapla.plugin.examplecolumn;

import org.rapla.entities.configuration.RaplaConfiguration;
import org.rapla.framework.TypedComponentRole;

public class ExampleColumnPlugin  {
public static final String PLUGIN_CLASS = ExampleColumnPlugin.class.getName();
    public static final boolean ENABLE_BY_DEFAULT = false;

    public static final String PLUGIN_ID = "org.rapla.plugin.examplecolumn";

    public static final TypedComponentRole<RaplaConfiguration> SYSTEM_CONFIG = new TypedComponentRole<>(PLUGIN_ID);
    public static final TypedComponentRole<RaplaConfiguration> USER_CONFIG = new TypedComponentRole<>(PLUGIN_ID);

}