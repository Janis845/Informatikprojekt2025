package org.rapla.plugin.availability.AdminMenuEntry;

import org.rapla.client.RaplaWidget;
import org.rapla.entities.EntityNotFoundException;
import org.rapla.entities.configuration.Preferences;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.storage.ReferenceInfo;
import org.rapla.facade.RaplaFacade;
import org.rapla.framework.RaplaException;
import org.rapla.plugin.availability.AvailabilityPlugin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.*;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class UrlOverviewDialog implements RaplaWidget {
    private JPanel panel;
    private JTable urlTable;
    private DefaultTableModel tableModel;
    private Map<String, UrlEntry> urlEntries;
    private JButton closeButton;
    private AdminMenuEntryDialog entryDialog;
    private Preferences writablePreferences;
    private RaplaFacade writableFacade;
    private String serverDomain;
    private List<String> savedRaplaIDs = new ArrayList<>();

    // Enhanced UrlEntry class to track state
    private static class UrlEntry {
        String name; // "Lastname, Firstname"
        String url;
        boolean isActive;
        boolean isDeleted;

        UrlEntry(String url, String name, boolean isActive) {
            this.url = url;
            this.name = name;
            this.isActive = isActive;
            this.isDeleted = false;
        }
    }

    public UrlOverviewDialog(List<String> raplaIDs, AdminMenuEntryDialog entryDialog, RaplaFacade facade) {
        // Initialize urlEntries with loaded URLs from XML
        this.urlEntries = new HashMap<>();
        this.entryDialog = entryDialog;
        //loadUrlStatesFromXml();
        writableFacade = facade;
        try {
			writablePreferences = writableFacade.edit(writableFacade.getSystemPreferences());
			serverDomain = writablePreferences.getEntryAsString(AvailabilityPlugin.SERVER_DOMAIN,"");
			loadIdsFromPreferences();
			
		} catch (RaplaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Add any new URLs that aren't already in the map
        raplaIDs.forEach(id -> {
            // Hole den Namen aus einer anderen Quelle (z.B. eine Methode, die den Namen abruft)
        	String name = getNameById(id); // Abrufen des Namens anhand der ID
            if (!urlEntries.containsKey(id)) {
            	 String url =serverDomain + "/rapla/availability/" + id;
                urlEntries.put(id, new UrlEntry(url, name, true));
            }
        });

        //saveUrlStatesToXml(); // Persist the updated state
        initUI();
    }

    private void initUI() {
    	
  
        panel = new JPanel(new BorderLayout());

        // Create table model with custom column types
        tableModel = new DefaultTableModel(
            new String[]{"Dozent", "URL", "Aktiv"}, 0) {
            Class<?>[] columnTypes = new Class[]{
                String.class, String.class, Boolean.class
            };
            boolean[] columnEditables = new boolean[]{
                false, false, true
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        };

        // Create the table and populate it
        urlTable = new JTable(tableModel);
        
        // Add row sorter for alphabetical sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        urlTable.setRowSorter(sorter);
        
        // Komplexerer Comparator für die Sortierung nach Nachname und Vorname
        sorter.setComparator(0, (Object o1, Object o2) -> {
            if (o1 == null || o2 == null) {
                return 0;
            }
            
            String name1 = o1.toString();
            String name2 = o2.toString();
            
            // Stelle sicher, dass beide Namen das erwartete Format haben
            if (!name1.contains(",") || !name2.contains(",")) {
                return name1.compareTo(name2);
            }
            
            // Teile Namen in Nachname und Vorname
            String[] parts1 = name1.split(",");
            String[] parts2 = name2.split(",");
            
            String lastName1 = parts1[0].trim();
            String lastName2 = parts2[0].trim();
            
            // Vergleiche zunächst Nachnamen
            int lastNameComparison = lastName1.compareToIgnoreCase(lastName2);
            
            // Wenn Nachnamen gleich, dann nach Vornamen sortieren
            if (lastNameComparison == 0) {
                String firstName1 = parts1[1].trim();
                String firstName2 = parts2[1].trim();
                return firstName1.compareToIgnoreCase(firstName2);
            }
            
            return lastNameComparison;
        });
        
        // Optional: Standardmäßig nach Nachnamen sortieren
        sorter.setSortKeys(Collections.singletonList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        
        // Configure table appearance
        urlTable.setRowHeight(30);
        urlTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        urlTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        urlTable.getColumnModel().getColumn(2).setPreferredWidth(50);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(urlTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(700, 400));

        // Header label
        JLabel headerLabel = new JLabel("Alle erzeugten Links:");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel for copy and delete
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton copyButton = new JButton("Kopieren");
        copyButton.addActionListener(e -> {
        	int selectedRow = urlTable.getSelectedRow();
        	if (selectedRow != -1) {
        	    int modelRow = urlTable.convertRowIndexToModel(selectedRow); // Wandle um!
        	    String url = (String) tableModel.getValueAt(modelRow, 1); // Richtiger Index
        	    Toolkit.getDefaultToolkit().getSystemClipboard()
        	        .setContents(new StringSelection(url), null);
        	    JOptionPane.showMessageDialog(panel,  
        	        "URL wurde in die Zwischenablage kopiert!",  
        	        "Erfolg", JOptionPane.INFORMATION_MESSAGE);
        	

            } else {
                JOptionPane.showMessageDialog(panel, 
                    "Bitte wählen Sie zuerst eine URL aus.", 
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton deleteButton = new JButton("Löschen");
        deleteButton.addActionListener(e -> {
            int selectedRow = urlTable.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = urlTable.convertRowIndexToModel(selectedRow); // View → Model
                int confirm = JOptionPane.showConfirmDialog(panel,  
                    "Möchtest du diesen Eintrag wirklich löschen?",  
                    "Löschen bestätigen", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String url = (String) tableModel.getValueAt(modelRow, 1);
                    UrlEntry entry = urlEntries.get(extractIdFromUrl(url));
                    if (entry != null) {
                        entry.isDeleted = true;
                        //saveUrlStatesToXml();
                        saveIdsToPreferences();
                        refreshUrlList();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(panel,  
                    "Bitte wählen Sie zuerst eine URL aus.",  
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });


        buttonPanel.add(copyButton);
        buttonPanel.add(deleteButton);

        // Close button
        closeButton = new JButton("Schließen");
        closeButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(panel);
            if (window != null) {
                window.dispose();
            }
        });
        JPanel closeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closeButtonPanel.add(closeButton);

        // Add components to panel
        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.WEST);
        panel.add(closeButtonPanel, BorderLayout.SOUTH);

        // Populate initial data
        refreshUrlList();

        // Add listener to handle active/inactive toggle
        urlTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 2) { // Active column
                int row = e.getFirstRow();
                String url = (String) tableModel.getValueAt(row, 1);
                boolean isActive = (Boolean) tableModel.getValueAt(row, 2);
                
                UrlEntry entry = urlEntries.get(url);
                if (entry != null) {
                    entry.isActive = isActive;
                    //saveUrlStatesToXml();
                    saveIdsToPreferences();
                }
            }
        });
    }

    private void refreshUrlList() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Add non-deleted entries to the table
        urlEntries.entrySet().stream()
            .filter(entry -> !entry.getValue().isDeleted)
            .forEach(entry -> {
                UrlEntry urlEntry = entry.getValue();
                tableModel.addRow(new Object[]{
                    urlEntry.name, 
                    urlEntry.url, 
                    urlEntry.isActive
                });
            });
    }

//    private void saveUrlStatesToXml() {
//        try {
//            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//            Document doc = docBuilder.newDocument();
//            
//            // Root element
//            Element rootElement = doc.createElement("url-states");
//            doc.appendChild(rootElement);
//
//            // Save only non-deleted entries
//            for (Map.Entry<String, UrlEntry> entry : urlEntries.entrySet()) {
//                UrlEntry urlEntry = entry.getValue();
//                
//                // Only save non-deleted entries
//                if (!urlEntry.isDeleted) {
//                    Element urlElement = doc.createElement("url");
//                    rootElement.appendChild(urlElement);
//
//                    Element nameElement = doc.createElement("name");
//                    nameElement.appendChild(doc.createTextNode(urlEntry.name));
//                    urlElement.appendChild(nameElement);
//
//                    Element linkElement = doc.createElement("link");
//                    linkElement.appendChild(doc.createTextNode(urlEntry.url));
//                    urlElement.appendChild(linkElement);
//
//                    Element activeElement = doc.createElement("active");
//                    activeElement.appendChild(doc.createTextNode(String.valueOf(urlEntry.isActive)));
//                    urlElement.appendChild(activeElement);
//                }
//            }
//
//            // Write to XML file
//            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//            Transformer transformer = transformerFactory.newTransformer();
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            DOMSource source = new DOMSource(doc);
//            StreamResult result = new StreamResult(new File("url_states.xml"));
//            transformer.transform(source, result);
//			entryDialog.saveIdsToPreferences(); //speichert die aktuellen URLs aus generatedUrls ab und löscht damit auch URLs, falls sie gelöscht werden
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    

    	void saveIdsToPreferences() {
    	    try {
    	        // Vorherige IDs aus den Preferences laden
    	        String oldIds = writablePreferences.getEntryAsString(AvailabilityPlugin.ID, "");
    	        List<String> idList = new ArrayList<>();

    	        // Falls bereits IDs gespeichert sind, diese zur Liste hinzufügen
    	        if (!oldIds.isEmpty()) {
    	            idList.addAll(Arrays.asList(oldIds.split(",")));
    	        }

    	        // Kopie der alten Liste zur Änderungserkennung
    	        List<String> originalIdList = new ArrayList<>(idList);

    	        // Alle IDs durchgehen und löschen, wenn sie als "deleted" markiert sind
    	        Iterator<String> iterator = idList.iterator();
    	        while (iterator.hasNext()) {
    	            String id = iterator.next();
    	            if (urlEntries.containsKey(id) && urlEntries.get(id).isDeleted) {
    	                iterator.remove(); // Entferne gelöschte IDs aus der Liste
    	            }
    	        }

    	        // Durch urlEntries iterieren und neue IDs hinzufügen
    	        for (Map.Entry<String, UrlEntry> entry : urlEntries.entrySet()) {
    	            String generatedId = entry.getKey();
    	            UrlEntry urlEntry = entry.getValue();

    	            // Überspringe gelöschte IDs
    	            if (urlEntry.isDeleted) {
    	                continue;
    	            }

    	            String url = urlEntry.url;
    	            String idFromUrl = extractIdFromUrl(url);

    	            // Füge nur nicht vorhandene IDs hinzu
    	            if (!idList.contains(idFromUrl)) {
    	                idList.add(idFromUrl);
    	            }
    	        }

    	        // Speichern, falls sich die Liste geändert hat
    	        if (!originalIdList.equals(idList)) {
    	            writablePreferences.putEntry(AvailabilityPlugin.ID, String.join(",", idList));
    	            writableFacade.store(writablePreferences);
    	        }

    	    } catch (RaplaException e) {
    	        e.printStackTrace();
    	    }
    	}

    // Hilfsmethode zum Extrahieren der ID aus der URL
    private String extractIdFromUrl(String url) {
        // Beispiel: Extrahiere die ID, die nach dem letzten '/' kommt
        String[] parts = url.split("/");
        return parts[parts.length - 1];  // Die ID ist der letzte Teil des URL-Pfads
    }

   
    void loadIdsFromPreferences() throws RaplaException {
        // IDs als String aus den Preferences laden
		String storedIds = writablePreferences.getEntryAsString(AvailabilityPlugin.ID, "");
		
		// Sicherstellen, dass die urlEntries-Map nicht null ist
		if (urlEntries == null) {
		    urlEntries = new HashMap<>();
		} else {
		    urlEntries.clear(); // Falls sie bereits Daten enthält, leeren
		}

		// Falls gespeicherte IDs vorhanden sind, verarbeite sie
		if (!storedIds.isEmpty()) {
		    String[] idArray = storedIds.split(",");
		    for (String id : idArray) {
		        id = id.trim();
		        if (!id.isEmpty()) {
		            String url = serverDomain + "/rapla/availability/" + id;

		            String name = getNameById(id); // Name muss über ID ermittelt werden!!
		            
		            // Einfügen in die Map mit Standardwerten
		            urlEntries.put(id, new UrlEntry(url, name, true));
		        }
		    }
		}
    }


//    private void loadUrlStatesFromXml() {
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
//                UrlEntry entry = new UrlEntry(link, name, isActive);
//                urlEntries.put(link, entry);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public String getNameById(String id) {
        try {
			Allocatable allocatable = writableFacade.resolve(new ReferenceInfo<Allocatable>(id, Allocatable.class));
			 return allocatable.getName(null);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Unbekannt";
		}
       
    }
    
    public void updateRaplaID(List<String> newID) {
        // Add new URLs only if they don't already exist and are not deleted
        // Add any new URLs that aren't already in the map
        newID.forEach(id -> {
            String name = getNameById(id); // Abrufen des Namens anhand der ID
            if (!urlEntries.containsKey(id)) {
            	 String url = serverDomain + "/rapla/availability/" + id;
                urlEntries.put(id, new UrlEntry(url, name, true));
            }
        });

        saveIdsToPreferences();
        initUI();
    }
    
    

    public void cleanUpDeletedEntries() {
        urlEntries.entrySet().removeIf(entry -> entry.getValue().isDeleted);
        //saveUrlStatesToXml();
        saveIdsToPreferences();
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }
}