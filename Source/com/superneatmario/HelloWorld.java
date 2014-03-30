package com.superneatmario;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.Random;

import javax.swing.JApplet;
import javax.swing.JFrame;

import org.jgap.Chromosome;

import com.anji.neat.Evolver;
import com.anji.util.Properties;
import com.mojang.mario.Art;
import com.mojang.mario.MarioComponent;
import com.mojang.mario.level.LevelGenerator;



public class HelloWorld {

	private static final String MARIO_PROPS = "mario.properties";
	
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World");
        //
        jumpStartAnji();
        //jumpStartMario();
    }
    
    /**
     * This method "jumpstarts" Anji with Xor.Properties file in its Property Folder.
     * @return 
     * @throws Exception 
     */
    public static void jumpStartAnji() throws Exception
    {

    		Properties props = new Properties(MARIO_PROPS);
        	Evolver evolve = new Evolver();
        	evolve.init(props);
        	evolve.run();
        	
        	//return evolve.getChamp();
		//return null;
    	
    }
    
    /**
     * This method mimics the FrameLauncher.java class in starting the Mario Program.
     * @param ch 
     */
   
    

}