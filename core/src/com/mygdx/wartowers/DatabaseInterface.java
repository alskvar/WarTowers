package com.mygdx.wartowers;

public interface DatabaseInterface {
    void addScore(String playerName, int score);
    ScoreEntry getScore(String playerName);
}
