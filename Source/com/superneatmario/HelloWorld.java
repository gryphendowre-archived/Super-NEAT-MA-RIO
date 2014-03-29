package com.superneatmario;
import java.awt.Dimension;
import java.awt.Toolkit;
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
	
    public static void main(String[] args) {
        System.out.println("Hello, World");
        //
        jumpStartAnji();
        //jumpStartMario();
    }
    
    /**
     * This method "jumpstarts" Anji with Xor.Properties file in its Property Folder.
     * @return 
     */
    public static void jumpStartAnji()
    {
    	try
    	{
    		Properties props = new Properties(MARIO_PROPS);
        	Evolver evolve = new Evolver();
        	evolve.init(props);
        	evolve.run();
        	
        	//return evolve.getChamp();
    	}
    	catch(Exception e)
    	{
    		System.out.println("Error is: " + e);
    	}
		//return null;
    	
    }
    
    /**
     * This method mimics the FrameLauncher.java class in starting the Mario Program.
     * @param ch 
     */
    public static void jumpStartMario()
    {
    	
    	//for (int i = 0; i < 4; i++)
		//{
    		System.out.println("Serial ID " + 79318775993206607L); 
    		MarioComponent marioComponent = new MarioComponent(640, 480,  (79318775993206607L));
            JFrame frame = new JFrame("Mario Test"+0);
            frame.setContentPane(marioComponent);
            frame.pack();
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation((screenSize.width-frame.getWidth())/2, (screenSize.height-frame.getHeight())/2);
            
            frame.setVisible(true);
            
            marioComponent.setFocusCycleRoot(true);
            
            marioComponent.start();
            //marioComponent.keyPressed();
            frame.addKeyListener(marioComponent);
            frame.addFocusListener(marioComponent);
    	//SimANJI sa = new SimANJI(); 
    	//sa.start(1);
		//}
         /*
    	SimANJI sa2 = new SimANJI(); 
    	sa2.start(2);
    	
    	SimANJI sa3 = new SimANJI(); 
    	sa3.start(3);
       */
        
    }
    

}