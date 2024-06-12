package com.mygdx.wartowers.sprites;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.wartowers.utils.Constants;

import java.util.Random;

public class Catastrophe {

    private float timeTillNextCatastrophe;
    private boolean waitTillSetNextCatastrophe;
    private boolean directionLeftToRight;
    private boolean eventHasStarted;
    private final Texture notifyTexture;
    private final Texture ctastropheTexture;
    private float animationTime;
    private static final float CATASTROPHE_INTERVAL = 10.0f;
    private static final float NOTIFY_INTERVAL = 3.0f;
    private static final float CATASTROPHE_DURATION = 1.00f;
    private final Random random;

    public Catastrophe() {
        this.timeTillNextCatastrophe = CATASTROPHE_INTERVAL;
        this.notifyTexture = new Texture("events/notify.png");
        this.ctastropheTexture = new Texture("events/event1.png");
        this.animationTime = 0f;
        this.waitTillSetNextCatastrophe = false;
        this.random = new Random();
        this.directionLeftToRight = random.nextBoolean();
        this.eventHasStarted = false;
    }

    public float getTimeTillNextCatastrophe() {
        return timeTillNextCatastrophe;
    }

    public boolean isEventHasStarted() {
        if (eventHasStarted){
            eventHasStarted = false;
            return true;
        }
        return false;
    }

    public void update(float dt) {
        if (!waitTillSetNextCatastrophe) {
            timeTillNextCatastrophe -= dt;
            if (timeTillNextCatastrophe <= 0) {
                waitTillSetNextCatastrophe = true;
                animationTime = 0f;
                eventHasStarted = true;
            }
        } else {
            animationTime += dt;
            if (animationTime >= CATASTROPHE_DURATION) {
                waitTillSetNextCatastrophe = false;
                timeTillNextCatastrophe = CATASTROPHE_INTERVAL;
                directionLeftToRight = random.nextBoolean();
                eventHasStarted = false;
            }
        }
    }

    public void render(SpriteBatch sb) {
        if (waitTillSetNextCatastrophe) {
            float positionX;
            if (directionLeftToRight) {
                positionX = (Constants.APP_WIDTH + ctastropheTexture.getWidth()) * (animationTime / CATASTROPHE_DURATION) - ctastropheTexture.getWidth();
            } else {
                positionX = (Constants.APP_WIDTH + ctastropheTexture.getWidth()) * (1 - (animationTime / CATASTROPHE_DURATION)) - ctastropheTexture.getWidth();
            }
            float positionY = 0f;

            sb.draw(ctastropheTexture, positionX, positionY, Constants.APP_WIDTH, Constants.APP_HEIGHT);
        } else {
            if (timeTillNextCatastrophe <= NOTIFY_INTERVAL) {
                float notifyX, notifyY;
                if (directionLeftToRight) {
                    notifyX = Constants.APP_WIDTH * 0.06f;
                } else {
                    notifyX = Constants.APP_WIDTH * 0.8f;
                }
                notifyY = Constants.APP_HEIGHT * 0.8f;
                sb.draw(notifyTexture, notifyX, notifyY, Constants.APP_WIDTH/6, Constants.APP_WIDTH/6);
            }
        }
    }

    public void dispose() {
        notifyTexture.dispose();
        ctastropheTexture.dispose();
    }
}

