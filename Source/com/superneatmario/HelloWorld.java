package com.superneatmario;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jgap.Chromosome;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.EvalThreadTask;
import com.anji.integration.TranscriberException;
import com.anji.neat.Evolver;
import com.anji.util.Properties;



public class HelloWorld {

	private static final String MARIO_PROPS = "mario.properties";
	
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World");
        //
        trainMario();
        //testAnji();
        
        System.out.println("Fin");
    }
    
    private static void testAnji() throws Exception {

    	Properties props = new Properties(MARIO_PROPS);
    	Evolver evolve = new Evolver();
    	evolve.init(props);

    	Date runStartDate = Calendar.getInstance().getTime();
    	evolve.logger.info( "Run: start" );
    	DateFormat fmt = new SimpleDateFormat( "HH:mm:ss" );

    	ActivatorTranscriber activatorFactory = (ActivatorTranscriber) props
				.singletonObjectProperty( ActivatorTranscriber.class );
    	// initialize result data
    	Chromosome bestChampEva = evolve.getChamp(); 

    	
    	//ExecutorService service = Executors.newFixedThreadPool(1);
    	double totalDistance =0.0; 
    	double totalCompletedLevel = 0.0; 
    	double totalTimeLeft = 0.0; 
    	double totalKills = 0.0; 
    	double totalMode = 0.0; 
    	int seed = 17564; 
    	for(int difficulty = 0; difficulty < 41; difficulty ++)
    	{	
    		
    		Chromosome genotype = bestChampEva; 
    	    //service.execute(new EvalThreadTask(activatorFactory, genotype, 0, seed, difficulty));
    		try 
    		{
    			Activator activator = activatorFactory.newActivator(genotype);
    			SimANJI sa = new SimANJI(activator, seed, difficulty);
    			boolean isDone = sa.start();
    			
    			double [][] responses = null;
    			
    			if (isDone)
    			{
    				System.out.println(); 
    				System.out.println("Genome " + genotype.getId()); 
    				System.out.println("Difficulty " + difficulty); 
    				//genotype.setFitnessValue( (int)(sa.getDistance()*1.5 +  sa.didMarioWin()*sa.getTimeLeft()/*+ sa.getCoins()*COIN_ALPHA*/ ) );
    				
    				totalDistance += sa.getDistance(); 
    		    	totalCompletedLevel += sa.didMarioWin(); 
    		    	totalTimeLeft += sa.getTimeLeft(); 
    		    	totalKills += sa.getEnemyKillCount(); 
    		    	totalMode += sa.getMarioMode(); 
    		    	
    		    	System.out.println("totalDistance " + totalDistance); 
    		    	System.out.println("totalCompletedLevel " + totalCompletedLevel); 
    		    	System.out.println("totalTimeLeft " + totalTimeLeft); 
    		    	System.out.println("totalKills " +totalKills); 
    		    	System.out.println("totalMode " + totalMode); 
    		    	
    		    	
    		    	System.out.println(); 
    			}
    			
    		} 
    		catch (TranscriberException e) 
    		{
    			// TODO Auto-generated catch block
    			System.out.println( "transcriber error: " + e.getMessage() );
    			genotype.setFitnessValue( 1 );
    		}
    	}
    }
	/**
     * This method "jumpstarts" Anji with Xor.Properties file in its Property Folder.
     * @return 
     * @throws Exception 
     */
    public static void trainMario() throws Exception
    {

		Properties props = new Properties(MARIO_PROPS);
		System.out.println("After Properties" ); 
    	Evolver evolve = new Evolver();
    	evolve.init(props);
    	System.out.println("Evolve initialized" );
    	evolve.run();
    }
    
    /**
     * This method mimics the FrameLauncher.java class in starting the Mario Program.
     * @param ch 
     */
   
    

}