package com.raidenclone.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.raidenclone.game.screens.GameScreen;
import com.raidenclone.game.screens.MenuScreen;
import com.raidenclone.game.utils.AssetManager;

public class RaidenClone extends Game {
    public SpriteBatch batch;
    public AssetManager assetManager;
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        AssetManager.setInstance(assetManager);
        assetManager.loadAll();
        setScreen(new MenuScreen(this));
    }
    
    @Override
    public void render() {
        super.render();
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
        super.dispose();
    }
}