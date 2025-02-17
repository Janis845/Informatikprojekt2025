package org.rapla.plugin.export2ical.client.swing;

import org.rapla.RaplaResources;
import org.rapla.client.swing.PublishExtension;
import org.rapla.client.swing.RaplaGUIComponent;
import org.rapla.client.swing.images.RaplaImages;
import org.rapla.client.swing.toolkit.RaplaButton;
import org.rapla.components.iolayer.IOInterface;
import org.rapla.components.layout.TableLayout;
import org.rapla.facade.CalendarSelectionModel;
import org.rapla.facade.client.ClientFacade;
import org.rapla.framework.RaplaLocale;
import org.rapla.logger.Logger;
import org.rapla.plugin.export2ical.Export2iCalPlugin;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class IcalPublishExtension extends RaplaGUIComponent implements PublishExtension
{
	JPanel panel = new JPanel();
	CalendarSelectionModel model;
	final JCheckBox checkbox;
    final JTextField icalURL;
    private final IOInterface ioInterface;
	 
	public IcalPublishExtension(ClientFacade facade, RaplaResources i18n, RaplaLocale raplaLocale, Logger logger, CalendarSelectionModel model, IOInterface ioInterface) {
		super(facade, i18n, raplaLocale, logger);
		this.model = model;
        this.ioInterface = ioInterface;

        panel.setLayout(new TableLayout( new double[][] {{TableLayout.PREFERRED,5,TableLayout.PREFERRED,5,TableLayout.FILL},
                {TableLayout.PREFERRED,5,TableLayout.PREFERRED       }}));
        icalURL = new JTextField();

    	checkbox = new JCheckBox("ICAL " + getString("publish"));
    	final JPanel statusICal = createStatus( icalURL);
    	checkbox.addChangeListener(e -> {
            boolean icalEnabled = checkbox.isSelected() ;
            statusICal.setEnabled( icalEnabled);
        });
        panel.add(checkbox,"0,0");
        panel.add( statusICal, "2,2,4,1");
        
        final String entry = model.getOption(Export2iCalPlugin.ICAL_EXPORT);
        boolean selected = entry != null && entry.equals("true");
		checkbox.setSelected( selected);
		statusICal.setEnabled( selected);
	}
	
	JPanel createStatus( final JTextField urlLabel)  
    {
        addCopyPaste(urlLabel, getI18n(), getRaplaLocale(), ioInterface, getLogger());
        final RaplaButton copyButton = new RaplaButton();
        JPanel status = new JPanel()
        {
			private static final long serialVersionUID = 1L;
            public void setEnabled(boolean enabled)
            {
                super.setEnabled(enabled);
                urlLabel.setEnabled( enabled);
                copyButton.setEnabled( enabled);
            }
        };
        status.setLayout( new BorderLayout());
        urlLabel.setText( "");
        urlLabel.setEditable( true );
        urlLabel.setFont( urlLabel.getFont().deriveFont( (float)10.0));
        status.add( new JLabel("URL: "), BorderLayout.WEST );
        status.add( urlLabel, BorderLayout.CENTER );
        
        copyButton.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        copyButton.setFocusable(false);
        copyButton.setRolloverEnabled(false);
        copyButton.setIcon(RaplaImages.getIcon(i18n.getIcon("icon.copy")));
        copyButton.setToolTipText(getString("copy_to_clipboard"));
        copyButton.addActionListener(e -> {
            urlLabel.requestFocus();
            urlLabel.selectAll();
            copy(urlLabel,e, ioInterface, getRaplaLocale());
        });
        status.add(copyButton, BorderLayout.EAST);
        return status;
    }

	public JPanel getPanel() 
	{
		return panel;
	}

	public void mapOptionTo() 
	{
		 final String icalSelected = checkbox.isSelected() ? "true" : "false";
         model.setOption( Export2iCalPlugin.ICAL_EXPORT, icalSelected);
	}

    @Override
    public void setAdress(String generator, String address) {
        icalURL.setText( address );
    }

    public boolean hasAddressCreationStrategy() {
		return false;
	}

	public String getAddress(String filename, String generator) {
		return null;
	}
	
	public String[] getGenerators() {
	     return new String[] {Export2iCalPlugin.GENERATOR};
	}
	

}

