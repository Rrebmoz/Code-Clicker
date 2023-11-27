package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Clicker extends Game {
	// Game components
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private BitmapFont font;
	private float amountOfPoints = 0;
	private float timer = 0;
	private float boostedIdle = 1;

	// Click button variables
	private Rectangle clickButtonBounds;
	private Color clickButtonColor;
	private boolean clickButtonPressed;

	// Reset button variables
	private Rectangle resetButtonBounds;
	private Color resetButtonColor;

	// upgrade Buttons variables
	final int NUM_UPGRADE_BUTTONS = 7;
	final Color UPGRADE_COLOR = Color.FOREST;
	final int[] UPGRADE_PRICES = { 100, 1000, 10000, 100000, 1000000, 10000000, 100000000 };
	final float IDLE_MULTIPLIER = 2.0f;
	private List<Rectangle> upgradeButtonBounds;
	private List<Color> upgradeButtonColors;
	private List<Boolean> upgradeButtonPressed;

	@Override
	public void create() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		// Initialize click button.
		clickButtonBounds = new Rectangle((float) Gdx.graphics.getWidth() / 6, (float) Gdx.graphics.getHeight() / 3,(float) Gdx.graphics.getWidth() / 4, (float) Gdx.graphics.getHeight() / 3);
		clickButtonColor = Color.BLUE;
		clickButtonPressed = false;
		// Initialize reset button.
		resetButtonBounds = new Rectangle((float) Gdx.graphics.getWidth() / 2 - (float) Gdx.graphics.getWidth() / 20,
				10,
				(float) Gdx.graphics.getWidth() / 10,
				(float) Gdx.graphics.getHeight() / 12);

		resetButtonColor = Color.FIREBRICK;

		font = new BitmapFont();
		font.getData().setScale(4);
		font.setColor(Color.BLACK);

		// Load saved preferences or default values
		Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
		amountOfPoints = prefs.getFloat("points", amountOfPoints);
		boostedIdle = prefs.getFloat("boostedIdle", boostedIdle);

		// Initialize upgrade buttons
		initializeUpgradeButtons(prefs);
	}

	// Initialize upgrade buttons based on saved preferences or default values
	private void initializeUpgradeButtons(Preferences prefs) {
		upgradeButtonBounds = new ArrayList<>();
		upgradeButtonColors = new ArrayList<>();
		upgradeButtonPressed = new ArrayList<>();

		float initialY = Gdx.graphics.getHeight() / 1.18f;
		float buttonHeight = (float) Gdx.graphics.getHeight() / 10;

		for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
			Rectangle button = new Rectangle(Gdx.graphics.getWidth() * 4.7f / 6, initialY - i * 125,
					(float) Gdx.graphics.getWidth() / 5, buttonHeight);
			upgradeButtonBounds.add(button);

			// Set button color based on whether it was clicked before
			boolean isButtonClicked = prefs.getBoolean("upgradeButton_" + i, false);
			if (isButtonClicked) {
				upgradeButtonColors.add(UPGRADE_COLOR);
			} else {
				upgradeButtonColors.add(Color.GREEN);
			}

			upgradeButtonPressed.add(false);
		}
	}

	@Override
	public void render() {
		handleClick();
		update(Gdx.graphics.getDeltaTime());

		// Clear screen
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Render buttons
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(clickButtonColor);
		shapeRenderer.rect(clickButtonBounds.x, clickButtonBounds.y, clickButtonBounds.width, clickButtonBounds.height);
		shapeRenderer.setColor(resetButtonColor);
		shapeRenderer.rect(resetButtonBounds.x, resetButtonBounds.y, resetButtonBounds.width, resetButtonBounds.height);

		for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
			shapeRenderer.setColor(upgradeButtonColors.get(i));
			shapeRenderer.rect(upgradeButtonBounds.get(i).x, upgradeButtonBounds.get(i).y,
					upgradeButtonBounds.get(i).width, upgradeButtonBounds.get(i).height);
		}
		shapeRenderer.end();

		// Render text
		batch.begin();
		font.draw(batch, "Points: " + amountOfPoints, 10, Gdx.graphics.getHeight() - 20);
		font.draw(batch, "Idle points: " + boostedIdle + "/s", 10, Gdx.graphics.getHeight() - 80);
		font.draw(batch, "RESET", resetButtonBounds.x + 10, resetButtonBounds.y + resetButtonBounds.height - 10);
		for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
			font.draw(batch, "Cost: " + UPGRADE_PRICES[i], upgradeButtonBounds.get(i).x + 10, upgradeButtonBounds.get(i).y + upgradeButtonBounds.get(i).height - 10);
		}
		batch.end();
	}

	// Handle Clicks
	private void handleClick() {
		if (Gdx.input.justTouched()) {
			float touchX = Gdx.input.getX();
			float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

			// Check if the click is on the click button, reset button, or upgrade buttons
			if (clickButtonBounds.contains(touchX, touchY)) {
				clickButtonPressed = true;
			} else if (resetButtonBounds.contains(touchX, touchY)) {
				// If the reset button is pressed, clear preferences and reset game state
				resetGame();
			} else {
				for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
					if (upgradeButtonBounds.get(i).contains(touchX, touchY)) {
						upgradeButtonPressed.set(i, true);
						saveUpgradeButtonClickState(i);
						break;
					}
				}
			}
		}
	}

	// Method to reset game state and clear preferences
	private void resetGame() {
		Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
		prefs.clear(); // Clears all preferences related to "MyGamePreferences"
		prefs.flush();

		// Reset variables to initial values.
		amountOfPoints = 0;
		boostedIdle = 1;

		// Reset upgrade button colors and states
		for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
			upgradeButtonColors.set(i, Color.GREEN);
			upgradeButtonPressed.set(i, false);
		}
		save();
	}

	// Update game logic
	private void update(float delta) {
		// Makes the timer go up faster.
		timer += delta * boostedIdle;

		// Timer reset
		if (timer >= 1.0f) {
			amountOfPoints++;
			timer -= 1.0f;
			save();
		}

		// Visual feedback for blue clicking button (Usability Engineering coming in clutch)
		if (clickButtonPressed) {
			clickButtonColor = Color.SKY;
			amountOfPoints++;
			clickButtonPressed = false;
			save();
		} else {
			clickButtonColor = Color.BLUE;
		}

		// Check for upgrade button clicks and apply upgrades if affordable
		for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
			if (upgradeButtonPressed.get(i) && upgradeButtonColors.get(i) != UPGRADE_COLOR
					&& amountOfPoints >= UPGRADE_PRICES[i]) {
				upgradeButtonColors.set(i, UPGRADE_COLOR);
				amountOfPoints -= UPGRADE_PRICES[i];
				boostedIdle *= IDLE_MULTIPLIER;
				upgradeButtonPressed.set(i, false);
				save();
				break;
			}
		}
	}

	// Saves the game to local storage.
	public void save() {
		Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
		prefs.putFloat("points", amountOfPoints);
		prefs.putFloat("boostedIdle", boostedIdle);
		prefs.flush();
	}

	// Save upgrade button to localstorage.
	private void saveUpgradeButtonClickState(int buttonIndex) {
		Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
		prefs.putBoolean("upgradeButton_" + buttonIndex, true);
		prefs.flush();
	}
}