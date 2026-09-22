package com.raidenclone.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.raidenclone.game.utils.AssetManager;

public class Powerup extends Entity {
    public enum Type {
        POWER(AssetManager.POWERUP, 0x00FF88FF),      // Green - weapon upgrade
        BOMB(AssetManager.BOMB_ICON, 0xFF8800FF),      // Orange - bomb
        LIFE(AssetManager.POWERUP, 0xFF0088FF),        // Red - extra life
        SCORE(AssetManager.POWERUP, 0xFFFF00FF);       // Yellow - bonus points
        
        public final String textureKey;
        public final int color;
        
        Type(String textureKey, int color) {
            this.textureKey = textureKey;
            this.color = color;
        }
    }
    
    private Type type;
    private float bobTimer = 0;
    private float bobHeight = 10;
    private float baseY;
    private float glowTimer = 0;
    
    public Powerup(float x, float y, Type type) {
        super(x, y, type.textureKey);
        this.type = type;
        this.velocity.set(0, -50);
        this.baseY = y;
        this.bounds.setSize(width * 0.7f, height * 0.7f);
        
        // Adjust color based on type
        if (type == Type.BOMB) {
            scale = 0.8f;
        }
    }
    
    @Override
    public void update(float delta) {
        super.update(delta);
        bobTimer += delta * 3;
        glowTimer += delta * 5;
        
        // Bobbing motion
        position.y = baseY + MathUtils.sin(bobTimer) * bobHeight;
        baseY += velocity.y * delta;
        bounds.setPosition(position.x, position.y);
        
        if (position.y < -height || position.y > 800 + height) {
            active = false;
        }
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (active) {
            float glow = 1.0f + MathUtils.sin(glowTimer) * 0.2f;
            batch.setColor(
                ((type.color >> 24) & 0xFF) / 255f,
                ((type.color >> 16) & 0xFF) / 255f,
                ((type.color >> 8) & 0xFF) / 255f,
                ((type.color) & 0xFF) / 255f
            );
            batch.draw(texture, position.x, position.y, width / 2, height / 2, 
                      width, height, scale * glow, scale * glow, rotation);
            batch.setColor(1, 1, 1, 1);
        }
    }
    
    public Type getType() { return type; }
}