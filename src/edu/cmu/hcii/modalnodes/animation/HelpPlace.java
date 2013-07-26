/**
 * 
 */
package edu.cmu.hcii.modalnodes.animation;

import java.util.logging.Logger;

import processing.core.PApplet;

/**
 * A help animation for showing how to place nodes.  Uses the userId to draw
 * points relative to the current user's position.
 * 
 * Shows a ghost of the user's skeleton making the pose for placing nodes.
 * 
 * @author Chris
 *
 */
public class HelpPlace implements HelpAnimation {
	private final Logger log = Logger.getLogger(HelpPlace.class.getName());
	
	private PApplet parent;
	private int userId;
	
	private static final long startThreshold = 10000;
	private static final long keyFrameThreshold = 5000;
	
	public HelpPlace(PApplet parent, int userId) {
		log.finest("Creating place help for user: " + userId);
		
		this.parent = parent;
		this.userId = userId;
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.hcii.modalnodes.animation.HelpAnimation#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
