package org.rapla.plugin.availability.AdminMenuEntry;

import org.rapla.client.RaplaWidget;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;

public class UrlOverviewDialog implements RaplaWidget {
    private JPanel panel;
    private JPanel urlPanel;
    private Map<String, String> urlNameMap;
    private Map<String, Boolean> urlStatusMap;

    public UrlOverviewDialog(Map<String, String> urlNameMap) {
        this.urlNameMap = new HashMap<>(urlNameMap);
        this.urlStatusMap = new HashMap<>();
        for (String url : urlNameMap.keySet()) {
            urlStatusMap.put(url, true); // Standardmäßig alle URLs aktiv
        }
        initUI();
    }

    private void initUI() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("Alle erzeugten Links:");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(headerLabel, BorderLayout.NORTH);

        urlPanel = new JPanel();
        urlPanel.setLayout(new BoxLayout(urlPanel, BoxLayout.Y_AXIS));

        // URLs mit Namen anzeigen
        for (Map.Entry<String, String> entry : urlNameMap.entrySet()) {
            String url = entry.getKey();
            String name = entry.getValue();

            JPanel urlEntryPanel = new JPanel();
            urlEntryPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            urlEntryPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JLabel nameLabel = new JLabel("Dozent: " + name + " ");
            JTextField urlField = new JTextField(url, 30);
            urlField.setEditable(false);

            JCheckBox toggleButton = new JCheckBox("Aktiv", true);
            toggleButton.addActionListener(e -> {
                urlStatusMap.put(url, toggleButton.isSelected());
                JOptionPane.showMessageDialog(panel, "URL für " + name + " " + (toggleButton.isSelected() ? "aktiviert" : "deaktiviert"), "Status geändert", JOptionPane.INFORMATION_MESSAGE);
            });

            JButton copyButton = new JButton("Kopieren");
            copyButton.addActionListener(e -> {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(url), null);
                JOptionPane.showMessageDialog(panel, "URL wurde in die Zwischenablage kopiert!", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
            });

            urlEntryPanel.add(nameLabel);
            urlEntryPanel.add(urlField);
            urlEntryPanel.add(toggleButton);
            urlEntryPanel.add(copyButton);

            urlPanel.add(urlEntryPanel);
        }

        JScrollPane scrollPane = new JScrollPane(urlPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    public boolean isUrlActive(String url) {
        return urlStatusMap.getOrDefault(url, false);
    }
}
