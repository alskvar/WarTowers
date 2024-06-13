package com.mygdx.wartowers.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.wartowers.Database.FireStoreInterface;
import com.mygdx.wartowers.utils.Constants;

public class LoginState extends State {

    private Stage stage;
    private TextField nicknameField;
    private TextButton loginButton;
    private Label errorLabel;
    private Texture background;
    private FireStoreInterface dbInterface;

    public LoginState(GameStateManager gsm, FireStoreInterface dbInterface) {
        super(gsm);
        this.dbInterface = dbInterface;
        background = new Texture("backgroundImages/mainMenu_bg.jpg");
        setStage();
    }

    private void setStage() {
        stage = new Stage(new ScreenViewport());
        Skin skin = new Skin(Gdx.files.internal(Constants.SKIN_COSMIC_PATH));

        nicknameField = new TextField("", skin);
        nicknameField.getStyle().font.getData().setScale(1.8f);
        nicknameField.setMessageText("Enter your nickname");
        nicknameField.setSize(Constants.APP_WIDTH / 3.0f, Constants.APP_HEIGHT / 15);
        nicknameField.setPosition(Constants.APP_WIDTH / 2.0f - nicknameField.getWidth() / 2, Constants.APP_HEIGHT / 2 - nicknameField.getHeight() / 2);

        loginButton = new TextButton("Login", skin);
        loginButton.setSize(Constants.APP_WIDTH / 3.0f, Constants.APP_HEIGHT / 14);
        loginButton.setPosition(Constants.APP_WIDTH / 2 - loginButton.getWidth() / 2, Constants.APP_HEIGHT / 3 - loginButton.getHeight());
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleLogin();
            }
        });

        errorLabel = new Label("", skin);
        errorLabel.setColor(1, 0, 0, 1);  // red color
        errorLabel.setPosition(Constants.APP_WIDTH / 2 - errorLabel.getWidth() / 2, Constants.APP_HEIGHT / 2 - 50);

        stage.addActor(nicknameField);
        stage.addActor(loginButton);
        stage.addActor(errorLabel);

        Gdx.input.setInputProcessor(stage);
    }

    private void handleLogin() {
        String nickname = nicknameField.getText();
        if (nickname.trim().isEmpty() || nickname.length() > 15) {
            errorLabel.setText("Nickname cannot be empty and bigger than 15 symbols");
            return;
        }
        gsm.set(new MenuState(gsm, dbInterface, nickname.trim()));
    }

    @Override
    protected void handleInput() {
    }

    @Override
    protected void activateStagesInputProcessor() {

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
