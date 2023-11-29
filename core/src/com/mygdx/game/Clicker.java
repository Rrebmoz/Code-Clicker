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
	// Scores
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
	final int[] UPGRADE_PRICES = { 100, 200, 400, 800, 1600, 3200, 6400 };
	final int IDLE_MULTIPLIER = 2;
	float clickValue = 1.0f;
	private List<Rectangle> upgradeButtonBounds;
	private List<Color> upgradeButtonColors;
	private List<Boolean> upgradeButtonPressed;

	@Override
	public void create() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		// Initialize click button.
		clickButtonBounds = new Rectangle((float) Gdx.graphics.getWidth() / 6, (float) Gdx.graphics.getHeight() / 3,(float) Gdx.graphics.getWidth() / 4, (float) Gdx.graphics.getHeight() / 3);
		clickButtonColor = Color.LIGHT_GRAY;
		clickButtonPressed = false;
		// Initialize reset button.
		resetButtonBounds = new Rectangle((float) Gdx.graphics.getWidth() / 2 - (float) Gdx.graphics.getWidth() / 20,
				10,
				(float) Gdx.graphics.getWidth() / 10,
				(float) Gdx.graphics.getHeight() / 12);

		resetButtonColor = Color.FIREBRICK;

		font = new BitmapFont();
		float scale = (float) Gdx.graphics.getHeight() / 270;
		font.getData().setScale(scale);
		font.setColor(Color.BLACK);

		// Load saved preferences (local storage) or default values
		Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
		amountOfPoints = prefs.getFloat("points", amountOfPoints);
		boostedIdle = prefs.getFloat("boostedIdle", boostedIdle);
		clickValue = prefs.getFloat("clickValue", clickValue);

		// Initialize upgrade buttons
		initializeUpgradeButtons(prefs);
	}

	// Initialize upgrade buttons based on saved preferences or default values
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
		float heightChanger = (float) Gdx.graphics.getHeight() / 54f;
		batch.begin();
		font.draw(batch, "Points: " + amountOfPoints, 10, Gdx.graphics.getHeight() - heightChanger);
		font.draw(batch, "Idle points: " + boostedIdle + "/s", 10, Gdx.graphics.getHeight() - heightChanger * 4);
		font.draw(batch, "Click value: " + clickValue + "/click", 10, Gdx.graphics.getHeight() - heightChanger * 7);
		font.draw(batch, "RESET", resetButtonBounds.x + 10, resetButtonBounds.y + resetButtonBounds.height - 10);
		for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
			if (upgradeButtonColors.get(i) == UPGRADE_COLOR) {
				// Button already bought, show "Bought!" text
				font.draw(batch, "Bought!", upgradeButtonBounds.get(i).x + 10, upgradeButtonBounds.get(i).y + upgradeButtonBounds.get(i).height - 10);
			} else {
				if (amountOfPoints >= UPGRADE_PRICES[i]) {
					// Able to buy the button, change color to green
					upgradeButtonColors.set(i, Color.GREEN);
					font.draw(batch, "Cost: " + UPGRADE_PRICES[i], upgradeButtonBounds.get(i).x + 10, upgradeButtonBounds.get(i).y + upgradeButtonBounds.get(i).height - 10);
				} else {
					// Not enough points to buy, keep the original color
					upgradeButtonColors.set(i, Color.LIGHT_GRAY);
					font.draw(batch, "Cost: " + UPGRADE_PRICES[i], upgradeButtonBounds.get(i).x + 10, upgradeButtonBounds.get(i).y + upgradeButtonBounds.get(i).height - 10);
				}
			}
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

	// Method to reset game state and clear preferences
	private void resetGame() {
		Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
		prefs.clear(); // Clears all preferences related to "MyGamePreferences"
		prefs.flush();

		// Reset variables to initial values.
		amountOfPoints = 0;
		boostedIdle = 1;
		clickValue = 1.0f;

		// Reset upgrade button colors and states
		for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
			upgradeButtonColors.set(i, Color.LIGHT_GRAY);
			upgradeButtonPressed.set(i, false);
		}
		GameState.save(amountOfPoints, boostedIdle, clickValue);
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
}