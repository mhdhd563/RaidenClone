package com.raidenclone.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.raidenclone.game.utils.AssetManager;

public class Player extends Entity {
    private float shootTimer = 0;
    private float shootInterval = 0.12f;
    private int powerLevel = 1;
    private float powerTimer = 0;
    private int bombs = 3;
    private int maxBombs = 3;
    private int lives = 3;
    private int score = 0;
    private boolean invulnerable = false;
    private float invulnTimer = 0;
    private float invulnDuration = 2.0f;
    private boolean autoFire = true;
    
    // Visual effects
    private float engineGlowTimer = 0;
    private float screenShake = 0;
    
    public Player(float x, float y) {
        super(x, y, AssetManager.PLAYER_SHIP);
        this.maxHealth = 3;
        this.health = 3;
        this.bounds.setSize(width * 0.6f, height * 0.6f);
    }
    
    @Override
    public void update(float delta) {
        super.update(delta);
        
        // Invulnerability timer
        if (invulnerable) {
            invulnTimer += delta;
            if (invulnTimer >= invulnDuration) {
                invulnerable = false;
                invulnTimer = 0;
            }
        }
        
        // Power level timer
        if (powerLevel > 1) {
            powerTimer += delta;
            if (powerTimer >= 10.0f) {
                powerLevel = Math.max(1, powerLevel - 1);
                powerTimer = 0;
            }
        }
        
        // Shoot timer
        if (autoFire) {
            shootTimer += delta;
            if (shootTimer >= shootInterval) {
                shootTimer = 0;
            }
        }
        
        // Engine glow animation
        engineGlowTimer += delta * 20;
        
        // Screen shake decay
        if (screenShake > 0) {
            screenShake -= delta * 5;
            if (screenShake < 0) screenShake = 0;
        }
        
        // Keep in bounds
        clampToScreen();
    }
    
    public void move(float x, float y, float delta) {
        float speed = 400f * delta;
        position.x += x * speed;
        position.y += y * speed;
        clampToScreen();
    }
    
    private void clampToScreen() {
        float halfW = width * 0.3f;
        float halfH = height * 0.3f;
        position.x = MathUtils.clamp(position.x, halfW, 480 - halfW);
        position.y = MathUtils.clamp(position.y, halfH, 800 - halfH);
    }
    
    public boolean canShoot() {
        return autoFire && shootTimer >= shootInterval;
    }
    
    public void resetShootTimer() {
        shootTimer = 0;
    }
    
    public Bullet[] createBullets() {
        int count = powerLevel;
        Bullet[] bullets = new Bullet[count];
        float spread = (count - 1) * 0.15f;
        float startX = -spread / 2;
        
        for (int i = 0; i < count; i++) {
            float offsetX = startX + i * 0.15f;
            bullets[i] = new Bullet(
                position.x + offsetX * width, 
                position.y + height * 0.5f,
                new Vector2(offsetX * 200, 800),
                true
            );
        }
        return bullets;
    }
    
    public void powerUp() {
        powerLevel = Math.min(4, powerLevel + 1);
        powerTimer = 0;
    }
    
    public void addBomb() {
        bombs = Math.min(maxBombs, bombs + 1);
    }
    
    public boolean useBomb() {
        if (bombs > 0) {
            bombs--;
            return true;
        }
        return false;
    }
    
    public int getBombs() { return bombs; }
    public int getMaxBombs() { return maxBombs; }
    public int getLives() { return lives; }
    public int getScore() { return score; }
    public void addScore(int points) { score += points; }
    public int getPowerLevel() { return powerLevel; }
    public float getPowerTimer() { return powerTimer; }
    public boolean isInvulnerable() { return invulnerable; }
    public float getInvulnProgress() { return invulnTimer / invulnDuration; }
    public float getEngineGlowPhase() { return engineGlowTimer; }
    public float getScreenShake() { return screenShake; }
    
    public void setScreenShake(float screenShake) { this.screenShake = screenShake; }
    
    public void hit() {
        if (!invulnerable) {
            health--;
            if (health <= 0) {
                lives--;
                health = maxHealth;
                if (lives > 0) {
                    invulnerable = true;
                    invulnTimer = 0;
                    powerLevel = 1;
                }
            } else {
                invulnerable = true;
                invulnTimer = 0;
            }
            screenShake = 10;
        }
    }
    
    public void addLife() {
        lives = Math.min(5, lives + 1);
    }
    
    public void setAutoFire(boolean autoFire) {
        this.autoFire = autoFire;
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (active) {
            // Invulnerability flicker
            if (invulnerable && (int)(invulnTimer * 15) % 2 == 0) {
                return;
            }
            
            // Engine glow
            float glowScale = 1.0f + MathUtils.sin(engineGlowTimer) * 0.15f;
            batch.draw(texture, position.x, position.y, width / 2, height / 2, 
                      width, height, scale * glowScale, scale * glowScale, rotation);
            
            // Health indicator
            batch.setColor(1, 1, 1, 0.5f);
        }
    }
}