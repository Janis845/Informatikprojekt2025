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
import javax.swing.JTextField;

import java.util.Locale;

@Extension(provides = PluginOptionPanel.class, id = AvailabilityPlugin.PLUGIN_ID)
public class AvailabilityPluginOption implements PluginOptionPanel
{
	 JTextField domainField = new JTextField();
    JComponent component;
    Preferences preferences;

    @Inject
    public AvailabilityPluginOption()
    {
    }

    protected JPanel createPanel() throws RaplaException
    {
        JPanel content = new JPanel();
        double[][] sizes = new double[][] { 
            { 5, TableLayout.PREFERRED, 5, TableLayout.FILL, 5 },
            { TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED }
        };
        TableLayout tableLayout = new TableLayout(sizes);
        content.setLayout(tableLayout);
        
        content.add(new JLabel("Enter the Server Domain (e.g. http://dhbw-heidenheim)"), "1,4");
        content.add(domainField, "1,6,3,4");
        
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
        preferences.putEntry(AvailabilityPlugin.SERVER_DOMAIN,domainField.getText());
    }

    @Override
    public void show() throws RaplaException
    {
        domainField.setText(preferences.getEntryAsString(AvailabilityPlugin.SERVER_DOMAIN,""));
        component = createPanel();
    }

    @Override
    public String getName(Locale locale)
    {
        return "Availability Plugin";
    }

}
