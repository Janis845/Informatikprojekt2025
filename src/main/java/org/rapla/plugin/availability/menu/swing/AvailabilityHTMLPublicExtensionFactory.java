package org.rapla.plugin.availability.menu.swing;

import org.rapla.RaplaResources;
import org.rapla.client.extensionpoints.PublishExtensionFactory;
import org.rapla.client.swing.PublishExtension;
import org.rapla.components.iolayer.IOInterface;
import org.rapla.facade.CalendarSelectionModel;
import org.rapla.facade.client.ClientFacade;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;
import org.rapla.inject.Extension;
import org.rapla.logger.Logger;
import org.rapla.plugin.availability.AvailabilityResources;

import javax.inject.Inject;
import java.beans.PropertyChangeListener;

@Extension(provides=PublishExtensionFactory.class,id="availabilityHTML")
public class AvailabilityHTMLPublicExtensionFactory implements PublishExtensionFactory
{
    private final ClientFacade facade;
    private final RaplaResources i18n;
    private final RaplaLocale raplaLocale;
    private final Logger logger;
    private final AvailabilityResources availabilityI18n;
    private final IOInterface ioInterface;

    @Inject
	public AvailabilityHTMLPublicExtensionFactory(ClientFacade facade, RaplaResources i18n, RaplaLocale raplaLocale, Logger logger, AvailabilityResources availabilityI18n, IOInterface ioInterface) {
		this.facade = facade;
        this.i18n = i18n;
        this.raplaLocale = raplaLocale;
        this.logger = logger;
        this.availabilityI18n = availabilityI18n;
        this.ioInterface = ioInterface;
	}
    
    @Override
    public boolean isEnabled()
    {
        return true;
    }

	public PublishExtension creatExtension(CalendarSelectionModel model,PropertyChangeListener revalidateCallback) throws RaplaException 
	{
		return new AvailabilityHTMLPublishExtension(facade, i18n, raplaLocale, logger, model, availabilityI18n, ioInterface);
	}

}