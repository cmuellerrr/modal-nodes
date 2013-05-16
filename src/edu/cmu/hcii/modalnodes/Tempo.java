package edu.cmu.hcii.modalnodes;

import java.util.logging.Logger;

import processing.core.PApplet;

/**
 * An implementation of a global, application tempo.  Basically just a bar that
 * moves from left to right based on a given rate.
 * 
 * @author Chris
 *
 */
public class Tempo {
	private final static Logger log = Logger.getLogger(Tempo.class.getName());
	
	private PApplet parent;
	private float bpm;
	private float xPos;
	
	private static final float minBpm = 5;
	private static final float maxBpm = 20;
	
	/**
	 * Create a new tempo object.  
	 * 
	 * TODO Make bpm an actual bpm instead of just a pixel-step value
	 * 
	 * @param parent The parent application
	 * @param bpm The tempo's desired bpm (beats per minute)
	 */
	public Tempo(PApplet parent, float bpm) {
		log.fine("Creating new tempo with " + bpm + " BPM");
		
		this.parent = parent;
		this.bpm = bpm;
		this.xPos = 0;
	}
	
	/**
	 * "Run" the tempo.  Basically tell it to handle itself.
	 */
	public void run() {
		if(!Sequencer.isPaused()) update();
		display();
	}
	
	/**
	 * Update the tempo according to its bpm.
	 */
	public void update() {
		xPos += bpm; 
	    if (xPos > parent.width + 200) xPos = 0;
	}
	
	/**
	 * Display the tempos current state.
	 */
	public void display() {
	    parent.pushStyle();	    		
		parent.stroke(255, 150);
		//parent.strokeWeight(4);    
	    //parent.noStroke();
	    //parent.fill(255, 150);
	    parent.line(xPos, 0, xPos, parent.height);  
	    //parent.rect(xPos, 0, 10, parent.height);
	    parent.popStyle();
	}

	/**
	 * @return the bpm
	 */
	public float getBpm() {
		return bpm;
	}

	/**
	 * @param bpm the bpm to set
	 */
	public void setBpm(float bpm) {
		if (bpm >= minBpm && bpm <= maxBpm) {
			this.bpm = bpm;
		}
	}

	/**
	 * @return the xPos
	 */
	public float getXPos() {
		return xPos;
	}	
}
