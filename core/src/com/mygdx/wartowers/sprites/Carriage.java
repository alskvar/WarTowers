package com.mygdx.wartowers.sprites;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mygdx.wartowers.utils.Constants;

public class Carriage {
    private final Warrior warrior;
    private final int owner;
    private final int destination;
    private final Array<Battleground.ConnectionGraph> path;
    private int numberOfPassedTowers;
    private final int[] info;
    private final BitmapFont font;
    private float remainingTimeToHide;

    private final Vector3 currentPosition;
    private final Vector3 direction;
    private float remainingDistanceToNextTower;

    public Carriage(int owner, int troopsKind, int[] info,  Array<Battleground.ConnectionGraph> path, Vector3 startPosition) {
        this.owner = owner;
        this.warrior = new Warrior(troopsKind);
        this.info = info;
        this.destination = path.peek().toTowerId;
        this.path = new Array<>(path);
        this.numberOfPassedTowers = 1;
        this.currentPosition = new Vector3(startPosition);

        this.direction = new Vector3();
        this.remainingDistanceToNextTower = -1;

        this.font = new BitmapFont();
        this.remainingTimeToHide = 0;
        font.getData().scale(1.3f);
        font.getData().markupEnabled = true;
        font.setColor(Constants.playerColors[owner]);
    }

    public int getWarriorsType() {
        return warrior.getKind();
    }

    public int getOwner() {
        return owner;
    }

    public int getDestination() {
        return destination;
    }

    public Array<Battleground.ConnectionGraph> getPath() {
        return path;
    }

    public int getNumberOfPassedTowers() {
        return numberOfPassedTowers;
    }

    public boolean hasReachedDestination() {
        return numberOfPassedTowers == path.size;
    }

    public void moveToNextTower() {
        if (numberOfPassedTowers < path.size) {
            numberOfPassedTowers++;
        }
    }

    public int[] getInfo() {
        return info;
    }

    public void update(float dt, Array<Tower> towers, final boolean hide, final boolean catastropheIsNow) {
        if (hasReachedDestination()) {
            return;
        }
        if (hide) {
            System.out.println("Carriage hide");
            remainingTimeToHide = Constants.CARRIAGE_HIDE_TIME;
            return;
        }
        if (remainingTimeToHide > 0f) {
            System.out.println("Carriage hide time " + remainingTimeToHide);
            remainingTimeToHide = Math.max(0f, remainingTimeToHide - dt);
            return;
        }
        if (catastropheIsNow && remainingTimeToHide == 0f) {
            damage(Constants.damagedPart[1]);
        }

        // Update direction and remaining distance if moving to the next tower
        if (remainingDistanceToNextTower <= 0) {
            Tower currentTower = towers.get(path.get(numberOfPassedTowers - 1).toTowerId);
            Tower nextTower = towers.get(path.get(numberOfPassedTowers).toTowerId);
            direction.set(nextTower.getCenterX()).sub(currentTower.getCenterX()).nor();
            remainingDistanceToNextTower = currentTower.getCenterX().dst(nextTower.getCenterX());
        }

        float distanceToMove = warrior.getSpeed() * dt;

        if (distanceToMove >= remainingDistanceToNextTower) {
            distanceToMove = remainingDistanceToNextTower;
            moveToNextTower();
        }

        currentPosition.mulAdd(direction, distanceToMove);
        remainingDistanceToNextTower -= distanceToMove;
    }

    public void render(SpriteBatch sb) {
        sb.draw(warrior.getTexture(), currentPosition.x - warrior.getTexture().getWidth()/2.0f, currentPosition.y);
        String amountText = "" + getAmount();
        font.draw(sb, amountText, currentPosition.x - warrior.getTexture().getWidth()/2.0f + 5, currentPosition.y + warrior.getTexture().getHeight() + 25);
    }

    public int getAmount() {
        return info[0];
    }

    public void damage(float damage) {
        if (info[0] > 0) {
            if (info[0] == 1){
                info[0] = 0;
            }else{
                info[0] -= (int) (info[0] * damage);
            }
        }
    }
}



