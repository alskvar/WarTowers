package com.mygdx.wartowers.database;

import com.mygdx.wartowers.ScoreEntry;

public interface DatabaseInterface {
    void addScore(String playerName, int score);
    ScoreEntry getScore(String playerName);
}
