package org.rapla.plugin.availability.swing;


import org.rapla.client.extensionpoints.PluginOptionPanel;
import org.rapla.components.layout.TableLayout;
import org.rapla.entities.configuration.Preferences;
import org.rapla.framework.RaplaException;
import org.rapla.inject.Extension;
import org.rapla.plugin.availability.menu.AvailabilityPlugin;
import org.rapla.plugin.availability.menu.AvailabilityPlugin;

import javax.inject.Inject;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.Locale;

@Extension(provides = PluginOptionPanel.class, id = AvailabilityPlugin.PLUGIN_ID)
public class AvailabilityPluginOption implements PluginOptionPanel
{
    JCheckBox test1 = new JCheckBox();
    JCheckBox test2 = new JCheckBox();
    JComponent component;
    Preferences preferences;

    @Inject
    public AvailabilityPluginOption()
    {
    }

    protected JPanel createPanel() throws RaplaException
    {
        JPanel content = new JPanel();
        double[][] sizes = new double[][] { { 5, TableLayout.PREFERRED, 5, TableLayout.FILL, 5 },
                { TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED } };
        TableLayout tableLayout = new TableLayout(sizes);
        content.setLayout(tableLayout);
        content.add(new JLabel("This is a test 1"), "1,4");
        content.add(test1, "3,4");
        content.add(new JLabel("This is another test"), "1,6");
        content.add(test2, "3,6");
        return content;

    }

    @Override
    public Object getComponent()
    {
        return component;
    }

    @Override
    public void setPreferences(Preferences preferences)
    {
        this.preferences = preferences;
    }

    @Override
    public void commit() throws RaplaException
    {
        preferences.putEntry(AvailabilityPlugin.SHOW_CALENDAR_LIST_IN_HTML_MENU, test1.isSelected());
        preferences.putEntry(AvailabilityPlugin.SHOW_TOOLTIP_IN_EXPORT_CONFIG_ENTRY, test2.isSelected());
    }

    @Override
    public void show() throws RaplaException
    {
        test1.setSelected(preferences.getEntryAsBoolean(AvailabilityPlugin.SHOW_CALENDAR_LIST_IN_HTML_MENU, true));
        test2.setSelected(preferences.getEntryAsBoolean(AvailabilityPlugin.SHOW_TOOLTIP_IN_EXPORT_CONFIG_ENTRY, true));
        component = createPanel();
    }

    @Override
    public String getName(Locale locale)
    {
        return "Availability Plugin";
    }

}
