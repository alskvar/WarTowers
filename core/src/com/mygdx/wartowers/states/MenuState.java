package com.mygdx.wartowers.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.wartowers.Database.DataHolderClass;
import com.mygdx.wartowers.Database.FireStoreInterface;
import com.mygdx.wartowers.sprites.PlayerData;
import com.mygdx.wartowers.utils.Constants;

import java.util.concurrent.CountDownLatch;

public class MenuState extends State{


    private final Texture background;
    private final Texture emblem;
    private final String nickname;
    private final float emblemWidth = Constants.APP_WIDTH / 2.2f;
    private final float emblemHeight = Constants.APP_WIDTH / 2.2f;
    protected FireStoreInterface dbInterface;

//    public MenuState(GameStateManager gsm){
    public MenuState(GameStateManager gsm, FireStoreInterface dbInterface, String nickname){
        super(gsm);
        this.nickname = nickname;
        inputProcessors = new Array<InputProcessor>();
        background = new Texture("backgroundImages/mainMenu_bg.jpg");
        emblem = new Texture("emblem2.png");
        this.dbInterface = dbInterface;
        set_stage();
    }

    private Label[] fill_bestPlayersList(Skin skin){
        final DataHolderClass dataHolder = new DataHolderClass();
        final CountDownLatch latch = new CountDownLatch(1);
        dbInterface.getTopPlayers(new OnPlayersFetchedListener() {
            @Override
            public void onPlayersFetched(Array<PlayerData> players) {
                dataHolder.setPlayerDataArray(players);
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Array<PlayerData> players = dataHolder.getPlayerDataArray();
        Label[] labels = new Label[players.size];

        for (int i = 0; i < players.size; i++) {
            PlayerData player = players.get(i);
            String labelText = String.format("%d. %s (%d, %.0f%%)", i + 1, player.getName(), player.getWins(), player.getWinPercentage());
            labels[i] = new Label(labelText, skin);
            labels[i].setFontScale(2.4f, 2.4f);
        }
        return labels;
    }

    protected void activateStagesInputProcessor(){
        InputMultiplexer multiplexer = new InputMultiplexer();

        // Iterate over the actors and add their input processors to the multiplexer
        for (InputProcessor inputProcessor: inputProcessors) {
            multiplexer.addProcessor(inputProcessor);
        }
        Gdx.input.setInputProcessor(multiplexer);
    }

    private Table updateScoresList(Skin skin){
        Table scrollTable = new Table(skin);
        scrollTable.clear();
        Label[] scores = fill_bestPlayersList(skin);
        for(Label curLabel : scores){
            scrollTable.add(curLabel);
            scrollTable.row();
        }
        return scrollTable;
    }

    private void set_stage(){
        final Skin skin = new Skin(Gdx.files.internal(Constants.SKIN_COSMIC_PATH));
        Skin skin_def = new Skin(Gdx.files.internal("font_skins/default/uiskin.json"));

        ////////////////////////////////////////////////////////////////
        TextButton button = new TextButton("PLAY", skin);
        button.setSize(Constants.APP_WIDTH/3.5f, Constants.APP_HEIGHT/15);
        button.getLabel().setFontScale(1.8f, 1.8f);
        button.setPosition(Constants.APP_WIDTH/2 - button.getWidth()/2, Constants.APP_HEIGHT/4 - button.getHeight()/2);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                gsm.set(new PlayState(gsm));
                gsm.push(new PlayState(gsm, dbInterface));
//                gsm.push(new BattleResultState(gsm, "Sasha"));
            }
        });

        ////////////////////////////////////////////////////////////////
        Label nicknameLabel = new Label(this.nickname, skin);
        nicknameLabel.setFontScale(2.5f, 2.5f);
        nicknameLabel.setPosition(Constants.APP_WIDTH / 2 - nicknameLabel.getWidth(), Constants.APP_HEIGHT / 2 - nicknameLabel.getHeight() / 2);
        nicknameLabel.setColor(new Color(0, 0, 0.55f, 1));
        ////////////////////////////////////////////////////////////////
//        Texture buttonTexture = new Texture(Gdx.files.internal("assets/menuButton.png"));
        Texture buttonTexture = new Texture(Gdx.files.internal("buttons/menuButton.png"));
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        ImageButton menu_button = new ImageButton(buttonDrawable);
        menu_button.setSize(Constants.APP_WIDTH/5, Constants.APP_HEIGHT/10);
        menu_button.setPosition(Constants.APP_WIDTH/2 - menu_button.getWidth()/2, Constants.APP_HEIGHT / 2.5f - menu_button.getHeight()/2);
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
        bestPlayersButton.setSize(Constants.APP_WIDTH/3, Constants.APP_HEIGHT/16);
        bestPlayersButton.getLabel().setFontScale(1.5f, 1.5f);
        // Set the position of the button on the top right corner under the menu button
        bestPlayersButton.setPosition(Constants.APP_WIDTH/2 - bestPlayersButton.getWidth()/2,
                Constants.APP_HEIGHT/3 - bestPlayersButton.getHeight()/2);


        ////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////
        // Create the list of "Best players"

        final ScrollPane scoresList = new ScrollPane(updateScoresList(skin));
        scoresList.setHeight(Constants.APP_HEIGHT/2);
        scoresList.setWidth(Constants.APP_WIDTH / 2);
        scoresList.setPosition(Constants.APP_WIDTH  / 2 - scoresList.getWidth() / 2, Constants.APP_HEIGHT / 2 - scoresList.getHeight() / 2);

        Texture backgroundTexture = new Texture(Gdx.files.internal("backgroundImages/scroll.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        scoresList.getStyle().background = backgroundDrawable;


        scoresList.setVisible(false);
        bestPlayersButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                scoresList.setWidget(updateScoresList(skin));
                scoresList.setVisible(!scoresList.isVisible());
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////

        stage = new Stage(new ScreenViewport());
        stage.addActor(bestPlayersButton);
        stage.addActor(nicknameLabel);
        stage.addActor(button);
        stage.addActor(menu_button);
        stage.addActor(scoresList);
        stage.addActor(settingsTable);

        InputProcessor settingsInputProcessor = createMenuWindowTouchProcessor(settingsTable);
        InputProcessor scrollInputProcessor = createMenuWindowTouchProcessor(scoresList);

        inputProcessors.add(settingsInputProcessor);
        inputProcessors.add(scrollInputProcessor);
        inputProcessors.add(stage);

        activateStagesInputProcessor();
    }

    private Table createSettingsSubfield(Skin skin) {
        final Table settingsTable = new Table(skin);

        // Music Volume Slider
        Label musicLabel = new Label("Game Music", skin);
        musicLabel.setFontScale(2.2f, 2.2f);
        Slider musicSlider = new Slider(0, 1, 0.01f, false, skin);
        musicSlider.setValue((float)0.5);

        // Sound Effects Volume Slider
        Label soundLabel = new Label("Sound Effects", skin);
        soundLabel.setFontScale(2.2f, 2.2f);
        Slider soundSlider = new Slider(0, 1, 0.01f, false, skin);
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
        settingsTable.add(musicSlider).width(Constants.APP_WIDTH/4).height(Constants.APP_WIDTH/16).pad(10);
        settingsTable.row();
        settingsTable.add(soundLabel).pad(10);
        settingsTable.row();
        settingsTable.add(soundSlider).width(Constants.APP_WIDTH/4).height(Constants.APP_WIDTH/16).pad(10);
        settingsTable.row();
        settingsTable.add(exitButton).width(Constants.APP_WIDTH/5).height(Constants.APP_WIDTH/15).pad(10);

        settingsTable.setSize(Constants.APP_WIDTH/2, Constants.APP_HEIGHT/2);
        settingsTable.setPosition(Constants.APP_WIDTH / 2 - settingsTable.getWidth() / 2, Constants.APP_HEIGHT / 2 - settingsTable.getHeight() / 2);
        settingsTable.setVisible(false); // Initially hidden

        Texture backgroundTexture = new Texture(Gdx.files.internal("backgroundImages/scroll.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        settingsTable.setBackground(backgroundDrawable);

        return settingsTable;
    }

    @Override
    protected void handleInput() {
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, Constants.APP_WIDTH, Constants.APP_HEIGHT);
        sb.draw(emblem, Constants.APP_WIDTH / 2.0f - emblemWidth/ 2.0f, Constants.APP_HEIGHT * 0.86f - emblemHeight, emblemWidth, emblemHeight);
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

    public static InputProcessor createMenuWindowTouchProcessor(final Actor actorToHide) {
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

    public interface OnPlayersFetchedListener {
        void onPlayersFetched(Array<PlayerData> players);
    }

    public Stage getStage(){
        return stage;
    }

}
