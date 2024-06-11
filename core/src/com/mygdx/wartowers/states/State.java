package com.mygdx.wartowers.states;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public abstract class State {
    protected OrthographicCamera camera;
    protected Vector3 mouse;
    protected GameStateManager gsm;
    protected Stage stage;
    protected Array<InputProcessor> inputProcessors;

    public State(GameStateManager gsm){
        inputProcessors = null;
        stage = null;
        this.gsm = gsm;
        camera = new OrthographicCamera();
        mouse = new Vector3();
    }

    protected abstract void handleInput();
    protected abstract void activateStagesInputProcessor();
    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb);
    public abstract void dispose();

}
