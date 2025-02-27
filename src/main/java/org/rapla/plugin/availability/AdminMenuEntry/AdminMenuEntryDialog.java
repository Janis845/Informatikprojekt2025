package org.rapla.plugin.availability.AdminMenuEntry;

import org.rapla.RaplaResources;
import org.rapla.client.RaplaWidget;
import org.rapla.client.swing.RaplaGUIComponent;
import org.rapla.client.swing.internal.SwingPopupContext;
import org.rapla.client.dialog.DialogUiFactoryInterface;
import org.rapla.facade.client.ClientFacade;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaInitializationException;
import org.rapla.framework.RaplaLocale;
import org.rapla.logger.Logger;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

public class AdminMenuEntryDialog extends RaplaGUIComponent implements RaplaWidget {

    private JPanel panel;
    private JTextField raplaIdField;
    private JButton generateButton;
    private JTextField urlField;
    private JButton copyButton;
    private JButton overviewButton;
    private List<String> generatedUrls = new ArrayList<>();
    
    @Inject
    public AdminMenuEntryDialog(ClientFacade facade, RaplaResources i18n, RaplaLocale raplaLocale, Logger logger, DialogUiFactoryInterface dialogUiFactory) throws RaplaInitializationException {
        super(facade, i18n, raplaLocale, logger);
        initUI();
    }
    
    private void initUI() {
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel label = new JLabel("Rapla-ID eingeben:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label, gbc);
        
        raplaIdField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(raplaIdField, gbc);
        
        generateButton = new JButton("Webseite generieren");
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(generateButton, gbc);
        
        JLabel urlLabel = new JLabel("Generierte URL:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(urlLabel, gbc);
        
        urlField = new JTextField(30);
        urlField.setEditable(false);
        gbc.gridx = 1;
        panel.add(urlField, gbc);
        
        copyButton = new JButton("Kopieren");
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(copyButton, gbc);
        
        overviewButton = new JButton("URL Übersicht");
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(overviewButton, gbc);
        
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredId = raplaIdField.getText();
                if (enteredId.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Bitte eine Rapla-ID eingeben!", "Fehler", JOptionPane.ERROR_MESSAGE);
                } else {
                    String generatedUrl = "http://example.com/availability?raplaId=" + enteredId;
                    urlField.setText(generatedUrl);
                    generatedUrls.add(generatedUrl);  // URL zur Liste hinzufügen
                }
            }
        });
        
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = urlField.getText();
                if (!url.isEmpty()) {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(url), null);
                    JOptionPane.showMessageDialog(panel, "URL wurde in die Zwischenablage kopiert!", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(panel, "Keine URL zum Kopieren vorhanden!", "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        overviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UrlOverviewDialog overviewDialog = new UrlOverviewDialog(generatedUrls);
                JOptionPane.showMessageDialog(panel, overviewDialog.getComponent(), "URL Übersicht", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
    
    @Override
    public JComponent getComponent() {
        return panel;
    }
}
