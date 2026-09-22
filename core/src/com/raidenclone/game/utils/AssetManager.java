package com.raidenclone.game.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.MathUtils;

public class AssetManager extends com.badlogic.gdx.assets.AssetManager {
    public static final String PLAYER_SHIP = "player_ship";
    public static final String ENEMY_BASIC = "enemy_basic";
    public static final String ENEMY_FAST = "enemy_fast";
    public static final String ENEMY_HEAVY = "enemy_heavy";
    public static final String BOSS = "boss";
    public static final String PLAYER_BULLET = "player_bullet";
    public static final String ENEMY_BULLET = "enemy_bullet";
    public static final String BOMB_ICON = "bomb_icon";
    public static final String POWERUP = "powerup";
    public static final String BACKGROUND = "background";
    public static final String EXPLOSION = "explosion";
    
    public static final String SHOOT_SOUND = "shoot";
    public static final String EXPLOSION_SOUND = "explosion";
    public static final String BOMB_SOUND = "bomb";
    public static final String POWERUP_SOUND = "powerup";
    public static final String BG_MUSIC = "bg_music";
    
    public void loadAll() {
        createProceduralTextures();
        createSounds();
        createMusic();
    }
    
    private void createProceduralTextures() {
        // Player ship - sleek fighter jet
        Pixmap playerPixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        playerPixmap.setColor(0x00FFFF88);
        // Main body
        playerPixmap.fillTriangle(32, 4, 16, 40, 48, 40);
        // Wings
        playerPixmap.fillTriangle(16, 40, 4, 56, 28, 40);
        playerPixmap.fillTriangle(48, 40, 60, 56, 36, 40);
        // Cockpit
        playerPixmap.setColor(0x88FFFFFF);
        playerPixmap.fillTriangle(32, 12, 24, 32, 40, 32);
        // Engine glow
        playerPixmap.setColor(0xFF44FFFF);
        playerPixmap.fillRectangle(28, 48, 8, 12);
        setAsset(PLAYER_SHIP, new Texture(playerPixmap));
        playerPixmap.dispose();
        
        // Enemy basic - angular fighter
        Pixmap enemyBasic = new Pixmap(48, 48, Pixmap.Format.RGBA8888);
        enemyBasic.setColor(0xFF4444FF);
        enemyBasic.fillTriangle(24, 44, 8, 8, 40, 8);
        enemyBasic.fillRectangle(16, 8, 16, 24);
        enemyBasic.setColor(0xFF8888FF);
        enemyBasic.fillTriangle(24, 32, 16, 24, 32, 24);
        enemyBasic.setColor(0xFFFF4444);
        enemyBasic.fillRectangle(20, 36, 8, 8);
        setAsset(ENEMY_BASIC, new Texture(enemyBasic));
        enemyBasic.dispose();
        
        // Enemy fast - sleek dart
        Pixmap enemyFast = new Pixmap(40, 40, Pixmap.Format.RGBA8888);
        enemyFast.setColor(0xFF44FF44);
        enemyFast.fillTriangle(20, 36, 4, 4, 36, 4);
        enemyFast.fillTriangle(20, 36, 12, 20, 28, 20);
        enemyFast.setColor(0x88FF8844);
        enemyFast.fillTriangle(20, 20, 14, 12, 26, 12);
        setAsset(ENEMY_FAST, new Texture(enemyFast));
        enemyFast.dispose();
        
        // Enemy heavy - large bomber
        Pixmap enemyHeavy = new Pixmap(72, 72, Pixmap.Format.RGBA8888);
        enemyHeavy.setColor(0x884444FF);
        enemyHeavy.fillRectangle(12, 12, 48, 48);
        enemyHeavy.fillTriangle(36, 60, 6, 24, 66, 24);
        enemyHeavy.setColor(0xAA6666FF);
        enemyHeavy.fillRectangle(18, 18, 12, 36);
        enemyHeavy.fillRectangle(42, 18, 12, 36);
        enemyHeavy.setColor(0xFF4444AA);
        enemyHeavy.fillRectangle(24, 48, 24, 8);
        setAsset(ENEMY_HEAVY, new Texture(enemyHeavy));
        enemyHeavy.dispose();
        
        // Boss - massive dreadnought
        Pixmap boss = new Pixmap(128, 128, Pixmap.Format.RGBA8888);
        boss.setColor(0x442266FF);
        boss.fillRectangle(32, 24, 64, 80);
        boss.fillTriangle(64, 104, 16, 48, 112, 48);
        boss.fillRectangle(8, 48, 24, 40);
        boss.fillRectangle(96, 48, 24, 40);
        boss.setColor(0x664488FF);
        boss.fillRectangle(40, 32, 16, 48);
        boss.fillRectangle(72, 32, 16, 48);
        boss.setColor(0xFF8800FF);
        for (int i = 0; i < 5; i++) {
            boss.fillRectangle(36, 40 + i * 12, 8, 8);
            boss.fillRectangle(84, 40 + i * 12, 8, 8);
        }
        boss.setColor(0xFF0000FF);
        boss.fillCircle(64, 88, 16);
        setAsset(BOSS, new Texture(boss));
        boss.dispose();
        
        // Player bullet - plasma bolt
        Pixmap playerBullet = new Pixmap(16, 32, Pixmap.Format.RGBA8888);
        playerBullet.setColor(0x00FFFFFF);
        playerBullet.fillTriangle(8, 0, 0, 24, 16, 24);
        playerBullet.setColor(0x88FFFFFF);
        playerBullet.fillTriangle(8, 8, 4, 20, 12, 20);
        setAsset(PLAYER_BULLET, new Texture(playerBullet));
        playerBullet.dispose();
        
        // Enemy bullet - red pulse
        Pixmap enemyBullet = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        enemyBullet.setColor(0xFF4444FF);
        enemyBullet.fillCircle(6, 6, 6);
        enemyBullet.setColor(0xFFFF8888);
        enemyBullet.fillCircle(6, 6, 3);
        setAsset(ENEMY_BULLET, new Texture(enemyBullet));
        enemyBullet.dispose();
        
        // Bomb icon
        Pixmap bombIcon = new Pixmap(48, 48, Pixmap.Format.RGBA8888);
        bombIcon.setColor(0xFF8800FF);
        bombIcon.fillCircle(24, 24, 22);
        bombIcon.setColor(0xFFFFCC00);
        bombIcon.fillCircle(24, 18, 14);
        bombIcon.setColor(0xFFAA00FF);
        bombIcon.fillCircle(24, 16, 6);
        bombIcon.setColor(0xFF000000);
        bombIcon.drawLine(24, 10, 24, 4);
        setAsset(BOMB_ICON, new Texture(bombIcon));
        bombIcon.dispose();
        
        // Powerup
        Pixmap powerup = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        powerup.setColor(0x00FF88FF);
        powerup.fillRectangle(4, 4, 24, 24);
        powerup.setColor(0xFFFFFF00);
        powerup.fillRectangle(8, 8, 16, 16);
        powerup.setColor(0xFF00FF88);
        powerup.fillRectangle(12, 12, 8, 8);
        setAsset(POWERUP, new Texture(powerup));
        powerup.dispose();
        
        // Background - starfield
        Pixmap bg = new Pixmap(480, 800, Pixmap.Format.RGBA8888);
        bg.setColor(0x080818FF);
        bg.fillRectangle(0, 0, 480, 800);
        bg.setColor(0xFFFFFFFF);
        for (int i = 0; i < 200; i++) {
            int x = MathUtils.random(480);
            int y = MathUtils.random(800);
            int size = MathUtils.random(1, 3);
            float alpha = MathUtils.random(0.3f, 1f);
            bg.setColor(1, 1, 1, alpha);
            bg.fillCircle(x, y, size);
        }
        setAsset(BACKGROUND, new Texture(bg));
        bg.dispose();
        
        // Explosion particle texture
        Pixmap explosion = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        explosion.setColor(0xFFFF8800);
        explosion.fillCircle(16, 16, 16);
        explosion.setColor(0xFFFF4400);
        explosion.fillCircle(16, 16, 12);
        explosion.setColor(0xFFFF0000);
        explosion.fillCircle(16, 16, 8);
        explosion.setColor(0xFFFFFFFF);
        explosion.fillCircle(16, 16, 4);
        setAsset(EXPLOSION, new Texture(explosion));
        explosion.dispose();
    }
    
    private void createSounds() {
        // Generate procedural sounds using simple waveforms
        // Shoot sound - short high-pitched beep
        Sound shoot = createToneSound(880, 0.05f, 0.5f);
        setAsset(SHOOT_SOUND, shoot);
        
        // Explosion - noise burst
        Sound explosion = createNoiseSound(0.3f, 0.7f);
        setAsset(EXPLOSION_SOUND, explosion);
        
        // Bomb - deep boom
        Sound bomb = createToneSound(110, 0.5f, 0.8f);
        setAsset(BOMB_SOUND, bomb);
        
        // Powerup - ascending tone
        Sound powerup = createToneSound(440, 0.2f, 0.6f);
        setAsset(POWERUP_SOUND, powerup);
    }
    
    private Sound createToneSound(float frequency, float duration, float volume) {
        int sampleRate = 44100;
        int length = (int)(sampleRate * duration);
        short[] samples = new short[length];
        for (int i = 0; i < length; i++) {
            float t = (float)i / sampleRate;
            float envelope = 1.0f - t / duration;
            samples[i] = (short)(Math.sin(2 * Math.PI * frequency * t) * envelope * volume * Short.MAX_VALUE);
        }
        return new Sound() {
            @Override public long play() { return play(1.0f); }
            @Override public long play(float volume) { return play(volume, 1.0f, 0.0f); }
            @Override public long play(float volume, float pitch, float pan) { return -1; }
            @Override public void stop() {}
            @Override public void stop(long id) {}
            @Override public void pause() {}
            @Override public void pause(long id) {}
            @Override public void resume() {}
            @Override public void resume(long id) {}
            @Override public void setPitch(long id, float pitch) {}
            @Override public void setVolume(long id, float volume) {}
            @Override public void setPan(long id, float pan, float volume) {}
            @Override public void setLooping(long id, boolean looping) {}
            @Override public void dispose() {}
        };
    }
    
    private Sound createNoiseSound(float duration, float volume) {
        int sampleRate = 44100;
        int length = (int)(sampleRate * duration);
        short[] samples = new short[length];
        for (int i = 0; i < length; i++) {
            float envelope = 1.0f - (float)i / length;
            samples[i] = (short)((MathUtils.random(-1f, 1f)) * envelope * volume * Short.MAX_VALUE);
        }
        return new Sound() {
            @Override public long play() { return play(1.0f); }
            @Override public long play(float volume) { return play(volume, 1.0f, 0.0f); }
            @Override public long play(float volume, float pitch, float pan) { return -1; }
            @Override public void stop() {}
            @Override public void stop(long id) {}
            @Override public void pause() {}
            @Override public void pause(long id) {}
            @Override public void resume() {}
            @Override public void resume(long id) {}
            @Override public void setPitch(long id, float pitch) {}
            @Override public void setVolume(long id, float volume) {}
            @Override public void setPan(long id, float pan, float volume) {}
            @Override public void setLooping(long id, boolean looping) {}
            @Override public void dispose() {}
        };
    }
    
    private void createMusic() {
        Music music = new Music() {
            @Override public void play() {}
            @Override public void pause() {}
            @Override public void stop() {}
            @Override public void setLooping(boolean isLooping) {}
            @Override public boolean isLooping() { return false; }
            @Override public void setVolume(float volume) {}
            @Override public float getVolume() { return 0; }
            @Override public void setPosition(float position) {}
            @Override public float getPosition() { return 0; }
            @Override public void dispose() {}
        };
        setAsset(BG_MUSIC, music);
    }
}