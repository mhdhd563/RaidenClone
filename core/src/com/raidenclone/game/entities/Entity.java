package com.raidenclone.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.raidenclone.game.utils.AssetManager;

public abstract class Entity {
    protected Vector2 position;
    protected Vector2 velocity;
    protected TextureRegion texture;
    protected Rectangle bounds;
    protected float width, height;
    protected boolean active = true;
    protected int health = 1;
    protected int maxHealth = 1;
    protected float rotation = 0;
    protected float scale = 1.0f;
    
    public Entity(float x, float y, String textureKey) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        Texture tex = AssetManager.getInstance().get(textureKey, Texture.class);
        this.texture = new TextureRegion(tex);
        this.width = tex.getWidth();
        this.height = tex.getHeight();
        this.bounds = new Rectangle(x, y, width, height);
    }
    
    public Entity(float x, float y, TextureRegion textureRegion) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.texture = textureRegion;
        this.width = textureRegion.getRegionWidth();
        this.height = textureRegion.getRegionHeight();
        this.bounds = new Rectangle(x, y, width, height);
    }
    
    public void update(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);
        bounds.setPosition(position.x, position.y);
    }
    
    public void render(SpriteBatch batch) {
        if (active && texture != null) {
            batch.draw(texture, position.x, position.y, width / 2, height / 2, 
                      width, height, scale, scale, rotation);
        }
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            active = false;
        }
    }
    
    public boolean collidesWith(Entity other) {
        return bounds.overlaps(other.bounds);
    }
    
    public Rectangle getBounds() { return bounds; }
    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getRotation() { return rotation; }
    public void setRotation(float rotation) { this.rotation = rotation; }
    public float getScale() { return scale; }
    public void setScale(float scale) { this.scale = scale; }
}