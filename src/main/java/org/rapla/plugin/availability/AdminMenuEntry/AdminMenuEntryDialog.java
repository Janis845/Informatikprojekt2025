package org.rapla.plugin.availability.AdminMenuEntry;

import org.rapla.RaplaResources;


import org.rapla.client.RaplaWidget;
import org.rapla.client.swing.RaplaGUIComponent;
import org.rapla.client.dialog.DialogUiFactoryInterface;
import org.rapla.facade.RaplaFacade;
import org.rapla.facade.client.ClientFacade;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaInitializationException;
import org.rapla.framework.RaplaLocale;
import org.rapla.logger.Logger;
import org.rapla.plugin.availability.AvailabilityPlugin;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import java.util.Arrays;


import org.rapla.entities.configuration.Preferences;



public class AdminMenuEntryDialog extends RaplaGUIComponent implements RaplaWidget {
    private JPanel panel;
    private JTextField raplaIdField;
    private JButton generateButton;
    private JTextField urlField;
    private JButton copyButton;
    private JButton overviewButton;
    private List<String> savedRaplaIDs = new ArrayList<>();
    private UrlOverviewDialog overviewDialog;
    private  RaplaFacade writableFacade;
    private Preferences writablePreferences;
    private String serverDomain;

    @Inject
    public AdminMenuEntryDialog(ClientFacade facade, RaplaResources i18n, RaplaLocale raplaLocale, Logger logger, DialogUiFactoryInterface dialogUiFactory) throws RaplaInitializationException {
        super(facade, i18n, raplaLocale, logger);
        writableFacade = facade.getRaplaFacade();
    	try {
			writablePreferences = writableFacade.edit(writableFacade.getSystemPreferences());
		} catch (RaplaException e) {
			e.printStackTrace();
		}
        initUI();
        overviewDialog = new UrlOverviewDialog(savedRaplaIDs, this,writableFacade);
        loadIdsFromPreferences();
        
    }


    private void initUI() {
        // Add a method to clean up deleted entries on startup
    	serverDomain = writablePreferences.getEntryAsString(AvailabilityPlugin.SERVER_DOMAIN,"");
    	 
    	//overviewDialog.cleanUpDeletedEntries();
        
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("Rapla-ID eingeben:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(idLabel, gbc);

        raplaIdField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(raplaIdField, gbc);

        generateButton = new JButton("Webseite generieren");
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(generateButton, gbc);

        JLabel urlLabel = new JLabel("Generierte URL:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(urlLabel, gbc);

        urlField = new JTextField(30);
        urlField.setEditable(false);
        gbc.gridx = 1;
        panel.add(urlField, gbc);

        copyButton = new JButton("Kopieren");
        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(copyButton, gbc);

        overviewButton = new JButton("URL Übersicht");
        gbc.gridx = 1;
        gbc.gridy = 6;
        panel.add(overviewButton, gbc);

        generateButton.addActionListener(e -> {
            String enteredId = raplaIdField.getText().trim();
            
            if (enteredId.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Rapla-ID eingeben!", "Fehler", JOptionPane.ERROR_MESSAGE);
            } else {
                String generatedUrl = serverDomain + "/rapla/availability/" + enteredId;
                urlField.setText(generatedUrl);
                savedRaplaIDs.add(enteredId);
                System.out.println("Gespeicherte Rapla-IDs: " + String.join(", ", savedRaplaIDs));
                overviewDialog.updateRaplaID(savedRaplaIDs);
                saveIdsToPreferences();
                raplaIdField.setText("");
            }
        });

        copyButton.addActionListener(e -> {
            String url = urlField.getText();
            if (!url.isEmpty()) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(url), null);
                JOptionPane.showMessageDialog(panel, "URL wurde in die Zwischenablage kopiert!", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                urlField.setText("");
            } else {
                JOptionPane.showMessageDialog(panel, "Keine URL zum Kopieren vorhanden!", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        overviewButton.addActionListener(e -> {
        	saveIdsToPreferences();
            overviewDialog.updateRaplaID(savedRaplaIDs); // Aktualisiere die Übersicht mit den geladenen URLs
            JOptionPane.showMessageDialog(panel, overviewDialog.getComponent(), "URL Übersicht", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    // Methode zum Speichern der URLs in einer XML-Datei
//    private void saveUrlsToXml() {
//        try {
//            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//            Document doc = docBuilder.newDocument();
//            
//            // Wurzel-Element
//            Element rootElement = doc.createElement("url-states");
//            doc.appendChild(rootElement);
//
//            for (Map.Entry<String, String> entry : generatedUrls.entrySet()) {
//                Element urlElement = doc.createElement("url");
//                rootElement.appendChild(urlElement);
//
//                Element nameElement = doc.createElement("name");
//                nameElement.appendChild(doc.createTextNode(entry.getValue()));
//                urlElement.appendChild(nameElement);
//
//                Element linkElement = doc.createElement("link");
//                linkElement.appendChild(doc.createTextNode(entry.getKey()));
//                urlElement.appendChild(linkElement);
//
//                Element activeElement = doc.createElement("active");
//                activeElement.appendChild(doc.createTextNode("true"));
//                urlElement.appendChild(activeElement);
//            }
//
//            // Schreiben in die XML-Datei
//            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//            Transformer transformer = transformerFactory.newTransformer();
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            DOMSource source = new DOMSource(doc);
//            StreamResult result = new StreamResult(new File("url_states.xml"));
//            transformer.transform(source, result);
//            saveUrlsToPreferences();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Methode zum Laden der URLs aus einer XML-Datei
//    private void loadUrlsFromXml() {
//        try {
//            File xmlFile = new File("url_states.xml");
//            if (!xmlFile.exists()) return;
//
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            Document doc = dBuilder.parse(xmlFile);
//            doc.getDocumentElement().normalize();
//            
//            NodeList urlList = doc.getElementsByTagName("url");
//            for (int i = 0; i < urlList.getLength(); i++) {
//                Element urlElement = (Element) urlList.item(i);
//                
//                String name = urlElement.getElementsByTagName("name").item(0).getTextContent();
//                String link = urlElement.getElementsByTagName("link").item(0).getTextContent();
//                
//                // Get active state, defaulting to true if not specified
//                boolean isActive = true;
//                if (urlElement.getElementsByTagName("active").getLength() > 0) {
//                    isActive = Boolean.parseBoolean(
//                        urlElement.getElementsByTagName("active").item(0).getTextContent()
//                    );
//                }
//                
//                // Only add if the entry is not marked as inactive
//                if (isActive) {
//                    generatedUrls.put(link, name);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

   
    void saveIdsToPreferences() {
        try {
            // Vorher gespeicherte IDs aus den Preferences laden
            String oldIds = writablePreferences.getEntryAsString(AvailabilityPlugin.ID, "");
            List<String> idList = new ArrayList<>();
            System.out.println("Gespeicherte Rapla-IDs (Methode save " + String.join(", ", savedRaplaIDs));
            // Falls bereits IDs vorhanden sind, zur Liste hinzufügen
            if (!oldIds.isEmpty()) {
                idList.addAll(Arrays.asList(oldIds.split(",")));
            }

            // Neue IDs hinzufügen, falls sie noch nicht existieren
            for (String generatedId : savedRaplaIDs) {
                if (!idList.contains(generatedId)) {
                    idList.add(generatedId);
                }
            }

            // Entfernte IDs bereinigen
            idList.retainAll(savedRaplaIDs);

            // Speichern der aktualisierten Liste
            writablePreferences.putEntry(AvailabilityPlugin.ID, String.join(",", idList));
            writableFacade.store(writablePreferences); // Änderungen speichern
        } catch (RaplaException e) {
            e.printStackTrace();
        }
    }

    
    void loadIdsFromPreferences() {
        // Vorher gespeicherte IDs aus den Preferences abrufen
		String oldIds = writablePreferences.getEntryAsString(AvailabilityPlugin.ID, "");

		// Wenn keine IDs gespeichert wurden, eine leere Liste zurückgeben
		if (oldIds.isEmpty()) {
		    savedRaplaIDs = new ArrayList<>();
		} else {
		    // Gespeicherte IDs in eine Liste umwandeln
		    savedRaplaIDs = new ArrayList<>(Arrays.asList(oldIds.split(",")));
		}
    }


    

    @Override
    public JComponent getComponent() {
        return panel;
    }
}