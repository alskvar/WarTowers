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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.wartowers.Database.FireStoreInterface;
import com.mygdx.wartowers.sprites.Battleground;
import com.mygdx.wartowers.sprites.Carriage;
import com.mygdx.wartowers.sprites.Tower;
import com.mygdx.wartowers.utils.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


public class PlayState extends State implements InputProcessor, GestureDetector.GestureListener {

    private final GestureDetector gestureDetector;
    private boolean doubleTapDetected;

//    private final Stage stage;
    private final Battleground battleground;
    private final Texture bg;
    private final Random random;
    private final ArrayList<Carriage> carriages;
    private ImageButton menuButton;
    protected FireStoreInterface dbInterface;


    public PlayState(GameStateManager gsm, FireStoreInterface dbInterface) {
        super(gsm);
        random = new Random();
        this.dbInterface = dbInterface;
        battleground = new Battleground(loadJsonFromFile(Constants.BATTLEGROUND_JSON_PATH));
        carriages = new ArrayList<>();
        bg = new Texture(battleground.getBackgroundImagePath());
        stage = new Stage(new ScreenViewport());
        doubleTapDetected = false;
        gestureDetector = new GestureDetector(this);
        Gdx.input.setInputProcessor(new InputMultiplexer(this, gestureDetector));
    }

    private static String loadJsonFromFile(String filePath) {
        FileHandle fileHandle = Gdx.files.internal(filePath);
        return fileHandle.readString();
    }

    @Override
    protected void handleInput() {
    }

    @Override
    public void update(float dt) {
        battleground.updateTowers(dt);
        if(battleground.checkIsGameOver()){
            gsm.set(new BattleResultState(gsm, "Sasha"));
            return;
        }
        for (Iterator<Carriage> iterator = carriages.iterator(); iterator.hasNext();) {
            Carriage carriage = iterator.next();
            carriage.update(dt, battleground.getTowers());
            if (carriage.hasReachedDestination()) {
                battleground.getTowers().get(carriage.getDestination()).transferIn(carriage.getInfo());
                iterator.remove();  // Safe removal
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(bg, 0, 0, Constants.APP_WIDTH, Constants.APP_HEIGHT);
        for (Tower tower : battleground.getTowers()) {
            sb.draw(tower.getTowerTexture(), tower.getPosition().x, tower.getPosition().y);
            tower.getFont().draw(sb, "has " + tower.getAmount(), tower.getPosition().x, tower.getPosition().y - 10);
            tower.getFont().draw(sb, "warType " + tower.getWarrior().getKind(), tower.getPosition().x - 5, tower.getPosition().y - 20);
        }
        for (Carriage carriage : carriages) {
            carriage.render(sb);
        }
        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        stage.dispose();
        battleground.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Gdx.app.log("xY tap", "Xh: " + x + ", Y: " + y);
        y = Constants.APP_HEIGHT - y;
        Gdx.app.log("doubleTaP", "Now: " + doubleTapDetected);
        if (doubleTapDetected) {
            System.out.println("Double tap detected!");
            Gdx.app.log("doubleTaP", "Boolean: " + doubleTapDetected);
            for(int i = 0; i < battleground.getTowers().size; ++i) {
                Tower tower = battleground.getTowers().get(i);
                if (tower.overlap(x, y)) {
                    tower.upgradeTower();
                    break;
                }
            }
            doubleTapDetected = false;
        }else {
            Gdx.app.log("doubleTaP", "NowChangeFromBoolean: " + doubleTapDetected);
            doubleTapDetected = true;
            Gdx.app.log("doubleTaP", "ChangedToBoolean: " + doubleTapDetected);

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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screenY = Constants.APP_HEIGHT - screenY;
//        System.out.println("point: " + pointer);
//        System.out.println("x: " + screenX + ", y: " + screenY + ", point: " + pointer + ", but:" + button);
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
        screenY = Constants.APP_HEIGHT - screenY;
//        System.out.println("x: " + screenX + ", y: " + screenY + ", point: " + pointer + ", but:" + button);
        Tower selected = null;
        for(int i = 0; i < battleground.getTowers().size; ++i){
            if(battleground.getTowers().get(i).isSelected()){
                selected = battleground.getTowers().get(i);
                break;
            }
        }
        if(selected == null)
            return false;
        System.out.println("selected: " + selected.getId());
        for(int i = 0; i < battleground.getTowers().size; ++i){
            Tower tower = battleground.getTowers().get(i);
            if(tower != selected && tower.overlap(screenX, screenY)){
                Battleground.PathResult pathResult =
                        battleground.getShortestPath(selected.getId(), tower.getId());
                if (pathResult.getDistance() == Integer.MAX_VALUE) {
                    System.out.println("no path");
                    break;
                }
                if (tower.getId() == selected.getId()) {
                    System.out.println("can't send to yourself");
                    break;
                }
                System.out.println("" + selected.getId() + " attacks " + tower.getId());

                Carriage carriage = new Carriage(selected.getOwner(),
                        selected.getWarrior().getKind(), selected.transferOut(1), pathResult.getPath(), selected.getPosition());
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
