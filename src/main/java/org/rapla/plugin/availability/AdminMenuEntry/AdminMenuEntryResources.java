package org.rapla.plugin.availability.AdminMenuEntry;

import org.jetbrains.annotations.PropertyKey;
import org.rapla.components.i18n.AbstractBundle;
import org.rapla.components.i18n.BundleManager;
import org.rapla.components.i18n.I18nBundle;
import org.rapla.inject.Extension;

import javax.inject.Inject;

@Extension(provides = I18nBundle.class, id = AdminMenuEntryResources.BUNDLENAME)
public class AdminMenuEntryResources extends AbstractBundle
{
    
    public static final String BUNDLENAME = AdminMenuEntry.PLUGIN_ID + ".AdminMenuEntryResources";

    @Inject
    public AdminMenuEntryResources(BundleManager bundleManager)
    {
        super(BUNDLENAME, bundleManager);
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
