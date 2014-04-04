package com.anji.integration;

import org.jgap.Chromosome;

import com.superneatmario.SimANJI;

public class EvalThreadTask implements Runnable {

	private final static double COIN_ALPHA = 2.0; 
	private String command;
	private ActivatorTranscriber activatorFactory;
	private Chromosome genotype;
	private int genomeNum;
	private int seed;
	
	public EvalThreadTask(ActivatorTranscriber activatorFactory, Chromosome geno, int genoNum, int seed)
	{
		this.activatorFactory = activatorFactory;
		this.genotype = geno;
		this.genomeNum = genoNum;
		this.seed = seed;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try 
		{
			Activator activator = this.activatorFactory.newActivator(this.genotype);
			SimANJI sa = new SimANJI(activator, this.seed, this.genomeNum);
			boolean isDone = sa.start();
			
			double [][] responses = null;
			
			if (isDone)
			{
				System.out.println("Fitness Val " + (int)(sa.getDistance() + sa.getCoins() )); 
				genotype.setFitnessValue( (int)(sa.getDistance()*1.5 + sa.getCoins()*COIN_ALPHA ) );/**/
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
