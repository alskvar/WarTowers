package com.mygdx.wartowers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper implements DatabaseInterface {
    private Connection connect() {

        // SQLite connection st
        String url = "jdbc:sqlite:score.db";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    @Override
    public void addScore(String playerName, int score) {
        String sql = "INSERT INTO scores(player_name, score) VALUES(?,?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Didnt worked");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public ScoreEntry getScore(String name) {
        String sql = "SELECT id, player_name, score FROM scores WHERE player_name = ? ORDER BY score DESC";
        ScoreEntry score = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try (Connection conn = this.connect()) {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                score = new ScoreEntry();
                score.setId(rs.getInt("id"));
                score.setPlayerName(rs.getString("player_name"));
                score.setScore(rs.getInt("score"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving score for player: " + name);
            System.out.println(e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources");
                System.out.println(e.getMessage());
            }
        }
        return score;
    }

    public void createTableIfNotExist() {
        String sql = "CREATE TABLE IF NOT EXISTS scores (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player_name TEXT NOT NULL," +
                "score INTEGER)";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
