package com.raidenclone.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.raidenclone.game.utils.AssetManager;

public class Enemy extends Entity {
    public enum Type {
        BASIC(AssetManager.ENEMY_BASIC, 1, 10, 150, 300),
        FAST(AssetManager.ENEMY_FAST, 1, 20, 250, 100),
        HEAVY(AssetManager.ENEMY_HEAVY, 5, 50, 80, 800),
        BOSS(AssetManager.BOSS, 50, 500, 40, 5000);
        
        public final String textureKey;
        public final int health;
        public final int scoreValue;
        public final float speed;
        public final int maxHealth;
        
        Type(String textureKey, int health, int scoreValue, float speed, int maxHealth) {
            this.textureKey = textureKey;
            this.health = health;
            this.scoreValue = scoreValue;
            this.speed = speed;
            this.maxHealth = maxHealth;
        }
    }
    
    private Type type;
    private float shootTimer = 0;
    private float shootInterval;
    private Vector2 targetPos;
    private int patternIndex = 0;
    private float patternTimer = 0;
    private float entryDelay = 0;
    private boolean entered = false;
    private int phase = 0;
    private float phaseTimer = 0;
    
    public Enemy(float x, float y, Type type) {
        super(x, y, type.textureKey);
        this.type = type;
        this.health = type.health;
        this.maxHealth = type.maxHealth;
        this.velocity.set(0, -type.speed);
        this.shootInterval = MathUtils.random(1.5f, 3.0f);
        this.bounds.setSize(width * 0.7f, height * 0.7f);
        
        if (type == Type.BOSS) {
            this.bounds.setSize(width * 0.5f, height * 0.5f);
            this.scale = 1.5f;
        }
    }
    
    @Override
    public void update(float delta) {
        if (entryDelay > 0) {
            entryDelay -= delta;
            return;
        }
        
        if (!entered) {
            entered = true;
            if (type == Type.BOSS) {
                targetPos = new Vector2(240, 650);
            }
        }
        
        // Boss behavior
        if (type == Type.BOSS) {
            updateBoss(delta);
        } else {
            updateRegular(delta);
        }
        
        super.update(delta);
        
        // Shooting
        shootTimer += delta;
        if (shootTimer >= shootInterval && position.y < 750 && position.y > 50) {
            shootTimer = 0;
            shootInterval = MathUtils.random(1.0f, 2.5f);
        }
    }
    
    private void updateRegular(float delta) {
        // Side movement for variety
        if (type == Type.FAST) {
            velocity.x = MathUtils.sin(patternTimer * 5) * 100;
        } else if (type == Type.HEAVY) {
            velocity.x = MathUtils.sin(patternTimer * 2) * 40;
        }
        patternTimer += delta;
    }
    
    private void updateBoss(float delta) {
        phaseTimer += delta;
        
        // Move to target position
        if (position.dst(targetPos) > 20) {
            Vector2 dir = targetPos.cpy().sub(position).nor();
            velocity.set(dir.scl(type.speed * 0.5f));
        } else {
            velocity.set(0, 0);
            // Attack patterns
            if (phaseTimer > 3.0f) {
                phase = (phase + 1) % 4;
                phaseTimer = 0;
                shootInterval = 0.5f;
            }
            
            switch (phase) {
                case 0: // Spread shot
                    velocity.x = MathUtils.sin(patternTimer * 2) * 80;
                    break;
                case 1: // Circle pattern
                    velocity.x = MathUtils.cos(patternTimer * 1.5f) * 60;
                    break;
                case 2: // Targeted burst
                    velocity.x = 0;
                    shootInterval = 0.15f;
                    break;
                case 3: // Fast movement
                    velocity.x = MathUtils.sin(patternTimer * 4) * 150;
                    shootInterval = 1.0f;
                    break;
            }
        }
        patternTimer += delta;
    }
    
    public boolean canShoot() {
        return shootTimer >= shootInterval && entered && position.y > 0 && position.y < 800;
    }
    
    public void resetShootTimer() {
        shootTimer = 0;
    }
    
    public Bullet[] createBullets() {
        if (type == Type.BOSS) {
            return createBossBullets();
        }
        
        Bullet bullet = new Bullet(
            position.x + width / 2,
            position.y,
            new Vector2(0, -300),
            false
        );
        
        if (type == Type.HEAVY) {
            // Triple shot
            Bullet[] bullets = new Bullet[3];
            bullets[0] = bullet;
            bullets[1] = new Bullet(position.x + width / 2 - 20, position.y, new Vector2(-100, -300), false);
            bullets[2] = new Bullet(position.x + width / 2 + 20, position.y, new Vector2(100, -300), false);
            return bullets;
        } else if (type == Type.FAST) {
            // Aimed shot
            bullet.velocity.set(-position.x * 0.5f, -300).nor().scl(350);
        }
        
        return new Bullet[]{bullet};
    }
    
    private Bullet[] createBossBullets() {
        Bullet[] bullets;
        switch (phase) {
            case 0: // 5-way spread
                bullets = new Bullet[5];
                for (int i = 0; i < 5; i++) {
                    float angle = (i - 2) * 0.3f;
                    bullets[i] = new Bullet(position.x + width / 2, position.y + height / 2,
                        new Vector2(MathUtils.sin(angle) * 400, MathUtils.cos(angle) * -400), false);
                }
                break;
            case 1: // Spiral
                bullets = new Bullet[8];
                for (int i = 0; i < 8; i++) {
                    float angle = patternTimer * 3 + i * MathUtils.PI2 / 8;
                    bullets[i] = new Bullet(position.x + width / 2, position.y + height / 2,
                        new Vector2(MathUtils.cos(angle) * 300, MathUtils.sin(angle) * -300), false);
                }
                break;
            case 2: // Targeted burst (handled by shoot interval)
                bullets = new Bullet[3];
                for (int i = 0; i < 3; i++) {
                    bullets[i] = new Bullet(position.x + width / 2 + (i - 1) * 30, position.y + height / 2,
                        new Vector2(0, -500), false);
                }
                break;
            case 3: // Fast aimed
                bullets = new Bullet[2];
                float targetX = MathUtils.random(100, 380);
                bullets[0] = new Bullet(position.x + width / 2 - 30, position.y + height / 2,
                    new Vector2(targetX - position.x, -600).nor().scl(500), false);
                bullets[1] = new Bullet(position.x + width / 2 + 30, position.y + height / 2,
                    new Vector2(targetX - position.x, -600).nor().scl(500), false);
                break;
            default:
                bullets = new Bullet[1];
                bullets[0] = new Bullet(position.x + width / 2, position.y, new Vector2(0, -400), false);
        }
        return bullets;
    }
    
    public Type getType() { return type; }
    public int getScoreValue() { return type.scoreValue; }
    public float getHealthPercent() { return (float)health / maxHealth; }
    public void setEntryDelay(float delay) { entryDelay = delay; }
    public int getPhase() { return phase; }
}