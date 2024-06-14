package com.mygdx.wartowers.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.wartowers.WarTowers;
import com.mygdx.wartowers.utils.Constants;

import java.util.ArrayList;

public class BluetoothState extends State {
    private ArrayList<TextButton> bluetoothButtons;

    public BluetoothState(GameStateManager gsm) {
        super(gsm);
        setupUI();
        Gdx.input.setCatchBackKey(true);
    }

    private void setupUI() {
        Skin skin = new Skin(Gdx.files.internal(Constants.SKIN_COSMIC_PATH));

        Label label = new Label("Choose a device to connect to", skin);
        label.setFontScale(2.5f);
        label.setPosition(Constants.APP_WIDTH / 2 - label.getWidth(), Constants.APP_HEIGHT / 1.5f + label.getHeight() / 2);


        Array<String> bluetoothNames= WarTowers.bluetoothService.getConnectedDevicesChoices();
        ScrollPane table = generateBluetoothButtons(bluetoothNames, skin);

        stage = new Stage(new ScreenViewport());
        stage.addActor(label);
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    private ScrollPane generateBluetoothButtons(final Array<String> bluetoothNames, Skin skin ){
        bluetoothButtons = new ArrayList<>();
        for (String name : bluetoothNames){
            final String finalName = name;
            TextButton curButton = new TextButton(name, skin);
            curButton.setName(name);
            curButton.setWidth(Constants.APP_WIDTH / 5);
            curButton.setHeight(Constants.APP_HEIGHT / 10);
            curButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    WarTowers.bluetoothService.connectToDeviceByName(finalName);
                    gsm.set(new BluetoothPlayersReadyState(gsm));
                }
            });
            bluetoothButtons.add(curButton);
        }
        Table scrollTable = new Table(skin);


        for(TextButton curBluetooth : bluetoothButtons){
            scrollTable.add(curBluetooth);
            scrollTable.row();
        }

        ScrollPane bluetoothList = new ScrollPane(scrollTable);
        bluetoothList.setHeight(Constants.APP_HEIGHT / 3);
        bluetoothList.setWidth((float) (Constants.APP_WIDTH * 0.9));

        bluetoothList.setPosition(Constants.APP_WIDTH / 2 - bluetoothList.getWidth() / 2,  Constants.APP_HEIGHT / 5f);
        return bluetoothList;
    }

    @Override
    protected void handleInput() {
        // Handle input if needed
    }

    @Override
    protected void activateStagesInputProcessor() {
        // Activate stage input processors if needed
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        stage.act();
        stage.draw();
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            gsm.pop();
            gsm.peek().activateStagesInputProcessor();
            Gdx.input.setCatchBackKey(false);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

