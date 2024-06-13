package com.mygdx.wartowers;


import com.mygdx.wartowers.database.FireStoreInterface;
import com.mygdx.wartowers.sprites.BattleResult;
import com.mygdx.wartowers.sprites.PlayerData;
import com.mygdx.wartowers.states.MenuState;

public class DesktopBDInterfaceClass implements FireStoreInterface {


    @Override
    public void updateBattleResult(BattleResult battleResult) {

    }

    @Override
    public void getTopPlayers(MenuState.OnPlayersFetchedListener listener) {

    }

    @Override
    public void getPlayerStats(String playerName, PlayerData playerData) {

    }

    @Override
    public void addPlayer(PlayerData playerData) {

    }

    @Override
    public void SomeFunction() {

    }
}