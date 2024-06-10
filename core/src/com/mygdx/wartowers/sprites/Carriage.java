package com.mygdx.wartowers.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Carriage {
    private final Warrior warrior;
    private final int owner;
    private final int destination;
    private final Array<Battleground.ConnectionGraph> path;
    private int numberOfPassedTowers;
    private final int[] info;

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

        // Initialize the direction and remaining distance to the next tower
        this.direction = new Vector3();
        this.remainingDistanceToNextTower = -1;
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

    public void update(float dt, Array<Tower> towers) {
        if (hasReachedDestination()) {
            return;
        }

        // Update direction and remaining distance if moving to the next tower
        if (remainingDistanceToNextTower <= 0) {
            Tower currentTower = towers.get(path.get(numberOfPassedTowers - 1).toTowerId);
            Tower nextTower = towers.get(path.get(numberOfPassedTowers).toTowerId);
            direction.set(nextTower.getPosition()).sub(currentTower.getPosition()).nor();
            remainingDistanceToNextTower = currentTower.getPosition().dst(nextTower.getPosition());
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
        sb.draw(warrior.getTexture(), currentPosition.x, currentPosition.y);
    }
}



