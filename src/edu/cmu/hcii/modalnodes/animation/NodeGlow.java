package edu.cmu.hcii.modalnodes.animation;

import java.util.Calendar;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * An implementation of an glow-like animation.  Causes the node to gradually
 * grow a yellow circle from its center.  The glow growth is a function of the 
 * desired run time of the animation.
 * 
 * @author Chris
 *
 */
public class NodeGlow implements NodeAnimation {
	private final Logger log = Logger.getLogger(NodeGlow.class.getName());
	
	private PApplet parent;
	private PVector pos;	
	private float maxDiam;
	private float curDiam;
	private int color;
	private float runTime;
	private long startTime;
	
	public NodeGlow(PApplet parent, PVector pos, float maxDiam, int color, long runTime) {
		log.finest("Adding glow at " + pos);
		
		this.parent = parent;
		this.pos = pos;
		this.maxDiam = maxDiam;
		this.curDiam = 0;
		this.color = color;
		this.runTime = runTime;
		this.startTime = Calendar.getInstance().getTimeInMillis();
	}
	
	/**
	 * Run the animation.
	 */
	public void run() {
		update();
		display();
	}
	
	/**
	 * Update the animation.  Determine the glow's size based on the percentage
	 * of time which has passed since beginning the animation.
	 */
	private void update() {
		curDiam = ((Calendar.getInstance().getTimeInMillis() - startTime) / runTime) * maxDiam;
	}
	
	/**
	 * Display the animation in its current state.
	 */
	private void display() {
		parent.pushStyle();
		
		parent.fill(color);
		parent.ellipse(pos.x, pos.y, curDiam, curDiam);
		
		parent.popStyle();
	}

	/**
	 * Check if the animation is done.
	 */
	public boolean isDone() {
		return (Calendar.getInstance().getTimeInMillis() - startTime) >= runTime;
	}

}
