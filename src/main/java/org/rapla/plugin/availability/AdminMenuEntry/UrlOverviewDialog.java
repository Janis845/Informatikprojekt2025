package org.rapla.plugin.availability.AdminMenuEntry;

import org.rapla.client.RaplaWidget;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.util.List;

public class UrlOverviewDialog implements RaplaWidget {
    private JPanel panel;
    private JPanel urlPanel;

    public UrlOverviewDialog(List<String> urls) {
        initUI(urls);
    }

    private void initUI(List<String> urls) {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("Alle aktiven Links:");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(headerLabel, BorderLayout.NORTH);

        urlPanel = new JPanel();
        urlPanel.setLayout(new BoxLayout(urlPanel, BoxLayout.Y_AXIS));

        // URLs und Kopier-Buttons hinzufügen
        for (String url : urls) {
            JPanel urlEntryPanel = new JPanel();
            urlEntryPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

            JTextField urlField = new JTextField(url, 30);
            urlField.setEditable(false);

            JButton copyButton = new JButton("Kopieren");
            copyButton.addActionListener(e -> {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(url), null);
                JOptionPane.showMessageDialog(panel, "URL wurde in die Zwischenablage kopiert!", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
            });

            urlEntryPanel.add(urlField);
            urlEntryPanel.add(copyButton);

            urlPanel.add(urlEntryPanel);
        }

        panel.add(new JScrollPane(urlPanel), BorderLayout.CENTER);
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }
}
