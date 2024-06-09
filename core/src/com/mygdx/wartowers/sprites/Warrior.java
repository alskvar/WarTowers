package com.mygdx.wartowers.sprites;

import com.mygdx.wartowers.utils.Constants;

import java.util.Random;

public class Warrior {
    private final int kind;
    private final int defence;
    private final int attack;

//    private final Random random;

    public  Warrior(){
        this(new Random().nextInt(2));
    }

    public Warrior(int kind){
//        this.random = new Random();
        this.kind = kind;
        this.defence = Constants.warriors_defence[this.kind];
        this.attack = Constants.warriors_attack[this.kind];

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
}
