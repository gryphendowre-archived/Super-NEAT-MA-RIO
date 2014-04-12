package com.superneatmario;



import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
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
import com.mojang.mario.sprites.Mario;

public class SimANJI implements Runnable{

	private boolean running = false;
	private int generation = 0; 
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
	private int randomThreadNum;
	private int didWin; 
	private int marioMode;
	private int enemyKills;
	private int difficulty; 
	
	public SimANJI(Activator activator, int seed, int difficulty ) {
		this.genomeNum = genomeNum; 
		this.activator = activator; 
		this.seed = seed; 
		//increase with testing
		this.difficulty = difficulty; 
		//sets time to always be 200 for testing
		this.generation = 180; 
	}
	
	public SimANJI(Activator activator, int seed, int genomeNum, int generation, int threadNum) {
		this.genomeNum = genomeNum; 
		this.activator = activator; 
		this.seed = seed; 
		this.generation = generation;
		this.randomThreadNum = threadNum;
		//static for training
		this.difficulty = 4; 
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
	
	public boolean start()
	{
		run();
		return true;
	}
	
	public void run() 
	{
		
		//while mario level is running
		//For each tick in mario
		// get sensor data for input from levelscene? make new data fields? 
		// responses = keypressed for mario
		//System.out.println("Serial ID " + ( genomeNum+18775993206607L)); 
		
		MarioComponent marioComponent = new MarioComponent(640, 480,  ( genomeNum+18775993206607L), seed, generation, genomeNum, difficulty );
        JFrame frame = new JFrame("Mario");
        frame.setContentPane(marioComponent);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Random randW = new Random(screenSize.width);
        Random randH = new Random(screenSize.height);
        frame.setLocation((screenSize.width-frame.getWidth()),
        		(screenSize.height - frame.getHeight())/2);
        //randH.nextInt()
        //frame.setLocation((screenSize.width-frame.getWidth()), (screenSize.height-frame.getHeight())/2);
        
        frame.setVisible(true);
        
        marioComponent.setFocusCycleRoot(true);
        
        marioComponent.start();
        //marioComponent.run(); 
        //marioComponent.keyPressed();
        frame.addKeyListener(marioComponent);
        frame.addFocusListener(marioComponent);
        
        
        try {
            Thread.sleep(2000);
        } catch(InterruptedException ex) {
            //Thread.currentThread().interrupt();
        	System.out.println("Wat"); 
        }

        
        //Continue until ded or win
        Random rand = new Random();

        int display = 0; 
        for (int loop = 0; ; loop++)
        {        	
        	if(marioComponent.scene == null)
        		continue; 
        	LevelScene curScene = (LevelScene)marioComponent.scene;          
			if (curScene == null)
			{
				continue;
			}
        	//System.out.println("Win or loss? " + marioComponent.isLossed + "  " +  marioComponent.isWon); 
        	if(curScene.isLose || curScene.isWon)
			{
        		if (curScene.isWon)
        			didWin = 1; 
				//System.out.println("Mario won or loss"); 
				curScene = (LevelScene)marioComponent.scene; 
				//System.out.println("Mario distance = " + curScene.mario.x);
				//System.out.println("Mario coins = " + curScene.mario.coins);   
				setDistance(curScene.mario.x) ;
				setCoins(curScene.mario.coins) ;
				setTimeLeft(curScene.timeLeft); 
				setMarioMode(curScene.mario); 
				setEnemyKillCount(curScene.enemyKillCount); 
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
				marioComponent = null; 
				System.gc(); 
				System.out.println("This is a test" ); 
				//convertImg_to_vid();
				//System.out.println("Thread count " + Thread.activeCount()); 
				break;
			}
			else
			{	//NOTE: creates a pseudo sync w/ mario component
				//stimuli were broke w/o this
				if(marioComponent.endofNewTick == false)
	        		continue; 
	        	
				try{
					
					
					 /* reduce dim w/o including good items atm or how low holes are*/
				double[] stimuli = {
						curScene.enemyD1LeftRight , 
						curScene.enemyD2LeftRight , 
						curScene.enemyD3LeftRight , 
						curScene.enemyD4LeftRight , 
//						curScene.enemyD5LeftRight , 
//						curScene.goodItemD1LeftRight ,
//				        curScene.goodItemD2LeftRight ,
//				        curScene.goodItemD3LeftRight ,
//				        curScene.goodItemD4LeftRight ,
//				        curScene.goodItemD5LeftRight ,
				        curScene.holeD1LeftRight , 
				        curScene.holeD2LeftRight , 
//				        curScene.holeD3LeftRight , 
//				        curScene.holeD4LeftRight , 
//				        curScene.holeD5LeftRight ,
				        curScene.obstacleD1LeftRight , 
				        curScene.obstacleD2LeftRight , 
//				        curScene.obstacleD3LeftRight , 
//				        curScene.obstacleD4LeftRight , 
//				        curScene.obstacleD5LeftRight,
				        curScene.enemyD1UpDown , 
						curScene.enemyD2UpDown , 
						curScene.enemyD3UpDown , 
						curScene.enemyD4UpDown , 
//						curScene.enemyD5UpDown , 
//						curScene.goodItemD1UpDown ,
//				        curScene.goodItemD2UpDown ,
//				        curScene.goodItemD3UpDown ,
//				        curScene.goodItemD4UpDown ,
//				        curScene.goodItemD5UpDown ,
				        curScene.holeD1UpDown , 
				        curScene.holeD2UpDown , 
//				        curScene.holeD3UpDown , 
//				        curScene.holeD4UpDown , 
//				        curScene.holeD5UpDown , 
				        curScene.obstacleD1UpDown , 
				        curScene.obstacleD2UpDown , 
				        curScene.obstacleD3UpDown , 
				        curScene.obstacleD4UpDown , 
//				        curScene.obstacleD5UpDown, 
				        curScene.mario.onGround?1.0:0, 				        		
				        (double)Math.round(curScene.mario.xa * 1000) / 1000, 
				        (double)Math.round(curScene.mario.ya * 1000) / 1000, 
				        curScene.numEmptySpace, 
				        curScene.yBlockSensor}; 
				
				
				//System.out.println("0) Red Koopa Count " + curScene.distToRedKoopa.size()); 
				//System.out.println("Enemy Count " + curScene.enemyD1LeftRight);			
				 			
				//System.out.println("Enemy Count " + curScene.enemyD1LeftRight);
				//System.out.println("Good Item Count " + curScene.goodItemD1LeftRight);
				//System.out.println("Obsticle Count " + curScene.obstacleD1LeftRight);
				//System.out.println("Hole Count " + curScene.holeD1LeftRight);
				
				responses = activator.next( stimuli );
				
//				if(display%48 == 0)
//				{
//					System.out.println("Stimuli "); 
//					for(int idx = 0; idx< stimuli.length; idx++)
//					{
//						System.out.print(stimuli[idx] + ", "); 
//					}
//					System.out.println(); 
//				}
				display++; 
				
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
//		                if (r==1)
//		                {
//		                //down crouches when big
//		                // else nothing
//		                	//keyCode = KeyEvent.VK_DOWN;
//		                	keyCode = KeyEvent.VK_RIGHT;
//		                }
		                if (r==1)
		                {
		                	keyCode = KeyEvent.VK_LEFT;
		                	//keyCode = KeyEvent.VK_RIGHT;
		                }
		                if (r==2)
		                {
		                //hold a runs 
		                // tap a fires fireball
		                	keyCode = KeyEvent.VK_A;
		                }
		                //attempt to manage triggers speed freq of button mashing
		                /**/
		                if (r==3)
		                {
		                //s jump
		                	keyCode = KeyEvent.VK_S;
		                	
		                }
		                
		                if(responses[i]>activator.getMaxResponse()-0.1)
						{    
		                	//System.out.println("response "+i + "  " + responses[i]);
		                	/*switch(r)
		                	{
		                	case 0: 
		                		System.out.println("Right"); 
		                		break;
		                	case 1: 
		                		System.out.println("Down"); 
		                		break;
		                	case 2: 
		                		System.out.println("Left"); 
		                		break;
		                	case 3: 
		                		System.out.println("A"); 
		                		break;
		                	case 4: 
		                		System.out.println("Jump"); 
		                		break;
		                	}*/
		                	marioComponent.toggleKey(keyCode, true);	


						}
						else
						{
							
							marioComponent.toggleKey(keyCode, false);
							
						}
					}
				}				
				
				}
				catch(Exception e)
				{
					
				}
	        }
	    }
        //return true; 
	}
	private void setEnemyKillCount(int enemyKillCount) {
		enemyKills = enemyKillCount; 
	}
	public int getEnemyKillCount()
	{
		return enemyKills; 
	}

	private void setMarioMode(Mario mario) {
		
		if(mario.fire && mario.large)
			marioMode = 2; 
		else if(mario.large && !mario.fire)
			marioMode = 1; 
		else if(!mario.large)
			marioMode = 0; 
		
	}
	public int getMarioMode() {
		return marioMode; 
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
	public int didMarioWin()
	{
		return didWin; 
	}
	//For rendering
	void convertImg_to_vid()
    {
        /*Process chperm;
        String path = "..\\Windows Programs\\Program Files (x86)\\FFMpeg\\bin\\ffmpeg.exe -f image2 -i Documents"+File.separator+"School"+File.separator+"Grad School"+File.separator+"Spring 2014"+File.separator+"Project"+File.separator+"Videos"+File.separator+"img%d.jpg -vcodecmpeg2video Documents"+File.separator+"School"+File.separator+"Grad School"+File.separator+"Spring 2014"+File.separator+"Project"+File.separator+"MakeVideo"+File.separator+"a.mpg\n";
        try {
        	System.out.println(path); 
            chperm=Runtime.getRuntime().exec("cmd");
        	//ByteArrayOutputStream baos = new ByteArrayOutputStream();
              DataOutputStream os = 
                  new DataOutputStream(chperm.getOutputStream());

                  os.writeBytes(path);
                  os.flush();


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } */
    }
}
