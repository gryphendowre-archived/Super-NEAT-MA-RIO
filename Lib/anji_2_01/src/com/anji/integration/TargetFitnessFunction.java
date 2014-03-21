/*
 * Copyright (C) 2004 Derek James and Philip Tucker
 * 
 * This file is part of ANJI (Another NEAT Java Implementation).
 * 
 * ANJI is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 * created by Philip Tucker
 */
package com.anji.integration;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import com.anji.util.Configurable;
import com.anji.util.Properties;
import com.anji.util.Randomizer;
import com.mojang.mario.LevelScene;
import com.mojang.mario.MarioComponent;
import com.mojang.mario.sprites.Mario;

/**
 * Determines fitness based on how close <code>Activator</code> output is to a target.
 * 
 * @author Philip Tucker
 */
public abstract class TargetFitnessFunction implements BulkFitnessFunction, Configurable {

private static Logger logger = Logger.getLogger( TargetFitnessFunction.class );

private final static String ADJUST_FOR_NETWORK_SIZE_FACTOR_KEY = "fitness.function.adjust.for.network.size.factor";

private float adjustForNetworkSizeFactor = 0.0f;

/**
 * properties key, file containing strimuli
 */
public final static String STIMULI_FILE_NAME_KEY = "stimuli.file";

/**
 * properties key, file containing output targets
 */
public final static String TARGETS_FILE_NAME_KEY = "targets.file";

private final static String TARGETS_RANGE_KEY = "targets.range";

/**
 * dimension # training sets by dim stimuli
 */
private double[][] stimuli;

/**
 * dimension # training sets by dim response
 */
private double[][] targets;

private double targetRange = 0.0d;

private int maxFitnessValue;

private ActivatorTranscriber activatorFactory;

private Randomizer randomizer;

/**
 * See <a href=" {@docRoot}/params.htm" target="anji_params">Parameter Details </a> for
 * specific property settings.
 * 
 * @param props configuration parameters
 */
public void init( Properties props ) {
	try {
		randomizer = (Randomizer) props.singletonObjectProperty( Randomizer.class );
		activatorFactory = (ActivatorTranscriber) props
				.singletonObjectProperty( ActivatorTranscriber.class );

		stimuli = Properties.loadArrayFromFile( props.getResourceProperty( STIMULI_FILE_NAME_KEY ) );
		targets = Properties.loadArrayFromFile( props.getResourceProperty( TARGETS_FILE_NAME_KEY ) );
		targetRange = props.getDoubleProperty( TARGETS_RANGE_KEY, 0.0d );
		adjustForNetworkSizeFactor = props.getFloatProperty( ADJUST_FOR_NETWORK_SIZE_FACTOR_KEY,
				0.0f );

		if ( stimuli.length == 0 || targets.length == 0 )
			throw new IllegalArgumentException( "require at least 1 training set for stimuli ["
					+ stimuli.length + "] and targets [" + targets.length + "]" );
		if ( stimuli.length != targets.length )
			throw new IllegalArgumentException( "# training sets does not match for stimuli ["
					+ stimuli.length + "] and targets [" + targets.length + "]" );
	}
	catch ( Exception e ) {
		throw new IllegalArgumentException( "invalid properties: " + e.getClass().toString() + ": "
				+ e.getMessage() );
	}
}

/**
 * @param aMaxFitnessValue maximum raw fitness this function will return
 */
protected void setMaxFitnessValue( int aMaxFitnessValue ) {
	int minGenes = stimuli[ 0 ].length + targets[ 0 ].length;
	maxFitnessValue = aMaxFitnessValue - (int) ( adjustForNetworkSizeFactor * minGenes );
}

/**
 * Iterates through chromosomes. For each, transcribe it to an <code>Activator</code> and
 * present the stimuli to the activator. The stimuli are presented in random order to ensure the
 * underlying network is not memorizing the sequence of inputs. Calculation of the fitness based
 * on error is delegated to the subclass. This method adjusts fitness for network size, based on
 * configuration.
 * 
 * @param genotypes <code>List</code> contains <code>Chromosome</code> objects.
 * @see TargetFitnessFunction#calculateErrorFitness(double[][], double, double)
 */


///TODO run mario here? Not sure how to have layer talk to each other when in mid process for evaluation
final public void evaluate( List genotypes ) {
	Iterator it = genotypes.iterator();
	while ( it.hasNext() ) {
		Chromosome genotype = (Chromosome) it.next();

		try {
			Activator activator = activatorFactory.newActivator( genotype );
			
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
            for(int i = 0; ; i ++)
            {
            	int keyCode = KeyEvent.VK_RIGHT; 

            	int r = rand.nextInt(6); 
            	
            	
            	/*if (r==0)
                {
            		keyCode = KeyEvent.VK_LEFT; 
                }*/
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
                System.out.println("r "  + r +  "  key " + keyCode); 
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
            	
            	if(marioComponent.scene instanceof LevelScene && (marioComponent.isLossed || marioComponent.isWon))
            	{
            		LevelScene curScene = (LevelScene)marioComponent.scene; 
            		System.out.println("Mario distance = " + curScene.mario.x); 
            		break;
            	}
            }
            frame.setVisible(false); //you can't see me!
            frame.dispose(); //Destroy the JFrame object
			//after death, or win, fitness = distance mario made
			List idxs = new ArrayList();
			for ( int i = 0; i < stimuli.length; ++i )
				idxs.add( new Integer( i ) );
			Collections.shuffle( idxs, randomizer.getRand() );

			Iterator iter = idxs.iterator();

			double[][] shuffledStimuli = new double[ stimuli.length ][ stimuli[ 0 ].length ];

			int k = 0;
			while ( iter.hasNext() ) {
				Integer idx = (Integer) iter.next();
				int i = idx.intValue();
				for ( int j = 0; j < stimuli[ 0 ].length; j++ )
					shuffledStimuli[ k ][ j ] = stimuli[ i ][ j ];
				k++;
			}

			double[][] shuffledResponses = activator.next( shuffledStimuli );
			double[][] responses = new double[ shuffledResponses.length ][ 1 ];

			for ( int i = 0; i < responses.length; ++i ) {
				Integer idx = (Integer) idxs.get( i );
				responses[ idx.intValue() ] = shuffledResponses[ i ];
			}

			genotype.setFitnessValue( calculateErrorFitness( responses, activator.getMinResponse(),
					activator.getMaxResponse() )
					- (int) ( adjustForNetworkSizeFactor * genotype.size() ) );
		}
		catch ( TranscriberException e ) {
			logger.warn( "transcriber error: " + e.getMessage() );
			genotype.setFitnessValue( 1 );
		}
	}
}

/**
 * @param responses
 * @param minResponse
 * @param maxResponse
 * @return fitness based on error.
 * @see TargetFitnessFunction#evaluate(List)
 */
protected abstract int calculateErrorFitness( double[][] responses, double minResponse,
		double maxResponse );

/**
 * @return if response is within this range of the target, error is 0
 */
protected double getTargetRange() {
	return targetRange;
}

/**
 * @return sequence of stimuli activation patterns
 */
protected double[][] getStimuli() {
	return stimuli;
}

/**
 * @return sequence of target values
 */
protected double[][] getTargets() {
	return targets;
}

/**
 * @return maximum possible fitness value for this function
 */
public int getMaxFitnessValue() {
	return maxFitnessValue;
}

}
