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
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rapla.entities.configuration.Preferences;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AdminMenuEntryDialog extends RaplaGUIComponent implements RaplaWidget {
    private JPanel panel;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField raplaIdField;
    private JButton generateButton;
    private JTextField urlField;
    private JButton copyButton;
    private JButton overviewButton;
    private Map<String, String> generatedUrls = new HashMap<>();
    private UrlOverviewDialog overviewDialog;
    private  RaplaFacade writableFacade;
    private Preferences writablePreferences;
    private String serverDomain;

    @Inject
    public AdminMenuEntryDialog(ClientFacade facade, RaplaResources i18n, RaplaLocale raplaLocale, Logger logger, DialogUiFactoryInterface dialogUiFactory) throws RaplaInitializationException {
        super(facade, i18n, raplaLocale, logger);
        writableFacade = facade.getRaplaFacade();
        overviewDialog = new UrlOverviewDialog(generatedUrls, this);
        initUI();
        saveUrlsToPreferences();
        loadUrlsFromXml(); // Load URLs from XML
        
    }


    private void initUI() {
        // Add a method to clean up deleted entries on startup
    	try {
			writablePreferences = writableFacade.edit(writableFacade.getSystemPreferences());
		} catch (RaplaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	serverDomain = writablePreferences.getEntryAsString(AvailabilityPlugin.SERVER_DOMAIN,"");
    	 
    	overviewDialog.cleanUpDeletedEntries();
        
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel firstNameLabel = new JLabel("Vorname eingeben:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(firstNameLabel, gbc);

        firstNameField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(firstNameField, gbc);

        JLabel lastNameLabel = new JLabel("Nachname eingeben:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lastNameLabel, gbc);

        lastNameField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(lastNameField, gbc);

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
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String enteredId = raplaIdField.getText().trim();
            
            if (firstName.isEmpty() || lastName.isEmpty() || enteredId.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Bitte Vor- und Nachnamen sowie Rapla-ID eingeben!", "Fehler", JOptionPane.ERROR_MESSAGE);
            } else {
                String generatedUrl = serverDomain + "/rapla/availability/" + enteredId;
                //String generatedUrl = "http://dhbw-heidenheim/Dozenten/rapla/availability/" + enteredId;
                urlField.setText(generatedUrl);
                
                // Store full name as "Lastname, Firstname" for alphabetical sorting
                String fullName = lastName + ", " + firstName;
                generatedUrls.put(generatedUrl, fullName);
                
                overviewDialog.updateUrls(generatedUrls);
                saveUrlsToPreferences();
                saveUrlsToXml(); // URLs in XML speichern
                firstNameField.setText("");
                lastNameField.setText("");
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
        	saveUrlsToPreferences();
            overviewDialog.updateUrls(generatedUrls); // Aktualisiere die Übersicht mit den geladenen URLs
            JOptionPane.showMessageDialog(panel, overviewDialog.getComponent(), "URL Übersicht", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    // Methode zum Speichern der URLs in einer XML-Datei
    private void saveUrlsToXml() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            
            // Wurzel-Element
            Element rootElement = doc.createElement("url-states");
            doc.appendChild(rootElement);

            for (Map.Entry<String, String> entry : generatedUrls.entrySet()) {
                Element urlElement = doc.createElement("url");
                rootElement.appendChild(urlElement);

                Element nameElement = doc.createElement("name");
                nameElement.appendChild(doc.createTextNode(entry.getValue()));
                urlElement.appendChild(nameElement);

                Element linkElement = doc.createElement("link");
                linkElement.appendChild(doc.createTextNode(entry.getKey()));
                urlElement.appendChild(linkElement);

                Element activeElement = doc.createElement("active");
                activeElement.appendChild(doc.createTextNode("true"));
                urlElement.appendChild(activeElement);
            }

            // Schreiben in die XML-Datei
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("url_states.xml"));
            transformer.transform(source, result);
            saveUrlsToPreferences();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Methode zum Laden der URLs aus einer XML-Datei
    private void loadUrlsFromXml() {
        try {
            File xmlFile = new File("url_states.xml");
            if (!xmlFile.exists()) return;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            
            NodeList urlList = doc.getElementsByTagName("url");
            for (int i = 0; i < urlList.getLength(); i++) {
                Element urlElement = (Element) urlList.item(i);
                
                String name = urlElement.getElementsByTagName("name").item(0).getTextContent();
                String link = urlElement.getElementsByTagName("link").item(0).getTextContent();
                
                // Get active state, defaulting to true if not specified
                boolean isActive = true;
                if (urlElement.getElementsByTagName("active").getLength() > 0) {
                    isActive = Boolean.parseBoolean(
                        urlElement.getElementsByTagName("active").item(0).getTextContent()
                    );
                }
                
                // Only add if the entry is not marked as inactive
                if (isActive) {
                    generatedUrls.put(link, name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
    void saveUrlsToPreferences() {
		try {
	        String oldUrls = writablePreferences.getEntryAsString(AvailabilityPlugin.URLS, "");
	        List<String> urlList = new ArrayList<>(Arrays.asList(oldUrls.split(",")));

	        // Neue URLs hinzufügen
	        for (Map.Entry<String, String> entry : generatedUrls.entrySet()) {
	            String generatedUrl = entry.getKey();
	            String fullName = entry.getValue();

	            // Überprüfen, ob die URL bereits vorhanden ist
	            if (!urlList.contains(generatedUrl)) {
	                urlList.add(generatedUrl);
	            }
	        }
	        Set<String> previousUrls = new HashSet<>(urlList);
	       
	        // Überprüfen, ob URLs entfernt wurden
	        previousUrls.removeAll(generatedUrls.keySet());
	        if (!previousUrls.isEmpty()) {
	            // Entfernte URLs aus den Preferences löschen
	            urlList.removeAll(previousUrls);
	        }

	        // Speichern der aktualisierten Liste als String
	        writablePreferences.putEntry(AvailabilityPlugin.URLS, String.join(",", urlList));
	        writableFacade.store(writablePreferences); // Änderungen speichern
		} catch (RaplaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    }

    

    @Override
    public JComponent getComponent() {
        return panel;
    }
}