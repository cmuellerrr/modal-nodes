package edu.cmu.hcii.modalnodes.animation;

import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import edu.cmu.hcii.modalnodes.Controls;

/*
 * spining animation visualizes the nodes echo by spinning then 
 * gradually slowing to a stop
 */

public class NodeSpin {
	private PApplet parent;
	private float x, y, rad, rotation;
	private float rotSpeed = 100;
	private float curRot = 0;
	
	public NodeSpin(PApplet parent) {
		final Logger log = Logger.getLogger(Controls.class.getName());		
		this.parent = parent;
		//log.info("start node spin");
	}
	
	public void drawNode(float xLocation, float yLocation, float rad) {
		curRot += rotSpeed;
		rotSpeed = decay(rotSpeed);
		
		this.x = xLocation;
		this.y = yLocation;
		this.rad = rad;
		this.rotation = curRot;
		
		parent.noFill();
		parent.strokeWeight(10);
		parent.stroke(255, 150);
		parent.strokeCap(PConstants.SQUARE);
		
		
		parent.pushMatrix();
		parent.translate(x, y);
		// arch params arch(xCord, yCord, width, height, startAngle, stopAngle)
	    parent.rotate(PApplet.radians(rotation));
	    parent.arc(0, 0, rad -50, rad - 50, PApplet.radians(30), PApplet.radians(80));  
	    
	    parent.rotate(PApplet.radians(rotation * -2));
	    parent.arc(0, 0, rad - 50, rad - 50, PApplet.radians(50), PApplet.radians(180));
	    
	    parent.rotate(PApplet.radians(rotation / 2));
	    parent.arc(0, 0, rad - 25, rad - 25, PApplet.radians(270), PApplet.radians(360));
	    
	    parent.rotate(PApplet.radians(rotation * 2 / 20));  
	    parent.arc(0, 0, rad - 50 , rad - 50, PApplet.radians(200), PApplet.radians(300));
	    
	    parent.rotate(PApplet.radians(rotation - 20f * -1.5f));
	    parent.arc(0, 0, rad - 25, rad - 25, PApplet.radians(0), PApplet.radians(175));
	    
	    parent.rotate(PApplet.radians(rotation * 1.2f));  
	    parent.arc(0, 0, rad, rad, PApplet.radians(0), PApplet.radians(300));
	    
	    parent.popMatrix();   		
		
	}
	
	float decay(float in) {
		return in/1.06f;
	}
	
	public void spinNode() {
		rotSpeed = 100;
	}	
	
}








