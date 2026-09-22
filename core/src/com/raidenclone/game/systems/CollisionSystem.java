package com.raidenclone.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.raidenclone.game.entities.*;

public class CollisionSystem {
    private EntityManager entityManager;
    
    public CollisionSystem(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public void checkCollisions() {
        Player player = entityManager.getPlayer();
        if (player == null || !player.isActive()) return;
        
        // Player bullets vs Enemies
        checkPlayerBulletsVsEnemies();
        
        // Enemy bullets vs Player
        checkEnemyBulletsVsPlayer();
        
        // Enemies vs Player
        checkEnemiesVsPlayer();
        
        // Player vs Powerups
        checkPlayerVsPowerups();
    }
    
    private void checkPlayerBulletsVsEnemies() {
        for (Bullet bullet : entityManager.getPlayerBullets()) {
            if (!bullet.isActive()) continue;
            
            for (Enemy enemy : entityManager.getEnemies()) {
                if (!enemy.isActive()) continue;
                
                if (bullet.collidesWith(enemy)) {
                    enemy.takeDamage(bullet.getDamage());
                    bullet.incrementHitCount();
                    
                    // Create hit particles
                    createHitParticles(enemy.getPosition().x, enemy.getPosition().y);
                    
                    if (!bullet.isPiercing() || bullet.getHitCount() >= bullet.getMaxHits()) {
                        bullet.setActive(false);
                    }
                    
                    if (!enemy.isActive()) {
                        // Enemy destroyed
                        player.addScore(enemy.getScoreValue());
                        createExplosionParticles(enemy);
                        tryDropPowerup(enemy);
                    }
                    break; // Bullet can only hit one enemy per frame
                }
            }
        }
    }
    
    private void checkEnemyBulletsVsPlayer() {
        Player player = entityManager.getPlayer();
        if (player.isInvulnerable()) return;
        
        for (Bullet bullet : entityManager.getEnemyBullets()) {
            if (!bullet.isActive()) continue;
            
            if (bullet.collidesWith(player)) {
                bullet.setActive(false);
                player.hit();
                createHitParticles(player.getPosition().x, player.getPosition().y);
            }
        }
    }
    
    private void checkEnemiesVsPlayer() {
        Player player = entityManager.getPlayer();
        if (player.isInvulnerable()) return;
        
        for (Enemy enemy : entityManager.getEnemies()) {
            if (!enemy.isActive()) continue;
            
            if (enemy.collidesWith(player)) {
                player.hit();
                enemy.takeDamage(1);
                createExplosionParticles(enemy);
                
                if (!enemy.isActive()) {
                    player.addScore(enemy.getScoreValue());
                    tryDropPowerup(enemy);
                }
            }
        }
    }
    
    private void checkPlayerVsPowerups() {
        Player player = entityManager.getPlayer();
        
        for (Powerup powerup : entityManager.getPowerups()) {
            if (!powerup.isActive()) continue;
            
            if (powerup.collidesWith(player)) {
                applyPowerup(player, powerup);
                powerup.setActive(false);
                createPowerupParticles(powerup);
            }
        }
    }
    
    private void applyPowerup(Player player, Powerup powerup) {
        switch (powerup.getType()) {
            case POWER:
                player.powerUp();
                break;
            case BOMB:
                player.addBomb();
                break;
            case LIFE:
                player.addLife();
                break;
            case SCORE:
                player.addScore(1000);
                break;
        }
    }
    
    private void createHitParticles(float x, float y) {
        for (int i = 0; i < 5; i++) {
            Particle p = Particle.createSpark(x, y, new com.badlogic.gdx.math.Vector2(
                MathUtils.random(-1, 1), MathUtils.random(-1, 1)).nor());
            entityManager.addEntity(p);
        }
    }
    
    private void createExplosionParticles(Enemy enemy) {
        int count = enemy.getType() == Enemy.Type.BOSS ? 50 : 
                      enemy.getType() == Enemy.Type.HEAVY ? 25 : 15;
        
        for (int i = 0; i < count; i++) {
            float angle = MathUtils.random(MathUtils.PI2);
            float speed = MathUtils.random(50, 200);
            Particle p = new Particle(enemy.getPosition().x + enemy.getWidth()/2, 
                                    enemy.getPosition().y + enemy.getHeight()/2,
                                    new com.badlogic.gdx.graphics.g2d.TextureRegion(
                                        com.raidenclone.game.utils.AssetManager.getInstance()
                                            .get(com.raidenclone.game.utils.AssetManager.EXPLOSION)));
            p.velocity.set(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed);
            p.color.set(1, MathUtils.random(0.3f, 0.8f), 0, 1);
            p.endColor.set(1, 0, 0, 0);
            p.maxLifetime = MathUtils.random(0.5f, 1.2f);
            p.startScale = MathUtils.random(0.5f, 1.5f);
            p.endScale = 0;
            p.rotationSpeed = MathUtils.random(-720, 720);
            p.additive = true;
            entityManager.addEntity(p);
        }
        
        // Screen shake for boss
        if (enemy.getType() == Enemy.Type.BOSS) {
            entityManager.getPlayer().screenShake = 30;
        }
    }
    
    private void tryDropPowerup(Enemy enemy) {
        float dropChance = 0.15f;
        if (enemy.getType() == Enemy.Type.HEAVY) dropChance = 0.3f;
        if (enemy.getType() == Enemy.Type.BOSS) dropChance = 1.0f;
        
        if (MathUtils.random() < dropChance) {
            Powerup.Type type;
            float r = MathUtils.random();
            
            if (enemy.getType() == Enemy.Type.BOSS) {
                // Boss drops multiple
                for (int i = 0; i < 3; i++) {
                    type = getRandomPowerupType();
                    Powerup p = new Powerup(enemy.getPosition().x + MathUtils.random(-50, 50),
                                          enemy.getPosition().y + MathUtils.random(-30, 30), type);
                    entityManager.addEntity(p);
                }
                return;
            }
            
            if (r < 0.4f) type = Powerup.Type.POWER;
            else if (r < 0.6f) type = Powerup.Type.BOMB;
            else if (r < 0.8f) type = Powerup.Type.SCORE;
            else type = Powerup.Type.LIFE;
            
            Powerup p = new Powerup(enemy.getPosition().x + enemy.getWidth()/2,
                                  enemy.getPosition().y + enemy.getHeight()/2, type);
            entityManager.addEntity(p);
        }
    }
    
    private Powerup.Type getRandomPowerupType() {
        float r = MathUtils.random();
        if (r < 0.4f) return Powerup.Type.POWER;
        if (r < 0.6f) return Powerup.Type.BOMB;
        if (r < 0.8f) return Powerup.Type.SCORE;
        return Powerup.Type.LIFE;
    }
    
    private void createPowerupParticles(Powerup powerup) {
        for (int i = 0; i < 10; i++) {
            Particle p = Particle.createSpark(powerup.getPosition().x, powerup.getPosition().y,
                new com.badlogic.gdx.math.Vector2(MathUtils.random(-1, 1), MathUtils.random(-1, 1)).nor());
            p.color.set(
                ((powerup.getType().color >> 24) & 0xFF) / 255f,
                ((powerup.getType().color >> 16) & 0xFF) / 255f,
                ((powerup.getType().color >> 8) & 0xFF) / 255f,
                1
            );
            p.endColor.set(p.color.r, p.color.g, p.color.b, 0);
            p.maxLifetime = MathUtils.random(0.5f, 1.0f);
            entityManager.addEntity(p);
        }
    }
}