package com.mygdx.wartowers.database;


import com.mygdx.wartowers.sprites.BattleResult;
import com.mygdx.wartowers.sprites.PlayerData;
import com.mygdx.wartowers.states.MenuState;

public interface FireStoreInterface {
        String TAG = "FireBaseInterface";

        void updateBattleResult(BattleResult battleResult);

        void getTopPlayers(MenuState.OnPlayersFetchedListener listener);

        void getPlayerStats(String playerName, final PlayerData playerData);

        void addPlayer(PlayerData playerData);

        public void SomeFunction();
}
