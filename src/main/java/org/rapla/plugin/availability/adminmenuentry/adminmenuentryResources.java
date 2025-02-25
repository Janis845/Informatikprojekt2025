//package org.rapla.plugin.availability.adminmenuentry;
//
//import org.jetbrains.annotations.PropertyKey;
//import org.rapla.components.i18n.AbstractBundle;
//import org.rapla.components.i18n.BundleManager;
//import org.rapla.components.i18n.I18nBundle;
//import org.rapla.inject.Extension;
//
//import javax.inject.Inject;
//
//@Extension(provides = I18nBundle.class, id = adminmenuentryResources.BUNDLENAME)
//public class adminmenuentryResources extends AbstractBundle
//{
//    
//    public static final String BUNDLENAME = adminmenuentry.PLUGIN_ID + ".adminmenuentryResources";
//
//    @Inject
//    public adminmenuentryResources( BundleManager bundleManager)
//    {
//        super(BUNDLENAME, bundleManager);
//    }
//
//    public String getString(@PropertyKey(resourceBundle = BUNDLENAME) String key)
//    {
//        return super.getString(key);
//    }
//
//    public String format(@PropertyKey(resourceBundle = BUNDLENAME) String key, Object... obj)
//    {
//        return super.format(key, obj);
//    }
//
//}
