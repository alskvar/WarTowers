package com.mygdx.wartowers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mygdx.wartowers.database.DataHolderClass;
import com.mygdx.wartowers.database.FireStoreInterface;
import com.mygdx.wartowers.sprites.BattleResult;
import com.mygdx.wartowers.sprites.PlayerData;
import com.mygdx.wartowers.states.MenuState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AndroidDBInterfaceClass implements FireStoreInterface {
    private static final String TAG = "AndroidDBInterfaceClass";
    FirebaseFirestore db;
    final DocumentReference playersDocRef;

    public AndroidDBInterfaceClass() {
        db = FirebaseFirestore.getInstance();
        playersDocRef = db.collection("Players").document("PlayersData");
    }

    @Override
    public void getPlayerStats(String playerName, final PlayerData playerData) {
        playersDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    playerData.copyFrom(getDataFromDocumentSnapshot(documentSnapshot, playerName));
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "getPlayerStats:onCancelled", e);
                }
            });
    }

    @Override
    public void getTopPlayers(final MenuState.OnPlayersFetchedListener listener) {
        playersDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    return;
                }
                List<PlayerData> playerList = new ArrayList<>();
                Map<String, Object> playersData = documentSnapshot.getData();
                if (playersData == null) {
                    return;
                }
                    // Convert player data to PlayerData objects and add to the list
                for (Map.Entry<String, Object> entry : playersData.entrySet()) {
                    String playerName = entry.getKey();
                    Map<String, Object> playerDataMap = (Map<String, Object>) entry.getValue();
                    if (playerDataMap != null && playerDataMap.containsKey("wins")) {
                        Long wins = (Long) playerDataMap.get("wins");
                        Long gamesPlayed = (Long) playerDataMap.get("gamesPlayed");
                        PlayerData playerData = new PlayerData(playerName, wins.intValue(), gamesPlayed.intValue());
                        playerList.add(playerData);
                    }
                }

                // Sort the player list based on wins in descending order
                Collections.sort(playerList, new Comparator<PlayerData>() {
                    @Override
                    public int compare(PlayerData p1, PlayerData p2) {
                        return Integer.compare(p2.getWins(), p1.getWins());
                    }
                });

//                Collections.reverse(playerList);
                // Take at most 100 records
                if (playerList.size() > 100) {
                    playerList = playerList.subList(0, 100);
                }

                DataHolderClass dataHolder = new DataHolderClass();
                for(PlayerData player : playerList){
                    dataHolder.addPlayerData(player);
                }

                listener.onPlayersFetched(dataHolder.getPlayerDataArray());
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error getting top players", e);
            }
        });
    }

    private PlayerData getDataFromDocumentSnapshot(DocumentSnapshot documentSnapshot, String playerName) {
        PlayerData playerData = new PlayerData(playerName, 0, 0);
        if (documentSnapshot.exists()) {
            Map<String, Object> playerDataMapGlobal = documentSnapshot.getData();
            if (playerDataMapGlobal != null && playerDataMapGlobal.containsKey(playerName)) {
                Map<String, Object> playerDataMap = (Map<String, Object>) playerDataMapGlobal.get(playerName);
                Long wins = (Long) playerDataMap.get("wins");
                Long gamesPlayed = (Long) playerDataMap.get("gamesPlayed");
                playerData.setWins(wins.intValue());
                playerData.setGamesPlayed(gamesPlayed.intValue());
                Log.d(TAG, "Player found in database.");
            } else {
                Log.d(TAG, "Player NOT found in database.");
            }
        }
        return playerData;
    }

    @Override
    public void updateBattleResult(BattleResult battleResult) {
        if (battleResult.getWinner() == null) {
           Log.e(TAG, "Invalid battle result data");
           return;
        }
        final CountDownLatch latch = new CountDownLatch(1);

        final PlayerData[] newPlayerData1 = {new PlayerData(battleResult.getPlayer1Name(), 0, 0)};
        final PlayerData[] newPlayerData2 = {new PlayerData(battleResult.getPlayer1Name(), 0, 0)};
        playersDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> playersData = documentSnapshot.getData();
                        if (playersData == null){
                            Log.d(TAG, "got playerData need to parse");
                            return;
                        }
                        newPlayerData1[0] = getDataFromDocumentSnapshot(documentSnapshot, battleResult.getPlayer1Name());
                        newPlayerData2[0] = getDataFromDocumentSnapshot(documentSnapshot, battleResult.getPlayer2Name());
                        latch.countDown();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error checking if player exists", e);
                    }
                });
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "Interrupted while waiting for player data", e);
        }
        if(battleResult.getWinner().equals(battleResult.getPlayer1Name())){
            newPlayerData1[0].incrementWins();
            newPlayerData1[0].incrementGamesPlayed();
            newPlayerData2[0].incrementGamesPlayed();
        } else {
            newPlayerData2[0].incrementWins();
            newPlayerData2[0].incrementGamesPlayed();
            newPlayerData1[0].incrementGamesPlayed();
        }

        if (battleResult.getPlayer1Name().equals(battleResult.getWinner())) {
            updatePlayerData(newPlayerData1[0], new int[]{1, 1});
            updatePlayerData(newPlayerData2[0], new int[]{0, 1});
        } else {
            updatePlayerData(newPlayerData1[0], new int[]{0, 1});
            updatePlayerData(newPlayerData2[0], new int[]{1, 1});
        }

    }

    private void updatePlayerData(PlayerData playerData, int[] addToData) {
        Map<String, Object> playerMap = new HashMap<>();
        playerMap.put("wins", playerData.getWins());
        playerMap.put("gamesPlayed", playerData.getGamesPlayed());

        playersDocRef.update(playerData.getName(), playerMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Player data updated successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error updating player data", e);
                    }
                });
    }

    private boolean checkPlayerExists(String playerName) {
        final boolean[] result = {false};
        final CountDownLatch latch = new CountDownLatch(1);

        playersDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> playersData = documentSnapshot.getData();
                        if (playersData != null && playersData.containsKey(playerName)) {
                            Log.d(TAG, "Player found in database: " + playerName);
                            result[0] = true;
                        } else {
                            Log.d(TAG, "Player not found in database.");
                            result[0] = false;
                        }
                        latch.countDown(); // Decrement latch count when operation completes
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error checking if player exists", e);
                        latch.countDown(); // Decrement latch count in case of failure
                    }
                });

        try {
            latch.await(5, TimeUnit.SECONDS); // Wait for up to 5 seconds for the operation to complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "Interrupted while checking if player exists", e);
        }

        return result[0];
    }

    @Override
    public void addPlayer(PlayerData playerData) {
        Map<String, Object> playerMap = new HashMap<>();
        playerMap.put("wins", playerData.getWins());
        playerMap.put("gamesPlayed", playerData.getGamesPlayed());

        if (checkPlayerExists(playerData.getName())) {
            Log.d(TAG, "Player already exists in database.");
            return;
        }

        playersDocRef.update(playerData.getName(), playerMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Player added successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding player", e);
                    }
                });
    }

    @Override
    public void SomeFunction() {
        System.out.println("Just some function");
    }
}
