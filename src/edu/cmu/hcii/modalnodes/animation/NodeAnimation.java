package edu.cmu.hcii.modalnodes.animation;

/**
 * An interface for node animations.  This allows new animations
 * to be applied to nodes with minimal effort.
 * 
 * @author Chris
 *
 */
public interface NodeAnimation {

	/**
	 * Run the animation
	 */
	public void run();
	
	/**
	 * Check if the animation is done
	 * 
	 * @return If the animation is done
	 */
	public boolean isDone();
}
