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
	double maxResponse; 
	int maxResponseNode; 
	int seed; 
	private boolean isManual = false;
	private int coins;
	private float distance;
	private int timeLeft;
	private int genomeNum; 
	public SimANJI(Activator activator, int seed, int genomeNum) {
		this.genomeNum = genomeNum; 
		this.activator = activator; 
		this.seed = seed; 
	}

	/*public void start(int i )
    {
		System.out.println("SimANJI Start " + running); 
		if (!running)
        {
			popNum = i; 
			running = true;
	       // new Thread(this, "Game Thread"+i).start();
        }

    }*/

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
		System.out.println("Serial ID " + ( genomeNum+18775993206607L)); 
		
		MarioComponent marioComponent = new MarioComponent(640, 480,  ( genomeNum+18775993206607L), seed);
        JFrame frame = new JFrame("Mario");
        frame.setContentPane(marioComponent);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width-frame.getWidth()), (screenSize.height-frame.getHeight())/2);
        
        frame.setVisible(true);
        
        marioComponent.setFocusCycleRoot(true);
        
        marioComponent.start();
        //marioComponent.run(); 
        //marioComponent.keyPressed();
        frame.addKeyListener(marioComponent);
        frame.addFocusListener(marioComponent);
        
        
/*        try {
            this.wait(1500);
        } catch(InterruptedException ex) {
            //Thread.currentThread().interrupt();
        	System.out.println("Wat"); 
        }*/

        
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
				setDistance(curScene.mario.x) ;
				setCoins(curScene.mario.coins) ;
				setTimeLeft(curScene.timeLeft); 
				curScene.mario.resetStatic();
				marioComponent.removeAll();				
				marioComponent.stop();
				frame.removeKeyListener(marioComponent);
		        frame.removeFocusListener(marioComponent);
		        //marioComponent = null; 
		        frame.removeAll(); 
				frame.setVisible(false); //disappear
				frame.dispose(); //Destroy the JFrame object
				//Thread.dumpStack();
				System.out.println("Thread count " + Thread.activeCount()); 
				marioComponent = null; 
				System.gc(); 
				System.out.println("Thread count " + Thread.activeCount()); 
				break;
			}
			else
			{	
				try{
					
					
					 /* total inputs from all enemy,items,holes,obstacles x5 in level scene*/ 
				double[] stimuli = {curScene.enemyD1LeftRight , 
						curScene.enemyD2LeftRight , 
						curScene.enemyD3LeftRight , 
						curScene.enemyD4LeftRight , 
						curScene.enemyD5LeftRight , 
						curScene.goodItemD1LeftRight ,
				        curScene.goodItemD2LeftRight ,
				        curScene.goodItemD3LeftRight ,
				        curScene.goodItemD4LeftRight ,
				        curScene.goodItemD5LeftRight ,
				        curScene.holeD1LeftRight , 
				        curScene.holeD2LeftRight , 
				        curScene.holeD3LeftRight , 
				        curScene.holeD4LeftRight , 
				        curScene.holeD5LeftRight , curScene.obstacleD1LeftRight , 
				        curScene.obstacleD2LeftRight , 
				        curScene.obstacleD3LeftRight , 
				        curScene.obstacleD4LeftRight , 
				        curScene.obstacleD5LeftRight, 1 }; 
				
				//System.out.println("0) Red Koopa Count " + curScene.distToRedKoopa.size()); 
				//System.out.println("Enemy Count " + curScene.enemyD1LeftRight);			
				 			
				//System.out.println("Enemy Count " + curScene.enemyD1LeftRight);
				//System.out.println("Good Item Count " + curScene.goodItemD1LeftRight);
				//System.out.println("Obsticle Count " + curScene.obstacleD1LeftRight);
				//System.out.println("Hole Count " + curScene.holeD1LeftRight);
				
				responses = activator.next( stimuli );
				
				
				
				maxResponse = 0; 
				maxResponseNode = 0; 
				for(int i = 0; i < responses.length; i ++)
				{
					//System.out.println("response "+i + "  " + responses[i]);
					/*if(responses[i] > maxResponse)
					{
						maxResponse = responses[i]; 
						maxResponseNode = i; 
					}*/
				
					
					//System.out.println("Max Response " + activator.getMaxResponse());
					//System.out.println("Max output dim " + activator.getOutputDimension());
					
					int keyCode = KeyEvent.VK_RIGHT; 
					if(!isManual)
					{
		            	int r = i; 
		            	
		                if (r==0)
		                {
		                	keyCode = KeyEvent.VK_RIGHT;
		                }
		                if (r==1)
		                {
		                //down crouches when big
		                // else nothing
		                	keyCode = KeyEvent.VK_DOWN;
		                }
		                if (r==2)
		                {
		                	keyCode = KeyEvent.VK_LEFT;
		                }
		                if (r==3)
		                {
		                //hold a runs 
		                // tap a fires fireball
		                	keyCode = KeyEvent.VK_A;
		                }
		                if (r==4)
		                {
		                //s jump
		                	keyCode = KeyEvent.VK_S;
		                }
		                //TODO duration
		                /* if (r==6)
		                {
		                	keyCode = KeyEvent.VK_UP;
		                }*/
		                if(responses[i]>activator.getMaxResponse()-0.1)
						{    
		                	switch(r)
		                	{
		                	case 0: 
		                		System.out.println("Right"); 
		                	case 1: 
		                		System.out.println("Down"); 
		                	case 2: 
		                		System.out.println("Left"); 
		                	case 3: 
		                		System.out.println("A"); 
		                	case 4: 
		                		System.out.println("Jump"); 
		                	}
			                
			            	marioComponent.toggleKey(keyCode, true);
						}
						else
						{
							marioComponent.toggleKey(keyCode, false);
						}
					}
				}
				/*try {
                    Thread.sleep(250);
                } catch(InterruptedException ex) {
                    //Thread.currentThread().interrupt();
                	System.out.println("Wat"); 
                }*/
            	/*if (r==5)
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
            		*/
				
				}
				catch(Exception e)
				{
					
				}
	        }
	    }
        return true; 
	}
	private void setTimeLeft(int timeLeft) {
		this.timeLeft = timeLeft; 		
	}

	private void setCoins(int coins) {
		this.coins = coins; 
		
	}
	public float getCoins()
	{
		return this.coins; 
	}
	private void setDistance(float x) {
		this.distance = x;
	}
	public float getDistance()
	{
		return this.distance; 
	}

	public double[] getResponses()
	{
		return responses; 
	}
	public Activator getActivator()
	{
		return activator; 
	}
	public int getMaxResponseNode()
	{
		return maxResponseNode; 
	}

	public double getTimeLeft() {
		return this.timeLeft; 
	}
	
}
