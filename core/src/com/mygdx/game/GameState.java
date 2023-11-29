package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameState {

    // Saves the game to local storage.
    public static void save(float amountOfPoints, float boostedIdle, float clickValue) {
        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
        prefs.putFloat("points", amountOfPoints);
        prefs.putFloat("boostedIdle", boostedIdle);
        prefs.putFloat("clickValue", clickValue);
        prefs.flush();
    }

    // Dynamically save upgrade button to localstorage.
    static void save(int buttonIndex) {
        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
        prefs.putBoolean("upgradeButton_" + buttonIndex, true);
        prefs.flush();
    }
}
