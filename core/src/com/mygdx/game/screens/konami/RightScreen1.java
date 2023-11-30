package com.mygdx.game.screens.konami;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.logic.GameState;
import com.mygdx.game.logic.Main;
import com.mygdx.game.screens.ClickerScreen;

public class RightScreen1 implements Screen, GestureDetector.GestureListener {
    final Main game;

    // Scaling camera
    OrthographicCamera camera = new OrthographicCamera();

    // Off-screen Points
    float amountOfPoints = 0;
    float timer = 0;
    float boostedIdle = 0.125f;
    float clickValue = 0.025f;

    public RightScreen1(Main game) {
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
        update(Gdx.graphics.getDeltaTime());
        // Clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render texts
        game.font.getData().setScale((float) Gdx.graphics.getHeight() / 235);
        game.batch.begin();
        game.font.draw(game.batch, "??????", (float) Gdx.graphics.getWidth() / 2, (float) Gdx.graphics.getHeight() / 2);
        game.batch.end();
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

    private void update(float delta) {
        // Makes the timer go up faster.
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
        if (velocityX > 2000) game.setScreen(new LeftScreen2(game));
        if (velocityY > 2000) game.setScreen(new ClickerScreen(game));
        if (velocityY < -2000) game.setScreen(new ClickerScreen(game));
        if (velocityX < -2000) game.setScreen(new ClickerScreen(game));
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
