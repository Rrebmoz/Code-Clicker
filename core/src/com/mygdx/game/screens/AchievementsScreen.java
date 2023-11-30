package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.logic.GameState;
import com.mygdx.game.logic.Main;

public class AchievementsScreen implements Screen, GestureDetector.GestureListener {
    final Main game;

    // Scaling camera
    OrthographicCamera camera = new OrthographicCamera();

    // Achievement button variables
    Rectangle achievementsScreenButtonBounds = new Rectangle((float) Gdx.graphics.getWidth() - (Gdx.graphics.getWidth() / 6.7f), Gdx.graphics.getHeight() / 2.75f, Gdx.graphics.getWidth() / 7.8f, Gdx.graphics.getHeight() / 3.65f); // Define your button's position and size
    Color achievementsScreenButtonColor = Color.GOLD;

    // Off-screen Points
    float amountOfPoints = 0;
    final int[] UPGRADE_PRICES = { 5, 150, 400, 1000, 2500, 6000, 15000 };
    float timer = 0;
    float boostedIdle = 0.125f;
    float clickValue = 0.025f;

    public AchievementsScreen(Main game) {
        this.game = game;
        camera.setToOrtho(false,800,480);
        // Check for swiping
        GestureDetector gestureDetector = new GestureDetector(this);
        Gdx.input.setInputProcessor(gestureDetector);

        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
        amountOfPoints = prefs.getFloat("points", amountOfPoints);
        boostedIdle = prefs.getFloat("boostedIdle", boostedIdle);
        clickValue = prefs.getFloat("clickValue", clickValue);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        handleClick();
        update(Gdx.graphics.getDeltaTime());
        // Clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render shapes
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(achievementsScreenButtonColor);
        game.shapeRenderer.rect(achievementsScreenButtonBounds.x, achievementsScreenButtonBounds.y, achievementsScreenButtonBounds.width, achievementsScreenButtonBounds.height);
        game.shapeRenderer.end();

        // Render texts
        game.font.getData().setScale((float) Gdx.graphics.getHeight() / 235);
        game.font.setColor(Color.BLACK);
        game.batch.begin();
        game.font.draw(game.batch, "--->", (float) Gdx.graphics.getWidth() - (Gdx.graphics.getWidth() / 8.5f), Gdx.graphics.getHeight() / 1.85f);
        game.batch.end();
        displayAchievements();
    }

    private void displayAchievements() {
        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
        float yPosition = Gdx.graphics.getHeight() - 50; // Adjust Y position for displaying achievements

        // Iterating through potential achievements
        for (int i = 1; i <= UPGRADE_PRICES.length; i++) {
            boolean achievementUnlocked = prefs.getBoolean("achievement_" + i, false);

            if (achievementUnlocked) game.font.setColor(Color.FOREST);
            else game.font.setColor(Color.RED);
            game.batch.begin();
            game.font.draw(game.batch, "Reach " + UPGRADE_PRICES[i - 1] + " points", 50, yPosition);
            game.batch.end();
            yPosition -= (float) Gdx.graphics.getHeight() / 15;
        }
        //TODO
        boolean achievementUnlocked = prefs.getBoolean("achievement_8", false);
        if (achievementUnlocked) {
            game.font.setColor(Color.FOREST);
            game.batch.begin();
            game.font.draw(game.batch, "UP UP DOWN DOWN LEFT RIGHT", 50, yPosition);
            yPosition -= (float) Gdx.graphics.getHeight() / 15;
            game.font.draw(game.batch, "LEFT RIGHT B A START", 50, yPosition);
            game.batch.end();
        }
        else {
            game.font.setColor(Color.RED);
            game.batch.begin();
            game.font.draw(game.batch, "???", 50, yPosition);
            game.batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
    private void handleClick() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Check if the click is on the click button, reset button, or upgrade buttons
            if (achievementsScreenButtonBounds.contains(touchX, touchY)) {
                game.setScreen(new ClickerScreen(game));
            }
        }
    }

    private void update(float delta) {
        // Makes the timer go up.
        timer += delta;

        // Timer reset
        if (timer >= 1.0f) {
            amountOfPoints += 1 * boostedIdle;
            timer -= 1.0f;
            GameState.save(amountOfPoints, boostedIdle, clickValue);
        }
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        if (velocityX < -2000) {
            game.setScreen(new ClickerScreen(game));
            return true;
        }
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
