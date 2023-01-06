package com.mygdx.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private final Stage stage;
    final Drop game;
    OrthographicCamera camera;

    public MainMenuScreen(final Drop game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());

        Button startButton = new TextButton("Start", game.cloudUI);
        startButton.setSize(100f, 50f);
        startButton.setPosition(Drop.WIDTH / 2f - startButton.getWidth() / 2f, Drop.HEIGHT / 2f - startButton.getHeight() / 2f);
        startButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });
        stage.addActor(startButton);

        Button exitButton = new TextButton("Exit", game.cloudUI);
        exitButton.setSize(100f, 50f);
        exitButton.setPosition(Drop.WIDTH / 2f - exitButton.getWidth() / 2f, Drop.HEIGHT / 2f - exitButton.getHeight() / 2f - 70);
        exitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                dispose();
            }
        });
        stage.addActor(exitButton);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Drop.WIDTH, Drop.HEIGHT);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Welcome to Drop!!", 100, 150);
        game.font.draw(game.batch, "Tap anywhere to begin", 100, 100);
        game.batch.end();

        stage.act();
        stage.draw();


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
        stage.dispose();
    }
}
