package com.raidenclone.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.raidenclone.game.utils.AssetManager;

public class Background {
    private Texture texture;
    private float y1 = 0;
    private float y2;
    private float scrollSpeed = 50;
    private Star[] stars;
    private static final int STAR_COUNT = 150;
    
    public static class Star {
        float x, y;
        float speed;
        float size;
        float alpha;
        float twinklePhase;
        
        public Star() {
            reset();
        }
        
        public void reset() {
            x = MathUtils.random(480);
            y = MathUtils.random(800);
            speed = MathUtils.random(20, 100);
            size = MathUtils.random(1, 3);
            alpha = MathUtils.random(0.2f, 1.0f);
            twinklePhase = MathUtils.random(0, MathUtils.PI2);
        }
        
        public void update(float delta) {
            y -= speed * delta;
            twinklePhase += delta * 3;
            if (y < -size) {
                y = 800 + size;
                x = MathUtils.random(480);
            }
        }
    }
    
    public Background() {
        texture = AssetManager.getInstance().get(AssetManager.BACKGROUND);
        y2 = -800;
        
        stars = new Star[STAR_COUNT];
        for (int i = 0; i < STAR_COUNT; i++) {
            stars[i] = new Star();
            stars[i].y = MathUtils.random(800);
        }
    }
    
    public void update(float delta) {
        y1 += scrollSpeed * delta;
        y2 += scrollSpeed * delta;
        
        if (y1 >= 800) y1 -= 1600;
        if (y2 >= 800) y2 -= 1600;
        
        for (Star star : stars) {
            star.update(delta);
        }
    }
    
    public void render(SpriteBatch batch) {
        // Draw main background texture
        batch.draw(texture, 0, y1, 480, 800);
        batch.draw(texture, 0, y2, 480, 800);
        
        // Draw stars
        for (Star star : stars) {
            float twinkle = 0.5f + MathUtils.sin(star.twinklePhase) * 0.5f;
            batch.setColor(1, 1, 1, star.alpha * twinkle);
            batch.draw(AssetManager.getInstance().get(AssetManager.EXPLOSION, Texture.class), 
                      star.x - star.size/2, star.y - star.size/2, star.size, star.size);
        }
        batch.setColor(1, 1, 1, 1);
    }
    
    public void setScrollSpeed(float speed) {
        scrollSpeed = speed;
    }
}