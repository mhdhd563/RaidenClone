package com.raidenclone.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.raidenclone.game.utils.AssetManager;

public class Bullet extends Entity {
    private boolean fromPlayer;
    private float lifetime = 0;
    private float maxLifetime = 3.0f;
    private int damage = 1;
    private boolean piercing = false;
    private int hitCount = 0;
    private int maxHits = 1;
    
    public Bullet(float x, float y, Vector2 velocity, boolean fromPlayer) {
        super(x, y, fromPlayer ? AssetManager.PLAYER_BULLET : AssetManager.ENEMY_BULLET);
        this.velocity.set(velocity);
        this.fromPlayer = fromPlayer;
        this.bounds.setSize(width * 0.5f, height * 0.5f);
        this.rotation = velocity.angleDeg();
        
        if (fromPlayer) {
            this.damage = 1;
            this.maxHits = 1;
        } else {
            this.damage = 1;
        }
    }
    
    @Override
    public void update(float delta) {
        super.update(delta);
        lifetime += delta;
        
        if (lifetime >= maxLifetime || 
            position.y < -height || position.y > 800 + height ||
            position.x < -width || position.x > 480 + width) {
            active = false;
        }
    }
    
    public boolean isFromPlayer() { return fromPlayer; }
    public int getDamage() { return damage; }
    public boolean isPiercing() { return piercing; }
    public void setPiercing(boolean piercing) { this.piercing = piercing; maxHits = 3; }
    public int getHitCount() { return hitCount; }
    public void incrementHitCount() { hitCount++; }
    public int getMaxHits() { return maxHits; }
    public float getLifetime() { return lifetime; }
}