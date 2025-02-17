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
package org.rapla.plugin.tableview.client.swing;

import org.jetbrains.annotations.NotNull;
import org.rapla.RaplaResources;
import org.rapla.client.EditController;
import org.rapla.client.ReservationController;
import org.rapla.client.dialog.DialogUiFactoryInterface;
import org.rapla.client.dialog.InfoFactory;
import org.rapla.client.menu.MenuFactory;
import org.rapla.client.swing.SwingCalendarView;
import org.rapla.client.swing.extensionpoints.SwingViewFactory;
import org.rapla.client.swing.images.RaplaImages;
import org.rapla.client.swing.internal.RaplaMenuBarContainer;
import org.rapla.components.i18n.I18nBundle;
import org.rapla.components.iolayer.IOInterface;
import org.rapla.entities.User;
import org.rapla.entities.domain.AppointmentBlock;
import org.rapla.facade.CalendarModel;
import org.rapla.facade.client.ClientFacade;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;
import org.rapla.inject.Extension;
import org.rapla.logger.Logger;
import org.rapla.plugin.abstractcalendar.client.swing.IntervalChooserPanel;
import org.rapla.plugin.tableview.RaplaTableColumn;
import org.rapla.plugin.tableview.TableViewPlugin;
import org.rapla.plugin.tableview.client.swing.extensionpoints.AppointmentSummaryExtension;
import org.rapla.plugin.tableview.internal.DefaultRaplaTableColumn;
import org.rapla.plugin.tableview.internal.TableConfig;
import org.rapla.scheduler.Promise;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.Icon;
import javax.swing.table.TableColumn;
import java.util.*;
import java.util.function.Supplier;

@Singleton
@Extension(provides = SwingViewFactory.class, id = TableViewPlugin.TABLE_APPOINTMENTS_PER_DAY_VIEW)
public class AppointmentsPerDayViewFactory implements SwingViewFactory {
    private final Set<AppointmentSummaryExtension> appointmentSummaryExtensions;
    private final TableConfig.TableConfigLoader tableConfigLoader;
    private final MenuFactory menuFactory;
    private final ReservationController reservationController;
    private final EditController editController;
    private final InfoFactory infoFactory;
    private final IntervalChooserPanel dateChooser;
    private final DialogUiFactoryInterface dialogUiFactory;
    private final ClientFacade facade;
    private final RaplaResources i18n;
    private final RaplaLocale raplaLocale;
    private final Logger logger;
    private final IOInterface ioInterface;
    private final RaplaMenuBarContainer menuBar;

    @Inject
    public AppointmentsPerDayViewFactory(ClientFacade facade, RaplaResources i18n, RaplaLocale raplaLocale, Logger logger, Set<AppointmentSummaryExtension> appointmentSummaryExtensions,
                                         TableConfig.TableConfigLoader tableConfigLoader, MenuFactory menuFactory,
                                         ReservationController reservationController, EditController editController, InfoFactory infoFactory, IntervalChooserPanel dateChooser, DialogUiFactoryInterface dialogUiFactory, IOInterface ioInterface,
                                         RaplaMenuBarContainer menuBar) {
        this.facade = facade;
        this.i18n = i18n;
        this.raplaLocale = raplaLocale;
        this.logger = logger;
        this.appointmentSummaryExtensions = appointmentSummaryExtensions;
        this.tableConfigLoader = tableConfigLoader;
        this.menuFactory = menuFactory;
        this.reservationController = reservationController;
        this.editController = editController;
        this.infoFactory = infoFactory;
        this.dateChooser = dateChooser;
        this.dialogUiFactory = dialogUiFactory;
        this.ioInterface = ioInterface;
        this.menuBar = menuBar;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public final static String TABLE_VIEW = TableViewPlugin.TABLE_APPOINTMENTS_PER_DAY_VIEW;

    public SwingCalendarView createSwingView(CalendarModel model, boolean editable, boolean printing) throws RaplaException {
        final Supplier<Promise<List<AppointmentBlock>>> initFunction = (() -> model.queryBlocks(model.getTimeIntervall()));

        final String tableName = TableConfig.APPOINTMENTS_PER_DAY_VIEW;
        final User user = facade.getUser();
        final List<RaplaTableColumn<AppointmentBlock>> configuredRaplaTableColumns = tableConfigLoader.loadColumns(tableName, user);

        List<RaplaTableColumn<AppointmentBlock>> raplaTableColumns = new ArrayList<>();
        raplaTableColumns.add(tableConfigLoader.createDateColumn ("appointment_per_date_date", user));
        raplaTableColumns.addAll(configuredRaplaTableColumns);

        SwingTableView<AppointmentBlock> view = new SwingTableView<>(menuBar, facade, i18n, raplaLocale, logger, model, appointmentSummaryExtensions, editable, printing, raplaTableColumns, menuFactory,
                editController, reservationController, infoFactory, dateChooser, dialogUiFactory, ioInterface, initFunction, tableName);
        return view;

    }


    public String getViewId() {
        return TABLE_VIEW;
    }

    public String getName() {
        return i18n.getString("appointments_per_day");
    }

    Icon icon;

    public Icon getIcon() {
        if (icon == null) {
            icon = RaplaImages.getIcon("/org/rapla/plugin/tableview/images/table.png");
        }
        return icon;
    }

    public String getMenuSortKey() {
        return "3";
    }

}
