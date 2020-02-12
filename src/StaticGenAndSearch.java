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
			while (!map[rowOnFire][colOnFire].isEmpty) { //if there is an obstacle, recompute cell on fire
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
		if (rowIndex+1 < map.length && map[rowIndex+1][colIndex].isEmpty && map[rowIndex+1][colIndex].prev == null && !visited[rowIndex+1][colIndex]) { //moving down is a non-repeated, viable choice (node not already in fringe or visited)
			map[rowIndex+1][colIndex].prev = curr;
			distance[rowIndex+1][colIndex] = distance[rowIndex][colIndex]+1; //distance to child = distance to parent + one additional operation
			if (usesEuclidean) {
				map[rowIndex+1][colIndex].distanceEst = distance[rowIndex+1][colIndex] + Math.sqrt((Math.pow((rowIndex+1)-(map.length-1), 2)) + (Math.pow((colIndex)-(map.length-1), 2))); //add Euclidean distance heuristic to goal to the current distance from start to node
			}else{
				map[rowIndex+1][colIndex].distanceEst = distance[rowIndex+1][colIndex] + Math.abs(((rowIndex+1)-(map.length-1)) + (colIndex-(map.length-1))); //add Manhattan distance heuristic to the current distance from start to node
			}
			fringe.add(map[rowIndex+1][colIndex]);
		}
		if (colIndex+1 < map.length && map[rowIndex][colIndex+1].isEmpty && map[rowIndex][colIndex+1].prev == null && !visited[rowIndex][colIndex+1]) { //moving right is a non-repeated, viable choice
			map[rowIndex][colIndex+1].prev = curr;
			distance[rowIndex][colIndex+1] = distance[rowIndex][colIndex]+1; //distance to child = distance to parent + one additional operation
			if (usesEuclidean) {
				map[rowIndex][colIndex+1].distanceEst = distance[rowIndex][colIndex+1] + Math.sqrt((Math.pow((rowIndex)-(map.length-1), 2)) + (Math.pow((colIndex+1)-(map.length-1), 2))); //add Euclidean distance heuristic to goal to the current distance from start to node
			}else{
				map[rowIndex][colIndex+1].distanceEst = distance[rowIndex][colIndex+1] + Math.abs(((rowIndex)-(map.length-1)) + ((colIndex+1)-(map.length-1))); //add Manhattan distance heuristic to the current distance from start to node
			}
			fringe.add(map[rowIndex][colIndex+1]);
		}
		if (rowIndex-1 >= 0 && map[rowIndex-1][colIndex].isEmpty && map[rowIndex-1][colIndex].prev == null && !visited[rowIndex-1][colIndex]) { //moving up is a non-repeated, viable choice
			map[rowIndex-1][colIndex].prev = curr;
			distance[rowIndex-1][colIndex] = distance[rowIndex][colIndex]+1; //distance to child = distance to parent + one additional operation
			if (usesEuclidean) {//Euclidean distance is the heuristic
				map[rowIndex-1][colIndex].distanceEst = distance[rowIndex-1][colIndex] + Math.sqrt((Math.pow((rowIndex-1)-(map.length-1), 2)) + (Math.pow((colIndex)-(map.length-1), 2))); //add Euclidean distance heuristic to goal to the current distance from start to node
			}else{ //use Manhattan distance
				map[rowIndex-1][colIndex].distanceEst = distance[rowIndex-1][colIndex] + Math.abs(((rowIndex-1)-(map.length-1)) + (colIndex-(map.length-1))); //add Manhattan distance heuristic to the current distance from start to node
			}
			fringe.add(map[rowIndex-1][colIndex]);
		}
		if (colIndex-1 >= 0 && map[rowIndex][colIndex-1].isEmpty && map[rowIndex][colIndex-1].prev == null && !visited[rowIndex][colIndex-1]) { //moving left is a non-repeated, viable choice
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
	
	public static LinkedList<PathNode> updateFringeButPoorly(LinkedList<PathNode> fringe, PathNode[][] map, PathNode curr, boolean[][] visited){
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
				fringe = updateFringeButPoorly(fringe, map, curr, visited); //updates fringe
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
			cellsTraversed++;
			PathNode curr = fringe.getFirst();
			fringe.remove(); //removes first element from list
			if (visited[curr.row][curr.col]) continue;
			visited[curr.row][curr.col] = true; //mark node as visited
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
			System.out.println("Visited by A*: (" + curr.row + "," + curr.col + ")");
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
			
		}
		return null;
	}
	
	// Helper method for bidirectional BFS. Returns null if fringe node has already been visited (should never happen)
	// or if all possible neighbors of first node in fringe were added to fringe normally. Otherwise, one of the neighbors
	// of the first node was already on the fringe of the BFS procedure occurring in the opposite direction.
	public static PathNode helperBFS (PathNode [][] map, boolean [][] visited, LinkedHashSet <PathNode> expandFringe, LinkedHashSet<PathNode> intersectFringe) {
		PathNode curr = expandFringe.iterator().next(); // We are visiting the first node on the fringe
		if (!visited [curr.row][curr.col]) { // This should always be true, because nodes added to the fringe are checked to not have already been visited.
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
				copy[i][j] = (PathNode) original[i][j].clone(); //TODO: figure out deep copy
			}
		}
		return null;
	}
	
	public static PathNode[][] helperFindHardest(PathNode[][] current, boolean usesDFS){ //find the hardest child of the current maze
		PathNode[][] hardest = null;
		if (usesDFS) { //use the # of nodes expanded by DFS as the hardness metric
			DepthFirstSearch(current);
			int mostCellsTraversed = cellsTraversed; //number of nodes expanded by original maze
			hardest = new PathNode[current.length][current.length];
			for (int i = 0; i < current.length; i++) {
				for (int j = 0; j < current.length; j++) {
					cellsTraversed = 0;
					resetMap(current); //allow the current hardest board to be traversed again
					current[i][j].isEmpty = !current[i][j].isEmpty; //change the occupation status of one node
					DepthFirstSearch(current);
					if (cellsTraversed > mostCellsTraversed) { //current child is the hardest child thus far
						//hardest  = deep copy of current
						mostCellsTraversed = cellsTraversed;
					}
					current[i][j].isEmpty = !current[i][j].isEmpty;
				} 
			}
		}else{
			
		}
		return hardest;
	}
	
	public static PathNode[][] getHardestMaze(PathNode[][] original, boolean usesDFS) {
		long startTime = System.nanoTime();
		//PathNode[][] hardestMaze = original;
		while ((System.nanoTime()/1000000000.0) < ((startTime)/1000000000.0)+1.0) { //while less than 1 second has elapsed
			//hardestMaze = helperFindHardest(hardestMaze, usesDFS);
			//resetVisited(hardestMaze);
		}
		return null;
	}
	

	public static void printMazeSolutionGUI(PathNode [][] map, PathNode goal, String algorithm) {
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
		
		while (goal != null) {
			cells[goal.row][goal.col].setBackground(Color.LIGHT_GRAY);
			goal = goal.prev;
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
		/*System.out.println("DFS Data: ");
		for (int i = 0; i < dfsData[0].length; i++) {
			System.out.println("p: " + dfsData[0][i] + " Solvability: " + dfsData[1][i]);
		}
		System.out.println("BFS Data: ");
		for (int i = 0; i < bfsData[0].length; i++) {
			System.out.println("p: " + bfsData[0][i] + " Solvability: " + bfsData[1][i]);
		}
		System.out.println("BD BFS Data: ");
		for (int i = 0; i < bfsData[0].length; i++) {
			System.out.println("p: " + bdbfsData[0][i] + " Solvability: " + bdbfsData[1][i]);
		}
		System.out.println("Euclid A* Data: ");
		for (int i = 0; i < dfsData[0].length; i++) {
			System.out.println("p: " + euclidData[0][i] + " Solvability: " + euclidData[1][i]);
		}
		System.out.println("Manhattan A* Data: ");
		for (int i = 0; i < manhattanData[0].length; i++) {
			System.out.println("p: " + manhattanData[0][i] + " Solvability: " + manhattanData[1][i]);
		}*/
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
	
	
	public static void dimTester() {
		for (int i = 10; i < 11; i++) {
			cellsTraversed = 0;
			maxFringeSize = 0;
			PathNode[][] testMap = generateMap(i, 0.25, false);
			long startTime = System.nanoTime();
			PathNode goal = bidirectionalBFS(testMap);
			long endTime = System.nanoTime();
			long executionTime = endTime - startTime;
			printMazeSolutionGUI(testMap, goal, "Bidirectional BFS");
			System.out.println("Time elapsed for BD-BFS to solve dim = " + i + " maze with p = 0.5: " + (executionTime/1000000000.0));
			System.out.println("Total number of cells traversed by BD-BFS with dim = " + i + " maze with p = 0.5: " + cellsTraversed);
			//System.out.println("Maximum fringe size using BD-DFS with dim = " + i + " maze with p = 0.5: " + maxFringeSize);
			printMazeSolutionGUI(testMap, goal, "BFS");
			System.out.println("Time elapsed for DFS to solve dim = " + i + " maze with p = 0.5: " + (executionTime/1000000000.0));
			System.out.println("Total number of cells traversed by DFS with dim = " + i + " maze with p = 0.5: " + cellsTraversed);
			System.out.println("Maximum fringe size using DFS with dim = " + i + " maze with p = 0.5: " + maxFringeSize);
			System.out.println();
			cellsTraversed = 0;
			maxFringeSize = 0;
			PathNode secondGoal = AStar(testMap, false);
			printMazeSolutionGUI(testMap, secondGoal, "A*-Manhattan");
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
				int numNbrFire = 0; //k = number of neighbors on fire
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
	
	// Allows for a single maze to be generated with density (p) set to the specified
	// parameter. Then, all of the algorithms for solving static mazes implemented in this
	// project are called on the same maze and their results displayed on the GUI.
	public static void pathsForAllAlgorithms(double p) {
		PathNode[][] testMap = generateMap(100, p, false);
		
		PathNode dfsGoal = DepthFirstSearch(testMap);
		printMazeSolutionGUI(testMap, dfsGoal, "DFS");
		resetMap(testMap);
		
		PathNode bfsGoal = BreadthFirstSearch(testMap);
		printMazeSolutionGUI(testMap, bfsGoal, "BFS");
		resetMap(testMap);
		
		PathNode bidirectBFSGoal = bidirectionalBFS(testMap);
		printMazeSolutionGUI(testMap, bidirectBFSGoal, "Bidirectional BFS");
		resetMap(testMap);
		
		PathNode euclidGoal = AStar(testMap, true);
		printMazeSolutionGUI(testMap, euclidGoal, "A*-Euclidean");
		resetMap(testMap);
		
		PathNode manhattanGoal = AStar(testMap, false);
		printMazeSolutionGUI(testMap, manhattanGoal, "A*-Manhattan");
	}
	
	public static void main(String[] args) {
		

		PathNode[][] fireMap = generateMap(100, 0.5, true);
		printMap(fireMap);
		System.out.println();
		PathNode fireGoal = fireMap[fireMap.length-1][fireMap.length-1];
		printMazeSolutionGUI(fireMap, fireGoal, "Adversarial Search");
		fireSpreads(fireMap, 1.0);

		dimTester();
		
		
		printMazeSolutionGUI(fireMap, fireGoal, "Adversarial Search"); 
		
		//plotMazeSolvability();
		pathsForAllAlgorithms(0.2);
		
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
	}

}
