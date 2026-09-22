package com.raidenclone.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Raiden Clone");
        config.setWindowedMode(480, 800);
        config.setResizable(false);
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.setIdleFPS(30);
        new Lwjgl3Application(new RaidenClone(), config);
    }
}