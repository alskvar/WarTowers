package com.mygdx.wartowers.utils;


import com.badlogic.gdx.graphics.Color;

public class Constants {
    public static float APP_WIDTH = 480;
    public static float APP_HEIGHT = 800;
    public static final float CARRIAGE_HIDE_TIME = 2;
    public static final String BATTLEGROUND_JSON_PATH = "BattlegroundPresets/preset1.json";
    public static final String SKIN_COSMIC_PATH = "font_skins/comic/comic-ui.json";

    public static final String APP_TITLE = "War Towers";

    public static final String[][] TowerSkins = {
            {"towers/TowerGray0_res.png", "towers/TowerGrayUp_res.png"},
            {"towers/TowerBlue0_res.png", "towers/TowerBlueUp_res.png"},
            {"towers/TowerRed0_res.png", "towers/TowerRedUp_res.png"},};

    public static final int[] warriors_defence = {3, 2};
    public static final int[] warriors_attack = {2, 3};
    public static int[] warriors_speed = {80, 120};

    public static final String[] warriors_names = {"shield-bearer", "assassin"};


    public static final Color[] playerColors = {
            new Color(0.5f, 0.5f, 0.5f, 1),
            new Color(0, 0, 1, 1),
            new Color(1, 0, 0, 1)
    };

    public static final float[] damagedPart = {0.2f, 0.3f, 0.5f};

}
