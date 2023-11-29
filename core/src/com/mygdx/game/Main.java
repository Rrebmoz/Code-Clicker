package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Main extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public ShapeRenderer shapeRenderer;

    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        shapeRenderer = new ShapeRenderer();
        this.setScreen(new ClickerScreen(this));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}