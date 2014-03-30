package com.mojang.mario;

import java.awt.Graphics;

import com.mojang.sonar.SonarSoundEngine;
import com.mojang.sonar.SoundListener;


public abstract class Scene 
{
    
    public static boolean[] keys = new boolean[16];

    public void toggleKey(int key, boolean isPressed)
    {
        keys[key] = isPressed;
    }

    public abstract void init();

    public abstract void tick();

    public abstract void render(Graphics og, float alpha);
}