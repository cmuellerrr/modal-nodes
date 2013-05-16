package edu.cmu.hcii.modalnodes.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import processing.core.PApplet;
import processing.core.PVector;
import edu.cmu.hcii.modalnodes.Sequencer;

/**
 * A class to represent the grid of nodes.  This is the main data structure
 * for holding all of the node objects.  Basically just a 2D array that 
 * is another abstraction to make things easier.
 * 
 * Determines grid size based on the parent window properties and the 
 * pre-determined number of columns.  The number of columns is pre-determined
 * because it affects the frequency of the notes, so we want to somewhat limit
 * it.
 * 
 * @author Chris
 *
 */
public class NodeGrid {
private final Logger log = Logger.getLogger(NodeGrid.class.getName());
	
	private PApplet parent;
	private int numRows = 9;
	private int numCols;
	private int cellSize;
	private NodeCell[][] grid;
	private Map<Integer, NodeCell> hovered;
	
	/**
	 * Create a new node grid based off of the parent properties.
	 * 
	 * @param parent the parent applet
	 */
	public NodeGrid(PApplet parent) {
		this.parent = parent;
		this.cellSize = parent.height / this.numRows;
		this.numCols = parent.width / this.cellSize;
		
		this.grid = new NodeCell[numCols+1][numRows+1];
		initializeGrid();
		
		this.hovered = new HashMap<Integer, NodeCell>();
		
		log.info("Columns: " + numCols + " Rows: " + numRows + " Cell Size: " + cellSize);
	}


	/**
	 * Initialize the grid array with empty NodeCells.
	 */
	private void initializeGrid() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				grid[i][j] = new NodeCell(i, j);
			}
		}
	}


	/**
	 * Draw the grid if necessary and run all current nodes.
	 */
	public void run() {
		if (Sequencer.isDebug()) display();
		
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if (!grid[i][j].isEmpty()) grid[i][j].run();
			}
		}
	}
	

	/**
	 * Draw the grid.
	 */
	public void display() {
		parent.pushStyle();
		
		parent.strokeWeight(1);
		parent.stroke(255, 30);
		
		for (int i = 0; i <= grid.length; i++) {
		    parent.line(i * cellSize, 0, i * cellSize, parent.height);	
		}
		
		for (int j = 0; j <= grid[0].length; j++) {
			parent.line(0, j * cellSize, parent.width, j * cellSize);	
		}
		
		parent.popStyle();
	}
	
	/**
	 * Check the currently populated cells to see if their 
	 * nodes are being hovered over and if they have been hovered
	 * over for long enough to become the focus of a user.  If
	 * a node needs to become a focus, remove it from the cell 
	 * and return it to the sequencer.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Node checkUserFocus(int userId, int x, int y) {
		//If the user was already hovering over a node, check that first
		if (hovered.containsKey(userId)) {
			NodeCell prevCell = hovered.get(userId);
			
			if (prevCell.nodeContains(x, y)) {
				if (prevCell.nodeHoverTime() >= Node.hoverThreshold) {
					prevCell.endNodeHover();
					hovered.remove(userId);
					Node result = prevCell.removeNode();
					result.startFocus(userId);
					return result;
				} else {
					return null;
				}
			} else {
				prevCell.endNodeHover();
				hovered.remove(userId);
			}
		} 
		
		//Otherwise, check a 3x3 grid around the current cell
		int col = getCol(x);
		int row = getRow(y);
		
		for (int i = col-1; i <= col+1; i++) {
			for (int j = row-1; j <= row+1; j++) {
				try {
					NodeCell curCell = grid[i][j];
						
					//If the node contains the given coordinates
					if (curCell.nodeContains(x, y)) {
						long time = curCell.nodeHoverTime();
						
						if (time == 0) {
							curCell.startNodeHover(userId);
							hovered.put(userId,  curCell);	
							return null;
						} else if (time >= Node.hoverThreshold) {
							curCell.endNodeHover();
							hovered.remove(userId);
							Node result = curCell.removeNode();
							result.startFocus(userId);
							return result;
						}
					}
				//I hate doing this, but it's kind of efficient
				} catch (ArrayIndexOutOfBoundsException e) {
				    log.finest("Ran out of bounds");
				}
			}
		}

		return null;
	}
	
	/**
	 * Clear any nodes being hovered on by the given user id.
	 * 
	 * @param userId
	 */
	public void clearUserHover(int userId) {
		NodeCell hoverCell = hovered.remove(userId);
		if (hoverCell != null) {
			hoverCell.endNodeHover();
		}
	}
	
	/**
	 * Add the given node to the grid.  Based off of the given 
	 * position, add the node to the corresponding grid call and
	 * set the node to the center of that cell.
	 * 
	 * This will override the node already in place
	 * 
	 * @param pos the node's current position
	 * @param rad the node's radius
	 */
	public void addNode(Node node) {
		int col = getCol(node.getPos().x);
		int row = getRow(node.getPos().y);
		
		if (col >= 0 && col < grid.length && row >= 0 && row < grid[0].length) {
			node.setPos(new PVector((col * cellSize) + (cellSize / 2), 
						(row * cellSize) + (cellSize / 2), 0));
					
			grid[col][row].addNode(node);
		} else {
			log.warning("Trying to put node out of bounds at " + col + ", " + row);
		}
	}
	
	/**
	 * Get the determined cell size.
	 * 
	 * @return the cellSize the size of each cell
	 */
	public int getCellSize() {
		return cellSize;
	}
	
	public int getCol(float x) {
		int result = (int)Math.floor(x / cellSize);
		
		if (result < 0) result = 0;
		if (result >= grid.length) result = grid.length - 1; 
		
		return result;
	}
	
	public int getRow(float y) {
		int result = (int) Math.floor(y / cellSize);

		if (result < 0) result = 0;
		if (result >= grid[0].length) result = grid[0].length - 1; 
		
		return result;
	}
}
