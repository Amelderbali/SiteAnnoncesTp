package com.lumina.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteConnection {

    private static final String DB_URL = "jdbc:sqlite:annonces.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            String createTableSQL = """
                                CREATE TABLE IF NOT EXISTS annonces (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    titre TEXT NOT NULL,
                                    lien TEXT NOT NULL,
                                    image TEXT,
                                    site TEXT NOT NULL,
                                    date_recuperation TEXT NOT NULL
                                );

                    CREATE TABLE IF NOT EXISTS recherches (
                        id INTEGER PRIMARY KEY,
                        mots_cles TEXT NOT NULL,
                        sites TEXT NOT NULL,
                        frequence INTEGER NOT NULL
                    );
                    
                    
                    """;
            stmt.execute(createTableSQL);
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
}
