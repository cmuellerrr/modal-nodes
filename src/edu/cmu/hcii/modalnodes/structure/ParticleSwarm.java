package edu.cmu.hcii.modalnodes.structure;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

/**
 * A class to represent a group of particles.  Works as a container
 * to control a group's behavior.  Can set a position to swarm around
 * or disperse them from any point.
 * 
 * @author Chris
 *
 */
public class ParticleSwarm {

	private Particle[] particles;
	private PVector centroid;
	private float intensity;
	
	public ParticleSwarm(PApplet parent, int count) {
		this.centroid = null;
		this.intensity = 1f;
		this.particles = new Particle[count];
		
		for (int i = 0; i < count; i++) {
			this.particles[i] = new Particle(parent, 255, 255, 255, 255);
		}
	}
	
	public ParticleSwarm(PApplet parent, int count, int r, int g, int b, int a) {
		this.centroid = null;
		this.intensity = 1f;
		this.particles = new Particle[count];
		
		for (int i = 0; i < count; i++) {
			this.particles[i] = new Particle(parent, r, g, b, a);
		}
	}
	
	public ParticleSwarm(PApplet parent, int count, int a) {
		this.centroid = null;
		this.particles = new Particle[count];
		
		for (int i = 0; i < count; i++) {
			this.particles[i] = new Particle(parent, 255, 255, 255, a);
		}
	}
	
	public ParticleSwarm(PApplet parent, int count, int a, float diameter, float initVelocity) {
		this.centroid = null;
		this.particles = new Particle[count];
		
		for (int i = 0; i < count; i++) {
			this.particles[i] = new Particle(parent, 255, 255, 255, a, diameter, initVelocity);
		}
	}
	
	/**
	 * Run the node swarm.  Tells the particles to move towards
	 * the swarm centroid if it exists.
	 */
	public void run() {
		if (centroid == null) {
			for (int i = 0; i < particles.length; i++) {
				particles[i].run();
			}
		} else {
			for (int i = 0; i < particles.length; i++) {
				particles[i].run(centroid, intensity);
			}
		}
	}
	
	/**
	 * Dispers the swarm.  Send each particle off 
	 * in a random direction.
	 */
	public void disperse() {
		this.centroid = null;
		for (int i = 0; i < particles.length; i++) {
			particles[i].randomizeDirection();
		}
	}
	
	public void swarm(PVector centroid, float intensity) {
		this.centroid = centroid;
		this.intensity = intensity;
	}
	
	/**
	 * Checks if the swarm is currently swarming around
	 * a centroid.
	 * 
	 * @return if the swarm is swarming
	 */
	public boolean isSwarming() {
		return this.centroid != null;
	}
}
