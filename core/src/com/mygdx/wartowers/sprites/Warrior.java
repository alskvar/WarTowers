package com.mygdx.wartowers.sprites;

import com.mygdx.wartowers.utils.Constants;

import java.util.Random;

public class Warrior {
    private final int kind;
    private final int defence;
    private final int attack;

    Random random;

    public Warrior(){
        random = new Random();
        kind = random.nextInt(2);
        defence = Constants.warriors_defence[kind];
        attack = Constants.warriors_attack[kind];

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
