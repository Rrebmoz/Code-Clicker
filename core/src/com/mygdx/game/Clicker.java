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
	// Constants
	private static final int NUM_UPGRADE_BUTTONS = 7;
	private static final Color UPGRADE_COLOR = Color.FOREST;
	private static final int[] UPGRADE_PRICES = {100, 1000, 10000, 100000, 1000000, 10000000, 100000000};
	private static final float BOOST_MULTIPLIER = 2.0f;

	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private BitmapFont font;
	private float amountOfPoints = 0;
	private float timer = 0;
	private float boostedIdle = 2;

	private final Rectangle clickButtonBounds = new Rectangle(Gdx.graphics.getWidth() / 6, Gdx.graphics.getHeight() / 3, Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 3);
	private Color clickButtonColor;
	private boolean clickButtonPressed;

	private List<Rectangle> upgradeButtonBounds;
	private List<Color> upgradeButtonColors;
	private List<Boolean> upgradeButtonPressed;

	@Override
	public void create() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

        clickButtonColor = Color.BLUE;
		clickButtonPressed = false;

		font = new BitmapFont();
		font.getData().setScale(4);
		font.setColor(Color.BLACK);

		Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
		amountOfPoints = prefs.getFloat("points", amountOfPoints);

		initializeUpgradeButtons();
	}

	private void initializeUpgradeButtons() {
		upgradeButtonBounds = new ArrayList<>();
		upgradeButtonColors = new ArrayList<>();
		upgradeButtonPressed = new ArrayList<>();

		float initialY = Gdx.graphics.getHeight() / 1.18f;
		float buttonHeight = Gdx.graphics.getHeight() / 8;

		for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
			Rectangle button = new Rectangle(Gdx.graphics.getWidth() * 4.7f / 6, initialY - i * 198, Gdx.graphics.getWidth() / 5, buttonHeight);
			upgradeButtonBounds.add(button);
			upgradeButtonColors.add(Color.GREEN);
			upgradeButtonPressed.add(false);
		}
	}

	@Override
	public void render() {
		handleInput();
		update(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(clickButtonColor);
		shapeRenderer.rect(clickButtonBounds.x, clickButtonBounds.y, clickButtonBounds.width, clickButtonBounds.height);

		for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
			shapeRenderer.setColor(upgradeButtonColors.get(i));
			shapeRenderer.rect(upgradeButtonBounds.get(i).x, upgradeButtonBounds.get(i).y, upgradeButtonBounds.get(i).width, upgradeButtonBounds.get(i).height);
		}

		shapeRenderer.end();

		batch.begin();
		font.draw(batch, "Points: " + amountOfPoints, 10, Gdx.graphics.getHeight() - 20);
		batch.end();
	}

	private void handleInput() {
		if (Gdx.input.justTouched()) {
			float touchX = Gdx.input.getX();
			float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

			if (clickButtonBounds.contains(touchX, touchY)) {
				clickButtonPressed = true;
			} else {
				for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
					if (upgradeButtonBounds.get(i).contains(touchX, touchY)) {
						upgradeButtonPressed.set(i, true);
						break;
					}
				}
			}
		}
	}

	private void update(float delta) {
		timer += delta * boostedIdle;

		if (timer >= 1.0f) {
			amountOfPoints++;
			timer -= 1.0f;
			save();
		}

		if (clickButtonPressed) {
			clickButtonColor = Color.SKY;
			amountOfPoints++;
			clickButtonPressed = false;
			save();
		} else {
			clickButtonColor = Color.BLUE;
		}

		for (int i = 0; i < NUM_UPGRADE_BUTTONS; i++) {
			if (upgradeButtonPressed.get(i) && upgradeButtonColors.get(i) != UPGRADE_COLOR && amountOfPoints >= UPGRADE_PRICES[i]) {
				upgradeButtonColors.set(i, UPGRADE_COLOR);
				amountOfPoints -= UPGRADE_PRICES[i];
				boostedIdle *= BOOST_MULTIPLIER;
				upgradeButtonPressed.set(i, false);
				break;
			}
		}
	}

	public void save() {
		Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
		prefs.putFloat("points", amountOfPoints);
		prefs.flush();
	}

	@Override
	public void dispose() {
		batch.dispose();
		shapeRenderer.dispose();
		font.dispose();
	}
}