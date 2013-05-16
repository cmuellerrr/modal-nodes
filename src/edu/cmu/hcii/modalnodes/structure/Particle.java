package edu.cmu.hcii.modalnodes.structure;

import java.util.Calendar;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class Particle {
	private final Logger log = Logger.getLogger(Node.class.getName());
	PImage imageLayer;
	PGraphics graphicsLayer;
	private PApplet parent;
	private PVector location;
	private PVector velocity;
	private PVector acceleration;
	private float overshoot;
	private float multiplier;
	private float diameter;
	private float initVelocity;
	private int r, g, b, a;
	private long setTime;

	public Particle(PApplet parent, int r, int g, int b, int a, float diameter, float initVelocity) {
		this.parent = parent;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		// create start location
		this.location = new PVector(parent.random(parent.width), parent.random(parent.height), 0);
		// create velocity(director over time)
		this.initVelocity = initVelocity;
		this.diameter = diameter;
		this.setTime = Calendar.getInstance().getTimeInMillis();
		
		this.overshoot = 20;
		this.multiplier = 0.8f;
		
		randomizeDirection();
	}
	
	public Particle(PApplet parent, int r, int g, int b, int a) {
		this.parent = parent;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		// create start location
		this.location = new PVector(parent.random(parent.width), parent.random(parent.height), 0);
		// create velocity(director over time)
		this.initVelocity = 3;
		this.diameter = 4;
		this.setTime = Calendar.getInstance().getTimeInMillis();
		
		this.overshoot = 20;
		this.multiplier = 0.8f;
		
		randomizeDirection();
	}

	/**
	 * Run the particle with a specified target position.
	 * 
	 * @param target the position to move towards.
	 */
	public void run(PVector target, float intensity) {
		setDirection(target, intensity);
		run();
	}

	/**
	 * Run the particle with no target position.
	 */
	public void run() {
		update();
		display();
	}
	
	/**
	 * Updates the node with no target position; it will continue on it's 
	 * current path.
	 */
	public void update() {
		checkEdges();

		//System.out.println("floating");
		// velocity = new PVector(random(-2.5, 2.5), random(-2.5, 2.5)); //***
		// reset to 0 below
		
		//add to the velocity a random acceleration to give a wind effect
		//call every nth time
		//log.info("time");
		//log.info(String.valueOf(setTime));
		//check now against then set settime to now
		if(Calendar.getInstance().getTimeInMillis() - setTime >= parent.random(2000, 6000)) {
			acceleration = new PVector(parent.random(-0.3f, 0.3f), parent.random(-0.3f, 0.3f)); //new
			acceleration.normalize();//new
			velocity.add(acceleration);//new
		    velocity.limit(initVelocity);//new
		    setTime = Calendar.getInstance().getTimeInMillis();
		}
		
		location.add(velocity);
		
	}

	public void display() {
		/*
		imageLayer = graphicsLayer.get(0, 0, graphicsLayer.width, graphicsLayer.height);
		imageLayer.resize(0, parent.width/2);
		imageLayer.filter(parent.BLUR, 2);
		imageLayer.resize(0, parent.width);
		parent.image(imageLayer, 0, 0);
		
		graphicsLayer.beginDraw();
		graphicsLayer.background(0);
		graphicsLayer.fill(255);
		graphicsLayer.ellipse(location.x, location.y, diameter * 2, diameter * 2);
		graphicsLayer.endDraw();
		*/
		
		parent.noStroke();
		parent.fill(r, g, b, a);
		//TODO It won't draw if less than 4 and has an alpha...
		parent.ellipse(location.x, location.y, diameter, diameter);
	}

	/**
	 * Update the node with a designated target; it will move
	 * towards the given vector.
	 * 
	 * @param target the position to move towards
	 */
	public void setDirection(PVector target, float intensity) {
		/*float yHeight = target.y;
		if (yHeight < 300 && yHeight > 215) {
			multiplier = 1.0f;
			// overshoot = 10;
			//System.out.println("300");
		} else if (yHeight < 215 && yHeight > 185) {
			multiplier = 2.0f;
			// overshoot = 10;
			//System.out.println("215");
		} else if (yHeight < 185 && yHeight > 150) {
			multiplier = 15.0f;
			// overshoot = 30;
			//System.out.println("185");
		} else {
			multiplier = 0.8f;
			// overshoot = 10;
			//System.out.println("else");
		}*/
		
		multiplier = intensity;
	
		// find a vector pointing from the particle to the mouse
		PVector acceleration = PVector.sub(target, location);
	
		// Set magnitude of acceleration
		// reset from 0 to 1
		acceleration.normalize();
		// set acceleration factor
		acceleration.mult(multiplier);
		// Velocity changes to acceleration
		velocity.add(acceleration);
		// limit the velocity by top speed
		velocity.limit(overshoot);
		// ------------------
	}

	public void checkEdges() {
		// if the particle reaches the edge of the screen flip its velocity
		if ((location.x > parent.width) || (location.x < 0)) {
			velocity.x *= -1;
		}
		if ((location.y > parent.height) || (location.y < 0)) {
			velocity.y *= -1;
		}
	}

	/**
	 * Sets the particle's velocity to a random value.
	 */
	public void randomizeDirection() { 
		this.velocity = new PVector(parent.random(-initVelocity, initVelocity), parent.random(-initVelocity, initVelocity), 0);
	}

}
