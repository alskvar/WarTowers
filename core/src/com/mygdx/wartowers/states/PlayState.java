package com.mygdx.wartowers.states;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.wartowers.sprites.Battleground;
import com.mygdx.wartowers.sprites.Carriage;
import com.mygdx.wartowers.sprites.Catastrophe;
import com.mygdx.wartowers.sprites.Tower;
import com.mygdx.wartowers.utils.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class PlayState extends State implements InputProcessor, GestureDetector.GestureListener {

    private final GestureDetector gestureDetector;
    protected boolean doubleTapDetected;

    protected final Battleground battleground;
    private final Texture bg;
    private final Random random;
    protected final ArrayList<Carriage> carriages;
    protected float lastGyroX;
    protected float lastGyroY;
    protected float lastGyroZ;
    protected float gyroscopeTimeout;
    protected final Catastrophe catastrophe;

    protected final Skin skin;


    public PlayState(GameStateManager gsm) {
        super(gsm);
        random = new Random();
        catastrophe = new Catastrophe();
        battleground = new Battleground(loadJsonFromFile());
        carriages = new ArrayList<>();
        bg = new Texture(battleground.getBackgroundImagePath());
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal(Constants.SKIN_COSMIC_PATH));
        doubleTapDetected = false;
        gestureDetector = new GestureDetector(this);
        this.lastGyroY = this.lastGyroX = this.lastGyroZ = 0;
        Gdx.input.setInputProcessor(new InputMultiplexer(this, gestureDetector));
    }

    private static String loadJsonFromFile() {
        FileHandle fileHandle = Gdx.files.internal(Constants.BATTLEGROUND_JSON_PATH);
        return fileHandle.readString();
    }

    @Override
    protected void handleInput() {
    }

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
                if (currentGyroY - lastGyroZ > 0)
                    System.out.println("Gyroscope rotation detected on right");
                else
                    System.out.println("Gyroscope rotation detected on left");
            }
        }
        lastGyroX = currentGyroX;
        lastGyroY = currentGyroY;
        lastGyroZ = currentGyroZ;
        return result;
    }

    @Override
    public void update(float dt) {
        battleground.updateTowers(dt);
        if(battleground.checkIsGameOver()){
            gsm.set(new BattleResultState(gsm, MenuState.nickname, "None", false));
            return;
        }
        catastrophe.update(dt);
        final boolean gyroscopeResult = updateGyroscope(dt);
        final boolean eventHasStarted = catastrophe.isEventHasStarted();
        for (Iterator<Carriage> iterator = carriages.iterator(); iterator.hasNext();) {
            Carriage carriage = iterator.next();
            carriage.update(dt, battleground.getTowers(), gyroscopeResult, eventHasStarted);
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
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(bg, 0, 0, Constants.APP_WIDTH, Constants.APP_HEIGHT);
        for (Tower tower : battleground.getTowers()) {
            tower.render(sb);
        }
        for (Carriage carriage : carriages) {
            carriage.render(sb);
        }
        catastrophe.render(sb);
        sb.end();
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        bg.dispose();
        stage.dispose();
        battleground.dispose();
        catastrophe.dispose();
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        y = Constants.APP_HEIGHT - y;
        if (doubleTapDetected) {
            for(int i = 0; i < battleground.getTowers().size; ++i) {
                Tower tower = battleground.getTowers().get(i);
                if (tower.overlap(x, y) && tower.getOwner() == 1) {
                    tower.upgradeTower();
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
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screenY = (int)Constants.APP_HEIGHT - screenY;
        for(int i = 0; i < battleground.getTowers().size; ++i){
            Tower tower = battleground.getTowers().get(i);
            if(tower.overlap(screenX, screenY)){
                tower.setSelected(true);
                break;
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        screenY = (int)Constants.APP_HEIGHT - screenY;
        Tower selected = null;
        for(int i = 0; i < battleground.getTowers().size; ++i){
            if(battleground.getTowers().get(i).isSelected()){
                selected = battleground.getTowers().get(i);
                break;
            }
        }
        if(selected == null)
            return false;
        if (selected.getOwner() != 1){
            selected.setSelected(false);
            return false;
        }
        for(int i = 0; i < battleground.getTowers().size; ++i){
            Tower tower = battleground.getTowers().get(i);
            if(tower.getId() != selected.getId() && tower.overlap(screenX, screenY)){
                Battleground.PathResult pathResult =
                        battleground.getShortestPath(selected.getId(), tower.getId());
                if (pathResult.getDistance() == Integer.MAX_VALUE) {
                    break;
                }
                System.out.println("" + selected.getId() + " attacks " + tower.getId());

                Carriage carriage = new Carriage(selected.getOwner(),
                        selected.getWarrior().getKind(), selected.transferOut(1), pathResult.getPath(), selected.getCenterX());
                carriages.add(carriage);
            }
        }
        selected.setSelected(false);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void activateStagesInputProcessor() {
        Gdx.input.setInputProcessor(new InputMultiplexer(this, gestureDetector));
    }

}
