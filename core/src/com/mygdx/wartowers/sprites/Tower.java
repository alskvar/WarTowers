package com.mygdx.wartowers.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.wartowers.utils.Constants;

public class Tower  {

    private Vector3 position;
    private Texture towerTexture;
    private final BitmapFont font;

    private final int id;
    private int level;
    private int owner;
    private int amount;
    private final Warrior warrior;
    private long lastWarriorsUpdateTime;

    private boolean selected;

    private final int [] capacity;
    private final long [] interval;
    private final int [] transferVal;

    public final int CAP1 = 20;
    public final int CAP2 = 40;
    public final long INTERVAL1 = 1000000000;
    public final long INTERVAL2 = 700000000;
    public final long INTERVAL_DIE = 1000000000;



    public Tower(int x, int y, int id, int owner, int level, int amount, int warriorKind){
        this.font = new BitmapFont();
        this.warrior = new Warrior(warriorKind);
        this.selected = false;
        this.owner = owner;
        this.id = id;
        this.level = level;
        if (Gdx.files.internal(Constants.TowerSkins[owner][level]).exists()) {
            Gdx.app.log("Asset Check", "File exists: " + Constants.TowerSkins[owner][level]);
            this.towerTexture = new Texture(Constants.TowerSkins[owner][level]);
        } else {
            Gdx.app.log("Asset Check", "File does not exist: " + Constants.TowerSkins[owner][level]);
        }
        this.position = new Vector3(x - towerTexture.getWidth()/2.0f, y, 1);
        this.amount = amount;
        capacity = new int[]{CAP1, CAP2};
        interval = new long[]{INTERVAL1, INTERVAL2};
        transferVal = new int[]{1, 2, 4};
        lastWarriorsUpdateTime = TimeUtils.nanoTime();
    }


    public void reloadTexture(){
        this.position = new Vector3(position.x + towerTexture.getWidth()/2.0f, position.y, 1);
        towerTexture.dispose();
        towerTexture = new Texture(Constants.TowerSkins[owner][level]);
        this.position = new Vector3(position.x - towerTexture.getWidth()/2.0f, position.y, 1);
    }

    public void upgradeTower(){
        if(owner != 1 || level == 1 || amount < 15)
            return;
        level = 1;
        reloadTexture();
    }

    public void setOwner(int owner){
        this.owner = owner;
    }

    public void setAmount(int amount){
        this.amount = amount;
    }

    private boolean itISTime(boolean increase){
        if(TimeUtils.nanoTime() - lastWarriorsUpdateTime > ((increase) ? interval[level] : INTERVAL_DIE)){
            lastWarriorsUpdateTime = TimeUtils.nanoTime();
            return true;
        }
        return false;
    }

    private void updateWarriors(){
        if(amount < capacity[level])
            if( itISTime(true))  amount++;
        if(amount > capacity[level])
            if( itISTime(false))  amount--;
//        System.out.println(amount);
    }

    public boolean overlap(float x, float y){
//        System.out.println("x: " + x + ", y: " + y + ", Tx: " + position.x + ", Ty:" + position.y);
        if(position.x > x || position.x + towerTexture.getWidth() < x)
            return false;
        if(position.y > y || position.y + towerTexture.getHeight() < y)
            return false;
//        System.out.println("ok");
        return true;
    }

    public void setSelected(boolean res){
        if (owner == 1)
            selected = res;
    }

    public int[] transferOut(int kind){
        /*
        ans[0] - amount,
        ans[1] - coeficient of attack
        ans[2] - owner of tower
        */
        int[] ans = new int[3];
        ans[0] = amount/transferVal[kind];
        ans[1] = warrior.getAttack();
        ans[2] = owner;
        amount -= ans[0];
        return ans;
    }

    public void transferIn(int[] gift){
//        System.out.println("" + gift[0] + ", " + gift[1] + ", " + gift[2]);
        if(owner == gift[2]){
            amount += gift[0];
            System.out.println("same owner = " + owner);
            return;
        }
        int defence = amount * warrior.getDefence();
        int attack = gift[0] * gift[1];
        if(defence >= attack){
            System.out.println("def wins");
            amount = (defence - attack)/warrior.getDefence();
        }
        else{
            System.out.println("" + owner + " now is " + gift[2]);
            amount = (attack - defence)/gift[1];
            owner = gift[2];
            reloadTexture();
        }
    }

    public void update(float dt){
        updateWarriors();
    }

    public Vector3 getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }

    public Texture getTowerTexture() {
        return towerTexture;
    }

    public BitmapFont getFont() {
        return font;
    }

    public boolean isSelected() {
        return selected;
    }

    public int getAmount() {
        return amount;
    }

    public int getOwner() {
        return owner;
    }

    public Warrior getWarrior() { return warrior;}

    public void dispose() {
        towerTexture.dispose();
        font.dispose();
    }
}

