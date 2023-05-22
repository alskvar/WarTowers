package com.mygdx.wartowers.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.wartowers.WarTowers;
import com.mygdx.wartowers.utils.Constants;

public class MenuState extends State{

    private Stage stage;
    private Texture background;

    public MenuState(GameStateManager gsm){
        super(gsm);
        background = new Texture("menu_bg.jpg");
        set_stage();
    }

    private void set_stage(){
        Skin skin = new Skin(Gdx.files.internal("font_skins/comic/comic-ui.json"));
        Skin skin_def = new Skin(Gdx.files.internal("font_skins/default/uiskin.json"));

        Label label = new Label("WAR TOWERS", skin_def);
        label.setFontScale(4.0f, 4.0f);
        label.setPosition((Constants.APP_WIDTH/2) - 210, Constants.APP_HEIGHT * 9 / 10);
//        System.out.println(label.getFontScaleX()/2);

        TextButton button = new TextButton("PLAY", skin);
        button.setSize(200, 100);
        button.getLabel().setFontScale(2.0f, 2.0f);
        button.setPosition(Constants.APP_WIDTH/2 - button.getWidth()/2, Constants.APP_HEIGHT/2);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.set(new PlayState(gsm));
            }
        });
        stage = new Stage(new ScreenViewport());
        stage.addActor(label);
        stage.addActor(button);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    protected void handleInput() {
//        if(Gdx.input.justTouched()){
//        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, Constants.APP_WIDTH, Constants.APP_HEIGHT);
        sb.end();
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        background.dispose();
        stage.dispose();
    }
}
