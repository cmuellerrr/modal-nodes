package edu.cmu.hcii.modalnodes.structure;

import java.util.logging.Logger;

import edu.cmu.hcii.modalnodes.Sequencer;

/**
 * An implementation of a cell within the node grid.  Basically just holds
 * a node if necessary.  The main reason for this class is if we want 
 * cells within the grid to eventually hold multiple nodes.  So that logic
 * would be here.
 * 
 * @author Chris
 *
 */
public class NodeCell {
	private final Logger log = Logger.getLogger(NodeCell.class.getName());
	
	private int col;
	private int row;
	private Node node;
	
	/**
	 * Create a node cell.
	 */
	public NodeCell(int col, int row) {
		this.col = col;
		this.row = row;
		this.node = null;
	}
	
	/**
	 * Create a node cell containing the given node.
	 * 
	 * @param node the node to hold
	 */
	public NodeCell(int col, int row, Node node) {
		this.col = col;
		this.row = row;
		this.node = node;
	}
	
	/**
	 * Run the contained node.
	 */
	public void run() {
		if (node != null) node.run();
	}
	
	/**
	 * Set this cell's node
	 * 
	 * @param node the node to set
	 */
	public void addNode(Node node) {
		node.setTone(Node.tones[row]);
		this.node = node;
	}

	/**
	 * Clear out the cell, removing any nodes.
	 * 
	 * TODO could be dangerous. Maybe we handle this at the grid level and just
	 * create a new cell.
	 */
	public Node removeNode() {
		log.finer("Removing node from cell");
		Node n = this.node;
		this.node = null;
		return n;
	}
	
	/**
	 * Check if the cell contains any nodes.
	 * 
	 * @return if the cell is empty
	 */
	public boolean isEmpty() {
		return node == null;
	}

	/**
	 * Mark the node as being hovered over.
	 */
	public void startNodeHover(int userId) {
		if (node != null) node.startHover(userId);
	}
	
	/**
	 * Mark the node as no longer being hovered over.
	 */
	public void endNodeHover() {
		if (node != null) node.endHover();
	}
	
	/**
	 * Return the node's hover time in milliseconds.
	 * 
	 * @return the time, in milliseconds, with which the node has been hovered over
	 */
	public long nodeHoverTime() {
		return (node == null) ?  0 : node.hoverTime();
	}
	
	/**
	 * Returns whether or not the given coordinates lie within the node.
	 * 
	 * @param x the x position to test
	 * @param y the y position to test
	 * @return if the given x,y lies within the node
	 */
	public boolean nodeContains(int x, int y) {
		return (node == null) ? false : node.contains(x, y); 
	}
}
