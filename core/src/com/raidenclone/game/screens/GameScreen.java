package com.raidenclone.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.raidenclone.game.RaidenClone;
import com.raidenclone.game.entities.*;
import com.raidenclone.game.systems.*;
import com.raidenclone.game.utils.AssetManager;

public class GameScreen implements Screen {
    private RaidenClone game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont uiFont;
    
    // Game systems
    private EntityManager entityManager;
    private WaveManager waveManager;
    private CollisionSystem collisionSystem;
    private BombSystem bombSystem;
    private Background background;
    
    // Input
    private Vector2 touchPos = new Vector2();
    private Vector2 lastTouchPos = new Vector2();
    private boolean touching = false;
    private long lastTapTime = 0;
    private static final long DOUBLE_TAP_THRESHOLD = 300; // ms
    
    // Game state
    private boolean gameOver = false;
    private float gameOverTimer = 0;
    private float screenShakeX = 0, screenShakeY = 0;
    
    // UI
    private float scorePopupTimer = 0;
    private String scorePopupText = "";
    private Vector2 scorePopupPos = new Vector2();
    private com.badlogic.gdx.graphics.g2d.GlyphLayout glyphLayout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
    
    public GameScreen(RaidenClone game) {
        this.game = game;
        this.batch = game.batch;
        
        font = new BitmapFont();
        font.getData().setScale(1.2f);
        
        uiFont = new BitmapFont();
        uiFont.getData().setScale(1.0f);
        
        initGame();
    }
    
    private void initGame() {
        entityManager = new EntityManager();
        waveManager = new WaveManager(entityManager);
        collisionSystem = new CollisionSystem(entityManager);
        bombSystem = new BombSystem(entityManager);
        background = new Background();
        
        // Create player
        Player player = new Player(240, 100);
        entityManager.setPlayer(player);
        
        gameOver = false;
        gameOverTimer = 0;
    }
    
    @Override
    public void show() {
        // Start game music
    }
    
    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
        
        handleInput();
    }
    
    private void update(float delta) {
        if (gameOver) {
            gameOverTimer += delta;
            if (gameOverTimer > 3.0f && Gdx.input.justTouched()) {
                game.setScreen(new MenuScreen(game));
            }
            return;
        }
        
        // Update systems
        background.update(delta);
        entityManager.update(delta);
        waveManager.update(delta);
        bombSystem.update(delta);
        collisionSystem.checkCollisions();
        
        // Update player position from touch
        updatePlayerFromTouch(delta);
        
        // Handle shooting
        handleShooting();
        
        // Screen shake
        updateScreenShake(delta);
        
        // Score popup
        if (scorePopupTimer > 0) {
            scorePopupTimer -= delta;
            scorePopupPos.y += delta * 30;
        }
    }
    
    private void updatePlayerFromTouch(float delta) {
        Player player = entityManager.getPlayer();
        if (player == null || !player.isActive()) return;
        
        if (touching) {
            float dx = touchPos.x - player.getPosition().x;
            float dy = touchPos.y - player.getPosition().y;
            player.move(dx * 0.02f, dy * 0.02f, delta);
        }
    }
    
    private void handleShooting() {
        Player player = entityManager.getPlayer();
        if (player == null || !player.isActive()) return;
        
        if (player.canShoot()) {
            Bullet[] bullets = player.createBullets();
            for (Bullet b : bullets) {
                entityManager.addEntity(b);
            }
            player.resetShootTimer();
            
            // Play shoot sound
            // game.assetManager.get(AssetManager.SHOOT_SOUND).play(0.3f);
            
            // Create muzzle flash particles
            for (Bullet b : bullets) {
                for (int i = 0; i < 2; i++) {
                    Particle p = Particle.createEngineTrail(
                        b.getPosition().x + MathUtils.random(-5, 5),
                        b.getPosition().y);
                    p.velocity.set(MathUtils.random(-20, 20), MathUtils.random(50, 100));
                    p.color.set(0, 1, 1, 0.8f);
                    entityManager.addEntity(p);
                }
            }
        }
    }
    
    private void handleInput() {
        if (gameOver) return;
        
        // Touch handling
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), 800 - Gdx.input.getY());
            
            if (!touching) {
                // Check for double tap (bomb)
                long now = System.currentTimeMillis();
                if (now - lastTapTime < DOUBLE_TAP_THRESHOLD) {
                    bombSystem.tryUseBomb();
                }
                lastTapTime = now;
                touching = true;
            }
            lastTouchPos.set(touchPos);
        } else {
            touching = false;
        }
        
        // Keyboard controls (for desktop testing)
        Player player = entityManager.getPlayer();
        if (player != null && player.isActive()) {
            float dx = 0, dy = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) dx = -1;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) dx = 1;
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) dy = 1;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) dy = -1;
            
            if (dx != 0 || dy != 0) {
                player.move(dx, dy, Gdx.graphics.getDeltaTime());
            }
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                bombSystem.tryUseBomb();
            }
        }
        
        // Pause
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            // Pause menu would go here
        }
    }
    
    private void updateScreenShake(float delta) {
        Player player = entityManager.getPlayer();
        if (player != null && player.getScreenShake() > 0) {
            screenShakeX = MathUtils.random(-player.getScreenShake(), player.getScreenShake());
            screenShakeY = MathUtils.random(-player.getScreenShake(), player.getScreenShake());
        } else {
            screenShakeX *= 0.9f;
            screenShakeY *= 0.9f;
            if (Math.abs(screenShakeX) < 0.1f) screenShakeX = 0;
            if (Math.abs(screenShakeY) < 0.1f) screenShakeY = 0;
        }
    }
    
    private void draw(float delta) {
        Gdx.gl.glClearColor(0.02f, 0.02f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.begin();
        
        // Apply screen shake
        if (screenShakeX != 0 || screenShakeY != 0) {
            batch.setTransformMatrix(batch.getTransformMatrix().cpy().translate(screenShakeX, screenShakeY, 0));
        }
        
        // Background
        background.render(batch);
        
        // Entities
        entityManager.render(batch);
        
        // Bomb effect
        bombSystem.render(batch);
        
        // Reset transform
        if (screenShakeX != 0 || screenShakeY != 0) {
            batch.setTransformMatrix(batch.getTransformMatrix().cpy().translate(-screenShakeX, -screenShakeY, 0));
        }
        
        // UI
        drawUI();
        
        batch.end();
    }
    
    private void drawUI() {
        Player player = entityManager.getPlayer();
        if (player == null) return;
        
        // Score
        uiFont.setColor(1, 1, 1, 1);
        uiFont.draw(batch, "SCORE: " + player.getScore(), 10, 785);
        
        // Wave
        uiFont.setColor(0.5f, 1, 0.5f, 1);
        uiFont.draw(batch, "WAVE: " + waveManager.getCurrentWave(), 480 - 120, 785);
        
        // Lives
        uiFont.setColor(1, 0.5f, 0.5f, 1);
        uiFont.draw(batch, "LIVES: ", 10, 760);
        for (int i = 0; i < player.getLives(); i++) {
            batch.draw(game.assetManager.get(AssetManager.PLAYER_SHIP), 
                      75 + i * 25, 745, 16, 16);
        }
        
        // Bombs (Unlimited indicator)
        uiFont.setColor(1, 0.8f, 0, 1);
        uiFont.draw(batch, "BOMBS: UNLIMITED", 10, 735);
        
        // Bomb icons (visual)
        for (int i = 0; i < player.getBombs(); i++) {
            batch.draw(game.assetManager.get(AssetManager.BOMB_ICON), 
                      480 - 40 - i * 35, 740, 32, 32);
        }
        
        // Power level
        if (player.getPowerLevel() > 1) {
            uiFont.setColor(0, 1, 0.8f, 1);
            uiFont.draw(batch, "POWER: " + player.getPowerLevel(), 10, 710);
            
            // Power timer bar
            float barWidth = 100 * (1 - player.getPowerTimer() / 10.0f);
            batch.setColor(0, 1, 0.8f, 0.5f);
            batch.draw(game.assetManager.get(AssetManager.EXPLOSION), 
                      80, 705, barWidth, 10);
            batch.setColor(1, 1, 1, 1);
        }
        
        // Score popup
        if (scorePopupTimer > 0) {
            font.setColor(1, 1, 0, scorePopupTimer);
            font.draw(batch, scorePopupText, scorePopupPos.x, scorePopupPos.y);
            font.setColor(1, 1, 1, 1);
        }
        
        // Game Over
        if (gameOver) {
            drawGameOver();
        }
        
        // Invulnerability indicator
        if (player.isInvulnerable()) {
            float alpha = (MathUtils.sin(player.getInvulnProgress() * 20) + 1) * 0.25f + 0.25f;
            uiFont.setColor(1, 1, 1, alpha);
            uiFont.draw(batch, "INVULNERABLE", 240 - 60, 400);
            uiFont.setColor(1, 1, 1, 1);
        }
    }
    
    private void drawGameOver() {
        // Dark overlay
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(game.assetManager.get(AssetManager.EXPLOSION), 0, 0, 480, 800);
        batch.setColor(1, 1, 1, 1);
        
        // Game Over text
        font.getData().setScale(3.0f);
        font.setColor(1, 0.3f, 0.3f, 1);
        font.draw(batch, "GAME OVER", 240 - font.getSpaceWidth() * 4.5f, 480);
        
        // Final score
        Player player = entityManager.getPlayer();
        font.getData().setScale(2.0f);
        font.setColor(1, 1, 0.5f, 1);
        font.draw(batch, "FINAL SCORE: " + player.getScore(), 240 - font.getSpaceWidth() * 10, 420);
        
        font.getData().setScale(1.2f);
        font.setColor(0.8f, 0.8f, 1, 1);
        font.draw(batch, "WAVES CLEARED: " + waveManager.getCurrentWave(), 240 - glyphLayout.setText(font, "WAVES CLEARED: " + waveManager.getCurrentWave()).width / 2, 370);
        
        // Blinking continue
        float blink = (MathUtils.sin(gameOverTimer * 5) + 1) * 0.5f;
        font.setColor(1, 1, 1, blink);
        font.draw(batch, "TOUCH TO RETURN TO MENU", 240 - glyphLayout.setText(font, "TOUCH TO RETURN TO MENU").width / 2, 280);
        font.setColor(1, 1, 1, 1);
    }
    
    public void showScorePopup(int points, float x, float y) {
        scorePopupText = "+" + points;
        scorePopupPos.set(x, y);
        scorePopupTimer = 1.0f;
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
        uiFont.dispose();
        entityManager.clear();
    }
}