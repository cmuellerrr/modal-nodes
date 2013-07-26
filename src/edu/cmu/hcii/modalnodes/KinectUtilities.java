package edu.cmu.hcii.modalnodes;

import processing.core.PApplet;
import processing.core.PVector;
import SimpleOpenNI.SimpleOpenNI;

public class KinectUtilities {
	//private final Logger log = Logger.getLogger(KinectUtilities.class.getName());

	private static final int overHeadOffset = 100;
	
	/**
	 * Draws the skeleton for the given user id
	 * 
	 * @param userId the id of the user to draw
	 */
	public static void drawSkeleton(PApplet parent, int userId) {
		parent.pushStyle();
		
		parent.strokeWeight(10);
		parent.stroke(Sequencer.colors[userId]);
		parent.fill(Sequencer.colors[userId]);
		
		drawProjectionLimb(parent, userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);
		 
		drawProjectionLimb(parent, userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
		drawProjectionLimb(parent, userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
		drawProjectionLimb(parent, userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);
		 
		drawProjectionLimb(parent, userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
		drawProjectionLimb(parent, userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
		drawProjectionLimb(parent, userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);
		 
		drawProjectionLimb(parent, userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
		drawProjectionLimb(parent, userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
		 
		drawProjectionLimb(parent, userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
		drawProjectionLimb(parent, userId, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
		drawProjectionLimb(parent, userId, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);
		 
		drawProjectionLimb(parent, userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
		drawProjectionLimb(parent, userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
		drawProjectionLimb(parent, userId, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);
				
		parent.popStyle();
	}
	
	public static void drawCreatePose(PApplet parent, int userId, int angleOffset) {
		
	}
	
	public static void drawPlacePose(PApplet parent, int userId, int angleOffset) {
		
	}
	
	/**
	 * Draw the limb specified by the two given joints for a user in the projected space
	 * for the overall application.
	 * 
	 * @param parent the parent application
	 * @param userId the id of the user whose limb to draw
	 * @param joint1 the first joint of a limb
	 * @param joint2 the second joint of a limb
	 */
	private static void drawProjectionLimb(PApplet parent, int userId, int joint1, int joint2) {
		PVector j1 = getProjectionJointPosition(userId, joint1);
		PVector j2 = getProjectionJointPosition(userId, joint2);
		
		parent.line(j1.x, j1.y, j2.x, j2.y);
	}
	
	/**
	 * Return a user's given joint position in terms of its projected position 
	 * within the overall application.
	 * 
	 * TODO this isn't a completely accurate projection position.
	 * 		Need to actually calibrate based on the projected pane.
	 * 
	 * @param userId the id of the user to check
	 * @param joint the joint to project
	 * @return
	 */
	public static PVector getProjectionJointPosition(int userId, int joint) {
		PVector jointPos = new PVector();
	    Sequencer.kinect.getJointPositionSkeleton(userId, joint, jointPos);
	    PVector projectionPos = new PVector();
	    Sequencer.kinect.convertRealWorldToProjective(jointPos,  projectionPos);
	    
	    projectionPos.x = (projectionPos.x / Sequencer.kinect.depthWidth()) * Sequencer.windowWidth;
	    projectionPos.y = (projectionPos.y / Sequencer.kinect.depthHeight()) * Sequencer.windowHeight;
	    
	    return projectionPos;
	}
	
	/**
	 * Checks to see if the given user's left arm is making a right angle with 
	 * the hand, elbow, and shoulder as the three vertices.
	 * 
	 * This does not take orientation into account.  Meaning, the hand can 
	 * be either above or below the shoulder.
	 * 
	 * @param userId the id of the user to check
	 * @return if the left arm forms a right angle
	 */
	public static boolean armRightAngle(int userId) {
		PVector lHand = new PVector();
		PVector lElbow = new PVector();
		PVector lShoulder = new PVector();
		
		Sequencer.kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND, lHand);
		Sequencer.kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, lElbow);
		Sequencer.kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, lShoulder);

		return isHorizontal(lElbow, lShoulder, 25) && isVertical(lHand, lElbow, 80);
	}
	
	/**
	 * Checks to see if the given user is making a "time out" T shape with their
	 * arms.
	 * 
	 * @param userId the id of the user to check
	 * @return if arms are making a T shape
	 */
	public static boolean timeOut(int userId) {
		PVector rHand = new PVector();
		PVector rElbow = new PVector();
		PVector lHand = new PVector();
		PVector lElbow = new PVector();
		
		Sequencer.kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HAND, rHand);
		Sequencer.kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, rElbow);
		Sequencer.kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND, lHand);
		Sequencer.kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, lElbow);
		
		//TODO this needs to be further refined, but it works.
		if (isHorizontal(lHand, lElbow, 50)) {
			return isVertical(rHand, rElbow, 100) && xInOrder(lElbow, rHand, lHand) && isHorizontal(lHand, rHand, 200);
		} else if (isHorizontal(rHand, rElbow, 50)) {
			return isVertical(lHand, lElbow, 100) && xInOrder(rHand, lHand, rElbow) && isHorizontal(lHand, rHand, 200);
		}
		
		return false;
	}
	
	/**
	 * Checks if the given user has their hands together above their head.
	 * 
	 * TODO: This doesn't take into account the overHeadOffset
	 * 
	 * @param userId the id of the user to check
	 * @return if hands are together above the head
	 */
	public static boolean handsTogetherOverHead(int userId) {
		PVector rHand = new PVector();
		PVector lHand = new PVector();
		PVector head = new PVector();
		
		Sequencer.kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HAND, rHand);
		Sequencer.kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND, lHand);
		Sequencer.kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_HEAD, head);
				
		//The real world y's are opposite of screen position
		return rHand.y >= head.y && lHand.y >= head.y && Math.abs(rHand.x - lHand.x) <= 100;
	}
	
	/**
	 * Determines the users percentage within the create node gesture.
	 * Determines the line connecting through he head and the given hand joint
	 * then interpolates the position on the line which is at shoulder 
	 * level.  The percentage the given hand joint resides on the line
	 * is returned.
	 * 
	 * @param parent
	 * @param userId the id of the user to check
	 * @param joint the hand joint to check against
	 * @return the percentage the given hand is within the create gesture
	 */
	public static float createNodePercentage(int userId, int joint) {
		int baseJoint = joint == SimpleOpenNI.SKEL_RIGHT_HAND ? SimpleOpenNI.SKEL_RIGHT_SHOULDER : SimpleOpenNI.SKEL_LEFT_SHOULDER;
		
		/*
		PVector goal = new PVector();
		PVector current = new PVector();
		PVector base = new PVector();
		
		Sequencer.kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_HEAD, goal);
		Sequencer.kinect.getJointPositionSkeleton(userId, joint, current);
		Sequencer.kinect.getJointPositionSkeleton(userId, baseJoint, base);
		*/
		
		PVector goal = getProjectionJointPosition(userId, SimpleOpenNI.SKEL_HEAD);
		PVector current = getProjectionJointPosition(userId, joint);
		PVector base = getProjectionJointPosition(userId, baseJoint);
		
		//Offset the goal to somewhere above the head
		goal.y -= overHeadOffset;	
		
		//Determine the line moving through the given joint and and the goal
		float slope = (current.y - goal.y) / (current.x - goal.x);
		float intercept = goal.y - (slope * goal.x);
		
		//Set the projected x value for the baseline
		base.x = (base.y - intercept) / slope;
	
		return current.y > base.y ? 0 : vectorDistance2D(current, base) / vectorDistance2D(goal, base);
	}
	
	/**
	 * Return the distance between a user's hands
	 * 
	 * @param userId the id of the user to check
	 * @return the distance between hands
	 */
	public static float handDistance(int userId) {
		PVector rHand = new PVector();
		PVector lHand = new PVector();
		
		Sequencer.kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HAND, rHand);
		Sequencer.kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND, lHand);
		
		return vectorDistance2D(rHand, lHand);
	}
	
	/**
	 * Return the distance between the two vectors, in 2D space
	 * @param p1
	 * @param p2
	 * @return
	 */
	private static float vectorDistance2D(PVector p1, PVector p2) {
		return (float)Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
	}
	
	/**
	 * Returns if the two given vectors form a horizontal line within the 
	 * given error.
	 * 
	 * @param p1 the first point to check
	 * @param p2 the second point to check
	 * @param error the amount of error (distance) from a perfect horizontal
	 * @return if the two vectors form a horizontal line
	 */
	private static boolean isHorizontal(PVector p1, PVector p2, int error) {
		return Math.abs(p1.y - p2.y) <= error;
	}
	
	/**
	 * Returns if the two given vectors form a vertical line within the 
	 * given error.
	 * 
	 * @param p1 the first point to check
	 * @param p2 the second point to check
	 * @param error the amount of error (distance) from a perfect vertical
	 * @return if the two vectors form a vertical line
	 */
	private static boolean isVertical(PVector p1, PVector p2, int error) {
		return Math.abs(p1.x - p2.x) <= error ;
	}
	
	/**
	 * Checks if the given vectors occur in order based on their x values.
	 * 
	 * @param left the expected left-most vector
	 * @param center the expected center vector
	 * @param right the expected right-most vector
	 * @return if the three vectors are indeed in order
	 */
	private static boolean xInOrder(PVector left, PVector center, PVector right) {
		return left.x <= center.x && center.x <= right.x;
	}
	
	/**
	 * Checks if the given vectors occur in order based on their y values.
	 * These values are assumed to be screen position, so 0 == top.
	 * 
	 * @param top the expected top-most vector
	 * @param center the expected center vector
	 * @param bottom the expected bottom-most vector
	 * @return if the three vectors are indeed in order
	 */
	private static boolean yInOrder(PVector top, PVector center, PVector bottom) {
		return top.y <= center.y && center.y <= bottom.y;
	}
}
