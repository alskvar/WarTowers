package com.mygdx.wartowers.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.wartowers.utils.Constants;

import java.util.Random;

public class Warrior {
    private final int kind;
    private final int defence;
    private final int attack;
    private final int speed;
    private final Texture texture;

//    private final Random random;

    public  Warrior(){
        this(new Random().nextInt(2));
    }

    public Warrior(int kind){
//        this.random = new Random();
        this.kind = kind;
        this.defence = Constants.warriors_defence[this.kind];
        this.attack = Constants.warriors_attack[this.kind];
        this.speed = Constants.warriors_speed[this.kind];
        this.texture = new Texture("warriors/warrior" + kind + ".png");
    }

    public int getKind() {
        return kind;
    }

    public int getDefence() {
        return defence;
    }

    public int getAttack() {
        return attack;
    }

    public int getSpeed() {
        return speed;
    }

    public Texture getTexture() {
        return texture;
    }

    public void dispose() {
        texture.dispose();
    }
}
