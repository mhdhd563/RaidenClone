package com.raidenclone.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.raidenclone.game.RaidenClone;
import com.raidenclone.game.entities.Background;
import com.raidenclone.game.utils.AssetManager;

public class MenuScreen implements Screen {
    private RaidenClone game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private Background background;
    private float titleTimer = 0;
    private float blinkTimer = 0;
    private boolean showPressStart = true;
    private MenuStar[] stars;
    private static final int MENU_STAR_COUNT = 100;
    
    public MenuScreen(RaidenClone game) {
        this.game = game;
        this.batch = game.batch;
        this.background = new Background();
        
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3.0f);
        
        stars = new MenuStar[MENU_STAR_COUNT];
        for (int i = 0; i < MENU_STAR_COUNT; i++) {
            stars[i] = new MenuStar();
        }
    }
    
    @Override
    public void show() {
        // Play menu music
    }
    
    @Override
    public void render(float delta) {
        update(delta);
        
        Gdx.gl.glClearColor(0.03f, 0.03f, 0.08f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.begin();
        
        // Render background
        background.update(delta * 0.3f);
        background.render(batch);
        
        // Render menu stars
        for (MenuStar star : stars) {
            star.update(delta);
            star.render(batch);
        }
        
        // Title
        titleTimer += delta;
        float titleY = 600 + MathUtils.sin(titleTimer * 2) * 10;
        float titleScale = 3.0f + MathUtils.sin(titleTimer * 3) * 0.1f;
        titleFont.getData().setScale(titleScale);
        
        // Glow effect
        for (int i = 0; i < 3; i++) {
            float offset = (i + 1) * 2;
            titleFont.setColor(0, 0.5f, 1, 0.3f - i * 0.1f);
            titleFont.draw(batch, "RAIDEN CLONE", 240 - titleFont.getSpaceWidth() * 6 * titleScale, titleY + offset);
            titleFont.draw(batch, "RAIDEN CLONE", 240 - titleFont.getSpaceWidth() * 6 * titleScale, titleY - offset);
        }
        
        titleFont.setColor(0, 1, 1, 1);
        titleFont.draw(batch, "RAIDEN CLONE", 240 - titleFont.getSpaceWidth() * 6 * titleScale, titleY);
        
        // Subtitle
        font.getData().setScale(1.2f);
        font.setColor(0.5f, 0.8f, 1, 0.8f);
        font.draw(batch, "A Classic Vertical Shooter Tribute", 240 - font.getSpaceWidth() * 18, titleY - 60);
        
        // Blinking press start
        blinkTimer += delta;
        if (blinkTimer >= 0.8f) {
            blinkTimer = 0;
            showPressStart = !showPressStart;
        }
        
        if (showPressStart) {
            font.getData().setScale(1.5f);
            font.setColor(1, 1, 0.5f, 1);
            font.draw(batch, "TOUCH TO START", 240 - font.getSpaceWidth() * 7, 300);
        }
        
        // Controls info
        font.getData().setScale(1.0f);
        font.setColor(0.7f, 0.7f, 0.8f, 0.8f);
        font.draw(batch, "DRAG: Move    TAP: Auto-Fire", 240 - font.getSpaceWidth() * 12, 220);
        font.draw(batch, "DOUBLE-TAP / BUTTON: BOMB (Unlimited!)", 240 - font.getSpaceWidth() * 20, 180);
        
        // High score placeholder
        font.setColor(0.5f, 1, 0.5f, 0.7f);
        font.draw(batch, "HIGH SCORE: 0", 240 - font.getSpaceWidth() * 7, 120);
        
        // Version
        font.getData().setScale(0.7f);
        font.setColor(0.4f, 0.4f, 0.5f, 0.6f);
        font.draw(batch, "v1.0 - LibGDX", 20, 30);
        
        batch.end();
        
        // Handle input
        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || 
            Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
        }
    }
    
    private void update(float delta) {
        // Handled in render
    }
    
    @Override
    public void resize(int width, int height) {}
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void hide() {}
    
    @Override
    public void dispose() {
        font.dispose();
        titleFont.dispose();
    }
    
    private static class MenuStar {
        float x, y;
        float speed;
        float size;
        float alpha;
        float twinkleSpeed;
        float twinklePhase;
        
        MenuStar() {
            reset();
        }
        
        void reset() {
            x = MathUtils.random(480);
            y = MathUtils.random(800);
            speed = MathUtils.random(10, 60);
            size = MathUtils.random(1, 4);
            alpha = MathUtils.random(0.1f, 0.6f);
            twinkleSpeed = MathUtils.random(1, 4);
            twinklePhase = MathUtils.random(0, MathUtils.PI2);
        }
        
        void update(float delta) {
            y -= speed * delta;
            twinklePhase += delta * twinkleSpeed;
            if (y < -size) {
                y = 800 + size;
                x = MathUtils.random(480);
            }
        }
        
        void render(SpriteBatch batch) {
            float twinkle = 0.4f + MathUtils.sin(twinklePhase) * 0.6f;
            batch.setColor(1, 1, 1, alpha * twinkle);
            batch.draw(com.raidenclone.game.utils.AssetManager.getInstance()
                          .get(com.raidenclone.game.utils.AssetManager.EXPLOSION),
                      x - size/2, y - size/2, size, size);
            batch.setColor(1, 1, 1, 1);
        }
    }
}