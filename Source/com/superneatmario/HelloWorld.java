package com.superneatmario;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Random;

import javax.swing.JFrame;

import com.anji.neat.Evolver;
import com.anji.util.Properties;
import com.mojang.mario.Art;
import com.mojang.mario.MarioComponent;
import com.mojang.mario.level.LevelGenerator;



public class HelloWorld {

	private static final String XOR_PROPS = "xor.properties";
	
    public static void main(String[] args) {
        System.out.println("Hello, World");
        jumpStartMario();
        jumpStartAnji();
    }
    
    /**
     * This method "jumpstarts" Anji with Xor.Properties file in its Property Folder.
     */
    public static void jumpStartAnji()
    {
    	try
    	{
    		Properties props = new Properties(XOR_PROPS);
        	Evolver evolve = new Evolver();
        	evolve.init(props);
        	evolve.run();
    	}
    	catch(Exception e)
    	{
    		System.out.println("Error is: " + e);
    	}
    	
    }
    
    /**
     * This method mimics the FrameLauncher.java class in starting the Mario Program.
     */
    public static void jumpStartMario()
    {
    	MarioComponent marioComponent = new MarioComponent(640, 480);
        JFrame frame = new JFrame("Mario Test");
        frame.setContentPane(marioComponent);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width-frame.getWidth())/2, (screenSize.height-frame.getHeight())/2);
        
        frame.setVisible(true);
        
        marioComponent.start();
        
        
    }
    

}