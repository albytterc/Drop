package com.mygdx.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Drop extends Game {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;

    public SpriteBatch batch;
    public BitmapFont font;
    public BitmapFont headerFont;
    public Skin cloudUI;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        headerFont = new BitmapFont(Gdx.files.internal("arial32.fnt"));
        cloudUI = new Skin(Gdx.files.internal("cloud-form/skin/cloud-form-ui.json"));
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        headerFont.dispose();
        cloudUI.dispose();
    }
}
