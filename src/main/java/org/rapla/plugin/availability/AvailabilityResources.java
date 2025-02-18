package org.rapla.plugin.availability;


import org.jetbrains.annotations.PropertyKey;
import org.rapla.components.i18n.AbstractBundle;
import org.rapla.components.i18n.BundleManager;
import org.rapla.components.i18n.I18nBundle;
import org.rapla.inject.Extension;
import org.rapla.plugin.availability.menu.AvailabilityPlugin;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Extension(provides = I18nBundle.class, id = AvailabilityPlugin.PLUGIN_ID)
public class AvailabilityResources extends AbstractBundle
{
    private static final String BUNDLENAME = AvailabilityPlugin.PLUGIN_ID +  ".AvailabilityResources";
        @Inject
        public AvailabilityResources(BundleManager loader)
        {
            super(BUNDLENAME, loader);
        }
        public String getString(@PropertyKey(resourceBundle = BUNDLENAME) String key)
        {
            return super.getString(key);
        }

        public String format(@PropertyKey(resourceBundle = BUNDLENAME) String key, Object... obj)
        {
            return super.format(key, obj);
        }

}

