package com.mygdx.wartowers.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.wartowers.DatabaseInterface;
import com.mygdx.wartowers.utils.Constants;

public class MenuState extends State{

    private Stage stage;
    private Texture background;
    private Texture menub;
    private DatabaseInterface dbInterface;

//    public MenuState(GameStateManager gsm){
    public MenuState(GameStateManager gsm, DatabaseInterface dbInterface){
        super(gsm);
        background = new Texture("backgroundImages/menu_bg.jpg");
//        menub = new Texture("menub.png");
        set_stage();
        this.dbInterface = dbInterface;
    }

    private Label[] fill_bestPlayersList(Skin skin){
        Label[] labels = new Label[25];
        for(int i = 0; i < 25; i++){
            labels[i] = new Label("" + i + ". Player " + i, skin);
        }
        return labels;
    }

    private void set_stage(){
        Skin skin = new Skin(Gdx.files.internal("font_skins/comic/comic-ui.json"));
        Skin skin_def = new Skin(Gdx.files.internal("font_skins/default/uiskin.json"));

        Label label = new Label("WAR TOWERS", skin_def);
        label.setFontScale(2.6f, 2.6f);
        label.setSize(300, 200);
        label.setPosition((Constants.APP_WIDTH/2) - label.getWidth()/2, Constants.APP_HEIGHT * 8 / 10);


        ////////////////////////////////////////////////////////////////
        TextButton button = new TextButton("PLAY", skin);
        button.setSize(200, 100);
        button.getLabel().setFontScale(2.0f, 2.0f);
        button.setPosition(Constants.APP_WIDTH/2 - button.getWidth()/2, Constants.APP_HEIGHT/4);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.set(new PlayState(gsm));
            }
        });

        ////////////////////////////////////////////////////////////////
        Texture buttonTexture = new Texture(Gdx.files.internal("menub.png"));
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        ImageButton menu_button = new ImageButton(buttonDrawable);
        menu_button.setSize(100, 80);
        menu_button.setPosition(Constants.APP_WIDTH - menu_button.getWidth(), Constants.APP_HEIGHT - menu_button.getHeight());
        menu_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                gsm.set(new MenuState(gsm));
            }
        });

        ////////////////////////////////////////////////////////////////

        // Create the settings subfield
        final Table settingsTable = createSettingsSubfield(skin);
        menu_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settingsTable.setVisible(!settingsTable.isVisible());
            }
        });

        ////////////////////////////////////////////////////////////////

        TextButton bestPlayersButton = new TextButton("Best Players", skin);
        bestPlayersButton.setSize(120, 80);
        bestPlayersButton.getLabel().setFontScale(0.6f, 0.6f);
        // Set the position of the button on the top right corner under the menu button
        bestPlayersButton.setPosition(Constants.APP_WIDTH/2 - bestPlayersButton.getWidth()/2,
                Constants.APP_HEIGHT/2 - bestPlayersButton.getHeight()/2);


        ////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////
        // Create the list of "Best players"

        Table scrollTable = new Table(skin);
        Label[] scores = fill_bestPlayersList(skin);
        for(Label curLabel : scores){
            scrollTable.add(curLabel);
            scrollTable.row();
        }

        final ScrollPane scoresList = new ScrollPane(scrollTable);
        scoresList.setHeight(Constants.APP_HEIGHT/2 - label.getHeight());
        scoresList.setWidth(Constants.APP_WIDTH / 3);
        scoresList.setPosition(Constants.APP_WIDTH  / 2 - scoresList.getWidth() / 2, Constants.APP_HEIGHT / 2 - scoresList.getHeight() / 2);


        Texture backgroundTexture = new Texture(Gdx.files.internal("backgroundImages/results_bg.jpg"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        scoresList.getStyle().background = backgroundDrawable;


        scoresList.setVisible(false);
        bestPlayersButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                scoresList.setVisible(!scoresList.isVisible());
            }
        });

        // Input processor for touch events on the main menu stage
//        InputProcessor mainMenuInputProcessor = new InputAdapter() {
//            @Override
//            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//                if (scoresList.isVisible()) {
//                    // Check if the touch is outside the list area
//                    if (screenX < scoresList.getX() || screenX > scoresList.getX() + scoresList.getWidth() ||
//                            screenY < scoresList.getY() || screenY > scoresList.getY() + scoresList.getHeight()) {
//                        scoresList.setVisible(false); // Hide the list
//                    }
//                }
////                return super.touchDown(screenX, screenY, pointer, button);
//                return false;
//            }
//        };

        ///////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////

        stage = new Stage(new ScreenViewport());
        stage.addActor(bestPlayersButton);
        stage.addActor(label);
        stage.addActor(button);
        stage.addActor(menu_button);
        stage.addActor(scoresList);
        stage.addActor(settingsTable);

        InputProcessor settingsInputProcessor = createTouchProcessor(settingsTable);
        InputProcessor scrollInputProcessor = createTouchProcessor(scoresList);
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, settingsInputProcessor, scrollInputProcessor));

//        Gdx.input.setInputProcessor(new InputMultiplexer(stage, mainMenuInputProcessor));
//        Gdx.input.setInputProcessor(stage);
    }

    private Table createSettingsSubfield(Skin skin) {
        final Table settingsTable = new Table(skin);

        // Music Volume Slider
        Label musicLabel = new Label("Game Music", skin);
        Slider musicSlider = new Slider(0, 1, 0.01f, false, skin);
        // Set the initial value or from saved settings
//        musicSlider.setValue(dbInterface.getMusicVolume());
        musicSlider.setValue((float)0.5);

        // Sound Effects Volume Slider
        Label soundLabel = new Label("Sound Effects", skin);
        Slider soundSlider = new Slider(0, 1, 0.01f, false, skin);
        // Set the initial value or from saved settings
//        soundSlider.setValue(dbInterface.getSoundVolume());
        soundSlider.setValue((float)0.5);

        // Exit Button
        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        settingsTable.add(musicLabel).pad(10);
        settingsTable.row();
        settingsTable.add(musicSlider).width(200).pad(10);
        settingsTable.row();
        settingsTable.add(soundLabel).pad(10);
        settingsTable.row();
        settingsTable.add(soundSlider).width(200).pad(10);
        settingsTable.row();
        settingsTable.add(exitButton).pad(10);

        settingsTable.setSize(300, 400);
        settingsTable.setPosition(Constants.APP_WIDTH / 2 - settingsTable.getWidth() / 2, Constants.APP_HEIGHT / 2 - settingsTable.getHeight() / 2);
        settingsTable.setVisible(false); // Initially hidden

        Texture backgroundTexture = new Texture(Gdx.files.internal("backgroundImages/results_bg.jpg"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        settingsTable.setBackground(backgroundDrawable);

        return settingsTable;
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
//        menub.dispose();
    }

    protected InputProcessor createTouchProcessor(final Actor actorToHide) {
        return new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (actorToHide.isVisible()) {
                    // Check if the touch is outside the area of the actor
                    if (screenX < actorToHide.getX() || screenX > actorToHide.getX() + actorToHide.getWidth() ||
                            screenY < actorToHide.getY() || screenY > actorToHide.getY() + actorToHide.getHeight()) {
                        actorToHide.setVisible(false); // Hide the actor
                    }
                }
                return false;
            }
        };
    }

}
