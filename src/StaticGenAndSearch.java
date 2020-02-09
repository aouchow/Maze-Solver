import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import javax.swing.*;
import java.awt.*;

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
		LinkedList<PathNode> fringeFromStart = new LinkedList<PathNode>();
		LinkedList<PathNode> fringeFromGoal = new LinkedList<PathNode>();
		
		//initially add the start and goals into the queues
		fringeFromStart.add(start);
		fringeFromGoal.add(goal);
		
		while (!fringeFromStart.isEmpty() && !fringeFromGoal.isEmpty()) {
			helperBFS (map, visitedFromStart, fringeFromStart);
			helperBFS (map, visitedFromGoal, fringeFromGoal);
			System.out.println("heyyy");
			
			//check for intersection
			for (int i = 0; i < map.length; i++) {
				for (int j = 0; j < map.length; j++) {
					if (visitedFromStart[i][j] && visitedFromGoal[i][j] == true) { //intersection found
						//need to reverse pointers for the visitedFromGoal
						System.out.println("heyyy");
						PathNode current = map[i][j];
						PathNode previous = null; //should not equal null!!!
						while (current !=null) {
							PathNode next = current.prev;
							current.prev = previous;
							current = next;
							previous = current;
						}
					}
				}
			}
		}
		return goal;
	}
	
	//helper method for bidirectional BFS
	public static void helperBFS (PathNode [][] map, boolean [][] visited, LinkedList <PathNode> fringe) {
		PathNode curr = fringe.getFirst();
		if (!visited [curr.row][curr.col]) {
			visited [curr.row][curr.col] = true;
			fringe = updateFringe(fringe, map, curr, visited); //updates fringe
		}
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
	public static void printMazeSolutionGUI(PathNode [][] map, PathNode goal) {
		JFrame maze = new JFrame("Maze with Dim = " + map.length + " Solved by A*");
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
		startLabel.setFont(new Font("Times New Roman", 1, 20));
		goalLabel.setFont(new Font("Times New Roman", 1, 20));
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
	public static void dimTester() {
		for (int i = 10; i < 11; i++) {
			cellsTraversed = 0;
			maxFringeSize = 0;
			PathNode[][] testMap = generateMap(i, 0.25, false);
			long startTime = System.nanoTime();
			PathNode goal = BreadthFirstSearch(testMap);
			long endTime = System.nanoTime();
			long executionTime = endTime - startTime;
			printMazeSolutionGUI(testMap, goal);
			System.out.println("Time elapsed for DFS to solve dim = " + i + " maze with p = 0.5: " + (executionTime/1000000000.0));
			System.out.println("Total number of cells traversed by DFS with dim = " + i + " maze with p = 0.5: " + cellsTraversed);
			System.out.println("Maximum fringe size using DFS with dim = " + i + " maze with p = 0.5: " + maxFringeSize);
			System.out.println();
			cellsTraversed = 0;
			maxFringeSize = 0;
			for (int g = 0; g < testMap.length; g++) {
				for (int j = 0; j < testMap.length; j++) {
					testMap[g][j].prev = null;
				}
			}
			PathNode secondGoal = AStar(testMap, false);
			printMazeSolutionGUI(testMap, secondGoal);
			System.out.println("Total number of cells traversed by A* with dim = " + i + " maze with p = 0.5: " + cellsTraversed);
			System.out.println("Maximum fringe size using A* with dim = " + i + " maze with p = 0.5: " + maxFringeSize);
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
	
	public static void main(String[] args) {
		PathNode[][] testMap = generateMap(4, 0.5, true);
		printMap(testMap);
		System.out.println();
		PathNode goal = testMap[testMap.length-1][testMap.length-1];
		printMazeSolutionGUI(testMap, goal);
		fireSpreads (testMap, 1.0);
		printMazeSolutionGUI(testMap, goal);
		/*PathNode goal = bidirectionalBFS(testMap);
		while (goal!= null) {
			System.out.println("Node: row - " + goal.row + " col - " + goal.col); 
			goal = goal.prev;
		}
		dimTester(); */
	}

}
