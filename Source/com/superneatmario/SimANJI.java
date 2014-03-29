package com.superneatmario;



import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.anji.integration.Activator;
import com.mojang.mario.Art;
import com.mojang.mario.LevelScene;
import com.mojang.mario.MarioComponent;

public class SimANJI {

	private boolean running = false;
	private int popNum = 0; 
	Activator activator;
	double[] responses; 
	public SimANJI(Activator activator) {
		this.activator = activator; 
	}

	public void start(int i )
    {
		System.out.println("SimANJI Start " + running); 
		if (!running)
        {
			popNum = i; 
			running = true;
	       // new Thread(this, "Game Thread"+i).start();
        }

    }

	public void stop()
    {
        Art.stopMusic();
        running = false;
    }
	
	public boolean run() 
	{
		
		//while mario level is running
		//For each tick in mario
		// get sensor data for input from levelscene? make new data fields? 
		// responses = keypressed for mario
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
        

        try {
            Thread.sleep(1000);
        } catch(InterruptedException ex) {
            //Thread.currentThread().interrupt();
        	System.out.println("Wat"); 
        }

        
        //Continue until ded or win
        Random rand = new Random();
        final int TICKS_PER_SECOND = 24;
        for (int loop = 0; ; loop++)
        {        	
        	LevelScene curScene = (LevelScene)marioComponent.scene;          
			
        	//System.out.println("Win or loss? " + marioComponent.isLossed + "  " +  marioComponent.isWon); 
        	if(marioComponent.isLossed || marioComponent.isWon)
			{
				System.out.println("Mario won or loss"); 
				curScene = (LevelScene)marioComponent.scene; 
				System.out.println("Mario distance = " + curScene.mario.x);
				System.out.println("Mario coins = " + curScene.mario.coins);            		
				curScene.mario.resetStatic();
				frame.setVisible(false); //disappear
				frame.dispose(); //Destroy the JFrame object
				break;
			}
			else
			{	
				try{
				List idxs = new ArrayList();
				for ( int i = 0; i < curScene.distToRedKoopa.size(); ++i )
					idxs.add( curScene.distToRedKoopa.get(i) );
				//Collections.shuffle( idxs, randomizer.getRand() );
				
				Iterator iter = idxs.iterator();
				
				//if all stimuli are 0 , continue 
				
				double[] shuffledStimuli = new double[ curScene.distToRedKoopa.size() ];
				
				int k = 0;
				while ( iter.hasNext() ) {
					BigDecimal idx = (BigDecimal) iter.next();
					shuffledStimuli[ k ] = (idx.doubleValue()<0.5)?1:0;
					k++;
				}
				//System.out.println("0) Red Koopa Count " + curScene.distToRedKoopa.size()); 
							
				 			
				      
				responses = activator.next( shuffledStimuli );
				
				System.out.println("1) Red Koopa Count " + curScene.distToRedKoopa.size());
				System.out.println("responses " + responses[0]); 
				System.out.println("Response " + activator.getMaxResponse());
				System.out.println("Max output " + activator.getOutputDimension());
				
				//int keyCode = KeyEvent.VK_RIGHT; 

            	//int r = rand.nextInt(6); 
            	/*
                if (r==0 ||r==1)
                {
                	keyCode = KeyEvent.VK_RIGHT;
                }
                if (r==2)
                {
                	keyCode = KeyEvent.VK_DOWN;
                }
                if (r==3)
                {
                	keyCode = KeyEvent.VK_UP;
                }
                if (r==4)
                {
                	keyCode = KeyEvent.VK_A;
                }
                if (r==5)
                {
                	keyCode = KeyEvent.VK_S;
                }
                
                //System.out.println("r "  + r +  "  key " + keyCode); 
            	marioComponent.toggleKey(keyCode, true);
            	if (r==5)
            	{
	            	try {
	                    Thread.sleep(rand.nextInt(240)+10);
	                } catch(InterruptedException ex) {
	                    //Thread.currentThread().interrupt();
	                	System.out.println("Wat"); 
	                }
            	}
            	else //if (r == 0 || r == 1)
            	{
            		try {
	                    Thread.sleep(250);
	                } catch(InterruptedException ex) {
	                    //Thread.currentThread().interrupt();
	                	System.out.println("Wat"); 
	                }
            	}
            	if (r!=4)
            		marioComponent.toggleKey(keyCode, false);

				//System.out.println("Win or loss? " + marioComponent.isLossed + "  " +  marioComponent.isWon); 
				}
				*/
				}
				catch(Exception e)
				{
					
				}
	        }
	    }
        return true; 
	}
	public double[] getResponses()
	{
		return responses; 
	}
	public Activator getActivator()
	{
		return activator; 
	}
	
}
