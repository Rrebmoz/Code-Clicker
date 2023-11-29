package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import com.mygdx.game.chapters.*;

public class ClickerScreen implements Screen, GestureDetector.GestureListener {
    Main game;

    // Scaling camera
    OrthographicCamera camera = new OrthographicCamera();

    // Points variables
    float amountOfPoints = 0;
    float timer = 0;
    float boostedIdle = 0.125f;

    // Click button variables
    Rectangle clickButtonBounds = new Rectangle((float) Gdx.graphics.getWidth() / 6, (float) Gdx.graphics.getHeight() / 3,(float) Gdx.graphics.getWidth() / 4, (float) Gdx.graphics.getHeight() / 3);
    Color clickButtonColor = Color.BROWN;
    boolean clickButtonPressed = false;

    // Reset button variables
    Rectangle resetButtonBounds = new Rectangle((float) Gdx.graphics.getWidth() / 2.0f - (float) Gdx.graphics.getWidth() / 20,
            10,
            (float) Gdx.graphics.getWidth() / 8,
            (float) Gdx.graphics.getHeight() / 12);
    Color resetButtonColor = Color.FIREBRICK;

    // Upgrade buttons variables
    final int NUM_UPGRADE_BUTTONS = 7;
    final Color UPGRADE_COLOR = Color.FOREST;
    final int[] UPGRADE_PRICES = { 5, 120, 288, 700, 1680, 4032, 10000 };
    final int IDLE_MULTIPLIER = 2;
    float clickValue = 0.025f;
    List<Rectangle> upgradeButtonBounds;
    List<Color> upgradeButtonColors;
    List<Boolean> upgradeButtonPressed;

    // Achievement button variables
    Rectangle achievementsScreenButtonBounds = new Rectangle((float) Gdx.graphics.getWidth() /50, Gdx.graphics.getHeight() / 2.75f, Gdx.graphics.getWidth() / 7.8f, Gdx.graphics.getHeight() / 3.65f); // Define your button's position and size
    Color achievementsScreenButtonColor = Color.GOLD;

    public ClickerScreen(Main game) {
        this.game = game;
        camera.setToOrtho(false, 800, 480);
        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
        amountOfPoints = prefs.getFloat("points", amountOfPoints);
        boostedIdle = prefs.getFloat("boostedIdle", boostedIdle);
        clickValue = prefs.getFloat("clickValue", clickValue);
        initializeUpgradeButtons(prefs);
        // Check for swiping
        GestureDetector gestureDetector = new GestureDetector(this);
        Gdx.input.setInputProcessor(gestureDetector);
    }


    @Override
    public void render(float delta) {
        handleClick();
        update(Gdx.graphics.getDeltaTime());

        // Clear screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render buttons
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(clickButtonColor);
        game.shapeRenderer.rect(clickButtonBounds.x, clickButtonBounds.y, clickButtonBounds.width, clickButtonBounds.height);
        game.shapeRenderer.setColor(resetButtonColor);
        game.shapeRenderer.rect(resetButtonBounds.x, resetButtonBounds.y, resetButtonBounds.width, resetButtonBounds.height);
        game.shapeRenderer.setColor(achievementsScreenButtonColor);
        game.shapeRenderer.rect(achievementsScreenButtonBounds.x, achievementsScreenButtonBounds.y, achievementsScreenButtonBounds.width, achievementsScreenButtonBounds.height);

        for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
            game.shapeRenderer.setColor(upgradeButtonColors.get(i));
            game.shapeRenderer.rect(upgradeButtonBounds.get(i).x, upgradeButtonBounds.get(i).y,
                    upgradeButtonBounds.get(i).width, upgradeButtonBounds.get(i).height);
        }
        game.shapeRenderer.end();


        // Render text
        float heightChanger = (float) Gdx.graphics.getHeight() / 54f;
        game.font.getData().setScale((float) Gdx.graphics.getHeight() / 235);
        game.batch.begin();
        game.font.draw(game.batch, "Points: " + String.format(Locale.US, "%.3f", amountOfPoints), 10, Gdx.graphics.getHeight() - heightChanger);
        game.font.draw(game.batch, "Idle points: " + boostedIdle + "/s", 10, Gdx.graphics.getHeight() - heightChanger * 5);
        game.font.draw(game.batch, "Click value: " + clickValue + "/click", 10, Gdx.graphics.getHeight() - heightChanger * 9);
        game.font.draw(game.batch, "ACH", (float) Gdx.graphics.getWidth() / 21, Gdx.graphics.getHeight() / 1.85f);
        game.font.draw(game.batch, "RESET", resetButtonBounds.x + (float) Gdx.graphics.getWidth() / 128, resetButtonBounds.y + resetButtonBounds.height - 10);
        for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
            if (upgradeButtonColors.get(i) == UPGRADE_COLOR) {
                game.font.draw(game.batch, "Chapter " + (i + 1) + " -->", upgradeButtonBounds.get(i).x + 10, upgradeButtonBounds.get(i).y + upgradeButtonBounds.get(i).height - 10);
            } else {
                if (amountOfPoints >= UPGRADE_PRICES[i]) {
                    // Able to buy the button, change color to green
                    upgradeButtonColors.set(i, Color.GREEN);
                    game.font.draw(game.batch, "Cost: " + UPGRADE_PRICES[i], upgradeButtonBounds.get(i).x + 10, upgradeButtonBounds.get(i).y + upgradeButtonBounds.get(i).height - 10);
                } else {
                    // Not enough points to buy, keep the original color
                    upgradeButtonColors.set(i, Color.LIGHT_GRAY);
                    game.font.draw(game.batch, "Cost: " + UPGRADE_PRICES[i], upgradeButtonBounds.get(i).x + 10, upgradeButtonBounds.get(i).y + upgradeButtonBounds.get(i).height - 10);
                }
            }
        }
        game.batch.end();
    }

    @Override
    public void show() {

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
        // Dispose textures, fonts, and other resources loaded in the Clicker class
        game.dispose();

        // Dispose the ShapeRenderer and Batch used in this screen
        game.shapeRenderer.dispose();
        game.batch.dispose();

        // Dispose any other resources specific to this screen
        // Clear lists and objects to release memory
        upgradeButtonBounds.clear();
        upgradeButtonColors.clear();
        upgradeButtonPressed.clear();
    }


    private void handleClick() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            //Redirects to Chapter class
            if (upgradeButtonBounds.get(0).contains(touchX, touchY) && upgradeButtonColors.get(0) == UPGRADE_COLOR) {
                game.setScreen(new Chapter1Screen(game));
            } else if (upgradeButtonBounds.get(1).contains(touchX, touchY) && upgradeButtonColors.get(1) == UPGRADE_COLOR) {
                game.setScreen(new Chapter2Screen(game));
            } else if (upgradeButtonBounds.get(2).contains(touchX, touchY) && upgradeButtonColors.get(2) == UPGRADE_COLOR) {
                game.setScreen(new Chapter3Screen(game));
            } else if (upgradeButtonBounds.get(3).contains(touchX, touchY) && upgradeButtonColors.get(3) == UPGRADE_COLOR) {
                game.setScreen(new Chapter4Screen(game));
            } else if (upgradeButtonBounds.get(4).contains(touchX, touchY) && upgradeButtonColors.get(4) == UPGRADE_COLOR) {
                game.setScreen(new Chapter5Screen(game));
            } else if (upgradeButtonBounds.get(5).contains(touchX, touchY) && upgradeButtonColors.get(5) == UPGRADE_COLOR) {
                game.setScreen(new Chapter6Screen(game));
            } else if (upgradeButtonBounds.get(6).contains(touchX, touchY) && upgradeButtonColors.get(6) == UPGRADE_COLOR) {
                game.setScreen(new Chapter7Screen(game));
            }

            // Check if the click is on the click button, reset button, or upgrade buttons
            if (clickButtonBounds.contains(touchX, touchY)) {
                clickButtonPressed = true;
            } else if (resetButtonBounds.contains(touchX, touchY)) {
                // If the reset button is pressed, clear preferences and reset game state
                resetGame();
            } else if (achievementsScreenButtonBounds.contains(touchX, touchY)) {
                // Handle button press - navigate to AchievementsScreen
                Gdx.app.log("ClickerScreen", "Achievement screen button pressed!");
                game.setScreen(new AchievementsScreen(game));
            } else {
                for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
                    if (upgradeButtonBounds.get(i).contains(touchX, touchY)) {
                        // Check if there are enough points to buy the upgrade
                        if (amountOfPoints >= UPGRADE_PRICES[i] && upgradeButtonColors.get(i) != UPGRADE_COLOR) {
                            upgradeButtonPressed.set(i, true);
                            upgradeButtonColors.set(i, UPGRADE_COLOR);
                            amountOfPoints -= UPGRADE_PRICES[i];
                            boostedIdle *= IDLE_MULTIPLIER;
                            clickValue *= 2.0f;
                            GameState.save(amountOfPoints, boostedIdle, clickValue);
                            GameState.save(i); // Save only when enough points
                        }
                        break;
                    }
                }
            }
        }
    }

    private void resetGame() {
        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
        prefs.clear(); // Clears all preferences related to "MyGamePreferences"
        prefs.flush();

        // Reset variables to initial values.
        amountOfPoints = 0;
        boostedIdle = 0.125f;
        clickValue = 0.025f;

        // Reset upgrade button colors and states
        for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
            upgradeButtonColors.set(i, Color.LIGHT_GRAY);
            upgradeButtonPressed.set(i, false);
        }
        GameState.save(amountOfPoints, boostedIdle, clickValue);
    }

    private void initializeUpgradeButtons(Preferences prefs) {
        upgradeButtonBounds = new ArrayList<>();
        upgradeButtonColors = new ArrayList<>();
        upgradeButtonPressed = new ArrayList<>();

        // Define ratios or percentages for button positioning and sizing
        float initialYRatio = 1.18f; // Percentage of screen height for initial Y position
        float buttonHeightRatio = 0.1f;
        float buttonWidthRatio = 0.2f;
        float verticalSpacingRatio = 0.12f;

        for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
            float initialY = Gdx.graphics.getHeight() / initialYRatio;
            float buttonHeight = Gdx.graphics.getHeight() * buttonHeightRatio;
            float buttonWidth = Gdx.graphics.getWidth() * buttonWidthRatio;
            float verticalSpacing = Gdx.graphics.getHeight() * verticalSpacingRatio;

            Rectangle button = new Rectangle(
                    Gdx.graphics.getWidth() * 4.7f / 6,
                    initialY - i * verticalSpacing,
                    buttonWidth,
                    buttonHeight
            );
            upgradeButtonBounds.add(button);

            boolean isButtonClicked = prefs.getBoolean("upgradeButton_" + i, false);
            if (isButtonClicked) {
                upgradeButtonColors.add(UPGRADE_COLOR);
            } else {
                upgradeButtonColors.add(Color.LIGHT_GRAY);
            }
            upgradeButtonPressed.add(false);
        }
    }

    // Update game logic
    private void update(float delta) {
        // Makes the timer go up faster.
        timer += delta;

        // Timer reset
        if (timer >= 1.0f) {
            amountOfPoints += 1 * boostedIdle;
            timer -= 1.0f;
            GameState.save(amountOfPoints, boostedIdle, clickValue);
        }

        // Visual feedback for clicking button
        if (clickButtonPressed) {
            clickButtonColor = Color.BLACK;
            amountOfPoints += clickValue;
            clickButtonPressed = false;
            GameState.save(amountOfPoints, boostedIdle, clickValue);
        } else {
            clickButtonColor = Color.BROWN;
        }

        // Check for upgrade button clicks and apply upgrades if affordable
        for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
            if (upgradeButtonPressed.get(i) && upgradeButtonColors.get(i) != UPGRADE_COLOR
                    && amountOfPoints >= UPGRADE_PRICES[i]) {
                upgradeButtonColors.set(i, UPGRADE_COLOR);
                amountOfPoints -= UPGRADE_PRICES[i];
                boostedIdle *= IDLE_MULTIPLIER;
                GameState.save(amountOfPoints, boostedIdle, clickValue);
                break;
            }
            upgradeButtonPressed.set(i, false);
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
        if (velocityX > 2000) game.setScreen(new AchievementsScreen(game));
        if (velocityX < -2000 && upgradeButtonColors.get(0) == UPGRADE_COLOR) game.setScreen(new Chapter1Screen(game));
        else if (velocityX < -2000 && upgradeButtonColors.get(1) == UPGRADE_COLOR) game.setScreen(new Chapter2Screen(game));
        else if (velocityX < -2000 && upgradeButtonColors.get(2) == UPGRADE_COLOR) game.setScreen(new Chapter3Screen(game));
        else if (velocityX < -2000 && upgradeButtonColors.get(3) == UPGRADE_COLOR) game.setScreen(new Chapter4Screen(game));
        else if (velocityX < -2000 && upgradeButtonColors.get(4) == UPGRADE_COLOR) game.setScreen(new Chapter5Screen(game));
        else if (velocityX < -2000 && upgradeButtonColors.get(5) == UPGRADE_COLOR) game.setScreen(new Chapter6Screen(game));
        else if (velocityX < -2000 && upgradeButtonColors.get(6) == UPGRADE_COLOR) game.setScreen(new Chapter7Screen(game));
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
