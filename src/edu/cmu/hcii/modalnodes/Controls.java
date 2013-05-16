package edu.cmu.hcii.modalnodes;


import java.util.logging.Logger;
import processing.core.PApplet;

import controlP5.*;



public class Controls {
	private final Logger log = Logger.getLogger(Controls.class.getName());
	
	private ControlP5 control;
	private ControlWindow w;
	
	
	public Controls(PApplet parent) {
		control = new ControlP5(parent);
		

		//Sequencer.background = 50;
		Sequencer.leftHand = (float) 0.95;
		
		w = control.addControlWindow("controlWindow", 10, 10, 350, 140);
		w.hideCoordinates();
		w.setTitle("Parameters");
		int y = 0;
		//control.addSlider("background", 0, 255, Sequencer.background, 10, y += 10, 256, 9).setWindow(w);
		control.addSlider("leftHand", 0, 1, Sequencer.leftHand, 10, y+= 10, 256, 9).setWindow(w);
		control.setAutoInitialization(true);
		
		//log.info(String.valueOf(Sequencer.background));
		//log.info("test");
	}
	
	/*
	private void setParameters(){ //could be public
		//set default params
		//param2 = 10;
		//param3 = 23;
	}
	 */
}
