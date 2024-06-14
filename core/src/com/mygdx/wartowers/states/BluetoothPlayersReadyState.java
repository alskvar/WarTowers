package com.mygdx.wartowers.states;

import static com.mygdx.wartowers.WarTowers.bluetoothService;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.wartowers.utils.Constants;

import java.util.Random;

public class BluetoothPlayersReadyState extends State{

    private Label firstPlayerLabel;
    private Label secondPlayerLabel;
    private final Texture background;
    String firstPlayerName = "None";
    String secondPlayerName = "None";
    boolean secondPlayerConnected = false;
    boolean firstPlayerReady = false;
    boolean secondPlayerReady = false;
    private boolean startedInput = false;

    private final TextButton ready;

    private int opponentRandom = 0;
    private int myRandom = 0;

    private long delta;

    public BluetoothPlayersReadyState(GameStateManager gsm) {
        super(gsm);
        Skin skin = new Skin(Gdx.files.internal(Constants.SKIN_COSMIC_PATH));
        this.firstPlayerName = MenuState.nickname;
        this.secondPlayerName = "Now None";
        background = new Texture("backgroundImages/scroll.png");
        delta = System.currentTimeMillis();

        Gdx.input.setCatchBackKey(true);

//        backGround = new Image(game.assetManager.get("BACKGROUND0001.png", Texture.class));
//        backGround.setPosition(0, 0);
//        backGround.setHeight(stage.getHeight());
//        backGround.setWidth(stage.getWidth());

        firstPlayerLabel = new Label("Connected: " + firstPlayerName, skin, "title");
        firstPlayerLabel.setHeight((float) (Constants.APP_HEIGHT * 0.2));
        firstPlayerLabel.setWidth((float) (Constants.APP_WIDTH * 0.8));
        firstPlayerLabel.setFontScale(1);
        firstPlayerLabel.setPosition(Constants.APP_WIDTH / 2 - firstPlayerLabel.getWidth() / 2, Constants.APP_HEIGHT / 4 * 3 - firstPlayerLabel.getHeight() / 2);

        secondPlayerLabel = new Label(!secondPlayerConnected ? "Not Connected: " +  secondPlayerName: "Connected" + secondPlayerName, skin, "title");
        secondPlayerLabel.setHeight((float) (Constants.APP_HEIGHT * 0.2));
        secondPlayerLabel.setWidth((float) (Constants.APP_WIDTH * 0.8));
        secondPlayerLabel.setFontScale(1);
        secondPlayerLabel.setPosition(Constants.APP_WIDTH / 2 - secondPlayerLabel.getWidth() / 2, Constants.APP_HEIGHT / 4 * 2 - secondPlayerLabel.getHeight() / 2);

        ready = new TextButton("Ready", skin);
        ready.setHeight((float) (Constants.APP_HEIGHT * 0.2));
        ready.setWidth((float) (Constants.APP_WIDTH * 0.5));
        ready.getLabel().setFontScale(2);
        ready.setPosition(Constants.APP_WIDTH / 2 - ready.getWidth() / 2, Constants.APP_HEIGHT / 4  - ready.getHeight()/ 2);

        ready.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!firstPlayerReady) {
                    firstPlayerReady = true;
                    ready.setText("Ready");
                }
                else{
                    firstPlayerReady = false;
                    ready.setText("NOT Ready");
                }
                sendReadyMessage(firstPlayerReady);
            }
        });

        stage = new Stage(new ScreenViewport());
//        stage.addActor(backGround);
        stage.addActor(firstPlayerLabel);
        stage.addActor(secondPlayerLabel);
        stage.addActor(ready);
        Gdx.input.setInputProcessor(stage);
    }

    private void sendNameMessage(String name) {
        if(bluetoothService != null) {
            bluetoothService.sendMessage("PLAYER_NAME:" + name);
        }
    }

    private void sendReadyMessage(boolean ready) {
        if(bluetoothService != null) {
            bluetoothService.sendMessage("PLAYER_READY:" + (ready ? "true" : "false"));
        }
    }

    private void sendConnectedMessage(){
        if(bluetoothService != null) {
            bluetoothService.sendMessage("PLAYER_CONNECTED");
        }
    }
    private void goThroughMessages(){
        Array<String> messages = bluetoothService.getLastMessages();
//        if (messages != null && messages.size > 0) {
//            Gdx.app.log("Bluetooth", "Messages: " + messages.toString());
//        }

        for(String message : messages){
            if(message.contains("PLAYER_NAME")){
                setSecondPlayerName(message.substring(12));
            }
            else if(message.contains("PLAYER_READY")){
                secondPlayerReady = message.substring(13).equals("true");
            }
            else if(message.contains("PLAYER_CONNECTED")){
                secondPlayerConnected = true;
            }
            else if(message.contains("RANDOM: ")){
                opponentRandom = extractIntegerPrefix(message, 8);
            }
        }
    }

    public static int extractIntegerPrefix(String message, int startIndex) {
        StringBuilder integerPrefix = new StringBuilder();
        for (int i = startIndex; i < message.length(); i++) {
            char c = message.charAt(i);
            if (Character.isDigit(c)) {
                integerPrefix.append(c);
            } else {
                break;
            }
        }
        if (integerPrefix.length() == 0) {
            throw new NumberFormatException("No integer prefix found starting from index " + startIndex);
        }
        return Integer.parseInt(integerPrefix.toString());
    }

    public void setSecondPlayerName(String name) {
        this.secondPlayerName = name;
    }

    @Override
    protected void handleInput() {
    }

    @Override
    protected void activateStagesInputProcessor() {
    }

    @Override
    public void update(float dt) {
        if (firstPlayerReady && secondPlayerReady) {
            if(myRandom == 0) {
                myRandom = Math.abs(new Random().nextInt()) + 1;
            }

            bluetoothService.sendMessage("RANDOM: " + myRandom);
            if(opponentRandom != 0){
                if(opponentRandom > myRandom){
                    gsm.set(new BluetoothPlayState(gsm, true, firstPlayerName, secondPlayerName));
//                    game.gameScreen = new MultiplayerGameScreen(game, myRandom, System.currentTimeMillis(), true, new BluetoothConnectionHandler(bluetoothService), firstPlayerName, secondPlayerName);
                    Gdx.app.log("Bluetooth", "Opponent Random: " + opponentRandom + " host is " + firstPlayerName);
                }
                else{
                    gsm.set(new BluetoothPlayState(gsm, false, firstPlayerName, secondPlayerName));
                    Gdx.app.log("Bluetooth", "Opponent Random: " + opponentRandom + " host is " + secondPlayerName);
//                    game.gameScreen = new MultiplayerGameScreen(game, opponentRandom, System.currentTimeMillis(), false, new BluetoothConnectionHandler(bluetoothService), firstPlayerName, secondPlayerName);
                }

//                game.setScreen(game.gameScreen);
//                Gdx.input.setInputProcessor(null);
            }

        }
        if(System.currentTimeMillis() - delta > 1000) {
            delta = System.currentTimeMillis();
            sendNameMessage(firstPlayerName);
            sendReadyMessage(firstPlayerReady);
            sendConnectedMessage();
        }
        goThroughMessages();
        if (!bluetoothService.checkSocketConnection()) {
            secondPlayerConnected = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            gsm.pop();
            gsm.peek().activateStagesInputProcessor();
            bluetoothService.closeAllConnections();
            Gdx.input.setCatchBackKey(false);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, Constants.APP_WIDTH, Constants.APP_HEIGHT);
        sb.end();
        firstPlayerLabel.setText("Connected: " + firstPlayerName + (!firstPlayerReady ? ": Not Ready" : ": Ready"));
        secondPlayerLabel.setText(!secondPlayerConnected ? "Not Connected: " : "Connected: " + secondPlayerName + (!secondPlayerConnected ? "" : !secondPlayerReady ? ": Not Ready" : ": Ready"));
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        background.dispose();
        stage.dispose();
    }

//    protected class
}
