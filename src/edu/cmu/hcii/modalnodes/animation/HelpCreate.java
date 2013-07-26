/**
 * 
 */
package edu.cmu.hcii.modalnodes.animation;

import java.util.Calendar;
import java.util.logging.Logger;

import edu.cmu.hcii.modalnodes.KinectUtilities;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * A help animation for showing how to create nodes.  Uses the userId to draw
 * points relative to the current user's position.
 * 
 * Shows a ghost of the user's skeleton making the gesture for creating nodes.
 * 
 * @author Chris
 *
 */
public class HelpCreate implements HelpAnimation {
	private final Logger log = Logger.getLogger(HelpCreate.class.getName());
	
	private enum State {IDLE, ONE, TWO, THREE};
	
	private PApplet parent;
	private int userId;	
	private State state;
	private PVector[] joints;
	
	private long startTime;
	
	private static final long startThreshold = 10000;
	private static final long keyFrameThreshold = 5000;
	
	public HelpCreate(PApplet parent, int userId) {
		log.finest("Creating create help for user: " + userId);

		this.parent = parent;
		this.userId = userId;
		this.state = State.IDLE;
		
		this.startTime = Calendar.getInstance().getTimeInMillis();
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.hcii.modalnodes.animation.HelpAnimation#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		update();
		display();
	}
	
	private void update() {
		switch (state) {
			case IDLE :
				long curTime = Calendar.getInstance().getTimeInMillis();
				if (curTime - startTime >= startThreshold) {
					state = State.ONE;//pose 1
				}
				break;
			//Arms straight out
			case ONE:
				//update fade for position 1
				//if fade is complete
				state = State.TWO;
				break;			
			//Arms halfway up
			case TWO:
				//update fade for position 2
				//if fade is complete
				state = State.THREE;				
				break;
			//Arms above head
			case THREE:
				//update fade for position 3
				//if fade is complete
				state = State.ONE;
				break;
			default:
				break;
		}
	}
	
	private void display() {
		if (state != State.IDLE){
			int armAngle = 0;
			if (state == State.ONE) armAngle = 0;
			else if (state == State.TWO) armAngle = 45;
			else if (state == State.THREE)armAngle = 90;
			KinectUtilities.drawCreatePose(parent, userId, armAngle);
		}
	}
}
