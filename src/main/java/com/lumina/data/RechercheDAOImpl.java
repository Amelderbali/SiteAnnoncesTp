package com.lumina.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class RechercheDAOImpl implements RechercheDAO {

    @Override
    public void saveRecherche(Recherche recherche) {
        String sql = """
            INSERT OR REPLACE INTO recherches (id, mots_cles, sites, frequence)
            VALUES (1, ?, ?, ?);
        """;

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, recherche.getMotsCles());
            pstmt.setString(2, String.join(",", recherche.getSites())); // Save sites as comma-separated values
            pstmt.setInt(3, recherche.getFrequence());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Recherche> getRecherche() {
        String sql = "SELECT * FROM recherches WHERE id = 1";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                Recherche recherche = new Recherche();
                recherche.setId(rs.getInt("id"));
                recherche.setMotsCles(rs.getString("mots_cles"));
                recherche.setSites(List.of(rs.getString("sites").split(","))); // Parse comma-separated values
                recherche.setFrequence(rs.getInt("frequence"));
                return Optional.of(recherche);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
