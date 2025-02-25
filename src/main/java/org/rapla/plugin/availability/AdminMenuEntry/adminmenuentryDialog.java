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
package org.rapla.plugin.availability.AdminMenuEntry;

import org.rapla.RaplaResources;
import org.rapla.client.RaplaWidget;
import org.rapla.client.dialog.DialogUiFactoryInterface;
import org.rapla.client.swing.RaplaGUIComponent;
import org.rapla.client.swing.internal.SwingPopupContext;
import org.rapla.client.swing.internal.common.NamedListCellRenderer;
import org.rapla.client.swing.internal.edit.fields.BooleanField;
import org.rapla.client.swing.internal.edit.fields.BooleanField.BooleanFieldFactory;
import org.rapla.components.calendar.DateChangeEvent;
import org.rapla.components.calendar.DateChangeListener;
import org.rapla.components.calendar.DateRenderer;
import org.rapla.components.calendar.RaplaCalendar;
import org.rapla.components.iolayer.IOInterface;
import org.rapla.components.layout.TableLayout;
import org.rapla.components.util.TimeInterval;
import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.Period;
import org.rapla.entities.domain.Repeating;
import org.rapla.entities.domain.Reservation;
import org.rapla.entities.domain.internal.PeriodImpl;
import org.rapla.facade.CalendarModel;
import org.rapla.facade.client.ClientFacade;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaInitializationException;
import org.rapla.framework.RaplaLocale;
import org.rapla.logger.Logger;
import org.rapla.plugin.periodcopy.PeriodCopyResources;
import org.rapla.scheduler.Promise;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AdminMenuEntryDialog extends RaplaGUIComponent implements RaplaWidget
{   
	RaplaLocale locale;
	
    JPanel panel = new JPanel();
    
    AdminMenuEntryResources editMenuI18n;
    private final CalendarModel model;
    
    @SuppressWarnings("unchecked")
    @Inject
	public AdminMenuEntryDialog(ClientFacade facade, RaplaResources i18n, RaplaLocale raplaLocale, Logger logger, AdminMenuEntryResources editMenuI18n, CalendarModel model, DateRenderer dateRenderer, BooleanFieldFactory booleanFieldFactory, final DialogUiFactoryInterface dialogUiFactory, IOInterface ioInterface) throws RaplaInitializationException {
        super(facade, i18n, raplaLocale, logger);
        this.editMenuI18n = editMenuI18n;
        this.model = model;
        locale = getRaplaLocale();        
        
        Period[] periods;
        try
        {
            periods = getFacade().getPeriods();
        }
        catch (RaplaException e1)
        {
            throw new RaplaInitializationException(e1);
        }

    }

    public JComponent getComponent() {
        return panel;
    }


//	public Promise<List<Reservation>> getReservations() throws RaplaException {
//	    Promise<Collection<Reservation>> reservationsPromise = model.queryReservations( new TimeInterval(getSourceStart(), getSourceEnd() ));
//	    final Promise<List<Reservation>> promise = reservationsPromise.thenApply((reservations) -> {
//            List<Reservation> listModel = new ArrayList<>();
//            for (Reservation reservation : reservations)
//            {
//
//                boolean includeSingleAppointments = isSingleAppointments();
//                if (isIncluded(reservation, includeSingleAppointments))
//                {
//                    listModel.add(reservation);
//                }
//            }
//            return listModel;
//        });
//	    return promise;
//	}
}

