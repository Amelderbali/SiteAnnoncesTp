package com.lumina.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnonceDAOImpl implements AnnonceDAO {


    @Override
    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS annonces (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    titre TEXT NOT NULL,
                    lien TEXT NOT NULL,
                    image TEXT,
                    site TEXT NOT NULL,
                    date_recuperation TEXT NOT NULL
                );
                """;

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la table : " + e.getMessage());
        }
    }


    @Override
    public void insertAnnonce(Annonce annonce) {
        String sql = """
                INSERT INTO annonces (titre, lien, image, site, date_recuperation)
                VALUES (?, ?, ?, ?, ?);
                """;

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, annonce.getTitre());
            pstmt.setString(2, annonce.getLien());
            pstmt.setString(3, annonce.getImage());
            pstmt.setString(4, annonce.getSite());
            pstmt.setString(5, annonce.getDateRecuperation());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'annonce : " + e.getMessage());
        }
    }

    /**
     * Insère une liste d'annonces dans la table "annonces".
     *
     * @param annonces La liste d'annonces à insérer.
     */
    @Override
    public void insertAnnonces(List<Annonce> annonces) {
        String sql = """
                INSERT OR IGNORE INTO annonces (titre, lien, image, site, date_recuperation)
                VALUES (?, ?, ?, ?, ?);
                """;

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Annonce annonce : annonces) {
                pstmt.setString(1, annonce.getTitre());
                pstmt.setString(2, annonce.getLien());
                pstmt.setString(3, annonce.getImage());
                pstmt.setString(4, annonce.getSite());
                pstmt.setString(5, annonce.getDateRecuperation());
                pstmt.addBatch();
            }
            pstmt.executeBatch();

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion des annonces : " + e.getMessage());
        }
    }

    /**
     * Récupère toutes les annonces de la table "annonces".
     *
     * @return Une liste contenant toutes les annonces.
     */
    @Override
    public List<Annonce> getAllAnnonces() {
        List<Annonce> annonces = new ArrayList<>();
        String sql = "SELECT * FROM annonces";

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Annonce annonce = new Annonce(rs.getInt("id"), rs.getString("titre"), rs.getString("lien"), rs.getString("image"), rs.getString("site"), rs.getString("date_recuperation"));
                annonce.setId(rs.getInt("id"));
                annonce.setTitre(rs.getString("titre"));
                annonce.setLien(rs.getString("lien"));
                annonce.setImage(rs.getString("image"));
                annonce.setSite(rs.getString("site"));
                annonce.setDateRecuperation(rs.getString("date_recuperation"));
                annonces.add(annonce);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des annonces : " + e.getMessage());
        }
        return annonces;
    }

    /**
     * Supprime une annonce de la table "annonces" en fonction de son identifiant.
     *
     * @param id L'identifiant de l'annonce à supprimer.
     */
    @Override
    public void deleteAnnonceById(int id) {
        String sql = "DELETE FROM annonces WHERE id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'annonce : " + e.getMessage());
        }
    }

    /**
     * Récupère une annonce en fonction de son identifiant.
     *
     * @param id L'identifiant de l'annonce.
     * @return L'annonce correspondante ou null si elle n'existe pas.
     */

    @Override
    public Annonce getAnnonceById(int id) {
        String sql = "SELECT * FROM annonces WHERE id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Assurez-vous que le constructeur ou les setters d'Annonce sont corrects
                return new Annonce(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("lien"),
                        rs.getString("image"),
                        rs.getString("site"),
                        rs.getString("date_recuperation")
                );
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'annonce par ID : " + e.getMessage());
        }
        return null;
    }

}
