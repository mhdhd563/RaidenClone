package com.raidenclone.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.raidenclone.game.utils.AssetManager;

public class Particle extends Entity {
    private float lifetime = 0;
    private float maxLifetime;
    private Color color = new Color(1, 1, 1, 1);
    private Color endColor = new Color(1, 0.5f, 0, 0);
    private float startScale = 1.0f;
    private float endScale = 0.0f;
    private float rotationSpeed = 0;
    private float gravity = 0;
    private boolean additive = false;
    
    public Particle(float x, float y, TextureRegion texture) {
        super(x, y, texture);
        this.maxLifetime = MathUtils.random(0.3f, 0.8f);
        this.velocity.set(MathUtils.random(-100, 100), MathUtils.random(-100, 100));
    }
    
    public static Particle createExplosion(float x, float y, Color color) {
        Particle p = new Particle(x, y, new TextureRegion(AssetManager.getInstance().get(AssetManager.EXPLOSION)));
        p.color.set(color);
        p.endColor.set(color.r, color.g, color.b, 0);
        p.maxLifetime = MathUtils.random(0.5f, 1.0f);
        p.velocity.set(MathUtils.random(-200, 200), MathUtils.random(-200, 200));
        p.startScale = MathUtils.random(0.5f, 1.5f);
        p.endScale = 0;
        p.rotationSpeed = MathUtils.random(-360, 360);
        p.gravity = -50;
        p.additive = true;
        return p;
    }
    
    public static Particle createSpark(float x, float y, Vector2 direction) {
        Particle p = new Particle(x, y, new TextureRegion(AssetManager.getInstance().get(AssetManager.EXPLOSION)));
        p.color.set(1, 1, 0.5f, 1);
        p.endColor.set(1, 0.3f, 0, 0);
        p.maxLifetime = MathUtils.random(0.2f, 0.5f);
        p.velocity.set(direction).scl(MathUtils.random(100, 300));
        p.startScale = MathUtils.random(0.3f, 0.8f);
        p.endScale = 0;
        p.additive = true;
        return p;
    }
    
    public static Particle createEngineTrail(float x, float y) {
        Particle p = new Particle(x, y, new TextureRegion(AssetManager.getInstance().get(AssetManager.EXPLOSION)));
        p.color.set(0, 0.8f, 1, 0.6f);
        p.endColor.set(0, 0.3f, 0.8f, 0);
        p.maxLifetime = MathUtils.random(0.15f, 0.3f);
        p.velocity.set(MathUtils.random(-30, 30), MathUtils.random(-50, -10));
        p.startScale = MathUtils.random(0.2f, 0.5f);
        p.endScale = 0;
        p.gravity = 20;
        p.additive = true;
        return p;
    }
    
    public static Particle createBombWave(float x, float y, float maxRadius) {
        Particle p = new Particle(x, y, new TextureRegion(AssetManager.getInstance().get(AssetManager.EXPLOSION)));
        p.color.set(1, 0.8f, 0, 0.8f);
        p.endColor.set(0.5f, 0, 0.5f, 0);
        p.maxLifetime = 1.5f;
        p.velocity.set(0, 0);
        p.startScale = 0.1f;
        p.endScale = maxRadius / 16f;
        p.additive = true;
        return p;
    }
    
    @Override
    public void update(float delta) {
        lifetime += delta;
        if (lifetime >= maxLifetime) {
            active = false;
            return;
        }
        
        float t = lifetime / maxLifetime;
        float invT = 1 - t;
        
        // Interpolate color
        if (color != null && endColor != null) {
            // Handled in render
        }
        
        // Interpolate scale
        scale = startScale * invT + endScale * t;
        
        // Rotation
        rotation += rotationSpeed * delta;
        
        // Gravity
        velocity.y += gravity * delta;
        
        super.update(delta);
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (active) {
            float t = lifetime / maxLifetime;
            float invT = 1 - t;
            
            // Interpolate color
            float r = color.r * invT + endColor.r * t;
            float g = color.g * invT + endColor.g * t;
            float b = color.b * invT + endColor.b * t;
            float a = color.a * invT + endColor.a * t;
            
            if (additive) {
                batch.setBlendFunction(SpriteBatch.GL_SRC_ALPHA, SpriteBatch.GL_ONE);
            }
            
            batch.setColor(r, g, b, a);
            super.render(batch);
            
            if (additive) {
                batch.setBlendFunction(SpriteBatch.GL_SRC_ALPHA, SpriteBatch.GL_ONE_MINUS_SRC_ALPHA);
            }
            batch.setColor(1, 1, 1, 1);
        }
    }
    
    public void setColor(Color color) { this.color.set(color); }
    public void setEndColor(Color endColor) { this.endColor.set(endColor); }
    public void setStartScale(float scale) { this.startScale = scale; }
    public void setEndScale(float scale) { this.endScale = scale; }
    public void setRotationSpeed(float speed) { this.rotationSpeed = speed; }
    public void setGravity(float gravity) { this.gravity = gravity; }
    public void setMaxLifetime(float lifetime) { this.maxLifetime = lifetime; }
    public void setAdditive(boolean additive) { this.additive = additive; }
}