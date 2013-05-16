package edu.cmu.hcii.modalnodes.animation;

import java.util.LinkedList;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * An implementation of an echo-like animation.  Causes the node to emit
 * a gradually larger and more transparent circle from its location. 
 * 
 * @author Chris
 *
 */
public class NodeEcho implements NodeAnimation {
	private final Logger log = Logger.getLogger(NodeEcho.class.getName());
	
	private PApplet parent;
	private PVector pos;
	private float initDiam;
	private float maxDiam;
	private LinkedList<Float> rings;
	private boolean growing;
	private final float alpha = 30;
	
	/**
	 * Create a new echo animation.
	 * 
	 * @param parent the parent applet
	 * @param pos position of the parent node
	 * @param diam the diameter of the parent node
	 * @param maxDiam the maximum diameter for the echo
	 */
	public NodeEcho(PApplet parent, PVector pos, float initDiam, float maxDiam) {
		log.finest("Adding echo at " + pos);
		
		this.parent = parent;
		this.pos = pos;
		this.initDiam = initDiam;
		this.maxDiam = maxDiam;
		this.rings = new LinkedList<Float>();
		this.growing = true;
	}
	
	/**
	 * Run the animation.
	 */
	public void run() {
		update();
		display();
	}
	
	/**
	 * Update the animation.  If still growing, add a new ring and increase the 
	 * diameter of all others.  If the maximum is reached by one ring, stop
	 * growing and finish off the rest of the rings.
	 */
	private void update() {
		if(growing) rings.addFirst(initDiam);
		
		for(int i = rings.size()-1; i >= 0; i--) {
			float curDiam = rings.get(i);
			
			if (curDiam < maxDiam) {
				rings.set(i, curDiam + 2f);
			} else {
				growing = false;
				rings.remove(i);
			}
		}
	}
	
	/**
	 * Display the animation in its current state.
	 */
	private void display() {
		parent.pushStyle();
		
		parent.noFill();
		for(int i = rings.size()-1; i >= 0; i--) {
			parent.stroke(255, alpha-i);
			parent.ellipse(pos.x, pos.y, rings.get(i), rings.get(i));
		}
		
		parent.popStyle();
	}
	
	/**
	 * Check if the animation is done.
	 */
	public boolean isDone() {
		return rings.isEmpty();
	}
}
