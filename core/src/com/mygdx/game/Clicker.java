package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

public class Clicker extends Game {
	SpriteBatch batch;
	Texture img;
	BitmapFont font;
	double totalClicks = 0;
	boolean isButtonClicked = false;

	@Override
	public void create() {
		batch = new SpriteBatch();
		img = new Texture("square.png");

		font = new BitmapFont();
		font.getData().setScale(4);

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				float squareX = (float) (Gdx.graphics.getWidth() - img.getWidth()) / 6;
				float squareY = (float) (Gdx.graphics.getHeight() - img.getHeight()) / 2;

				if (screenX >= squareX && screenX <= squareX + img.getWidth()
						&& screenY >= squareY && screenY <= squareY + img.getHeight()) {
					isButtonClicked = true;
					totalClicks++;
				}
				return true;
			}
		});
	}

	@Override
	public void render() {
		ScreenUtils.clear(1, 0, 0, 1);
		batch.begin();

		float screenWidth = Gdx.graphics.getWidth();
		float screenHeight = Gdx.graphics.getHeight();
		float squareWidth = img.getWidth();
		float squareHeight = img.getHeight();
		float x = (screenWidth - squareWidth) / 6;
		float y = (screenHeight - squareHeight) / 2;

		// Draw the text "Total Clicks:"
		font.draw(batch, "Points: " + totalClicks, x + 50, y + squareHeight + 30); // Adjust text position

		// Change square color when clicked
		if (isButtonClicked) {
			batch.setColor(0.5f, 0.5f, 0.5f, 1f); // Change to gray when clicked
		}

		// Draw the square at the calculated center position
		batch.draw(img, x, y);
		batch.setColor(1, 1, 1, 1); // Reset color

		batch.end();
		isButtonClicked = false; // Reset button click state for next frame
	}
}
