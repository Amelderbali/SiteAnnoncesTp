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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

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
            e.printStackTrace();
        }
    }

    @Override
    public List<Annonce> getAllAnnonces() {
        List<Annonce> annonces = new ArrayList<>();
        String sql = "SELECT * FROM annonces";

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Annonce annonce = new Annonce();
                annonce.setId(rs.getInt("id"));
                annonce.setTitre(rs.getString("titre"));
                annonce.setLien(rs.getString("lien"));
                annonce.setImage(rs.getString("image"));
                annonce.setSite(rs.getString("site"));
                annonce.setDateRecuperation(rs.getString("date_recuperation"));
                annonces.add(annonce);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return annonces;
    }

    @Override
    public void deleteAnnonceById(int id) {
        String sql = "DELETE FROM annonces WHERE id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
