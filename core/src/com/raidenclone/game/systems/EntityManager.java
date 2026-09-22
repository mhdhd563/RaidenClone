package com.raidenclone.game.systems;

import com.badlogic.gdx.utils.Array;
import com.raidenclone.game.entities.*;
import com.raidenclone.game.utils.AssetManager;

public class EntityManager {
    private Array<Entity> entities = new Array<>();
    private Array<Entity> toAdd = new Array<>();
    private Array<Entity> toRemove = new Array<>();
    private Array<Bullet> playerBullets = new Array<>();
    private Array<Bullet> enemyBullets = new Array<>();
    private Array<Enemy> enemies = new Array<>();
    private Array<Powerup> powerups = new Array<>();
    private Array<Particle> particles = new Array<>();
    private Player player;
    private AssetManager assetManager;
    
    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }
    
    public AssetManager getAssetManager() {
        return assetManager;
    }
    
    public void addEntity(Entity entity) {
        toAdd.add(entity);
        
        if (entity instanceof Bullet) {
            Bullet bullet = (Bullet) entity;
            if (bullet.isFromPlayer()) {
                playerBullets.add(bullet);
            } else {
                enemyBullets.add(bullet);
            }
        } else if (entity instanceof Enemy) {
            enemies.add((Enemy) entity);
        } else if (entity instanceof Powerup) {
            powerups.add((Powerup) entity);
        } else if (entity instanceof Particle) {
            particles.add((Particle) entity);
        }
    }
    
    public void removeEntity(Entity entity) {
        toRemove.add(entity);
    }
    
    public void setPlayer(Player player) {
        this.player = player;
        entities.add(player);
    }
    
    public void update(float delta) {
        // Process additions
        for (Entity e : toAdd) {
            entities.add(e);
        }
        toAdd.clear();
        
        // Process removals
        for (Entity e : toRemove) {
            entities.removeValue(e, true);
            if (e instanceof Bullet) {
                Bullet b = (Bullet) e;
                if (b.isFromPlayer()) playerBullets.removeValue(b, true);
                else enemyBullets.removeValue(b, true);
            } else if (e instanceof Enemy) {
                enemies.removeValue((Enemy) e, true);
            } else if (e instanceof Powerup) {
                powerups.removeValue((Powerup) e, true);
            } else if (e instanceof Particle) {
                particles.removeValue((Particle) e, true);
            }
        }
        toRemove.clear();
        
        // Update all entities
        for (Entity e : entities) {
            if (e.isActive()) {
                e.update(delta);
            }
        }
        
        // Clean up inactive entities
        for (int i = entities.size - 1; i >= 0; i--) {
            Entity e = entities.get(i);
            if (!e.isActive()) {
                removeEntity(e);
            }
        }
    }
    
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        for (Entity e : entities) {
            if (e.isActive()) {
                e.render(batch);
            }
        }
    }
    
    // Getters
    public Array<Entity> getEntities() { return entities; }
    public Array<Bullet> getPlayerBullets() { return playerBullets; }
    public Array<Bullet> getEnemyBullets() { return enemyBullets; }
    public Array<Enemy> getEnemies() { return enemies; }
    public Array<Powerup> getPowerups() { return powerups; }
    public Array<Particle> getParticles() { return particles; }
    public Player getPlayer() { return player; }
    
    public void clear() {
        entities.clear();
        toAdd.clear();
        toRemove.clear();
        playerBullets.clear();
        enemyBullets.clear();
        enemies.clear();
        powerups.clear();
        particles.clear();
        player = null;
    }
}