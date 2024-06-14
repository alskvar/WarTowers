package com.mygdx.wartowers.states;

import static com.mygdx.wartowers.WarTowers.bluetoothService;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.mygdx.wartowers.WarTowers;
import com.mygdx.wartowers.sprites.Battleground;
import com.mygdx.wartowers.sprites.Carriage;
import com.mygdx.wartowers.sprites.Tower;
import com.mygdx.wartowers.utils.Constants;

import java.util.Iterator;

public class BluetoothPlayState extends PlayState{
    private final boolean isHost;
    private final String player1Name;
    private final String player2Name;

    public BluetoothPlayState(GameStateManager gsm, boolean isHost, String player1Name, String player2Name) {
        super(gsm);
        this.isHost = isHost;
        this.player1Name = player1Name;
        this.player2Name = player2Name;


        Label firstPlayerLabel = new Label(player1Name + (isHost ? "(Host)" : ""), skin, "title");
        Label secondPlayerLabel = new Label(player2Name + (isHost ? "" : "(Host)"), skin, "title");
        secondPlayerLabel.setWidth((float) (Constants.APP_WIDTH * 0.3));
        firstPlayerLabel.setWidth((float) (Constants.APP_WIDTH * 0.3));
        Label vsLabel = new Label("VS", skin, "title");
        vsLabel.setFontScale(2);
        vsLabel.setWidth((float) (Constants.APP_WIDTH * 0.1f));
        vsLabel.setPosition(Constants.APP_WIDTH / 2 - vsLabel.getWidth() / 2, Constants.APP_HEIGHT * 0.94f - vsLabel.getHeight() / 2);
        firstPlayerLabel.setPosition(Constants.APP_WIDTH / 2 - firstPlayerLabel.getWidth()  - 50, Constants.APP_HEIGHT * 0.94f - firstPlayerLabel.getHeight() / 2);
        secondPlayerLabel.setPosition(Constants.APP_WIDTH / 2 + 100 + vsLabel.getWidth() / 2, Constants.APP_HEIGHT * 0.94f - secondPlayerLabel.getHeight() / 2);

        stage.addActor(firstPlayerLabel);
        stage.addActor(secondPlayerLabel);
        stage.addActor(vsLabel);
    }

    private void goThroughMessages(){
        Array<String> messages = bluetoothService.getLastMessages();
        if (messages != null && messages.size > 0) {
            Gdx.app.log("Bluetooth", "Messages: " + messages.toString());
        }
        for(String message : messages){
            if(message.contains("GYROSCOPE:")){
                updateEnemyCarriage(message.substring(10));
            }
            else if(message.contains("CARRIAGE:")){
                String[] parts = message.substring(9).split(":");
                Gdx.app.log("Bluetooth", "CARRIAGE: " + parts[0] + "." + parts[1] + "." + parts[2] + "..." + parts.length);
                findPathAndAdd(parts);
            }else if(message.contains("UPGRADE:")) {
                Gdx.app.log("Bluetooth", "UPGRADE:" + message.substring(8));
                int towerId = Integer.parseInt(message.substring(8));
                for (Tower tower : battleground.getTowers()) {
                    if (tower.getId() == towerId) {
                        tower.upgradeTower();
                    }
                }
            }else if(message.contains("GAMEOVER:")){
                String[] parts = message.substring(9).split(":");
                String winner = parts[0];
                String loser = parts[1];
                gsm.set(new BattleResultState(gsm, winner, loser, isHost));

            }
        }
    }

    public void updateEnemyCarriage(String Direction){
        for (Carriage carriage : carriages) {
            if (carriage.getOwner() != (isHost ? 1 : 2)) {
                carriage.update(0, battleground.getTowers(), true, false);
            }
        }
    }

    public void findPathAndAdd(String[] parts) {
        int owner = Integer.parseInt(parts[0]);
        int from = Integer.parseInt(parts[1]);
        int to = Integer.parseInt(parts[2]);
        Tower selected = battleground.getTowers().get(from);
        Tower tower = battleground.getTowers().get(to);
        if (tower.getId() == selected.getId()) {
            return;
        }
        Battleground.PathResult pathResult =
                battleground.getShortestPath(selected.getId(), tower.getId());
        if (pathResult.getDistance() == Integer.MAX_VALUE) {
            return;
        }

        Carriage carriage = new Carriage(selected.getOwner(),
                selected.getWarrior().getKind(), selected.transferOut(1), pathResult.getPath(), selected.getCenterX());
        carriages.add(carriage);
    }

    @Override
    public void update(float dt) {
        goThroughMessages();
        battleground.updateTowers(dt);
        if(battleground.checkIsGameOver()){
            int myself = isHost ? 1 : 2;
            if (battleground.getWinner() == -1) {
                return;
            }
            String winner = (battleground.getWinner() == myself) ? player1Name : player2Name;
            String loser = (battleground.getWinner() == myself) ? player2Name : player1Name;
            String toSend = "GAMEOVER:" + winner + ":" + loser;
            WarTowers.bluetoothService.sendMessage(toSend);
            gsm.set(new BattleResultState(gsm, winner, loser, isHost));
            return;
        }
        catastrophe.update(dt);
        final boolean gyroscopeResult = updateGyroscope(dt);
        final boolean eventHasStarted = catastrophe.isEventHasStarted();
        for (Iterator<Carriage> iterator = carriages.iterator(); iterator.hasNext();) {
            Carriage carriage = iterator.next();
            if (carriage.getOwner() == (isHost ? 1 : 2)) {
                carriage.update(dt, battleground.getTowers(), gyroscopeResult, eventHasStarted);
            }else{
                carriage.update(dt, battleground.getTowers(), false, eventHasStarted);
            }
            if (carriage.getAmount() == 0){
                iterator.remove();
                continue;
            }
            if (carriage.hasReachedDestination()) {
                battleground.getTowers().get(carriage.getDestination()).transferIn(carriage.getInfo());
                iterator.remove();
            }
        }
    }

    @Override
    protected boolean updateGyroscope(float dt){
        float currentGyroX = Gdx.input.getGyroscopeX();
        float currentGyroY = Gdx.input.getGyroscopeY();
        float currentGyroZ = Gdx.input.getGyroscopeZ();

        if (gyroscopeTimeout > 0) {
            gyroscopeTimeout -= dt;
            if (gyroscopeTimeout < 0) {
                gyroscopeTimeout = 0;
            }
        }

        boolean result = false;
        if (Math.abs(currentGyroZ - lastGyroZ) > 1.0f) {
            if (gyroscopeTimeout == 0) {
                gyroscopeTimeout = 3.0f;
                result = true;
                if (currentGyroY - lastGyroZ > 0) {
                    WarTowers.bluetoothService.sendMessage("GYROSCOPE:RIGHT");
                } else {
                    WarTowers.bluetoothService.sendMessage("GYROSCOPE:LEFT");
                }
            }
        }
        lastGyroX = currentGyroX;
        lastGyroY = currentGyroY;
        lastGyroZ = currentGyroZ;
        return result;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        screenY = (int) Constants.APP_HEIGHT - screenY;
        Tower selected = null;
        for(int i = 0; i < battleground.getTowers().size; ++i){
            Tower tower = battleground.getTowers().get(i);
            if(tower.isSelected()){
                if(tower.getOwner() == (isHost ? 1 : 2)) {
                    selected = battleground.getTowers().get(i);
                }
                break;
            }
        }
        if(selected == null)
            return false;
        for(int i = 0; i < battleground.getTowers().size; ++i){
            Tower tower = battleground.getTowers().get(i);
            if(tower.getId() != selected.getId() && tower.overlap(screenX, screenY)){
                Battleground.PathResult pathResult =
                        battleground.getShortestPath(selected.getId(), tower.getId());
                if (pathResult.getDistance() == Integer.MAX_VALUE) {
                    break;
                }
                Carriage carriage = new Carriage(selected.getOwner(),
                        selected.getWarrior().getKind(), selected.transferOut(1), pathResult.getPath(), selected.getCenterX());
                carriages.add(carriage);
                String toSend = "CARRIAGE:" + selected.getOwner() + ":" + selected.getId() + ":" + tower.getId();
                WarTowers.bluetoothService.sendMessage(toSend);
            }
        }
        selected.setSelected(false);
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        y = Constants.APP_HEIGHT - y;
        if (doubleTapDetected) {
            for(int i = 0; i < battleground.getTowers().size; ++i) {
                Tower tower = battleground.getTowers().get(i);
                if (tower.overlap(x, y)) {
                    if (tower.getOwner() == (isHost ? 1 : 2)) {
                        tower.upgradeTower();
                        String toSend = "UPGRADE:" + tower.getId();
                        Gdx.app.log("Bluetooth", "sent UPGRADE:" + toSend);
                        WarTowers.bluetoothService.sendMessage(toSend);
                    }
                    break;
                }
            }
            doubleTapDetected = false;
        }else {
            doubleTapDetected = true;
        }
        return false;
    }
    @Override
    public void render(SpriteBatch sb){
        super.render(sb);
    }

    @Override
    public void dispose() {
        super.dispose();
        WarTowers.bluetoothService.closeAllConnections();
    }

}
