package com.mygdx.drop;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Iterator;


public class GameScreen implements Screen {
    final Drop game;
    private final Stage stage;

    private enum State {
        RUNNING, PAUSED, GAMEOVER
    }

    private State gameState = State.RUNNING;

    private final Texture bucketTexture;
    private final Texture raindropTexture;
    private final Sound dropSound;
    private final Music rainMusic;

    private final OrthographicCamera camera;

    private final Rectangle bucket;
    private final Array<Rectangle> raindrops;

    private long lastDropTime;
    private int dropsCollected;

    private static final int RECT_WIDTH = 64;
    private static final int RECT_HEIGHT = 64;
    private static final int MOVE_SPEED = 200;
    private final Button backButton;
    private final Button resumeButton;
    private final Button restartButton;
    private final Label gameOverText;
    private final Label scoreTextSmall;
    private final Label scoreTextLarge;
    Label.LabelStyle headerStyle;
    Label.LabelStyle normalStyle;
    Array<Actor> endGameActors;

    public GameScreen(final Drop game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());

        bucketTexture = new Texture("bucket.png");
        raindropTexture = new Texture("drop.png");

        dropSound = Gdx.audio.newSound(Gdx.files.internal("raindrop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Drop.WIDTH, Drop.HEIGHT);

        bucket = new Rectangle();
        bucket.x = Drop.WIDTH / 2f - RECT_WIDTH / 2f;
        bucket.y = 20;
        bucket.width = RECT_WIDTH;
        bucket.height = RECT_HEIGHT;

        raindrops = new Array<>();
        spawnRaindrop();

        backButton = new TextButton("Back", game.cloudUI);
        backButton.setPosition(5, Drop.HEIGHT - backButton.getHeight() - 5);
        backButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });
        stage.addActor(backButton);

        resumeButton = new TextButton("Resume", game.cloudUI);
        resumeButton.setPosition(Drop.WIDTH / 2f - resumeButton.getWidth() / 2f, Drop.HEIGHT / 2f - resumeButton.getHeight() / 2f);
        resumeButton.setVisible(false);
        resumeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gameState = State.RUNNING;
            }
        });
        stage.addActor(resumeButton);

        headerStyle = new Label.LabelStyle(game.headerFont, null);
        normalStyle = new Label.LabelStyle(game.font, null);
        gameOverText = new Label("Game Over", headerStyle);
        gameOverText.setAlignment(Align.center);
        gameOverText.setSize(Drop.WIDTH, 20);
        gameOverText.setY(Drop.HEIGHT * 2/3f);
        gameOverText.setVisible(false);
        stage.addActor(gameOverText);

        scoreTextSmall = new Label("Score: " + dropsCollected, normalStyle);
        scoreTextSmall.setPosition(5, Drop.HEIGHT - 50);
        stage.addActor(scoreTextSmall);

        scoreTextLarge = new Label("Score: " + dropsCollected, headerStyle);
        scoreTextLarge.setVisible(false);
        stage.addActor(scoreTextLarge);

        restartButton = new TextButton("Restart", game.cloudUI);
        restartButton.setSize(100f, 50f);
        restartButton.setPosition(Drop.WIDTH / 2f - restartButton.getWidth() / 2f, Drop.HEIGHT / 2f - restartButton.getHeight() / 2f + 40);
        restartButton.setVisible(false);
        restartButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                restartGame();
            }
        });
        stage.addActor(restartButton);

        endGameActors = new Array<>();
        endGameActors.addAll(gameOverText, scoreTextLarge, restartButton);
    }

    private void restartGame() {
        raindrops.clear();
        dropsCollected = 0;
        for (Actor a : endGameActors) {
            a.setVisible(false);
        }
        scoreTextSmall.setVisible(true);
        gameState = State.RUNNING;
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, Drop.WIDTH - RECT_WIDTH);
        raindrop.y = Drop.HEIGHT;
        raindrop.width = RECT_WIDTH;
        raindrop.height = RECT_HEIGHT;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        scoreTextSmall.setText("Score: " + dropsCollected);
        game.batch.draw(bucketTexture, bucket.x, bucket.y, bucket.width, bucket.height);
        for (Rectangle raindrop : raindrops) {
            game.batch.draw(raindropTexture, raindrop.x, raindrop.y);
        }
        game.batch.end();

        stage.act();
        stage.draw();

        switch (gameState) {
            case RUNNING:
                resume();
                updateGameState();
                break;
            case PAUSED:
                pause();
                break;
            case GAMEOVER:
                showFinalState();
                break;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (gameState == State.RUNNING) {
                gameState = State.PAUSED;
            } else if (gameState == State.PAUSED) {
                gameState = State.RUNNING;
            }
        }
    }

    private void showFinalState() {
        if (rainMusic.isPlaying()) {
            rainMusic.stop();
        }

        scoreTextSmall.setVisible(false);

        scoreTextLarge.setText("Score: " + dropsCollected);
        scoreTextLarge.setStyle(headerStyle);
        scoreTextLarge.setFontScale(0.8f);
        scoreTextLarge.setWidth(Drop.WIDTH);
        scoreTextLarge.setY(Drop.HEIGHT / 2f, Align.center);
        scoreTextLarge.setAlignment(Align.center);

        for (Actor a : endGameActors) {
            a.setVisible(true);
        }
    }


    private void updateGameState() {
        if (Gdx.input.isTouched() && !backButton.isPressed()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - RECT_WIDTH / 2f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucket.x -= MOVE_SPEED * Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucket.x += MOVE_SPEED * Gdx.graphics.getDeltaTime();
        }

        if (bucket.x < 0) {
            bucket.x = 0;
        }

        if (bucket.x + RECT_WIDTH > Drop.WIDTH) {
            bucket.x = Drop.WIDTH - RECT_WIDTH;
        }

        if (TimeUtils.timeSinceNanos(lastDropTime) > 1000000000) {
            spawnRaindrop();
        }

        Iterator<Rectangle> iter = raindrops.iterator();
        while (iter.hasNext() && gameState == State.RUNNING) {
            Rectangle raindrop = iter.next();
            raindrop.y -= MOVE_SPEED * Gdx.graphics.getDeltaTime();
            if (raindrop.overlaps(bucket)) {
                dropsCollected++;
                dropSound.play();
                iter.remove();
            }
            if (raindrop.y + RECT_HEIGHT <= 0) {
                gameState = State.GAMEOVER;
                iter.remove();
            }
        }
    }

    @Override
    public void show() {
        rainMusic.play();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        if (rainMusic.isPlaying())
            rainMusic.pause();

        if (!resumeButton.isVisible())
            resumeButton.setVisible(true);

    }

    @Override
    public void resume() {
        if (!rainMusic.isPlaying())
            rainMusic.play();

        if (resumeButton.isVisible())
            resumeButton.setVisible(false);

        if (restartButton.isVisible()) {
            restartButton.setVisible(false);
        }
    }

    @Override
    public void hide() {
        // dispose();
    }

    @Override
    public void dispose() {
        bucketTexture.dispose();
        raindropTexture.dispose();
        dropSound.dispose();
        rainMusic.dispose();
        stage.dispose();

    }
}
