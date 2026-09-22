package com.raidenclone.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.raidenclone.game.entities.*;

public class BombSystem {
    private EntityManager entityManager;
    private float bombEffectTimer = 0;
    private float bombEffectDuration = 2.0f;
    private boolean bombActive = false;
    private float bombRadius = 0;
    private float maxBombRadius = 600;
    private float bombX, bombY;
    
    public BombSystem(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public void update(float delta) {
        if (bombActive) {
            bombEffectTimer += delta;
            bombRadius = (bombEffectTimer / bombEffectDuration) * maxBombRadius;
            
            if (bombEffectTimer >= bombEffectDuration) {
                bombActive = false;
                bombEffectTimer = 0;
                bombRadius = 0;
            }
        }
    }
    
    public boolean tryUseBomb() {
        Player player = entityManager.getPlayer();
        if (player != null && player.useBomb()) {
            activateBomb(player.getPosition().x, player.getPosition().y);
            return true;
        }
        return false;
    }
    
    private void activateBomb(float x, float y) {
        bombActive = true;
        bombEffectTimer = 0;
        bombRadius = 0;
        bombX = x;
        bombY = y;
        
        Player player = entityManager.getPlayer();
        if (player != null) {
            player.screenShake = 40;
        }
        
        // Create bomb wave particle
        Particle wave = Particle.createBombWave(x, y, maxBombRadius);
        entityManager.addEntity(wave);
        
        // Destroy all enemies and enemy bullets in radius
        destroyEnemiesInRadius();
        destroyBulletsInRadius();
        
        // Play bomb sound
        // AssetManager.getInstance().get(AssetManager.BOMB_SOUND).play(0.8f);
    }
    
    private void destroyEnemiesInRadius() {
        float radiusSq = maxBombRadius * maxBombRadius;
        
        for (Enemy enemy : entityManager.getEnemies()) {
            if (!enemy.isActive()) continue;
            
            float dx = enemy.getPosition().x + enemy.getWidth()/2 - bombX;
            float dy = enemy.getPosition().y + enemy.getHeight()/2 - bombY;
            float distSq = dx * dx + dy * dy;
            
            if (distSq <= radiusSq) {
                enemy.setActive(false);
                entityManager.getPlayer().addScore(enemy.getScoreValue());
                createExplosionParticles(enemy);
            }
        }
    }
    
    private void destroyBulletsInRadius() {
        float radiusSq = maxBombRadius * maxBombRadius;
        
        for (Bullet bullet : entityManager.getEnemyBullets()) {
            if (!bullet.isActive()) continue;
            
            float dx = bullet.getPosition().x - bombX;
            float dy = bullet.getPosition().y - bombY;
            float distSq = dx * dx + dy * dy;
            
            if (distSq <= radiusSq) {
                bullet.setActive(false);
                // Small particle for bullet destruction
                Particle p = new Particle(bullet.getPosition().x, bullet.getPosition().y,
                    new com.badlogic.gdx.graphics.g2d.TextureRegion(
                        com.raidenclone.game.utils.AssetManager.getInstance()
                            .get(com.raidenclone.game.utils.AssetManager.EXPLOSION)));
                p.color.set(1, 0.2f, 0.2f, 0.8f);
                p.endColor.set(1, 0, 0, 0);
                p.maxLifetime = 0.3f;
                p.startScale = 0.5f;
                p.endScale = 0;
                p.additive = true;
                entityManager.addEntity(p);
            }
        }
    }
    
    private void createExplosionParticles(Enemy enemy) {
        int count = enemy.getType() == Enemy.Type.BOSS ? 40 : 
                      enemy.getType() == Enemy.Type.HEAVY ? 20 : 12;
        
        for (int i = 0; i < count; i++) {
            float angle = MathUtils.random(MathUtils.PI2);
            float speed = MathUtils.random(100, 300);
            Particle p = new Particle(enemy.getPosition().x + enemy.getWidth()/2, 
                                    enemy.getPosition().y + enemy.getHeight()/2,
                                    new com.badlogic.gdx.graphics.g2d.TextureRegion(
                                        com.raidenclone.game.utils.AssetManager.getInstance()
                                            .get(com.raidenclone.game.utils.AssetManager.EXPLOSION)));
            p.velocity.set(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed);
            p.color.set(1, MathUtils.random(0.5f, 1f), MathUtils.random(0, 0.3f), 1);
            p.endColor.set(0.5f, 0, 0.5f, 0);
            p.maxLifetime = MathUtils.random(0.8f, 1.5f);
            p.startScale = MathUtils.random(0.8f, 2.0f);
            p.endScale = 0;
            p.rotationSpeed = MathUtils.random(-720, 720);
            p.additive = true;
            entityManager.addEntity(p);
        }
    }
    
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        if (bombActive) {
            // Render expanding ring
            float progress = bombEffectTimer / bombEffectDuration;
            float alpha = 1.0f - progress;
            float scale = bombRadius / 16f; // 16 is base texture radius
            
            batch.setBlendFunction(com.badlogic.gdx.graphics.g2d.SpriteBatch.GL_SRC_ALPHA, 
                                   com.badlogic.gdx.graphics.g2d.SpriteBatch.GL_ONE);
            batch.setColor(1, 0.8f, 0, alpha * 0.6f);
            batch.draw(com.raidenclone.game.utils.AssetManager.getInstance()
                          .get(com.raidenclone.game.utils.AssetManager.EXPLOSION),
                      bombX - bombRadius, bombY - bombRadius, bombRadius * 2, bombRadius * 2);
            
            // Inner ring
            batch.setColor(1, 1, 0.5f, alpha * 0.4f);
            batch.draw(com.raidenclone.game.utils.AssetManager.getInstance()
                          .get(com.raidenclone.game.utils.AssetManager.EXPLOSION),
                      bombX - bombRadius * 0.7f, bombY - bombRadius * 0.7f, bombRadius * 1.4f, bombRadius * 1.4f);
            
            batch.setBlendFunction(com.badlogic.gdx.graphics.g2d.SpriteBatch.GL_SRC_ALPHA, 
                                   com.badlogic.gdx.graphics.g2d.SpriteBatch.GL_ONE_MINUS_SRC_ALPHA);
            batch.setColor(1, 1, 1, 1);
        }
    }
    
    public boolean isBombActive() { return bombActive; }
    public float getBombProgress() { return bombEffectTimer / bombEffectDuration; }
    public float getBombRadius() { return bombRadius; }
}