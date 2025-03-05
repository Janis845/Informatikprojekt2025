package org.rapla.plugin.availability.AdminMenuEntry;

import org.rapla.client.RaplaWidget;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UrlOverviewDialog implements RaplaWidget {
    private JPanel panel;
    private JPanel urlPanel;
    private Map<String, UrlEntry> urlEntries;
    private JButton closeButton;

    // Enhanced UrlEntry class to track deletion state
    private static class UrlEntry {
        String name;
        boolean isActive;
        boolean isDeleted;  // New field to explicitly track deletion

        UrlEntry(String name, boolean isActive) {
            this.name = name;
            this.isActive = isActive;
            this.isDeleted = false;
        }
    }

    public UrlOverviewDialog(Map<String, String> urlNameMap) {
        // Initialize urlEntries with loaded URLs from XML
        this.urlEntries = new HashMap<>();
        loadUrlStatesFromXml();

        // Add any new URLs that aren't already in the map
        urlNameMap.forEach((url, name) -> {
            if (!urlEntries.containsKey(url)) {
                urlEntries.put(url, new UrlEntry(name, true));
            }
        });

        saveUrlStatesToXml(); // Persist the updated state
        initUI();
    }

    private void initUI() {
        panel = new JPanel(new BorderLayout());

        JLabel headerLabel = new JLabel("Alle erzeugten Links:");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(headerLabel, BorderLayout.NORTH);

        urlPanel = new JPanel();
        urlPanel.setLayout(new BoxLayout(urlPanel, BoxLayout.Y_AXIS));

        refreshUrlList();

        JScrollPane scrollPane = new JScrollPane(urlPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        closeButton = new JButton("Schließen");
        closeButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(panel);
            if (window != null) {
                window.dispose();
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveUrlStatesToXml() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            
            Element rootElement = doc.createElement("url-states");
            doc.appendChild(rootElement);

            // Only save non-deleted entries
            for (Map.Entry<String, UrlEntry> entry : urlEntries.entrySet()) {
                if (!entry.getValue().isDeleted) {
                    Element urlElement = doc.createElement("url");
                    
                    Element linkElement = doc.createElement("link");
                    linkElement.appendChild(doc.createTextNode(entry.getKey()));
                    urlElement.appendChild(linkElement);
                    
                    Element nameElement = doc.createElement("name");
                    nameElement.appendChild(doc.createTextNode(entry.getValue().name));
                    urlElement.appendChild(nameElement);
                    
                    Element activeElement = doc.createElement("active");
                    activeElement.appendChild(doc.createTextNode(String.valueOf(entry.getValue().isActive)));
                    urlElement.appendChild(activeElement);
                    
                    rootElement.appendChild(urlElement);
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("url_states.xml"));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUrlStatesFromXml() {
        try {
            File xmlFile = new File("url_states.xml");
            if (!xmlFile.exists()) return;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            org.w3c.dom.NodeList urlList = doc.getElementsByTagName("url");
            
            // Clear existing entries before loading from XML
            urlEntries.clear();

            for (int i = 0; i < urlList.getLength(); i++) {
                Element urlElement = (Element) urlList.item(i);
                
                String link = urlElement.getElementsByTagName("link").item(0).getTextContent();
                String name = urlElement.getElementsByTagName("name").item(0).getTextContent();
                boolean isActive = Boolean.parseBoolean(
                    urlElement.getElementsByTagName("active").item(0).getTextContent()
                );

                UrlEntry entry = new UrlEntry(name, isActive);
                entry.isDeleted = false;  // Explicitly set to false when loading
                urlEntries.put(link, entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshUrlList() {
        urlPanel.removeAll();
        
        // Only display non-deleted entries
        urlEntries.entrySet().stream()
            .filter(entry -> !entry.getValue().isDeleted)
            .forEach(entry -> {
                String url = entry.getKey();
                UrlEntry urlEntry = entry.getValue();

                JPanel urlEntryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                urlEntryPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                JLabel nameLabel = new JLabel("Dozent: " + urlEntry.name + " ");
                JTextField urlField = new JTextField(url, 30);
                urlField.setEditable(false);

                JCheckBox toggleButton = new JCheckBox("Aktiv", urlEntry.isActive);
                toggleButton.addActionListener(e -> {
                    urlEntry.isActive = toggleButton.isSelected();
                    saveUrlStatesToXml(); // Save state immediately
                });

                JButton copyButton = new JButton("Kopieren");
                copyButton.addActionListener(e -> {
                    Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(url), null);
                    JOptionPane.showMessageDialog(panel, 
                        "URL wurde in die Zwischenablage kopiert!", 
                        "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                });

                JButton deleteButton = new JButton("Löschen");
                deleteButton.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(panel, 
                        "Möchtest du diesen Eintrag wirklich löschen?", 
                        "Löschen bestätigen", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        urlEntry.isDeleted = true;  // Mark as deleted instead of removing
                        saveUrlStatesToXml(); // Persist deletion
                        refreshUrlList();
                        panel.revalidate();
                        panel.repaint();
                    }
                });

                urlEntryPanel.add(nameLabel);
                urlEntryPanel.add(urlField);
                urlEntryPanel.add(toggleButton);
                urlEntryPanel.add(copyButton);
                urlEntryPanel.add(deleteButton);
                urlPanel.add(urlEntryPanel);
            });

        panel.revalidate();
        panel.repaint();
    }

    public void updateUrls(Map<String, String> newUrls) {
        // Add new URLs only if they don't already exist and are not deleted
        newUrls.forEach((url, name) -> {
            if (!urlEntries.containsKey(url)) {
                UrlEntry newEntry = new UrlEntry(name, true);
                newEntry.isDeleted = false;
                urlEntries.put(url, newEntry);
            }
        });
        saveUrlStatesToXml();
        refreshUrlList();
    }

    // New method to clean up deleted entries
    public void cleanUpDeletedEntries() {
        urlEntries.entrySet().removeIf(entry -> entry.getValue().isDeleted);
        saveUrlStatesToXml();
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }
}