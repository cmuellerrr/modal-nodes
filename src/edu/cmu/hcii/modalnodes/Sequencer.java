package edu.cmu.hcii.modalnodes;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import SimpleOpenNI.SimpleOpenNI;
import edu.cmu.hcii.modalnodes.structure.Node;
import edu.cmu.hcii.modalnodes.structure.NodeGrid;
import edu.cmu.hcii.modalnodes.structure.ParticleSwarm;

/**
 * The overall sequencer implementation; essentially the playing field for 
 * users.  Holds the nodes and tempo and relays input accordingly.
 * 
 * @author Chris
 *
 */
@SuppressWarnings("serial")
public class Sequencer extends PApplet {
	private static final Logger log = Logger.getLogger(Sequencer.class.getName());
	
	public static final int windowWidth = 1600;
	public static final int windowHeight = 900;
	
	private static boolean paused;
	private static boolean debug;
	private static long pauseToggleTime;
	
	public static final long pauseThreshold = 1000;
	
	private static Tempo tempo;
	private static NodeGrid grid;
	
	public static float nodeRadius;
	
	private Map<Integer, Node> focused;
	
	private Avatar avatar;
	public static float leftHand = (float) 0.95;
	
	//PGraphics and PImage are used to render glow in the off screen buffer	
	private PGraphics graphicsLayer;
	private PImage imageLayer;
	
	private final int swarmSize = 70;
	private final float particleDiameter = 4f;
	private final float particleSpeed = 1;
	private static ParticleSwarm[] ambientParticles;
	private static ParticleSwarm[][] particles;
	
	//First two digits = alpha
	public static final int[] colors = new int[] {0x33000000, 0x33FF0000, 0x3300FF00, 
		0x330000FF, 0x33FFFF00, 0x3300FFFF, 0x33FF00FF, 0x33C0C0C0, 0x33FFFFFF, 0x33FF6600, 0x339900CC};
	
	public static SimpleOpenNI kinect;
	
	public static final int numUsers = 10;
	
	/**
	 * Make sure processing uses the right entry class.  We can add an 
	 * an additional string of "--present" at position 0 in the array
	 * to get a full screen mode.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
	    PApplet.main(new String[] { "edu.cmu.hcii.modalnodes.Sequencer" });
	}
	
	/**
	 * Setup the application using processing standards.  Set the application
	 * to 30 frames per second and make sure to use the OpenGL contexts (P2D 
	 * or P3D) to utilize the GPU.
	 */
	public void setup() {
		log.info("Setting up sequencer.");
		
		//See above, we may not need to specify p2d, but it is faster
		//JAVA2D renders much better than P2D
		size(windowWidth, windowHeight, JAVA2D);
		//curser glow
		graphicsLayer = createGraphics(width, height, JAVA2D);
		
		//TODO do something about this.  Setting the renderer calls setup again.
		kinect = new SimpleOpenNI(this, SimpleOpenNI.RUN_MODE_MULTI_THREADED);
		
		if (!kinect.enableDepth()) {
			log.severe("Cannot create depth map.");
			
			//Crate new person, good for testing effects
			avatar = new Avatar(this, windowWidth/2, windowHeight/2);
			Controls controls = new Controls(this); //create new console
		} else {
			kinect.setMirror(true);
			kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
			kinect.enableGesture();
		}
		
		//set graphics layer for off screen buffer
		graphicsLayer = createGraphics(width, height, JAVA2D);

		//Setup grid and node properties
		grid = new NodeGrid(this);
		nodeRadius = grid.getCellSize() / 2;
		
		//Roughly .5 bps (I think)
		tempo = new Tempo(this, windowWidth/200);
		
		pauseToggleTime = 0;
		paused = false;
		debug = false;
		
		focused = new HashMap<Integer, Node>();
		
		//Set up a mouse wheel listener for the scroll wheel
		addMouseWheelListener(new MouseWheelListener() { 
		    public void mouseWheelMoved(MouseWheelEvent mwe) { 
		      mouseWheel(mwe.getWheelRotation());
		}});
		
		//Changing the alpha gives a feeling of depth
		//They should move slower the further away they are
		ambientParticles = new ParticleSwarm[] {
				new ParticleSwarm(this, swarmSize/3, 200, particleDiameter, particleSpeed),
				new ParticleSwarm(this, swarmSize/2, 80, particleDiameter, particleSpeed / 2),
				new ParticleSwarm(this, (int)(swarmSize/1.5), 25, particleDiameter, particleSpeed / 3)
		};
		
		particles = new ParticleSwarm[numUsers + 1][2];
		
		for (int i = 0; i <= numUsers; i++) {
			particles[i][0] = new ParticleSwarm(this, swarmSize, 150, 5, particleSpeed);
			particles[i][1] = new ParticleSwarm(this, swarmSize, 150, 5, particleSpeed);
		}
	}
	
	/**
	 * The application's overall draw loop.
	 */
	public void draw() {
		pushStyle();
		smooth();
		//background(4, 8, 33);
		fill(4, 8, 33, 150);
		rect(0, 0, width, height);
						
		for (int i = 0; i < ambientParticles.length; i++) {
			ambientParticles[i].run();
		}
		
		fill(0, 30);
		noStroke();

		kinect.update();
		//image(kinect.depthImage(), 0, 0);
		
		for (int i = 0; i <= numUsers; i++) {
			
			if (i == 0) {
				if (focused.get(i) == null) {
					focused.put(i, grid.checkUserFocus(i, mouseX, mouseY));
				}
			} else if (kinect.isTrackingSkeleton(i)) {
				pushStyle();
				
				stroke(colors[i]);
				fill(colors[i]);
				
				PVector rHand = KinectUtilities.getProjectionJointPosition(i, SimpleOpenNI.SKEL_RIGHT_HAND);
				PVector lHand = KinectUtilities.getProjectionJointPosition(i, SimpleOpenNI.SKEL_LEFT_HAND);
				
				ellipse(rHand.x, rHand.y, 30, 30);
				KinectUtilities.drawSkeleton(this, i);
				
				//If they don't currently have a node attached
				if (focused.get(i) == null) {
					//Only allow T.O. if it was toggled more than 1 sec ago.
					if (KinectUtilities.timeOut(i)) togglePause();
					
					//Check to see if there should be one attached
					focused.put(i, grid.checkUserFocus(i, (int)rHand.x, (int)rHand.y));
					
					//Or create a node if the hands are together
					if (KinectUtilities.handsTogetherOverHead(i)) createNode(i, rHand.x, rHand.y);
					
					float rSwarmSize = 50 * KinectUtilities.createNodePercentage(i, SimpleOpenNI.SKEL_RIGHT_HAND);
					float lSwarmSize = 50 * KinectUtilities.createNodePercentage(i, SimpleOpenNI.SKEL_LEFT_HAND);
					ellipse(rHand.x, rHand.y, rSwarmSize, rSwarmSize);
					ellipse(lHand.x, lHand.y, lSwarmSize, lSwarmSize);
					
					/*
					float rPercent = KinectUtilities.createNodePercentage(i, SimpleOpenNI.SKEL_RIGHT_HAND);
					float lPercent = KinectUtilities.createNodePercentage(i, SimpleOpenNI.SKEL_LEFT_HAND);
										
					if (lPercent > 0) {
						particles[i][0].swarm(lHand, lPercent);
					} else {
						if (particles[i][0].isSwarming()) {
							particles[i][0].disperse();
						}
					}
					
					if (rPercent > 0) {
						particles[i][1].swarm(rHand, rPercent);
					} else {
						if (particles[i][1].isSwarming()) {
							particles[i][1].disperse();
						}
					}
					
					particles[i][0].run();
					particles[i][1].run();
					*/
				//If they are controlling a node
				} else {
					//The size multiplier is just Node.maxRad * 3
					focused.get(i).setRad((525 / rHand.z) * 150);
					//focused.get(i).setRad(KinectUtilities.handDistance(i)/2);
					
					//focused.get(i).setTone(Node.tones[grid.getRow(rHand.y)]);
					focused.get(i).setPos(new PVector(rHand.x, rHand.y));
					
					//Allow the user to put down a node
					if (KinectUtilities.armRightAngle(i)) placeNode(i);
				}
				
				popStyle();
			} else {
				grid.clearUserHover(i);
			}
		}

		grid.run();
		
		for (Node n : focused.values()) {
			if (n != null) n.run();
		}
		
		tempo.run();		
		popStyle();
		
		//disable the avatar if kinect connected
		if (avatar != null) {
			avatar.display();

			fill(0, 0);
			noStroke(); 
			rect(0, 0, width, height);
			
			if (avatar.yR < 380) {
				particles[0][0].swarm(new PVector(avatar.xL, avatar.yL, 0), 1);
				particles[0][1].swarm(new PVector(avatar.xR, avatar.yR, 0), 1);
			} else {
				if (particles[0][0].isSwarming()) {
					particles[0][0].disperse();
				}
				if (particles[0][1].isSwarming()) {
					particles[0][1].disperse();
				}
			}
			
			//particles[0][0].run();
			//particles[0][1].run();
		}
	}
	
	/**
	 * Create a new node at the given coordinates and attach it to 
	 * the given user.
	 * 
	 * @param userId the user creating the node
	 * @param x the node's x position
	 * @param y the node's y position
	 */
	private void createNode(int userId, float x, float y) {
		Node newNode = new Node(this, new PVector(x, y, 0), nodeRadius);
		newNode.startFocus(userId);
		focused.put(userId, newNode);
	}

	/**
	 * Place the node currently attached to the given user.
	 * If no such node exists, do nothing.
	 * 
	 * @param userId the user placing the node
	 */
	private void placeNode(int userId) {
		Node focusedNode = focused.get(userId);
		if (focusedNode != null && focusedNode.isPlaceable()) {
			grid.addNode(focusedNode);
			focusedNode.endFocus();
			focused.put(userId, null);
		}
	}
	
	/**
	 * Remove the node currently attached to the given user.
	 * If not such node exists, do nothing.
	 * 
	 * @param userId the user removing the node
	 */
	private void removeNode(int userId) {
		Node focusedNode = focused.get(userId);
		if (focusedNode != null) {
			focusedNode.endFocus();
			focusedNode.stop();
			focused.put(userId, null);
		}
	}

	/**
	 * When a new user is detected, look for a start pose.
	 * 
	 * @param userId the id assigned to the new user
	 */
	public void onNewUser(int userId) {
	    log.info("New User Detected - " + userId);
	    
	    kinect.requestCalibrationSkeleton(userId, true);
	}
	 
	/**
	 * Notify when a user has left the scene.
	 * 
	 * @param userId the id of the user who has left.
	 */
	public void onLostUser(int userId) 	{
		log.info("Lost user " + userId);
		placeNode(userId);
	}
	 
	/**
	 * When a user has begun a pose.
	 * 
	 * @param pose the pose detected
	 * @param userId the id of the user doing the pose
	 */
	public void onStartPose(String pose, int userId) {
		log.info("User " + userId + " starting pose " + pose);
		
		if (pose.equals("Psi")) {
			kinect.stopPoseDetection(userId);
	    	kinect.requestCalibrationSkeleton(userId, true); 
	    
	    //TODO: These don't work well.  Get rid of them after user test.
		} else if (pose.equals("Click")) {
			println(KinectUtilities.getProjectionJointPosition(userId, SimpleOpenNI.SKEL_RIGHT_HAND));
			placeNode(userId);
		} else if (pose.equals("Wave")) {
			removeNode(userId);
		}
	}
	
	public void onRecognizeGesture(String strGesture, PVector idPosition, PVector endPosition)
	{
	  //println("onRecognizeGesture - strGesture: " + strGesture + ", idPosition: " + idPosition + ", endPosition:" + endPosition);
	}
	 
	/**
	 * Notify that the calibration has started.
	 * 
	 * @param userId the id of the user being calibrated
	 */
	public void onStartCalibration(int userId) {
		log.info("Calibrating user " + userId);
	}
	 
	/**
	 * Report the calibration results.
	 * 
	 * @param userId the id of the user being calibrated
	 * @param successfull the result of the calibration
	 */
	public void onEndCalibration(int userId, boolean successfull) {
		if (successfull) { 
	    	log.info("Successfully calibrated user " + userId);
	    	
	        kinect.startTrackingSkeleton(userId);
	        kinect.startPoseDetection("Wave",userId);
	        kinect.startPoseDetection("Click",userId);
	    } else { 
	        log.info("Failed to calibrate user " + userId);
	        
	        kinect.startPoseDetection("Psi",userId);
	    }
	}
	
	/**
	 * When the mouse is clicked, create a focused node or, 
	 * if one already exists, add the focused to the node grid.
	 */
	public void mousePressed() {		
		if (focused.get(0) != null) {
			placeNode(0);
		} else {
			createNode(0, mouseX, mouseY);
		}
	}

	/**
	 * Have the focused node locked onto the cursor until finalized.
	 */
	public void mouseMoved() {
		Node mouseNode = focused.get(0);
		
		if (mouseNode != null) mouseNode.setPos(new PVector(mouseX, mouseY));
	}
	
	/**
	 * Change the size of the focused node when the mouse wheel is moved.
	 * 
	 * @param delta the movement of the scroll wheel
	 */
	void mouseWheel(int delta) {
		Node mouseNode = focused.get(0); 
		
		if (mouseNode != null) mouseNode.setRad(mouseNode.getRad() + delta);
	}
	
	/**
	 * When p is pressed, pause the sequencer.  Use the arrow keys
	 * to modify the global tempo.
	 */
	public void keyPressed() {
		switch (key) {
			case 'p':
				paused = !paused;
				break;
			case 'd':
				debug = !debug;
				break;
			case CODED:
				switch (keyCode){
					case UP:
						log.info("Increase BPM");
						tempo.setBpm(tempo.getBpm() + 1);
						break;
					case DOWN:
						log.info("Descrease BPM");
						tempo.setBpm(tempo.getBpm() - 1);
						break;
					default:
						break;
				}
				break;
			default: 
				break;
		}
	}
	
	/**
	 * Toggle whether or not the application is paused.  Take no 
	 * action if the predefined threshold has passes.
	 */
	public static void togglePause() {
		if (Calendar.getInstance().getTimeInMillis() - pauseToggleTime > pauseThreshold) {
			log.info("Paused: " + !paused);
			pauseToggleTime = Calendar.getInstance().getTimeInMillis();
			paused = !paused;
		}
	}

	/**
	 * @return the tempo
	 */
	public static Tempo getTempo() {
		return tempo;
	}

	/**
	 * @return the paused
	 */
	public static boolean isPaused() {
		return paused;
	}
	
	/**
	 * @return the debug
	 */
	public static boolean isDebug() {
		return debug;
	}
}
