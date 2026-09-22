package com.raidenclone.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.raidenclone.game.entities.Enemy;
import com.raidenclone.game.entities.EntityManager;

public class WaveManager {
    private EntityManager entityManager;
    private int currentWave = 0;
    private float waveTimer = 0;
    private float waveInterval = 3.0f;
    private int enemiesInWave = 0;
    private int enemiesSpawned = 0;
    private boolean waveActive = false;
    private boolean bossSpawned = false;
    private int wavePattern = 0;
    
    public WaveManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public void update(float delta) {
        if (!waveActive) {
            waveTimer += delta;
            if (waveTimer >= waveInterval) {
                startNextWave();
            }
        } else {
            // Check if wave is complete
            if (enemiesSpawned >= enemiesInWave && entityManager.getEnemies().size == 0) {
                waveActive = false;
                waveTimer = 0;
                waveInterval = MathUtils.random(2.0f, 4.0f);
                
                // Every 5 waves, spawn a boss
                if (currentWave % 5 == 0 && currentWave > 0) {
                    waveInterval = 5.0f;
                }
            }
        }
    }
    
    private void startNextWave() {
        currentWave++;
        waveActive = true;
        enemiesSpawned = 0;
        bossSpawned = false;
        wavePattern = MathUtils.random(0, 4);
        
        if (currentWave % 5 == 0 && currentWave > 0) {
            // Boss wave
            spawnBoss();
        } else {
            // Regular wave
            spawnWave();
        }
    }
    
    private void spawnWave() {
        switch (wavePattern) {
            case 0: // V formation
                spawnVFormation();
                break;
            case 1: // Horizontal line
                spawnHorizontalLine();
                break;
            case 2: // Random scatter
                spawnScatter();
                break;
            case 3: // Circling
                spawnCircling();
                break;
            case 4: // Fast attackers
                spawnFastAttackers();
                break;
        }
    }
    
    private void spawnVFormation() {
        int rows = 3 + currentWave / 3;
        int perRow = 3;
        enemiesInWave = rows * perRow;
        
        for (int row = 0; row < rows; row++) {
            float y = 800 + row * 80;
            for (int i = 0; i < perRow; i++) {
                float x = 240 + (i - 1) * 70;
                Enemy.Type type = getEnemyTypeForWave();
                Enemy enemy = new Enemy(x, y, type);
                enemy.setEntryDelay(row * 0.3f + i * 0.1f);
                entityManager.addEntity(enemy);
                enemiesSpawned++;
            }
        }
    }
    
    private void spawnHorizontalLine() {
        int count = 5 + currentWave / 2;
        enemiesInWave = count;
        
        float startX = MathUtils.random(80, 200);
        for (int i = 0; i < count; i++) {
            float x = startX + i * 50;
            Enemy.Type type = getEnemyTypeForWave();
            Enemy enemy = new Enemy(x, 850, type);
            enemy.setEntryDelay(i * 0.15f);
            entityManager.addEntity(enemy);
            enemiesSpawned++;
        }
    }
    
    private void spawnScatter() {
        int count = 6 + currentWave;
        enemiesInWave = count;
        
        for (int i = 0; i < count; i++) {
            float x = MathUtils.random(50, 430);
            float y = 800 + MathUtils.random(0, 200);
            Enemy.Type type = getEnemyTypeForWave();
            Enemy enemy = new Enemy(x, y, type);
            enemy.setEntryDelay(i * 0.2f);
            entityManager.addEntity(enemy);
            enemiesSpawned++;
        }
    }
    
    private void spawnCircling() {
        int count = 8;
        enemiesInWave = count;
        float centerX = 240;
        float centerY = 900;
        
        for (int i = 0; i < count; i++) {
            float angle = i * MathUtils.PI2 / count;
            float radius = 120;
            float x = centerX + MathUtils.cos(angle) * radius;
            float y = centerY + MathUtils.sin(angle) * radius;
            Enemy enemy = new Enemy(x, y, Enemy.Type.BASIC);
            enemy.setEntryDelay(i * 0.1f);
            entityManager.addEntity(enemy);
            enemiesSpawned++;
        }
    }
    
    private void spawnFastAttackers() {
        int count = 4 + currentWave / 2;
        enemiesInWave = count;
        
        for (int i = 0; i < count; i++) {
            float x = MathUtils.random(60, 420);
            Enemy enemy = new Enemy(x, 850 + i * 60, Enemy.Type.FAST);
            enemy.setEntryDelay(i * 0.25f);
            entityManager.addEntity(enemy);
            enemiesSpawned++;
        }
    }
    
    private void spawnBoss() {
        Enemy boss = new Enemy(240, 1000, Enemy.Type.BOSS);
        entityManager.addEntity(boss);
        enemiesInWave = 1;
        enemiesSpawned = 1;
        bossSpawned = true;
    }
    
    private Enemy.Type getEnemyTypeForWave() {
        float heavyChance = Math.min(0.3f, currentWave * 0.02f);
        float fastChance = Math.min(0.4f, currentWave * 0.03f);
        
        float r = MathUtils.random();
        if (r < heavyChance) return Enemy.Type.HEAVY;
        if (r < heavyChance + fastChance) return Enemy.Type.FAST;
        return Enemy.Type.BASIC;
    }
    
    public int getCurrentWave() { return currentWave; }
    public boolean isWaveActive() { return waveActive; }
    public float getWaveProgress() { 
        if (!waveActive) return 0;
        return (float)enemiesSpawned / Math.max(1, enemiesInWave);
    }
    public void reset() {
        currentWave = 0;
        waveTimer = 0;
        waveActive = false;
        bossSpawned = false;
    }
}