import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.LinkedHashSet;
import java.util.HashMap;
import javax.swing.*;
import java.awt.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class StaticGenAndSearch {
	static int cellsTraversed = 0;
	static int maxFringeSize = 0;
	
	
	public static PathNode[][] generateMap (int dim, double p, boolean onFire){
		PathNode[][] map = new PathNode[dim][dim];
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				double random = Math.random();
				while (random == 0) { //0 is not an acceptable value, assign a new #
					random = Math.random();
				}
				map[i][j] = new PathNode(i, j, p <= random, false); //probability(p <= random) == probability a cell is empty	
			}
		}
		map[0][0].isEmpty = true; //start is reachable
		map[dim-1][dim-1].isEmpty = true; //goal is reachable
		
		if (onFire) { //start or goal begin on fire?
			int rowOnFire = (int) (Math.random() * dim); //random() does not include 1
			int colOnFire = (int) (Math.random() * dim);
			while (!map[rowOnFire][colOnFire].isEmpty || rowOnFire == 0 && colOnFire == 0) { //if there is an obstacle or fire starts at 0,0, recompute cell on fire
				rowOnFire = (int) (Math.random() * dim);
				colOnFire = (int) (Math.random() * dim);
			}
			map[rowOnFire][colOnFire].isOnFire = true;
		}
		return map;
	}
	
	public static void resetMap(PathNode[][]map) {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				map[i][j].prev = null;
			}
		}
	}
	
	public static PriorityQueue<PathNode> updateFringeWithHeuristic(PriorityQueue<PathNode> fringe, PathNode[][] map, PathNode curr, boolean[][] visited, boolean usesEuclidean, int[][]distance){
		int rowIndex = curr.row;
		int colIndex = curr.col;
		if (rowIndex+1 < map.length && map[rowIndex+1][colIndex].isEmpty && !map[rowIndex+1][colIndex].isOnFire && map[rowIndex+1][colIndex].prev == null && !visited[rowIndex+1][colIndex]) { //moving down is a non-repeated, viable choice (node not already in fringe or visited)
			map[rowIndex+1][colIndex].prev = curr;
			distance[rowIndex+1][colIndex] = distance[rowIndex][colIndex]+1; //distance to child = distance to parent + one additional operation
			if (usesEuclidean) {
				map[rowIndex+1][colIndex].distanceEst = distance[rowIndex+1][colIndex] + Math.sqrt((Math.pow((rowIndex+1)-(map.length-1), 2)) + (Math.pow((colIndex)-(map.length-1), 2))); //add Euclidean distance heuristic to goal to the current distance from start to node
			}else{
				map[rowIndex+1][colIndex].distanceEst = distance[rowIndex+1][colIndex] + Math.abs(((rowIndex+1)-(map.length-1)) + (colIndex-(map.length-1))); //add Manhattan distance heuristic to the current distance from start to node
			}
			fringe.add(map[rowIndex+1][colIndex]);
		}
		if (colIndex+1 < map.length && map[rowIndex][colIndex+1].isEmpty && !map[rowIndex][colIndex+1].isOnFire && map[rowIndex][colIndex+1].prev == null && !visited[rowIndex][colIndex+1]) { //moving right is a non-repeated, viable choice
			map[rowIndex][colIndex+1].prev = curr;
			distance[rowIndex][colIndex+1] = distance[rowIndex][colIndex]+1; //distance to child = distance to parent + one additional operation
			if (usesEuclidean) {
				map[rowIndex][colIndex+1].distanceEst = distance[rowIndex][colIndex+1] + Math.sqrt((Math.pow((rowIndex)-(map.length-1), 2)) + (Math.pow((colIndex+1)-(map.length-1), 2))); //add Euclidean distance heuristic to goal to the current distance from start to node
			}else{
				map[rowIndex][colIndex+1].distanceEst = distance[rowIndex][colIndex+1] + Math.abs(((rowIndex)-(map.length-1)) + ((colIndex+1)-(map.length-1))); //add Manhattan distance heuristic to the current distance from start to node
			}
			fringe.add(map[rowIndex][colIndex+1]);
		}
		if (rowIndex-1 >= 0 && map[rowIndex-1][colIndex].isEmpty && !map[rowIndex-1][colIndex].isOnFire && map[rowIndex-1][colIndex].prev == null && !visited[rowIndex-1][colIndex]) { //moving up is a non-repeated, viable choice
			map[rowIndex-1][colIndex].prev = curr;
			distance[rowIndex-1][colIndex] = distance[rowIndex][colIndex]+1; //distance to child = distance to parent + one additional operation
			if (usesEuclidean) {//Euclidean distance is the heuristic
				map[rowIndex-1][colIndex].distanceEst = distance[rowIndex-1][colIndex] + Math.sqrt((Math.pow((rowIndex-1)-(map.length-1), 2)) + (Math.pow((colIndex)-(map.length-1), 2))); //add Euclidean distance heuristic to goal to the current distance from start to node
			}else{ //use Manhattan distance
				map[rowIndex-1][colIndex].distanceEst = distance[rowIndex-1][colIndex] + Math.abs(((rowIndex-1)-(map.length-1)) + (colIndex-(map.length-1))); //add Manhattan distance heuristic to the current distance from start to node
			}
			fringe.add(map[rowIndex-1][colIndex]);
		}
		if (colIndex-1 >= 0 && map[rowIndex][colIndex-1].isEmpty && !map[rowIndex][colIndex-1].isOnFire && map[rowIndex][colIndex-1].prev == null && !visited[rowIndex][colIndex-1]) { //moving left is a non-repeated, viable choice
			map[rowIndex][colIndex-1].prev = curr;
			distance[rowIndex][colIndex-1] = distance[rowIndex][colIndex]+1; //distance to child = distance to parent + one additional operation
			if (usesEuclidean) {
				map[rowIndex][colIndex-1].distanceEst = distance[rowIndex][colIndex-1] + Math.sqrt((Math.pow((rowIndex)-(map.length-1), 2)) + (Math.pow((colIndex-1)-(map.length-1), 2))); //add Euclidean distance heuristic to goal to the current distance from start to node
			}else{
				map[rowIndex][colIndex-1].distanceEst = distance[rowIndex][colIndex-1] + Math.abs(((rowIndex)-(map.length-1)) + ((colIndex-1)-(map.length-1))); //add Manhattan distance heuristic to the current distance from start to node
			}
			fringe.add(map[rowIndex][colIndex-1]);
		}
		return fringe;
	}
	
	public static PathNode updateFringeBDBFS(LinkedHashSet<PathNode> expandFringe, LinkedHashSet<PathNode> intersectFringe, PathNode [][] map, PathNode curr, boolean [][] visited) {
		int rowIndex = curr.row;
		int colIndex = curr.col;
		
		if (rowIndex+1 < map.length && map[rowIndex+1][colIndex].isEmpty && !visited[rowIndex+1][colIndex]) { //moving down is a non-repeated, viable choice (node not already in fringe or visited)
			if (intersectFringe.contains(map[rowIndex+1][colIndex])) {
				return map[rowIndex+1][colIndex];
			}
			map[rowIndex+1][colIndex].prev = curr;
			expandFringe.add(map[rowIndex+1][colIndex]);
		}
		if (colIndex+1 < map.length && map[rowIndex][colIndex+1].isEmpty && !visited[rowIndex][colIndex+1]) { //moving right is a non-repeated, viable choice
			if (intersectFringe.contains(map[rowIndex][colIndex+1])) {
				return map[rowIndex][colIndex+1];
			}
			map[rowIndex][colIndex+1].prev = curr;
			expandFringe.add(map[rowIndex][colIndex+1]);
		}
		if (rowIndex-1 >= 0 && map[rowIndex-1][colIndex].isEmpty && !visited[rowIndex-1][colIndex]) { //moving up is a non-repeated, viable choice
			if (intersectFringe.contains(map[rowIndex-1][colIndex])) {
				return map[rowIndex-1][colIndex];
			}
			map[rowIndex-1][colIndex].prev = curr;
			expandFringe.add(map[rowIndex-1][colIndex]);
		}
		if (colIndex-1 >= 0 && map[rowIndex][colIndex-1].isEmpty && !visited[rowIndex][colIndex-1]) { //moving left is a non-repeated, viable choice
			if (intersectFringe.contains(map[rowIndex][colIndex-1])) {
				return map[rowIndex][colIndex-1];
			}
			map[rowIndex][colIndex-1].prev = curr;
			expandFringe.add(map[rowIndex][colIndex-1]);
		}
		
		return null;
	}
	
	public static LinkedList<PathNode> updateFringe(LinkedList<PathNode> fringe, PathNode[][] map, PathNode curr, boolean[][] visited){
		int rowIndex = curr.row;
		int colIndex = curr.col;
		if (rowIndex+1 < map.length && map[rowIndex+1][colIndex].isEmpty && map[rowIndex+1][colIndex].prev == null && !visited[rowIndex+1][colIndex]) { //moving down is a non-repeated, viable choice (node not already in fringe or visited)
			map[rowIndex+1][colIndex].prev = curr;
			fringe.add(map[rowIndex+1][colIndex]);
		}
		if (colIndex+1 < map.length && map[rowIndex][colIndex+1].isEmpty && map[rowIndex][colIndex+1].prev == null && !visited[rowIndex][colIndex+1]) { //moving right is a non-repeated, viable choice
			map[rowIndex][colIndex+1].prev = curr;
			fringe.add(map[rowIndex][colIndex+1]);
		}
		if (rowIndex-1 >= 0 && map[rowIndex-1][colIndex].isEmpty && map[rowIndex-1][colIndex].prev == null && !visited[rowIndex-1][colIndex]) { //moving up is a non-repeated, viable choice
			map[rowIndex-1][colIndex].prev = curr;
			fringe.add(map[rowIndex-1][colIndex]);
		}
		if (colIndex-1 >= 0 && map[rowIndex][colIndex-1].isEmpty && map[rowIndex][colIndex-1].prev == null && !visited[rowIndex][colIndex-1]) { //moving left is a non-repeated, viable choice
			map[rowIndex][colIndex-1].prev = curr;
			fringe.add(map[rowIndex][colIndex-1]);
		}
		return fringe;
	}
	
	public static LinkedList<PathNode> updateFringeForDFS(LinkedList<PathNode> fringe, PathNode[][] map, PathNode curr, boolean[][] visited){
		int rowIndex = curr.row;
		int colIndex = curr.col;
		if (rowIndex-1 >= 0 && map[rowIndex-1][colIndex].isEmpty && map[rowIndex-1][colIndex].prev == null && !visited[rowIndex-1][colIndex]) { //moving up is a non-repeated, viable choice
			map[rowIndex-1][colIndex].prev = curr;
			fringe.add(map[rowIndex-1][colIndex]);
		}
		if (colIndex-1 >= 0 && map[rowIndex][colIndex-1].isEmpty && map[rowIndex][colIndex-1].prev == null && !visited[rowIndex][colIndex-1]) { //moving left is a non-repeated, viable choice
			map[rowIndex][colIndex-1].prev = curr;
			fringe.add(map[rowIndex][colIndex-1]);
		}
		if (rowIndex+1 < map.length && map[rowIndex+1][colIndex].isEmpty && map[rowIndex+1][colIndex].prev == null && !visited[rowIndex+1][colIndex]) { //moving down is a non-repeated, viable choice (node not already in fringe or visited)
			map[rowIndex+1][colIndex].prev = curr;
			fringe.add(map[rowIndex+1][colIndex]);
		}
		if (colIndex+1 < map.length && map[rowIndex][colIndex+1].isEmpty && map[rowIndex][colIndex+1].prev == null && !visited[rowIndex][colIndex+1]) { //moving right is a non-repeated, viable choice
			map[rowIndex][colIndex+1].prev = curr;
			fringe.add(map[rowIndex][colIndex+1]);
		}
		return fringe;
	}
	
	public static PathNode DepthFirstSearch(PathNode[][] map) {
		if (map == null || map[0] == null || map[0].length == 0) return null;
		map[0][0].prev = null; //start has no predecessor 
		LinkedList<PathNode> fringe = new LinkedList<PathNode>();
		boolean [][] visited = new boolean[map.length][map.length];
		for (int i = 0; i < visited.length; i++) {
			for (int j = 0; j < visited.length; j++) {
				visited[i][j] = false;
			}
		}
		fringe.add(map[0][0]);
		maxFringeSize = Math.max(maxFringeSize, fringe.size());
		while (!fringe.isEmpty()) {
			cellsTraversed++;
			PathNode curr = fringe.getLast();
			fringe.removeLast();
			visited[curr.row][curr.col] = true; //mark node as visited
			if (curr.equals(map[map.length-1][map.length-1])) { //curr is the goal state
				return curr;
			}else{
				fringe = updateFringeForDFS(fringe, map, curr, visited); //updates fringe
				maxFringeSize = Math.max(maxFringeSize, fringe.size());
			}
		}
		return null;
	}
	
	public static PathNode BreadthFirstSearch(PathNode[][] map) {
		if (map == null || map[0] == null || map[0].length == 0) return null;
		map[0][0].prev = null; //start has no predecessor 
		LinkedList<PathNode> fringe = new LinkedList<PathNode>();
		boolean [][] visited = new boolean[map.length][map.length];
		for (int i = 0; i < visited.length; i++) {
			for (int j = 0; j < visited.length; j++) {
				visited[i][j] = false;
			}
		}
		fringe.add(map[0][0]);
		maxFringeSize = Math.max(maxFringeSize, fringe.size());
		while (!fringe.isEmpty()) {
			PathNode curr = fringe.getFirst();
			fringe.remove(); //removes first element from list
			visited[curr.row][curr.col] = true; //mark node as visited
			cellsTraversed++;
			if (curr.equals(map[map.length-1][map.length-1])) { //curr is the goal state
				return curr;
			}else{
				fringe = updateFringe(fringe, map, curr, visited); //updates fringe
				maxFringeSize = Math.max(maxFringeSize, fringe.size());
			}
		}
		return null;
	}
	
	public static PathNode AStar(PathNode[][]map, boolean usesEuclidean) {
		if (map == null || map[0] == null || map[0].length == 0) return null;  //map isn't constructed in a valid way
		boolean[][] visited = new boolean[map.length][map.length];
		int[][]distance = new int[map.length][map.length]; //distance (# of operations) from start to PathNode at distance[i][j]
		for (int i = 0; i < visited.length; i++) {
			for (int j = 0; j < visited.length; j++) {
				visited[i][j] = false;
				distance[i][j] = -1;
			}
		}
		distance[0][0] = 0;
		PriorityQueue<PathNode> fringe = new PriorityQueue<PathNode>();
		fringe.add(map[0][0]);
		maxFringeSize = Math.max(maxFringeSize, fringe.size());
		map[0][0].prev = null;
		map[0][0].distanceEst = 0;
		while (!fringe.isEmpty()) {
			PathNode curr = fringe.poll();
			visited[curr.row][curr.col] = true;
			cellsTraversed++;
			if (curr.equals(map[map.length-1][map.length-1])) {
				maxFringeSize = Math.max(maxFringeSize, fringe.size());
				return curr;
			}else{
				fringe = updateFringeWithHeuristic(fringe, map, curr, visited, usesEuclidean, distance); 
				maxFringeSize = Math.max(maxFringeSize, fringe.size());
			}
		}
		return null;
	}
	
	//this method is called when AStar is repeatedly called to recalculate route to goal wh
	public static PathNode AStarWithNewStart (PathNode start, PathNode[][]map, boolean usesEuclidean) {
		if (map == null || map[0] == null || map[0].length == 0) return null;  //map isn't constructed in a valid way
		boolean[][] visited = new boolean[map.length][map.length];
		int[][]distance = new int[map.length][map.length]; //distance (# of operations) from start to PathNode at distance[i][j]
		for (int i = 0; i < visited.length; i++) {
			for (int j = 0; j < visited.length; j++) {
				visited[i][j] = false;
				distance[i][j] = -1;
			}
		}
		distance[start.row][start.col] = 0;
		PriorityQueue<PathNode> fringe = new PriorityQueue<PathNode>();
		fringe.add(start);
		maxFringeSize = Math.max(maxFringeSize, fringe.size());
		start.prev = null;
		start.distanceEst = 0;
		while (!fringe.isEmpty()) {
			PathNode curr = fringe.poll();
			visited[curr.row][curr.col] = true;
			cellsTraversed++;
			if (curr.equals(map[map.length-1][map.length-1])) {
				maxFringeSize = Math.max(maxFringeSize, fringe.size());
				return curr;
			}else{
				fringe = updateFringeWithHeuristic(fringe, map, curr, visited, usesEuclidean, distance); 
				maxFringeSize = Math.max(maxFringeSize, fringe.size());
			}
		}
		return null;
	}
	
	public static PathNode bidirectionalBFS (PathNode [][] map) {
		if (map == null || map[0] == null || map[0].length == 0) return null; //map isn't constructed in a valid way
		PathNode start = map[0][0];
		PathNode goal = map[map.length-1][map.length-1];		
		start.prev = null;
		goal.prev = null;
		
		//construct visited arrays for bfs from start and goal
		boolean [][] visitedFromStart = new boolean [map.length][map.length];
		boolean [][] visitedFromGoal = new boolean [map.length][map.length];
		
		//construct queues for bfs from start and goal
		LinkedHashSet<PathNode> fringeFromStart = new LinkedHashSet<PathNode>();
		LinkedHashSet<PathNode> fringeFromGoal = new LinkedHashSet<PathNode>();
		
		//initially add the start and goals into the queues
		fringeFromStart.add(start);
		fringeFromGoal.add(goal);
		
		while (!fringeFromStart.isEmpty() && !fringeFromGoal.isEmpty()) {
			PathNode intersectFromStart = helperBFS (map, visitedFromStart, fringeFromStart, fringeFromGoal);
			if (intersectFromStart != null) { // We found a node in fringeFromGoal that we tried to add to fringeFromStart
				PathNode ptr = fringeFromStart.iterator().next();
				PathNode ptr2 = intersectFromStart;
				while (ptr != null) {
					PathNode temp = ptr.prev;
					ptr.prev = ptr2;
					ptr2 = ptr;
					ptr = temp;
				}
				return ptr2; // We should be done with path construction, hopefully (this goes start to goal) and returns start PathNode.
			}
			PathNode intersectFromGoal = helperBFS (map, visitedFromGoal, fringeFromGoal, fringeFromStart);
			if (intersectFromGoal != null) { // We found a node in fringeFromStart that we tried to add to fringeFromGoal
				PathNode ptr = fringeFromGoal.iterator().next();
				PathNode ptr2 = intersectFromGoal;
				while (ptr != null) {
					PathNode temp = ptr.prev;
					ptr.prev = ptr2;
					ptr2 = ptr;
					ptr = temp;
				}
				return ptr2; // We should be done with path construction, hopefully (this goes goal to start) and returns goal PathNode.
			}
	
			// If we get here, then both BFS's were run for one step and neither intersected the other's fringe
			fringeFromStart.remove(fringeFromStart.iterator().next()); // Removes first node from the start fringe that we just processed
			fringeFromGoal.remove(fringeFromGoal.iterator().next()); // Removes first node from the goal fringe that we just processed
			// Update maximum fringe size with the larger value: sum of the fringes vs current max fringe size
			maxFringeSize = Math.max(maxFringeSize, fringeFromStart.size() + fringeFromGoal.size());
		}
		return null;
	}
	
	// Helper method for bidirectional BFS. Returns null if fringe node has already been visited (should never happen)
	// or if all possible neighbors of first node in fringe were added to fringe normally. Otherwise, one of the neighbors
	// of the first node was already on the fringe of the BFS procedure occurring in the opposite direction.
	public static PathNode helperBFS (PathNode [][] map, boolean [][] visited, LinkedHashSet <PathNode> expandFringe, LinkedHashSet<PathNode> intersectFringe) {
		PathNode curr = expandFringe.iterator().next(); // We are visiting the first node on the fringe
		if (!visited [curr.row][curr.col]) { // This should always be true, because nodes added to the fringe are checked to not have already been visited.
			cellsTraversed++;
			visited [curr.row][curr.col] = true;
			System.out.println("Visited by BD-DFS: (" + curr.row + "," + curr.col + ")");
			cellsTraversed++;
			PathNode intersect = updateFringeBDBFS(expandFringe, intersectFringe, map, curr, visited);
			return intersect;
		}
		return null;
	}
	
	public static void printMap(PathNode[][]map) {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				System.out.print("(" + map[i][j].row + "," + map[i][j].col + ") ");
				if (map[i][j].isEmpty && !map[i][j].isOnFire) {
					System.out.print("free");
				}else if (!map[i][j].isEmpty){
					System.out.print("occupied");
				}else if (map[i][j].isOnFire) {
					System.out.print("fire");
				}
				System.out.print("\t");
			}
			System.out.println();
		}
	}
	public static PathNode[][] deepCopy(PathNode[][] original) throws CloneNotSupportedException{ //should not throw exception--clone should be supported
		PathNode[][]copy = new PathNode[original.length][original.length];
		for (int i = 0; i < copy.length; i++) {
			for (int j = 0; j < copy.length; j++) {
				copy[i][j] = new PathNode(i, j, original[i][j].isEmpty, false);
			}
		}
		return copy;
	}
	
	public static PathNode[][] helperFindHardest(PathNode[][] current, boolean usesDFS){ //find the hardest child of the current maze
		PathNode[][] hardest = null;
		if (usesDFS) { //use the # of nodes expanded by DFS as the hardness metric
			DepthFirstSearch(current);
			int maxFringe = maxFringeSize; //number of nodes expanded by original maze
			for (int i = 0; i < current.length; i++) {
				for (int j = 0; j < current.length; j++) {
					maxFringeSize = 0;
					resetMap(current); //allow the current hardest board to be traversed again
					current[i][j].isEmpty = !current[i][j].isEmpty; //change the occupation status of one node
					PathNode goal = DepthFirstSearch(current);
					if (maxFringeSize > maxFringe && goal != null) { //current child is the hardest child thus far
						try {
							hardest  = deepCopy(current);
							maxFringe = maxFringeSize;
						} catch (CloneNotSupportedException e) { //should not occur
							e.printStackTrace();
						}
					}
					current[i][j].isEmpty = !current[i][j].isEmpty;
				} 
			}
		}else{ //uses A*
			AStar(current, false);
			int mostCellsTraversed = cellsTraversed; //number of nodes expanded by original maze
			for (int i = 0; i < current.length; i++) {
				for (int j = 0; j < current.length; j++) {
					cellsTraversed = 0;
					resetMap(current); //allow the current hardest board to be traversed again
					current[i][j].isEmpty = !current[i][j].isEmpty; //change the occupation status of one node
					PathNode goal = AStar(current, false);
					if (cellsTraversed > mostCellsTraversed && goal != null) { //current child is the hardest child thus far
						try {
							hardest  = deepCopy(current);
							mostCellsTraversed = cellsTraversed;
						} catch (CloneNotSupportedException e) { //should not occur
							e.printStackTrace();
						}
					}
					current[i][j].isEmpty = !current[i][j].isEmpty;
				} 
			}
		}
		if (hardest != null) resetMap(hardest); //if a harder maze was found
		return hardest;
	}
	
	public static PathNode[][] getHardestMaze(PathNode[][] original, boolean usesDFS) {//NOTE: when calling, the original maze must be solvable
		long startTime = System.nanoTime();
		PathNode[][] prev = null;
		while (original != null) { //while less than 10 seconds have elapsed
			try {
				prev = deepCopy(original);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			original = helperFindHardest(original, usesDFS);
		}
		return prev;
	}
	
	public static PathNode[][] findHardestPLevel(int dim, boolean usesDFS){ 
		PathNode[][] hardest = null;
		int fringeSize = 0;
		int maxCells = 0;
		for (int i = 0; i < 1000; i++){
			cellsTraversed = 0;
			maxFringeSize = 0;
			double p = Math.random();
			PathNode[][] map = generateMap(dim, p, false);
			if (usesDFS){
				map = getHardestMaze(map, true);
				if (maxFringeSize > fringeSize) {
					try {
						fringeSize = maxFringeSize;
						hardest = deepCopy(map);
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
			}else{
				map = getHardestMaze(map, false);
				if (cellsTraversed > maxCells) {
					try {
						maxCells = cellsTraversed;
						hardest = deepCopy(map);
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		cellsTraversed = maxCells; //set global vars to hold the stats of the hardest maze
		maxFringeSize = fringeSize;
		return hardest;
	}
	
	//currentPosition refers to the location of the player through the maze in adversarial searches
	//black = maze obstacle, white = open spaces in maze, light gray = shortest path 
	//orange = fire, red = fire intersects with shortest path, magenta = person burns
	public static void printMazeSolutionGUI(PathNode [][] map, PathNode goal, PathNode currentPosition, String algorithm) {
		JFrame maze = new JFrame("Maze with Dim = " + map.length + " Solved by " + algorithm);
		maze.setSize(500, 500);
		maze.setLayout(new GridLayout(map.length, map.length));
		JPanel cells[][] = new JPanel[map.length][map.length];
		
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				cells[i][j] = new JPanel();
				cells[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
				if (map[i][j].isEmpty && !map[i][j].isOnFire) {
					cells[i][j].setBackground(Color.white);
				} else if (!map[i][j].isEmpty) {
					cells[i][j].setBackground(Color.black);
				}
				else if (map[i][j].isOnFire) {
					cells[i][j].setBackground(Color.orange);
				}
				maze.add(cells[i][j]);
			}
		}
		
		JLabel startLabel = new JLabel("S");
		JLabel goalLabel = new JLabel("G");
		startLabel.setFont(new Font("Times New Roman", 1, 6));
		goalLabel.setFont(new Font("Times New Roman", 1, 6));
		cells[0][0].setLayout(new GridBagLayout());
		cells[0][0].add(startLabel);
		cells[map.length-1][map.length-1].setLayout(new GridBagLayout());
		cells[map.length-1][map.length-1].add(goalLabel);
		
		LinkedList <PathNode> path = generateSolvedPath (goal);
		for (int i = 0; i < path.size(); i++) {
			PathNode current = path.get(i);
			if (current == currentPosition && current.isOnFire) {
				cells[current.row][current.col].setBackground(Color.MAGENTA);
			}
			else if (current == currentPosition) {
				cells[current.row][current.col].setBackground(Color.green);
			}
			else if (current.isOnFire) {
				cells[current.row][current.col].setBackground(Color.RED);
			} 
			else {
				cells[current.row][current.col].setBackground(Color.BLUE);
			}
		}
		
		maze.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		maze.setVisible(true);
	}
	
	// Creates the dataset needed to plot density against maze solvability. To ensure a relatively
	// smooth curve, there are data points at 0.02 increments from p = 0 to p = 0.98. Each data point
	// is computed by generating 1000 mazes at that p value, solving each one of them, and counting 
	// the number of the total mazes that return solutions. That value is divided by 1000 to get the
	// decimal value of solvability between 0 and 1.
	public static DefaultXYDataset mazeSolvability() {
		DefaultXYDataset data = new DefaultXYDataset();
		
		double[][] dfsData = new double[2][50];
		double[][] bfsData = new double[2][50];
		double[][] bdbfsData = new double[2][50];
		double[][] euclidData = new double[2][50];
		double[][] manhattanData = new double[2][50];
		
		for (int p = 0; p < 50; p++) {
			int numDFSSolved = 0;
			int numBFSSolved = 0;
			int numBDBFSSolved = 0;
			int numManhattanSolved = 0;
			int numEuclidSolved = 0;
			
			for (int trial = 0; trial < 1000; trial++) {
				PathNode [][] testMap = generateMap(100, 0.02*p, false);
				
				PathNode dfsSoln = DepthFirstSearch(testMap);
				if (dfsSoln != null) {
					numDFSSolved++;
				}
				resetMap(testMap);
				
				PathNode bfsSoln = BreadthFirstSearch(testMap);
				if (bfsSoln != null) {
					numBFSSolved++;
				}
				resetMap(testMap);
				
				PathNode bdbfsSoln = bidirectionalBFS(testMap);
				if (bdbfsSoln != null) {
					numBDBFSSolved++;
				}
				resetMap(testMap);
				
				PathNode manhattanAStarSoln = AStar(testMap, false);
				if (manhattanAStarSoln != null) {
					numManhattanSolved++;
				}
				resetMap(testMap);
				
				PathNode euclidAStarSoln = AStar(testMap, true);
				if (euclidAStarSoln != null) {
					numEuclidSolved++;
				}
				
			}
			
			dfsData[0][p] = 0.02*p;
			dfsData[1][p] = (numDFSSolved/1000.0);
			bfsData[0][p] = 0.02*p;
			bfsData[1][p] = (numBFSSolved/1000.0);
			bdbfsData[0][p] = 0.02*p;
			bdbfsData[1][p] = (numBDBFSSolved/1000.0);
			euclidData[0][p] = 0.02*p;
			euclidData[1][p] = (numEuclidSolved/1000.0);
			manhattanData[0][p] = 0.02*p;
			manhattanData[1][p] = (numManhattanSolved/1000.0);
			
		}
		
		data.addSeries("A*-Manhattan", manhattanData);
		data.addSeries("DFS", dfsData);
		data.addSeries("BFS", bfsData);
		data.addSeries("Bidirectional BFS", bdbfsData);
		data.addSeries("A*-Euclidean", euclidData);
		return data;
	}
	
	// When called from main, generates a line plot between density (p) on the x-axis and 
	// a decimal value between 0 and 1 representing the probability that a randomly generated maze
	// with density p will be solved. Plots are generated for all the different algorithms
	// explored in this project, and the probability of being solved is generated based on sample
	// sizes of 100 mazes for each data point.
	public static void plotMazeSolvability() {
		ApplicationFrame solvabilityPlotApp = new ApplicationFrame("Maze Solvability Window");
		JFreeChart solvabilityPlot = ChartFactory.createXYLineChart("Maze Solvability at Different Densities", "Density (p)", "Fraction Solved", mazeSolvability(), PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(solvabilityPlot);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		solvabilityPlotApp.setContentPane(chartPanel);
		solvabilityPlotApp.pack();
		RefineryUtilities.centerFrameOnScreen(solvabilityPlotApp);
		solvabilityPlotApp.setVisible(true);
	}
	
	public static DefaultXYDataset shortestPath() {
		DefaultXYDataset averagePathLengths = new DefaultXYDataset();
		
		double[][] pathLengthData = new double[2][42];
		
		for (int p = 0; p < 42; p++) {
			int totalLength = 0;
			for (int trial = 0; trial < 1000; trial++) {
				PathNode [][] testMap = generateMap(100, 0.01*p, false);
				int length = 0;
			
				PathNode manhattanAStarSoln = AStar(testMap, false);
				while (manhattanAStarSoln == null) {
					testMap = generateMap(100, 0.01*p, false);
					manhattanAStarSoln = AStar(testMap, false);
				}
				
				while (manhattanAStarSoln != null) {
					length++;
					manhattanAStarSoln = manhattanAStarSoln.prev;
				}
				//System.out.println("Trial: " + trial);
				//System.out.println("Length: " + length);
				totalLength += length;
				
			}
			System.out.println("p: " + p*0.01);
			System.out.println("Avg Shortest Path Length: " + (totalLength/1000.0));
			pathLengthData[0][p] = p*0.01;
			pathLengthData[1][p] = totalLength/1000.0;
		}
		averagePathLengths.addSeries("Average Path Lengths with A*-Manhattan", pathLengthData);
		
		return averagePathLengths;
	}
	
	public static void plotShortestPaths() {
		ApplicationFrame shortestPathsPlotApp = new ApplicationFrame("Shortest Paths (With A*-Manhattan) Window");
		JFreeChart shortestPathsPlot = ChartFactory.createXYLineChart("Shortest Paths (Using A*-Manhattan) as a Function of Density (p)", "Density (p)", "Average Length of Shortest Path (# of cells)", shortestPath(), PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(shortestPathsPlot);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		shortestPathsPlotApp.setContentPane(chartPanel);
		shortestPathsPlotApp.pack();
		RefineryUtilities.centerFrameOnScreen(shortestPathsPlotApp);
		shortestPathsPlotApp.setVisible(true);
	}
	
	public static DefaultXYDataset avgNodesExpanded() {
		DefaultXYDataset nodesExpanded = new DefaultXYDataset();
		
		double[][] bfsNodes = new double[2][41];
		double[][] dfsNodes = new double[2][41];
		double[][] bdBFSNodes = new double[2][41];
		double[][] numEuclidNodes = new double[2][41];
		double[][] numManhattanNodes = new double[2][41];
		
		for (int p = 0; p < 41; p++) {
			int totalEuclidNodes = 0, totalManhattanNodes = 0, totalBFSNodes = 0, totalDFSNodes = 0, totalbdBFSNodes = 0;
			for (int trial = 0; trial < 1000; trial++) {
				PathNode[][] testMap = generateMap(100, 0.01*p, false);
				
				cellsTraversed = 0;
				PathNode AStarEuclid = AStar(testMap, true);
				while (AStarEuclid == null) {
					System.out.println("Generated unsolvable map, trying again.");
					cellsTraversed = 0;
					testMap = generateMap(100, 0.01*p, false);
					AStarEuclid = AStar(testMap, true);
				}
				System.out.println("Trial: " + trial);
				System.out.println("Number of Euclid Nodes: " + cellsTraversed);
				totalEuclidNodes += cellsTraversed;
				
				resetMap(testMap);
				cellsTraversed = 0;
				PathNode AStarManhattan = AStar(testMap, false);
				System.out.println("Number of Manhattan Nodes: " + cellsTraversed);
				totalManhattanNodes += cellsTraversed;
				
				resetMap(testMap);
				cellsTraversed = 0;
				PathNode bfs = BreadthFirstSearch(testMap);
				System.out.println("Number of BFS Nodes: " + cellsTraversed);
				totalBFSNodes += cellsTraversed;
				
				resetMap(testMap);
				cellsTraversed = 0;
				PathNode dfs = DepthFirstSearch(testMap);
				System.out.println("Number of DFS Nodes: " + cellsTraversed);
				totalDFSNodes += cellsTraversed;
				
				resetMap(testMap);
				cellsTraversed = 0;
				PathNode bdBFS = bidirectionalBFS(testMap);
				System.out.println("Number of Bidirectional BFS Nodes: " + cellsTraversed);
				totalbdBFSNodes += cellsTraversed;
			}
			dfsNodes[0][p] = 0.01*p;
			dfsNodes[1][p] = totalDFSNodes/1000.0;
			System.out.println("p: " + dfsNodes[0][p]);
			System.out.println("Avg Euclid Nodes: " + dfsNodes[1][p]);
			
			bfsNodes[0][p] = 0.01*p;
			bfsNodes[1][p] = totalBFSNodes/1000.0;
			System.out.println("p: " + bfsNodes[0][p]);
			System.out.println("Avg Euclid Nodes: " + bfsNodes[1][p]);
			
			bdBFSNodes[0][p] = 0.01*p;
			bdBFSNodes[1][p] = totalbdBFSNodes/1000.0;
			System.out.println("p: " + bdBFSNodes[0][p]);
			System.out.println("Avg Euclid Nodes: " + bdBFSNodes[1][p]);
			
			numEuclidNodes[0][p] = 0.01*p;
			numEuclidNodes[1][p] = totalEuclidNodes/1000.0;
			System.out.println("p: " + numEuclidNodes[0][p]);
			System.out.println("Avg Euclid Nodes: " + numEuclidNodes[1][p]);
			
			numManhattanNodes[0][p] = 0.01*p;
			numManhattanNodes[1][p] = totalManhattanNodes/1000.0;
			System.out.println("Avg Manhattan Nodes: " + numManhattanNodes[1][p]);
		}
		
		nodesExpanded.addSeries("BFS", bfsNodes);
		nodesExpanded.addSeries("DFS", dfsNodes);
		nodesExpanded.addSeries("Bidirectional BFS", bdBFSNodes);
		nodesExpanded.addSeries("A*-Euclidean", numEuclidNodes);
		nodesExpanded.addSeries("A*-Manhattan", numManhattanNodes);
		
		return nodesExpanded;
	}
	
	public static void plotExpandedNodes() {
		ApplicationFrame expandedNodesApp = new ApplicationFrame("Average Number of Nodes Expanded Window");
		JFreeChart expandedNodesPlot = ChartFactory.createXYLineChart("Avg Number of Nodes Expanded", "Density (p)", "Avg Number of Nodes Expanded", avgNodesExpanded(), PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(expandedNodesPlot);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		expandedNodesApp.setContentPane(chartPanel);
		expandedNodesApp.pack();
		RefineryUtilities.centerFrameOnScreen(expandedNodesApp);
		expandedNodesApp.setVisible(true);
	}
	
	public static DefaultXYDataset avgAStarNodes() {
		DefaultXYDataset AStarNodes = new DefaultXYDataset();
		
		double[][] numEuclidNodes = new double[2][41];
		double[][] numManhattanNodes = new double[2][41];
		
		for (int p = 0; p < 41; p++) {
			int totalEuclidNodes = 0, totalManhattanNodes = 0;
			for (int trial = 0; trial < 1000; trial++) {
				PathNode[][] testMap = generateMap(100, 0.01*p, false);
				
				cellsTraversed = 0;
				PathNode AStarEuclid = AStar(testMap, true);
				while (AStarEuclid == null) {
					System.out.println("Generated unsolvable map, trying again.");
					cellsTraversed = 0;
					testMap = generateMap(100, 0.01*p, false);
					AStarEuclid = AStar(testMap, true);
				}
				System.out.println("Trial: " + trial);
				System.out.println("Number of Euclid Nodes: " + cellsTraversed);
				totalEuclidNodes += cellsTraversed;
				
				resetMap(testMap);
				cellsTraversed = 0;
				PathNode AStarManhattan = AStar(testMap, false);
				System.out.println("Number of Manhattan Nodes: " + cellsTraversed);
				totalManhattanNodes += cellsTraversed;
				
			}
			numEuclidNodes[0][p] = 0.01*p;
			numEuclidNodes[1][p] = totalEuclidNodes/1000.0;
			System.out.println("p: " + numEuclidNodes[0][p]);
			System.out.println("Avg Euclid Nodes: " + numEuclidNodes[1][p]);
			
			numManhattanNodes[0][p] = 0.01*p;
			numManhattanNodes[1][p] = totalManhattanNodes/1000.0;
			System.out.println("Avg Manhattan Nodes: " + numManhattanNodes[1][p]);
		}
		AStarNodes.addSeries("A*-Euclidean", numEuclidNodes);
		AStarNodes.addSeries("A*-Manhattan", numManhattanNodes);
		
		return AStarNodes;
		
	}
	
	public static void plotAvgAStarNodes() {
		ApplicationFrame avgAStarNodesApp = new ApplicationFrame("Average Number of Nodes Expanded by A* Window");
		JFreeChart avgAStarNodesPlot = ChartFactory.createXYLineChart("Avg Number of Nodes Expanded by A*", "Density (p)", "Avg Number of Nodes Expanded", avgAStarNodes(), PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(avgAStarNodesPlot);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		avgAStarNodesApp.setContentPane(chartPanel);
		avgAStarNodesApp.pack();
		RefineryUtilities.centerFrameOnScreen(avgAStarNodesApp);
		avgAStarNodesApp.setVisible(true);
	}
	
	public static void dimTester() {
		for (int i = 100; i < 101; i++) {
			cellsTraversed = 0;
			maxFringeSize = 0;
			PathNode[][] testMap = generateMap(i, 0.2, false);
			long startTime = System.nanoTime();
			PathNode goal = bidirectionalBFS(testMap);
			long endTime = System.nanoTime();
			long executionTime = endTime - startTime;
			
			printMazeSolutionGUI(testMap, goal, testMap[0][0], "Bidirectional BFS");
			System.out.println("Time elapsed for BD-BFS to solve dim = " + i + " maze with p = 0.5: " + (executionTime/1000000000.0));
			System.out.println("Total number of cells traversed by BD-BFS with dim = " + i + " maze with p = 0.5: " + cellsTraversed);
			//System.out.println("Maximum fringe size using BD-DFS with dim = " + i + " maze with p = 0.5: " + maxFringeSize);
			printMazeSolutionGUI(testMap, goal, testMap[0][0], "BFS");
			System.out.println("Time elapsed for DFS to solve dim = " + i + " maze with p = 0.5: " + (executionTime/1000000000.0));
			System.out.println("Total number of cells traversed by DFS with dim = " + i + " maze with p = 0.5: " + cellsTraversed);
			System.out.println("Maximum fringe size using DFS with dim = " + i + " maze with p = 0.5: " + maxFringeSize);
			System.out.println();
			cellsTraversed = 0;
			maxFringeSize = 0;
			PathNode secondGoal = AStar(testMap, false);
			printMazeSolutionGUI(testMap, secondGoal, testMap[0][0], "A*-Manhattan");
			System.out.println("Total number of cells traversed by A* with dim = " + i + " maze with p = 0.5: " + cellsTraversed);
			//System.out.println("Maximum fringe size using A* with dim = " + i + " maze with p = 0.5: " + maxFringeSize);
			System.out.println();
		}
	}
	
	public static PathNode[][] fireSpreads (PathNode [][] map, double q) {
		boolean [][] onFireNext = new boolean [map.length][map.length];
		//q = flammability rate of fire
		//multiple cells can become on fire per move
		//neighbors not including diagonal neighbors
		//obstructions block the fire
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				int numNbrFire = 0;
				if (!map[i][j].isEmpty || map[i][j].isOnFire) {
					continue;
				}
				if (i+1 < map.length && map[i+1][j].isOnFire) {
					numNbrFire++;
				}
				if (j+1 < map.length && map[i][j+1].isOnFire) {
					numNbrFire++;
				}
				if (i-1 >= 0 && map[i-1][j].isOnFire) {
					numNbrFire++;
				}
				if (j-1 >= 0 && map[i][j-1].isOnFire) {
					numNbrFire++;
				}
//				System.out.println ("(" + i + ", " + j + ")" + "numNbrFire = " + numNbrFire);
				double onFire = 1-(Math.pow(1-q, numNbrFire));
				double random = Math.random();
				if (random <= onFire) {
					onFireNext[i][j] = true;
				}
			}
		}
		for (int i = 0; i < onFireNext.length; i++) {
			for (int j = 0; j < onFireNext.length; j++) {
				if (onFireNext[i][j]) {
					map[i][j].isOnFire = true;
				}
			}
		}
		return map;
	}
	
	//Method to implement Strategy 1: compute shortest path to goal at the start of the maze and move towards goal, ignoring fire movement
	//Initial shortest path is generated with A* Manhattan Distance and takes into account the initial position of the fire
	//initialFireMap must be solvable initially
	//You make the first move, fire makes the second move
	//Exit the maze or burn
	public static boolean ignoreFireSpreading (PathNode [][] initialFireMap, PathNode goal, double flammabilityOfFire) throws Exception {
		boolean success = true;
		if (goal == null) {
			throw new Exception ("maze is not initially solvable");
		}
		LinkedList<PathNode> path = generateSolvedPath(goal);
		PathNode currentPosition = path.pop(); //start at starting position
		while (currentPosition != initialFireMap[initialFireMap.length - 1][initialFireMap.length - 1]) { //termination conditions
			if (currentPosition.isOnFire) {
				return success = false;
			}
			currentPosition = path.pop();
			fireSpreads(initialFireMap, flammabilityOfFire);
			printMazeSolutionGUI(initialFireMap, goal, currentPosition, "Adversarial Search");
			
		}
		return success;
	}
	
	//Method to implement Strategy 2: recompute shortest path to goal every time fire moves
	//Initial shortest path is generated with A* Manhattan Distance and takes into account the initial position of the fire
	//You make the first move, fire makes the second move
	//If you burn or can't reach the goal anymore, you die
	public static boolean avoidFireSpreading (PathNode [][] newFireMap, PathNode goal, double flammabilityOfFire) throws Exception {
		boolean success = true;
		if (goal == null) {
			throw new Exception ("maze is not initially solvable");
		}
		LinkedList<PathNode> path = generateSolvedPath(goal);
		PathNode currentPosition = path.pop(); //start at starting position
		while (currentPosition != newFireMap[newFireMap.length - 1][newFireMap.length - 1]) { //termination conditions
			if (currentPosition.isOnFire) {
				System.out.println("burned");
				return success = false;
			}
			currentPosition = path.pop(); //person makes the first move
			printMazeSolutionGUI(newFireMap, goal, currentPosition, "Adversarial Search"); //print the move of the person
			fireSpreads(newFireMap, flammabilityOfFire);
			printMazeSolutionGUI(newFireMap, goal, currentPosition, "Adversarial Search"); //print the move of the fire
			if (currentPosition.isOnFire) { //after fire moves, re-check to see if you've burned
				System.out.println("burned");
				return success = false;
			}
			resetMap(newFireMap);
			goal = AStarWithNewStart (currentPosition, newFireMap, false);
			if (goal == null) {
				System.out.println("no more paths to goal");
				return success = false;
			}
			path = generateSolvedPath(goal);
//			PathNode newCurrentPosition = path.pop();
//			while (newCurrentPosition != currentPosition) {
//				newCurrentPosition = path.pop();
//			}
			currentPosition = path.pop();
		}
		return success;
	}
	
	//helper method to return the linked list representation of the path after the current position
	//this method is called when the path to goal is updated after the fire spreads
//	public static PathNode findCurrentPosition (PathNode currentPosition, LinkedList <PathNode> path) {
//		for (int i = 0; i < path.size(); i++) {
//			path.getFirst();
//		}
//		while (goal != currentPosition) {
//			newPath.pop();
//		}
//		newPath.pop();
//		return newPath;
//	}
	
	
	//helper method to find the location of the initial fire
	public static PathNode findInitialFire (PathNode [][] initialFireMap) throws Exception {
		for (int i = 0; i < initialFireMap.length; i++) {
			for (int j = 0; j < initialFireMap.length; j++) {
				if (initialFireMap[i][j].isOnFire) {
					return initialFireMap[i][j];
				}
			}
		}
		throw new Exception ("No initial fire found");
	}
	
	
	// Creates the dataset needed to plot density against fire maze solvability. To ensure a relatively
	// smooth curve, there are data points at 0.02 increments from q = 0.02 to q = 1.0. Each data point
	// is computed by generating 1000 mazes at that q value, solving each one of them, and counting 
	// the number of the total mazes that return solutions. That value is divided by 1000 to get the
	// decimal value of solvability between 0 and 1.
	public static DefaultXYDataset fireMazeSolvability () throws Exception {
		DefaultXYDataset data = new DefaultXYDataset();
		double[][] ignoreFireData = new double[2][50];
		double[][] avoidFireData = new double[2][50];
		
		for (int q = 1; q < 51; q++) {
			int numIgnoreFireSolved = 0;
			int numAvoidFireSolved = 0;
			
			for (int trial = 0; trial < 1000; trial++) {
				PathNode [][] testMap = generateMap(100, 0.28, true);
				PathNode initialFire = findInitialFire(testMap);
				initialFire.isOnFire = false; //temporarily set it to false so A* can find path between start and initial fire
				PathNode manhattanAStarFireSoln = AStar(testMap, false);
				while (manhattanAStarFireSoln == null) { //if there is no initial path between fire and start, ignore maze and create a new maze
					testMap = generateMap(100, 0.28, true);
					initialFire = findInitialFire(testMap);
					initialFire.isOnFire = false;
					manhattanAStarFireSoln = AStar(testMap, false);
				}
				initialFire.isOnFire = true;
//				
//				PathNode ignoreFireSoln = ignoreFireSpreading (testMap, 0.02*q); //use AStar Manhattan distance
//				if (ignoreFireSoln != null) {
//					numIgnoreFireSolved++;
//				}
//				resetMap(testMap);
//				
//				PathNode avoidFireSoln = avoidFireSpreading (testMap, 0.02*q); //use AStar Manhattan distance
//				if (avoidFireSoln != null) {
//					numAvoidFireSolved++;
//				}
				resetMap(testMap);
			}
			
			ignoreFireData[0][q] = 0.02*q;
			ignoreFireData[1][q] = (numIgnoreFireSolved/1000.0);
			avoidFireData[0][q] = 0.02*q;
			avoidFireData[1][q] = (numAvoidFireSolved/1000.0);
		}
		data.addSeries("Ignore Fire Stratgey", ignoreFireData);
		data.addSeries("Avoid Fire Stratgey", avoidFireData);
		return data;
	}
	
	// When called from main, generates a line plot between density (p) on the x-axis and 
	// a decimal value between 0 and 1 representing the probability that a randomly generated maze
	// with density p will be solved. Plots are generated for all the different algorithms
	// explored in this project, and the probability of being solved is generated based on sample
	// sizes of 100 mazes for each data point.
	public static void plotFireMazeSolvability() {
		ApplicationFrame solvabilityPlotApp = new ApplicationFrame("Maze Solvability Window");
		JFreeChart solvabilityPlot = ChartFactory.createXYLineChart("Maze Solvability at Different Flammabilities", "Density (q)", "Fraction Solved", mazeSolvability(), PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(solvabilityPlot);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		solvabilityPlotApp.setContentPane(chartPanel);
		solvabilityPlotApp.pack();
		RefineryUtilities.centerFrameOnScreen(solvabilityPlotApp);
		solvabilityPlotApp.setVisible(true);
	}
	
	
	//Returns a linked list representation of the solved path from start to goal
	public static LinkedList <PathNode> generateSolvedPath (PathNode goal) {
		LinkedList<PathNode> path = new LinkedList<PathNode> ();
		while (goal != null) {
			path.addFirst(goal);
			goal = goal.prev;
		}
		System.out.println();
		for (int i = 0; i < path.size(); i++) {
			System.out.print("(" + path.get(i).row + "," + path.get(i).col + ") ");
		}
		System.out.println();
		return path;
	}
	
	// Allows for a single maze to be generated with density (p) set to the specified
	// parameter. Then, all of the algorithms for solving static mazes implemented in this
	// project are called on the same maze and their results displayed on the GUI.
	public static void pathsForAllAlgorithms(double p) {
		PathNode bfsGoal, dfsGoal, bidirectBFSGoal, euclidGoal, manhattanGoal = null;
		do {
			PathNode[][] testMap = generateMap(100, p, false);
			PathNode goal = null;
			cellsTraversed = 0;
			maxFringeSize = 0;
			dfsGoal = DepthFirstSearch(testMap);
			printMazeSolutionGUI(testMap, dfsGoal, testMap[0][0], "DFS");
			System.out.println("DFS expanded " + cellsTraversed + " nodes for this maze.");
			System.out.println("The maximum fringe size of DFS for this maze was " + maxFringeSize + " nodes.");
			int length = 0;
			goal = dfsGoal;
			while (goal != null) {
				length++;
				goal = goal.prev;
			}
			System.out.println("The length of the path returned by DFS is: " + length);
			resetMap(testMap);
			
			cellsTraversed = 0;
			maxFringeSize = 0;
			bfsGoal = BreadthFirstSearch(testMap);
			printMazeSolutionGUI(testMap, bfsGoal, testMap[0][0], "BFS");
			System.out.println("BFS expanded " + cellsTraversed + " nodes for this maze.");
			System.out.println("The maximum fringe size of BFS for this maze was " + maxFringeSize + " nodes.");
			length = 0;
			goal = bfsGoal;
			while (goal != null) {
				length++;
				goal = goal.prev;
			}
			System.out.println("The length of the path returned by BFS is: " + length);
			resetMap(testMap);
			
			cellsTraversed = 0;
			maxFringeSize = 0;
			bidirectBFSGoal = bidirectionalBFS(testMap);
			printMazeSolutionGUI(testMap, bidirectBFSGoal, testMap[0][0], "Bidirectional BFS");
			System.out.println("Bidirectional BFS expanded " + cellsTraversed + " nodes for this maze.");
			System.out.println("The maximum total fringe size for both fringes in bidirectional BFS for this maze was " + maxFringeSize + " nodes.");
			length = 0;
			goal = bidirectBFSGoal;
			while (goal != null) {
				length++;
				goal = goal.prev;
			}
			System.out.println("The length of the path returned by bidirectional BFS is: " + length);
			resetMap(testMap);
			
			cellsTraversed = 0;
			maxFringeSize = 0;
			euclidGoal = AStar(testMap, true);
			printMazeSolutionGUI(testMap, euclidGoal, testMap[0][0], "A*-Euclidean");
			System.out.println("A* with the Euclidean distance as the heuristic expanded " + cellsTraversed + " nodes for this maze.");
			System.out.println("The maximum fringe size of A* with the Euclidean distance heuristic for this maze was " + maxFringeSize + " nodes.");
			length = 0;
			goal = euclidGoal;
			while (goal != null) {
				length++;
				goal = goal.prev;
			}
			System.out.println("The length of the path returned by A*-Euclidean is: " + length);
			resetMap(testMap);
			
			cellsTraversed = 0;
			maxFringeSize = 0;
			manhattanGoal = AStar(testMap, false);
			System.out.println("A* with the Manhattan distance as the heuristic expanded " + cellsTraversed + " nodes for this maze.");
			System.out.println("The maximum fringe size of A* with the Manhattan distance heuristic for this maze was " + maxFringeSize + " nodes.");
			length = 0;
			goal = manhattanGoal;
			while (goal != null) {
				length++;
				goal = goal.prev;
			}
			System.out.println("The length of the path returned by A*-Manhattan is: " + length);
			printMazeSolutionGUI(testMap, manhattanGoal, testMap[0][0], "A*-Manhattan");
			resetMap(testMap);
			
		} while (bfsGoal == null || dfsGoal == null || bidirectBFSGoal == null || euclidGoal == null || manhattanGoal == null);
		
	}
	
	public static void main(String[] args) throws Exception {
		
		PathNode[][] fireMap = generateMap(10, 0.1, true);
//		printMap(fireMap);
//		System.out.println();
		PathNode fireGoal = AStarWithNewStart(fireMap[0][0], fireMap, true);
		printMazeSolutionGUI(fireMap, fireGoal, fireMap[0][0], "Adversarial Search");
		boolean success = avoidFireSpreading (fireMap, fireGoal, 0.5);
		System.out.println("!!!!!" + success + "!!!!!");
//		LinkedList <PathNode> path = generateSolvedPath (fireGoal);
//		fireSpreads(fireMap, 1.0);

		/* dimTester();
		
		
		printMazeSolutionGUI(fireMap, fireGoal, "Adversarial Search"); 
		
		//plotMazeSolvability();
		pathsForAllAlgorithms(0.2);
		//plotAvgAStarNodes();
		//plotShortestPaths();
		//plotExpandedNodes();
		/*PathNode[][] testMap = generateMap(11, 0.22, false);
		printMap(testMap);
		System.out.println();
		PathNode goal = AStar(testMap, true);
		resetMap(testMap);
		goal = AStar(testMap, false);
		printMazeSolutionGUI(testMap, goal);

		while (goal!= null) {
			System.out.println("Node: row - " + goal.row + " col - " + goal.col); 
			goal = goal.prev;
		} */ 
		/*PathNode[][] regMap = generateMap(10, 0.2, false);
		printMazeSolutionGUI(regMap, null, "A* Manhattan - original maze");
		AStar(regMap, false);
		System.out.println("Nodes expanded on the original maze: " + cellsTraversed);
		regMap = getHardestMaze(regMap, false);
		printMazeSolutionGUI(regMap, null, "A* Manhattan - hardest maze");
		System.out.println("Nodes expanded on the hardest maze: " + cellsTraversed);
		/*PathNode[][] regMap2 = generateMap(10, 0.2, false);
		printMazeSolutionGUI(regMap2, null, "DFS - original maze");
		AStar(regMap2, false);
		System.out.println("Max fringe size on the original maze: " + maxFringeSize);
		regMap = getHardestMaze(regMap2, false);
		printMazeSolutionGUI(regMap2, null, "DFS - hardest maze");
		System.out.println("Max fringe size on the hardest maze: " + maxFringeSize);*/
//		PathNode[][] theHardest = findHardestPLevel(100, true);
//		printMazeSolutionGUI(theHardest, null, "DFS - hardest maze");
//		System.out.println("Size of fringe on the hardest maze: " + maxFringeSize);
	}

}
