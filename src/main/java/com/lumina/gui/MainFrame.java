package com.lumina.gui;

import com.lumina.data.*;
import com.lumina.utils.FetchAndStoreJob;
import com.lumina.wrappers.LeboncoinWrapper;
import com.lumina.wrappers.SiteWrapper;
import com.lumina.wrappers.TopAnnoncesWrapper;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainFrame extends JFrame {
    private JTextField keywordsField;
    private JCheckBox siteLeboncoin,topannonces, siteOther;
    private JSpinner refreshSpinner;
    private DefaultTableModel tableModel;
    private JTable annoncesTable;
    private JProgressBar loaderLabel;
    private JButton stopButton;
    private ScheduledExecutorService batchExecutor;
    private JLabel statusLabel;

    public MainFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the size of the window
        setSize(1000, 700); // Larger window size

        // Center the window on the screen
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Apply Nimbus Look and Feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load configuration
        loadPreviousSearchConfig();

        // Create and add components
        add(createSearchPanel(), BorderLayout.NORTH);
        add(createAdsTablePanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadPreviousSearchConfig() {
        RechercheDAOImpl rechercheDAO = new RechercheDAOImpl();
        rechercheDAO.getRecherche().ifPresent(recherche -> {
            // Restore keywords
            keywordsField.setText(recherche.getMotsCles());

            // Restore selected sites
            siteLeboncoin.setSelected(recherche.getSites().contains("Leboncoin"));
            topannonces.setSelected(recherche.getSites().contains("topannonces"));
            siteOther.setSelected(recherche.getSites().contains("Other"));

            // Restore frequency
            refreshSpinner.setValue(recherche.getFrequence());
        });
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Paramètres de recherche"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Keywords field
        JLabel keywordsLabel = new JLabel("Mots-clés :");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(keywordsLabel, gbc);

        keywordsField = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(keywordsField, gbc);

        // Sites checkboxes
        JLabel sitesLabel = new JLabel("Sites à analyser :");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(sitesLabel, gbc);

        JPanel sitesPanel = new JPanel();
        siteLeboncoin = new JCheckBox("Leboncoin");
        topannonces = new JCheckBox("topannonces");
        siteOther = new JCheckBox("Other");
        sitesPanel.add(siteLeboncoin);
        sitesPanel.add(topannonces);
        sitesPanel.add(siteOther);
        gbc.gridx = 1;
        panel.add(sitesPanel, gbc);

        // Refresh spinner
        JLabel refreshLabel = new JLabel("Fréquence (minutes) :");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(refreshLabel, gbc);

        refreshSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 60, 1));
        gbc.gridx = 1;
        panel.add(refreshSpinner, gbc);

        // Add a small progress spinner
        loaderLabel = new JProgressBar();
        loaderLabel.setIndeterminate(true);
        loaderLabel.setVisible(false); // Initially hidden
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(loaderLabel, gbc);

        keywordsField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                clearError();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                clearError();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                clearError();
            }
        });

        siteLeboncoin.addActionListener(e -> clearError());
        topannonces.addActionListener(e -> clearError());
        siteOther.addActionListener(e -> clearError());

        return panel;
    }

    private JPanel createAdsTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Annonces récupérées"));

        // Table model with a checkbox column
        tableModel = new DefaultTableModel(new Object[]{"", "Image", "Titre", "Lien", "Date", "Site", "ID"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class; // Checkbox for selection
                if (columnIndex == 1) return ImageIcon.class; // ImageIcon for the second column
                if (columnIndex == 6) return Integer.class;   // Integer for the hidden ID column
                return String.class;                         // String for other columns
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only the checkbox column is editable
            }
        };

        // Table setup
        annoncesTable = new JTable(tableModel);
        annoncesTable.setFillsViewportHeight(true);
        annoncesTable.setRowHeight(100); // Adjust row height for larger images

        // Set preferred widths for columns
        annoncesTable.getColumnModel().getColumn(0).setPreferredWidth(5); // Checkbox column
        annoncesTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Image column

        // Add custom renderer for image column to scale content properly
        annoncesTable.getColumnModel().getColumn(1).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            if (value instanceof ImageIcon) {
                ImageIcon imageIcon = (ImageIcon) value;
                JLabel label = new JLabel(imageIcon);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                return label;
            }
            return new JLabel(); // Fallback if value is not an ImageIcon
        });

        // Add MouseListener for double-click to open link
        annoncesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double-click event
                    int row = annoncesTable.getSelectedRow();
                    if (row != -1) {
                        String url = (String) tableModel.getValueAt(row, 3); // Get the URL column
                        openUrlInBrowser(url);
                    }
                }
            }
        });

        // Hide the ID column
        annoncesTable.getColumnModel().getColumn(6).setMinWidth(0);
        annoncesTable.getColumnModel().getColumn(6).setMaxWidth(0);
        annoncesTable.getColumnModel().getColumn(6).setPreferredWidth(0);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(annoncesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }


    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Align buttons to the left
        JButton saveButton = new JButton("Sauvegarder et Lancer");
        saveButton.setBackground(new Color(76, 175, 80)); // Green color for importance
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.addActionListener(e -> saveAndStartBatch());
        buttonPanel.add(saveButton);

        JButton deleteButton = new JButton("Supprimer sélection");
        deleteButton.setBackground(new Color(244, 67, 54)); // Red color for delete action
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.addActionListener(e -> deleteSelectedAds());
        buttonPanel.add(deleteButton);

        stopButton = new JButton("Arrêter le Batch");
        stopButton.setBackground(new Color(33, 150, 243)); // Blue color for batch stop
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);
        stopButton.setFont(new Font("Arial", Font.BOLD, 14));
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopBatch());
        buttonPanel.add(stopButton);

        panel.add(buttonPanel, BorderLayout.WEST);

        // Status Label
        statusLabel = new JLabel("Prêt", SwingConstants.RIGHT); // Default message
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Add padding
        panel.add(statusLabel, BorderLayout.EAST);

        return panel;
    }


    private void saveAndStartBatch() {
        String keywords = keywordsField.getText().trim();
        ArrayList<String> selectedSites = new ArrayList<>();
        if (siteLeboncoin.isSelected()) selectedSites.add("Leboncoin");
        if (topannonces.isSelected()) selectedSites.add("topannonces");
        if (siteOther.isSelected()) selectedSites.add("Other");
        int refreshFrequency = (int) refreshSpinner.getValue();

        // Validation
        if (keywords.isEmpty()) {
            showError("Veuillez saisir au moins un mot-clé pour la recherche.");
            return;
        }

        if (selectedSites.isEmpty()) {
            showError("Veuillez sélectionner au moins un site à analyser.");
            return;
        }

        // Save configuration to the database
        Recherche recherche = new Recherche();
        recherche.setMotsCles(keywords);
        recherche.setSites(selectedSites);
        recherche.setFrequence(refreshFrequency);

        RechercheDAOImpl rechercheDAO = new RechercheDAOImpl();
        rechercheDAO.saveRecherche(recherche);

        // Clear any previous error
        clearError();

        // Simulate saving configuration
        System.out.println("Mots-clés : " + keywords);
        System.out.println("Sites : " + selectedSites);
        System.out.println("Fréquence : " + refreshFrequency);

        updateStatus("Configuration sauvegardée et batch lancé !");
        startBatch(keywords, selectedSites, refreshFrequency);
    }


    private void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(Color.RED); // Set error color
        });
    }

    private void clearError() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Prêt"); // Default message
            statusLabel.setForeground(Color.BLACK); // Reset color
        });
    }

    private void startBatch(String keywords, List<String> sites, int refreshFrequency) {
        if (batchExecutor != null && !batchExecutor.isShutdown()) {
            batchExecutor.shutdown();
        }

        batchExecutor = Executors.newScheduledThreadPool(sites.size()); // One thread per site
        loaderLabel.setVisible(true); // Show spinner
        stopButton.setEnabled(true);

        AnnonceDAO annonceDAO = new AnnonceDAOImpl();

        for (String site : sites) {
            SiteWrapper wrapper = getWrapperForSite(site);
            if (wrapper != null) {
                FetchAndStoreJob job = new FetchAndStoreJob(wrapper, annonceDAO, keywords, "", new FetchAndStoreJob.JobListener() {
                    @Override
                    public void onJobCompleted(List<Annonce> annonces) {
                        SwingUtilities.invokeLater(() -> refreshTable(annonces)); // Refresh table on the UI thread
                    }

                    @Override
                    public void onJobFailed(Exception e) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(MainFrame.this, "Erreur : " + e.getMessage(), "Batch Job Failed", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                });

                batchExecutor.scheduleAtFixedRate(job, 0, refreshFrequency, TimeUnit.MINUTES);
            }
        }
    }

    private SiteWrapper getWrapperForSite(String site) {
        switch (site.toLowerCase()) {
            case "leboncoin":
                return new LeboncoinWrapper();
            case "topannonces":
                return new TopAnnoncesWrapper();
            default:
                System.err.println("No wrapper found for site: " + site);
                return null;
        }
    }

    private void refreshTable(List<Annonce> annonces) {
        for (Annonce annonce : annonces) {
            try {
                // Load image from URL
                ImageIcon imageIcon = new ImageIcon(new ImageIcon(new URL(annonce.getImage()))
                        .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)); // Scale the image

                // Add row to the table
                tableModel.addRow(new Object[]{
                        false,                        // Unchecked checkbox
                        imageIcon,                    // Image as ImageIcon
                        annonce.getTitre(),
                        annonce.getLien(),
                        annonce.getDateRecuperation(),
                        annonce.getSite(),
                        annonce.getId()              // Store ID in hidden column
                });
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error loading image for URL: " + annonce.getImage());
                tableModel.addRow(new Object[]{
                        false,                        // Unchecked checkbox
                        null,                         // No image in case of error
                        annonce.getTitre(),
                        annonce.getLien(),
                        annonce.getDateRecuperation(),
                        annonce.getSite(),
                        annonce.getId()              // Store ID in hidden column
                });
            }
        }
    }


    private void stopBatch() {
        if (batchExecutor != null && !batchExecutor.isShutdown()) {
            batchExecutor.shutdown();
            loaderLabel.setVisible(false); // Hide spinner
            updateStatus("Batch arrêté !");
        }
        stopButton.setEnabled(false);
    }

    private void deleteSelectedAds() {
        AnnonceDAO annonceDAO = new AnnonceDAOImpl();

        // Collect IDs of selected rows
        List<Integer> idsToDelete = new ArrayList<>();
        for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
            boolean isSelected = (Boolean) tableModel.getValueAt(i, 0); // Check the checkbox
            if (isSelected) {
                int annonceId = (int) tableModel.getValueAt(i, 6); // Get ID from the hidden column
                idsToDelete.add(annonceId);
                tableModel.removeRow(i); // Remove from UI table
            }
        }

        // Delete selected IDs from the database
        for (int id : idsToDelete) {
            annonceDAO.deleteAnnonceById(id);
        }

        updateStatus("Annonces sélectionnées supprimées !");
    }


    private void openUrlInBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Impossible d'ouvrir l'URL.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(message));
    }
}
