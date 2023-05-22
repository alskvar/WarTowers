package com.mygdx.wartowers.states;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import com.mygdx.wartowers.sprites.Tower;
import com.mygdx.wartowers.utils.Constants;

import java.util.Random;


public class PlayState extends State implements InputProcessor, GestureDetector.GestureListener {

    private GestureDetector gestureDetector;
    private boolean doubleTapDetected;

    private static final int TOWER_COUNT = 4;

    private Stage stage;
    private Array<Tower> towers;
//    private Tower tower;
    private Texture bg;
    private Random random;
    private Vector2 groundPos1, groundPos2;

    public PlayState(GameStateManager gsm){
        super(gsm);
        bg = new Texture("play_bg_tmp.jpg");

        random = new Random();
        towers = new Array<Tower>();
        for (int i = 0; i < TOWER_COUNT; ++i){
            towers.add(new Tower(i*100 + 40, 210, i));
        }

        stage = new Stage(new ScreenViewport());
//        set_table();
//        set_touchpad();
        gestureDetector = new GestureDetector(this);
        doubleTapDetected = false;
        Gdx.input.setInputProcessor(new InputMultiplexer(this, gestureDetector));
//        Gdx.input.setInputProcessor(this);
    }

    private void set_touchpad(){
        final float speed = 1;
        Skin skin = new Skin(Gdx.files.internal("font_skins/comic/comic-ui.json"));
        final Touchpad touchpad = new Touchpad(0, skin);
        touchpad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float deltaX = touchpad.getKnobPercentX() * speed;
                float deltaY = touchpad.getKnobPercentY() * speed;
                System.out.println(deltaX);
                System.out.println(deltaY);
            }
        });
        touchpad.setSize(50, 50);
        touchpad.setPosition(200, 200);
        stage.addActor(touchpad);
    }

    private void set_table(){
        Skin skin = new Skin(Gdx.files.internal("font_skins/comic/comic-ui.json"));

        Table menu = new Table();
        TextButton item1 = new TextButton("Menu Item 1", skin);
        TextButton item2 = new TextButton("Menu Item 2", skin);
        TextButton item3 = new TextButton("Menu Item 3", skin);

        menu.add(item1).expandX().fillX().pad(10);
        menu.row();
        menu.add(item2).expandX().fillX().pad(10);
        menu.row();
        menu.add(item3).expandX().fillX().pad(10);
        menu.row();

        menu.setSize(Constants.APP_WIDTH/3, Constants.APP_HEIGHT/3);
        menu.setPosition(0, 440);

        menu.setOrigin(Constants.APP_WIDTH/6, Constants.APP_HEIGHT/6);
        menu.setRotation(90);

        menu.addAction(Actions.sequence(
                Actions.rotateBy(-90, 0.5f),
                Actions.moveTo(150, 200, 0.5f),
                Actions.parallel(
                        Actions.sizeTo(100, 50, 0.5f),
                        Actions.rotateBy(90, 0.5f)
                )
        ));

        stage.addActor(menu);
    }


    @Override
    protected void handleInput() {
    }

    @Override
    public void update(float dt) {
        for(int i = 0; i < towers.size; ++i){
            towers.get(i).update(dt);
        }

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(bg, 0, 0, Constants.APP_WIDTH, Constants.APP_HEIGHT);
        for(int i = 0; i < towers.size; ++i) {
            Tower tower = towers.get(i);
            sb.draw(tower.getTower(), tower.getPosition().x, tower.getPosition().y);
            tower.getFont().draw(sb, "has " + tower.getAmount(), tower.getPosition().x, tower.getPosition().y - 10);
            tower.getFont().draw(sb, "warType " + tower.getWarrior().getKind(), tower.getPosition().x- 5, tower.getPosition().y - 20);
        }
        sb.end();
//        stage.act();
//        stage.draw();
    }

    @Override
    public void dispose() {
        bg.dispose();
        stage.dispose();
        towers.clear();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        y = 800 - y;
        if (doubleTapDetected) {
            System.out.println("Double tap detected!");
            for(int i = 0; i < towers.size; ++i) {
                Tower tower = towers.get(i);
                if (tower.overlap(x, y)) {
                    tower.upgradeTower();
                    break;
                }
            }
            doubleTapDetected = false;
        } else {
            doubleTapDetected = true;
        }
        return true;
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
        screenY = 800 - screenY;
//        System.out.println("point: " + pointer);
//        System.out.println("x: " + screenX + ", y: " + screenY + ", point: " + pointer + ", but:" + button);
        for(int i = 0; i < towers.size; ++i){
            Tower tower = towers.get(i);
            if(tower.overlap(screenX, screenY)){
                tower.setSelected(true);
                break;
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        screenY = 800 - screenY;
//        System.out.println("x: " + screenX + ", y: " + screenY + ", point: " + pointer + ", but:" + button);
        Tower selected = null;
        for(int i = 0; i < towers.size; ++i){
            if(towers.get(i).isSelected()){
                selected = towers.get(i);
                break;
            }
        }
        if(selected == null)
            return false;
        System.out.println("selected: " + selected.getId());
        for(int i = 0; i < towers.size; ++i){
            Tower tower = towers.get(i);
            if(tower.overlap(screenX, screenY)){
                if(tower.getId() == selected.getId()){
                    System.out.println("can't send to yourself");
                    break;
                }
                System.out.println("" + selected.getId() + " attacks " + tower.getId());
                tower.transferIn(selected.transferOut(1));
            }
        }
        selected.setSelected(false);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        doubleTapDetected = false;
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

}
