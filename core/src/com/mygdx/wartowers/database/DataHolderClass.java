package com.mygdx.wartowers.database;

import com.badlogic.gdx.utils.Array;
import com.mygdx.wartowers.sprites.PlayerData;

public class DataHolderClass {
    Array<PlayerData> playerDataArray;

    public DataHolderClass()
    {
        playerDataArray = new Array<PlayerData>();
    }

    public void addPlayerData(PlayerData playerData) {
        playerDataArray.add(playerData);
    }

    public Array<PlayerData> getPlayerDataArray() {
        return playerDataArray;
    }

    public void clearPlayerDataArray() {
        playerDataArray.clear();
    }

    public void setPlayerDataArray(Array<PlayerData> playerDataArray) {
        this.playerDataArray = playerDataArray;
    }
}
