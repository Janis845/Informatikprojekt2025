/*--------------------------------------------------------------------------*
 | Copyright (C) 2014 Christopher Kohlhaas                                  |
 |                                                                          |
 | This program is free software; you can redistribute it and/or modify     |
 | it under the terms of the GNU General Public License as published by the |
 | Free Software Foundation. A copy of the license has been included with   |
 | these distribution in the COPYING file, if not go to www.fsf.org         |
 |                                                                          |
 | As a special exception, you are granted the permissions to link this     |
 | program with every library, which license fulfills the Open Source       |
 | Definition as published by the Open Source Initiative (OSI).             |
 *--------------------------------------------------------------------------*/
package org.rapla.plugin.exampleeditmenu;

import org.rapla.RaplaResources;
import org.rapla.client.PopupContext;
import org.rapla.client.dialog.DialogInterface;
import org.rapla.client.dialog.DialogUiFactoryInterface;
import org.rapla.client.extensionpoints.EditMenuExtension;
import org.rapla.client.internal.SaveUndo;
import org.rapla.client.swing.RaplaGUIComponent;
import org.rapla.client.swing.images.RaplaImages;
import org.rapla.client.swing.toolkit.RaplaMenuItem;
import org.rapla.components.util.DateTools;
import org.rapla.entities.User;
import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.Repeating;
import org.rapla.entities.domain.Reservation;
import org.rapla.entities.domain.ReservationStartComparator;
import org.rapla.facade.RaplaFacade;
import org.rapla.facade.client.ClientFacade;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;
import org.rapla.inject.Extension;
import org.rapla.logger.Logger;
import org.rapla.scheduler.Promise;
import org.rapla.scheduler.ResolvedPromise;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Extension(provides = EditMenuExtension.class,id=ExampleEditMenu.PLUGIN_ID)
public class ExampleEditMenu  extends RaplaGUIComponent implements EditMenuExtension, ActionListener
{
	public static final String PLUGIN_ID ="org.rapla.plugin.exampleeditmenu";
	
	RaplaMenuItem item;
	String id = "editMenuEntry";
	final String label ;

	private boolean enabled = true;
    private final ExampleEditMenuResources editMenuI18n;
    private final Provider<ExampleEditMenuDialog> copyDialogProvider;
    private final DialogUiFactoryInterface dialogUiFactory;
	@Inject
    public ExampleEditMenu(ClientFacade facade, RaplaResources i18n, RaplaLocale raplaLocale, Logger logger, ExampleEditMenuResources editMenuI18n, Provider<ExampleEditMenuDialog> copyDialogProvider,  DialogUiFactoryInterface dialogUiFactory)  {
        super(facade, i18n, raplaLocale, logger);
        this.editMenuI18n = editMenuI18n;
        this.copyDialogProvider = copyDialogProvider;
        this.dialogUiFactory = dialogUiFactory;

        label = editMenuI18n.getString(id) ;
		item = new RaplaMenuItem(id);
		
        item.setText( label );
        item.setIcon( RaplaImages.getIcon(i18n.getIcon("icon.copy") ));
        item.addActionListener(this);
    }


    @Override
	public String getId() {
		return id;
	}


	@Override
	public JMenuItem getComponent() {
		return item;
	}
    
     
    public void actionPerformed(ActionEvent evt) {
    	// Beim Klick auf den Menüeintrag wird der Dialog geöffnet.
    	
		PopupContext popupContext = dialogUiFactory.createPopupContext( null);

        final ExampleEditMenuDialog useCase = copyDialogProvider.get();
        String[] buttons = new String[]{getString("abort"), getString("copy") };
		final JComponent component = useCase.getComponent();
		component.setSize( 600, 500);
		final DialogInterface dialog = dialogUiFactory.createContentDialog( popupContext, component, buttons);
            dialog.setTitle( label);
            dialog.getAction( 0).setIcon( i18n.getIcon("icon.abort"));
            dialog.getAction( 1).setIcon( i18n.getIcon("icon.copy"));
  
            dialog.start(false).thenCompose( index->
			{
				// Ergebnisse verarbeiten, bspw. in CopyPluginMenu nachsehen.
				return ResolvedPromise.VOID_PROMISE;
			}).exceptionally( ex ->dialogUiFactory.showException( ex, popupContext ));
    }
    
	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}