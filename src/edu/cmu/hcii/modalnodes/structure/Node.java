package edu.cmu.hcii.modalnodes.structure;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PVector;
import ddf.minim.AudioSample;
import ddf.minim.Minim;
import edu.cmu.hcii.modalnodes.Sequencer;
import edu.cmu.hcii.modalnodes.Tempo;
import edu.cmu.hcii.modalnodes.animation.NodeAnimation;
import edu.cmu.hcii.modalnodes.animation.NodeEcho;
import edu.cmu.hcii.modalnodes.animation.NodeGlow;
import edu.cmu.hcii.modalnodes.animation.NodeSpin;

/**
 * A node class to represent the objects within the sequencer which
 * make the sounds.  These nodes are the objects the user interacts
 * with.
 * 
 * @author Chris
 *
 */
public class Node {
	private final Logger log = Logger.getLogger(Node.class.getName());
	
	public static final String toneDir = "resources/sounds/";
	public static final String[] tones = new String[] {toneDir + "modal_nodes_01.aiff", toneDir + "modal_nodes_02.aiff", 
		toneDir + "modal_nodes_03.aiff", toneDir + "modal_nodes_04.aiff", toneDir + "modal_nodes_05.aiff", 
		toneDir + "modal_nodes_06.aiff", toneDir + "modal_nodes_07.aiff", toneDir + "modal_nodes_08.aiff", 
		toneDir + "modal_nodes_09.aiff", toneDir + "modal_nodes_10.aiff"};
	
	public static final long hoverThreshold = 1000;
	public static final long placeableThreshold = 1000;	
	
	//private PGraphics graphicsLayer;
	//private PImage imageLayer;
	private PApplet parent;
	private PVector pos;
	private float rad;
	private float diam;
	private boolean focused;
	private int userId;
	private NodeSpin nodeSpin;
	
	private static final float minRad = 20;
	private static final float maxRad = 50;
	
	//Not a big fan of explicitly typing the linked list but we
	//aren't going to cast it every time we add animations (see below)
	private LinkedList<NodeAnimation> animations;
	private boolean firedLastUpdate;
	
	private long createTime;
	private long placeTime;
	private long hoverStart;
	private long focusTime;
	
	Minim minim;
	AudioSample tone;
	String fileName;
	
	/**
	 * Create a new node object.
	 * 
	 * @param parent The parent application
	 * @param pos The node's position
	 * @param diam The node's diameter
	 */
	public Node(PApplet parent, PVector pos, float rad) {
		log.finer("Creating new node at " + pos);
		
		this.parent = parent;
		this.pos = pos;
		this.rad = rad;
		this.diam = rad*2;
		this.focused = false;
		this.userId = -1;
		
		this.animations = new LinkedList<NodeAnimation>();
		this.firedLastUpdate = false;
		
		this.createTime = Calendar.getInstance().getTimeInMillis();
		this.placeTime = 0;
		this.hoverStart = 0;
		this.focusTime = 0;
		
		this.minim = new Minim(parent);
		this.fileName = tones[0];
		this.tone = minim.loadSample(tones[0]);
		
		this.nodeSpin = new NodeSpin(parent);
	}
	
	public Node(Node n) {
		log.info("Creating new node at " + pos);
		
		this.parent = n.getParent();
		this.pos = n.getPos();
		this.rad = n.getRad();
		this.diam = this.rad * 2;
		this.focused = false;
		this.userId = -1;
		
		this.animations = new LinkedList<NodeAnimation>();
		this.firedLastUpdate = false;
		
		this.createTime = Calendar.getInstance().getTimeInMillis();
		this.placeTime = 0;
		this.hoverStart = 0;
		this.focusTime = 0;
		
		this.minim = new Minim(parent);
		this.tone = minim.loadSample(tones[0]);
		this.nodeSpin = new NodeSpin(parent);
	}
	
	/**
	 * "Run" the node.  Basically, tell it to handle itself for 
	 * the current cycle.
	 * 
	 * @param t The application's tempo object
	 */
	public void run() {
		if (!Sequencer.isPaused()) update(Sequencer.getTempo());
		display();
	}
	
	/**
	 * Update the node according to the state of the application's
	 * tempo.  If the node is being struck by the tempo for the 
	 * first time this beat, then the node needs to fire.
	 * 
	 * @param t The tempo to check against
	 */
	public void update(Tempo t) {
		if(Math.abs(t.getXPos() - pos.x) < rad) {
			if(!firedLastUpdate) fire();
			firedLastUpdate = true;
		} else {
			firedLastUpdate = false;
		}
	}
	
	/**
	 * Display the node in its current state and run any 
	 * active animations.
	 */
	public void display() {		
		parent.pushStyle();
		
		//parent.noStroke();
		//parent.fill(255);
		parent.noFill();
		parent.ellipse(pos.x, pos.y, diam, diam);
		nodeSpin.drawNode(pos.x, pos.y, diam);
		
		//This should match what happens in the hover animation
		if (focused && userId >= 0) {
			parent.fill(Sequencer.colors[userId]);
			parent.ellipse(pos.x, pos.y, diam, diam);
		}
		
		runAnimations();
		
		parent.popStyle();
	}
	
	/**
	 * Fire the node.  So, do whatever it needs to do when it should
	 * play.  For now, play the sound and add an animation.
	 */
	private void fire() {
		log.finer("Firing node");
		
		play();
		addAnimation(new NodeEcho(parent, pos, diam, diam + 15));
	}
	
	/**
	 * Play the node's audio.
	 */
	private void play() {
		log.finest("Playing sound");
		tone.trigger();
	}
	
	/**
	 * Loop through and run the node's current animations.  If an animation
	 * has finished, remove it from the list.  The animations are iterated 
	 * from back to front because we are modifying the list as we iterate.
	 */
	private void runAnimations() {
		for (int i = animations.size()-1; i >= 0; i--) {
			NodeAnimation anim = animations.get(i);
			anim.run();
			nodeSpin.spinNode();

			if (anim.isDone()) {
				log.finest("Animation done"); 
				
				animations.remove(i);
			}
		}
	}
	
	/**
	 * Add an animation to the node.  Add the animation to the front of the 
	 * list to ensure draw order because we run them from back to front.
	 * Oldest animations in the back.
	 * 
	 */
	private void addAnimation(NodeAnimation animation) {
		log.finest("Adding animation");
		
		animations.addFirst(animation);
	}

	/**
	 * Remove any animations from the given class name which may be active.
	 * Only checks against the simple class name, so "NodeEcho" instead of the 
	 * entire package name.
	 * 
	 * @param className the name of the animation class to remove
	 */
	private void removeAnimations(String className) {
		for (int i = animations.size()-1; i >= 0; i--) {
			if (animations.get(i).getClass().getSimpleName().equals(className)) {
				animations.remove(i);
			}
		}
	}

	/**
	 * Make sure this is called for all nodes when the application either exits
	 * or a node is removed
	 */
	public void stop() {
		log.finer("Stopping node audio channel");
	    // always close Minim audio classes when you are done with them
	    tone.close();
	    minim.stop();
	}

	/**
	 * Mark the time in which the node began to be hovered over.
	 */
	public void startHover(int userId) {
		//TODO this is reusing the placeable threshold
		if (Calendar.getInstance().getTimeInMillis() - placeTime >= placeableThreshold) {
			this.userId = userId;
			hoverStart = Calendar.getInstance().getTimeInMillis();
			addAnimation(new NodeGlow(parent, pos, diam, Sequencer.colors[userId], hoverThreshold));
			log.fine("Starting hover for user: " + userId);
			log.finest("Starting hover at " + hoverStart);
		}
	}

	/**
	 * Reset the time in which the node began to be hovered over.  Remove
	 * any hover animations which may be active.
	 */
	public void endHover() {
		if (hoverStart != 0) {
			log.finest("Ending hover");
			hoverStart = 0;
			userId = -1;
			removeAnimations("NodeGlow");
		}
	}

	/**
	 * Return the amount of time, in milliseconds, that the node has been hovered
	 * over.
	 * 
	 * @return the milliseconds with which the node has been hovered over
	 */
	public long hoverTime() {
		return hoverStart == 0 ? 0 : Calendar.getInstance().getTimeInMillis() - hoverStart;
	}

	/**
	 * Returns whether or not the given coordinates lie within the node.
 	 * Currently checks based on a rectangle but we can change later if
	 * more accuracy is needed.
	 * 
	 * @param x the x position to test
	 * @param y the y position to test
	 * @return if the given x,y lies within the node
	 */
	public boolean contains(int x, int y) {
		return (Math.abs(x - pos.x) <= rad) && (Math.abs(y - pos.y) <= rad); 
	}
	
	/**
	 * Mark the node as being focused.
	 * 
	 * @param userId the user attached to the node
	 */
	public void startFocus(int userId) {
		log.fine("Setting focus to user: " + userId);
		this.placeTime = 0;
		this.focusTime = Calendar.getInstance().getTimeInMillis();
		this.focused = true;
		this.userId = userId;
	}
	
	/**
	 * Mark the node as no longer being focused.
	 * 
	 */
	public void endFocus() {
		log.fine("Ending focus");
		this.placeTime = Calendar.getInstance().getTimeInMillis();
		this.focusTime = 0;
		this.focused = false;
		this.userId = -1;
	}
	
	/**
	 * Returns if the node is placeable.  Meaning, if it hasn't been
	 * given focus or created within the allowable time.
	 * 
	 * @return if the node is placeable
	 */
	public boolean isPlaceable() {
		long now = Calendar.getInstance().getTimeInMillis();		
		return now - createTime >= placeableThreshold && 
				now - focusTime >= placeableThreshold;
	}

	/**
	 * Get the node's current position.
	 * 
	 * @return the pos
	 */
	public PVector getPos() {
		return pos;
	}

	/**
	 * Set the node's position.
	 * 
	 * @param pos the pos to set
	 */
	public void setPos(PVector pos) {
		log.finest("Updating position to " + pos);
		this.pos = pos;
	}

	/**
	 * Get the node's radius.
	 * 
	 * @return the rad
	 */
	public float getRad() {
		return rad;
	}

	/**
	 * Set the nodes radius (and therefore diameter).  Keeps the radius within 
	 * the allowable bounds.
	 * 
	 * @param rad the rad to set
	 */
	public void setRad(float rad) {
		if (rad < minRad) rad = minRad;
		if (rad > maxRad) rad = maxRad;
		if (rad >= minRad && rad <= maxRad) {
			log.finest("Updating radius to " + rad);
			this.rad = rad;
			this.diam = rad*2;
		}
	}
	
	/**
	 * Get the node's parent.
	 * @return the parent
	 */
	public PApplet getParent() {
		return this.parent;
	}
	
	public void setTone(String fileName) {
		log.info("Setting tone to " + fileName);
		
		if (!this.fileName.equals(fileName)) {
			this.fileName = fileName;
			this.tone = minim.loadSample(fileName);
		}
	}
}
