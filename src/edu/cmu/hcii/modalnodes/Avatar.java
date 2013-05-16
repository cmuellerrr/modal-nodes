package edu.cmu.hcii.modalnodes;

import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PVector;

public class Avatar {
	private final Logger log = Logger.getLogger(Controls.class.getName());
	private PApplet parent;
	
	private int shoulderWidth = 30;
	private int neckHeight = 45;
	private int armLength = 150;

	private PVector head;
	private PVector rShoulder;
	private PVector lShoulder;
	private PVector rHand;
	private PVector lHand;
	
	float xL, yL, xR, yR;

	public Avatar(PApplet parent, int locationX, int locationY) {
		this.parent = parent;
		this.head = new PVector(locationX, locationY, 0);
		this.rShoulder = new PVector(this.head.x + shoulderWidth, this.head.y + neckHeight, 0);
		this.lShoulder = new PVector(this.head.x - shoulderWidth, this.head.y + neckHeight, 0);
	    this.rHand = new PVector(0, this.rShoulder.y + armLength, 0);
		this.lHand = new PVector(0, this.lShoulder.y + armLength, 0);

		log.info(String.valueOf(head.x));
	}
	
	public void display() {
		parent.noFill();
		parent.strokeWeight(1);
		parent.stroke(255);
		
		parent.ellipse(head.x,  head.y, 40,  40);
		parent.line(rShoulder.x, rShoulder.y, lShoulder.x, lShoulder.y);
		
	    //Left arm
	    int d1L = 1600;
	    int x1L = (int)head.x + d1L - 10;
	    int y1L = (int)head.y - 120;
	    parent.ellipse(x1L, y1L, 5, 5);    
	    int x2L = x1L - d1L;
	    int y2L = y1L;
	    parent.ellipse(x2L, y2L, 5, 5);
	    int x3L = x2L;
	    int y3L = y2L + 300;
	    parent.ellipse(x3L, y3L, 5, 5);
	    int x4L = x3L + d1L;
	    int y4L = y3L;
	    parent.ellipse(x4L, y4L, 5, 5);
		
	    parent.stroke(142, 0, 128);
	    parent.curve(x1L, y1L, x2L, y2L, x3L, y3L, x4L, y4L);
	    xL = parent.curvePoint(x1L, x2L, x3L, x4L, Sequencer.leftHand); //global var
	    yL = parent.curvePoint(y1L, y2L, y3L, y4L, Sequencer.leftHand);  
	    
	    parent.stroke(255, 255, 255);
	    parent.line(xL, yL, lShoulder.x, lShoulder.y);
	    parent.ellipse(xL, yL, 20, 20);
	    
	    //Right arm 
	    int d1R = - 1600;
	    int x1R = (int)head.x + d1R + 10;
	    int y1R = (int)head.y - 120;
	    parent.ellipse(x1R, y1R, 5, 5);    
	    int x2R = x1R - d1R;
	    int y2R = y1R;
	    parent.ellipse(x2R, y2R, 5, 5);
	    int x3R = x2R;
	    int y3R = y2R + 300;
	    parent.ellipse(x3R, y3R, 5, 5);
	    int x4R = x3R + d1R;
	    int y4R = y3R;
	    parent.ellipse(x4R, y4R, 5, 5);

	    parent.stroke(0, 85, 0);
	    parent.curve(x1R, y1R, x2R, y2R, x3R, y3R, x4R, y4R);
	    xR = parent.curvePoint(x1R, x2R, x3R, x4R, Sequencer.leftHand); //global var
	    yR = parent.curvePoint(y1R, y2R, y3R, y4R, Sequencer.leftHand);   

	    parent.stroke(255, 255, 255);
	    parent.line(xR, yR, rShoulder.x, rShoulder.y);
	    parent.ellipse(xR, yR, 20, 20);
	}
	
	public float returnY() {
		return yL;
	}
		

}
