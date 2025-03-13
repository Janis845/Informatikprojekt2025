package org.rapla.plugin.availability.AdminMenuEntry;

import org.rapla.RaplaResources;
import org.rapla.client.PopupContext;
import org.rapla.client.dialog.DialogInterface;
import org.rapla.client.dialog.DialogUiFactoryInterface;
import org.rapla.client.extensionpoints.AdminMenuExtension;
import org.rapla.client.extensionpoints.ImportMenuExtension;
import org.rapla.client.swing.RaplaGUIComponent;
import org.rapla.client.swing.images.RaplaImages;
import org.rapla.client.swing.toolkit.RaplaMenuItem;
import org.rapla.facade.client.ClientFacade;
import org.rapla.framework.RaplaLocale;
import org.rapla.inject.Extension;
import org.rapla.logger.Logger;
import org.rapla.scheduler.ResolvedPromise;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Extension(provides = ImportMenuExtension.class, id = AdminMenuEntry.PLUGIN_ID)
public class AdminMenuEntry extends RaplaGUIComponent implements ImportMenuExtension, ActionListener {
    public static final String PLUGIN_ID = "org.rapla.plugin.availability";
    RaplaMenuItem item;
    String id = "editMenuEntry";
    final String label;
    private boolean enabled = true;
    private final AdminMenuEntryResources editMenuI18n;
    private final Provider<AdminMenuEntryDialog> copyDialogProvider;
    private final DialogUiFactoryInterface dialogUiFactory;

    @Inject
    public AdminMenuEntry(ClientFacade facade, RaplaResources i18n, RaplaLocale raplaLocale, Logger logger,
                          AdminMenuEntryResources editMenuI18n, Provider<AdminMenuEntryDialog> copyDialogProvider,
                          DialogUiFactoryInterface dialogUiFactory) {
        super(facade, i18n, raplaLocale, logger);
        this.editMenuI18n = editMenuI18n;
        this.copyDialogProvider = copyDialogProvider;
        this.dialogUiFactory = dialogUiFactory;
        label = editMenuI18n.getString(id);
        item = new RaplaMenuItem(id);
        item.setText(label);
        item.setIcon(RaplaImages.getIcon(i18n.getIcon("icon.copy")));
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
        PopupContext popupContext = dialogUiFactory.createPopupContext(null);
        final AdminMenuEntryDialog useCase = copyDialogProvider.get();
        String[] buttons = new String[]{getString("abort")}; // Nur noch "Schließen"-Button
        final JComponent component = useCase.getComponent();
        component.setSize(600, 500);
        final DialogInterface dialog = dialogUiFactory.createContentDialog(popupContext, component, buttons);
        dialog.setTitle(label);
        dialog.getAction(0).setIcon(i18n.getIcon("icon.abort"));
        dialog.start(false).thenCompose(index -> ResolvedPromise.VOID_PROMISE)
                .exceptionally(ex -> dialogUiFactory.showException(ex, popupContext));
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}