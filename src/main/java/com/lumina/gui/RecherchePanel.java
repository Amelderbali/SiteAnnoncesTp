package com.lumina.gui;

import com.lumina.data.Recherche;
import com.lumina.data.RechercheDAO;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RecherchePanel extends JPanel {
    private final JTextField keywordsField = new JTextField(20);
    private final JComboBox<String> sitesDropdown = new JComboBox<>(new String[]{"Leboncoin", "Site2", "Site3"});
    private final JSpinner refreshSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 60, 1)); // Refresh in minutes
    private final RechercheDAO rechercheDAO;

    public RecherchePanel(RechercheDAO rechercheDAO) {
        this.rechercheDAO = rechercheDAO;
        setLayout(new GridLayout(4, 2, 10, 10));

        // Add components
        add(new JLabel("Mots-clés :"));
        add(keywordsField);

        add(new JLabel("Sites à analyser :"));
        add(sitesDropdown);

        add(new JLabel("Fréquence de rafraîchissement (minutes) :"));
        add(refreshSpinner);

        JButton saveButton = new JButton("Sauvegarder");
        saveButton.addActionListener(e -> saveRecherche());
        add(saveButton);
    }

    private void saveRecherche() {
        String keywords = keywordsField.getText();
        String selectedSite = (String) sitesDropdown.getSelectedItem();
        int refreshFrequency = (int) refreshSpinner.getValue();

        Recherche recherche = new Recherche();
        recherche.setMotsCles(keywords);
        recherche.setSites(List.of(selectedSite));
        recherche.setFrequence(refreshFrequency);

        rechercheDAO.saveRecherche(recherche);
        JOptionPane.showMessageDialog(this, "Configuration sauvegardée avec succès !");
    }
}
