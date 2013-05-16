package edu.cmu.hcii.modalnodes.animation;

import java.util.logging.Logger;

import processing.core.PApplet;
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
		parent.strokeCap(parent.SQUARE);
		
		
		parent.pushMatrix();
		parent.translate(x, y);
		// arch params arch(xCord, yCord, width, height, startAngle, stopAngle)
	    parent.rotate(parent.radians(rotation));
	    parent.arc(0, 0, rad -50, rad - 50, parent.radians(30), parent.radians(80));  
	    
	    parent.rotate(parent.radians(rotation * -2));
	    parent.arc(0, 0, rad - 50, rad - 50, parent.radians(50), parent.radians(180));
	    
	    parent.rotate(parent.radians(rotation / 2));
	    parent.arc(0, 0, rad - 25, rad - 25, parent.radians(270), parent.radians(360));
	    
	    parent.rotate(parent.radians(rotation * 2 / 20));  
	    parent.arc(0, 0, rad - 50 , rad - 50, parent.radians(200), parent.radians(300));
	    
	    parent.rotate(parent.radians(rotation - 20f * -1.5f));
	    parent.arc(0, 0, rad - 25, rad - 25, parent.radians(0), parent.radians(175));
	    
	    parent.rotate(parent.radians(rotation * 1.2f));  
	    parent.arc(0, 0, rad, rad, parent.radians(0), parent.radians(300));
	    
	    parent.popMatrix();   		
		
	}
	
	float decay(float in) {
		return in/1.06f;
	}
	
	public void spinNode() {
		rotSpeed = 100;
	}	
	
}








