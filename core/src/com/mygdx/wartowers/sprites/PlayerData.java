package com.mygdx.wartowers.sprites;

public class PlayerData {
    private String name;
    private int wins;
    private int gamesPlayed;

    public PlayerData() {
        // Default constructor required for Firebase
    }

    public PlayerData(String name, int wins, int gamesPlayed) {
        this.name = name;
        this.wins = wins;
        this.gamesPlayed = gamesPlayed;
    }

    public void copyFrom(PlayerData otherPlayer) {
        this.name = otherPlayer.getName();
        this.wins = otherPlayer.getWins();
        this.gamesPlayed = otherPlayer.getGamesPlayed();
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public void incrementWins() {
        wins++;
    }

    public void incrementGamesPlayed() {
        gamesPlayed++;
    }

    public float getWinPercentage() {
        if (gamesPlayed == 0) {
            return 0;
        }
        return (float) wins / gamesPlayed * 100;
    }
}
