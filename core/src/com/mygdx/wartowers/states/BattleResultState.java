package com.mygdx.wartowers.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.wartowers.WarTowers;
import com.mygdx.wartowers.sprites.BattleResult;
import com.mygdx.wartowers.utils.Constants;

public class BattleResultState extends State {
    private final Stage stage;
    private final Texture background;
    private final String winnerName;
    private boolean changeDB;
//    private final ImageButton menuButton;
//    private final ImageButton menuButton;

    public BattleResultState(final GameStateManager gsm, String winnerName, String loserName, boolean changeDB) {
        super(gsm);
        this.winnerName = winnerName;

        this.changeDB = changeDB;
        background = new Texture("backgroundImages/battleResult_bg.jpg");

        Skin skin = new Skin(Gdx.files.internal(Constants.SKIN_COSMIC_PATH));

        if (changeDB) {
            BattleResult btr = new BattleResult(winnerName, loserName, winnerName);
            WarTowers.dbInterface.updateBattleResult(btr);
        }

        // Create winner label
        Label winnerLabel = new Label("Winner: \n" + winnerName, skin);
        winnerLabel.setFontScale(Constants.APP_WIDTH/350.0f);
//        winnerLabel.setPosition(Constants.APP_WIDTH/2 - winnerLabel.getWidth()/2, Constants.APP_HEIGHT/3);

        TextButton button = new TextButton("BACK", skin);
        button.setSize(Constants.APP_WIDTH/4, Constants.APP_HEIGHT/12);
        button.getLabel().setFontScale(2.0f, 2.0f);
        button.setPosition(Constants.APP_WIDTH/2 - button.getWidth()/2, Constants.APP_HEIGHT/10);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.pop();
                gsm.peek().activateStagesInputProcessor();
            }
        });

        Table table = new Table();
        table.add(winnerLabel).padBottom(50);
        table.setPosition(Constants.APP_WIDTH/2 - table.getWidth()/2, Constants.APP_HEIGHT/2.2f - table.getHeight() - 50);


        stage = new Stage(new ScreenViewport());
        stage.addActor(table);
        stage.addActor(button);
//
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    protected void handleInput() {
        // Input is handled by stage
    }

    @Override
    public void update(float dt) {
        stage.act(dt);
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sb.end();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
    }

    @Override
    protected void activateStagesInputProcessor() {
        Gdx.input.setInputProcessor(stage);
    }
}

