package org.rapla.plugin.availability.AdminMenuEntry;

import org.rapla.RaplaResources;
import org.rapla.client.RaplaWidget;
import org.rapla.client.swing.RaplaGUIComponent;
import org.rapla.client.dialog.DialogUiFactoryInterface;
import org.rapla.facade.client.ClientFacade;
import org.rapla.framework.RaplaInitializationException;
import org.rapla.framework.RaplaLocale;
import org.rapla.logger.Logger;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
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
    private JTextField nameField;
    private JTextField raplaIdField;
    private JButton generateButton;
    private JTextField urlField;
    private JButton copyButton;
    private JButton overviewButton;
    private Map<String, String> generatedUrls = new HashMap<>();
    private UrlOverviewDialog overviewDialog;
    
    @Inject
    public AdminMenuEntryDialog(ClientFacade facade, RaplaResources i18n, RaplaLocale raplaLocale, Logger logger, DialogUiFactoryInterface dialogUiFactory) throws RaplaInitializationException {
        super(facade, i18n, raplaLocale, logger);
        overviewDialog = new UrlOverviewDialog(generatedUrls);
        loadUrlsFromXml(); // URLs aus XML laden
        initUI();
    }
    
    private void initUI() {
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel nameLabel = new JLabel("Dozenten-Name eingeben:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        
        nameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        JLabel idLabel = new JLabel("Rapla-ID eingeben:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(idLabel, gbc);
        
        raplaIdField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(raplaIdField, gbc);
        
        generateButton = new JButton("Webseite generieren");
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(generateButton, gbc);
        
        JLabel urlLabel = new JLabel("Generierte URL:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(urlLabel, gbc);
        
        urlField = new JTextField(30);
        urlField.setEditable(false);
        gbc.gridx = 1;
        panel.add(urlField, gbc);
        
        copyButton = new JButton("Kopieren");
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(copyButton, gbc);
        
        overviewButton = new JButton("URL Übersicht");
        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(overviewButton, gbc);
        
        generateButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String enteredId = raplaIdField.getText().trim();
            
            if (name.isEmpty() || enteredId.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Bitte sowohl den Namen als auch die Rapla-ID eingeben!", "Fehler", JOptionPane.ERROR_MESSAGE);
            } else {
                String generatedUrl = "http://localhost:8051/rapla/availability/" + enteredId;
                urlField.setText(generatedUrl);
                generatedUrls.put(generatedUrl, name);
                overviewDialog.updateUrls(generatedUrls);
                saveUrlsToXml(); // URLs in XML speichern
                nameField.setText("");
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
            Element rootElement = doc.createElement("urls");
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
            }

            // Schreiben in die XML-Datei
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("urls.xml"));
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Methode zum Laden der URLs aus einer XML-Datei
    private void loadUrlsFromXml() {
        try {
            File xmlFile = new File("urls.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList urlList = doc.getElementsByTagName("url");

            for (int i = 0; i < urlList.getLength(); i++) {
                Element urlElement = (Element) urlList.item(i);
                String name = urlElement.getElementsByTagName("name").item(0).getTextContent();
                String link = urlElement.getElementsByTagName("link").item(0).getTextContent();
                generatedUrls.put(link, name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Methode zum Laden der URLs in einer anderen Klasse
    public static Map<String, String> loadUrlsFromXmlinOtherClass() {
        Map<String, String> generatedUrls = new HashMap<>();

        try {
            File xmlFile = new File("urls.xml");
            if (!xmlFile.exists()) {
                System.out.println("XML-Datei nicht gefunden!");
                return generatedUrls;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList urlList = doc.getElementsByTagName("url");

            for (int i = 0; i < urlList.getLength(); i++) {
                Element urlElement = (Element) urlList.item(i);
                String name = urlElement.getElementsByTagName("name").item(0).getTextContent();
                String link = urlElement.getElementsByTagName("link").item(0).getTextContent();
                generatedUrls.put(link, name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedUrls;
    }


    @Override
    public JComponent getComponent() {
        return panel;
    }
}
