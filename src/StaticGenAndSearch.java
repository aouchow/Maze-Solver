import java.util.LinkedList;
import java.util.PriorityQueue;


public class StaticGenAndSearch {
	public static PathNode[][] generateMap(int dim, double p){
		PathNode[][] map = new PathNode[dim][dim];
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				double random = Math.random();
				while (random == 0) { //0 is not an acceptable value, assign a new #
					random = Math.random();
				}
				map[i][j] = new PathNode(i, j, p <= random); //probability(p <= random) == probability a cell is empty
					
			}
		}
		map[0][0].isEmpty = true; //start is reachable
		map[dim-1][dim-1].isEmpty = true; //goal is reachable
		return map;
	}
	
	public static PriorityQueue<PathNode> updateFringeWithHeuristic(PriorityQueue<PathNode> fringe, PathNode[][] map, PathNode curr, boolean[][] visited, boolean usesEuclidean, int[][]distance){
		int rowIndex = curr.row;
		int colIndex = curr.col;
		if (rowIndex+1 < map.length && map[rowIndex+1][colIndex].isEmpty && map[rowIndex+1][colIndex].prev == null && !visited[rowIndex+1][colIndex]) { //moving down is a non-repeated, viable choice (node not already in fringe or visited)
			map[rowIndex+1][colIndex].prev = curr;
			distance[rowIndex+1][colIndex] = distance[rowIndex+1][colIndex]+1; //distance to child = distance to parent + one additional operation
			if (usesEuclidean) {
				map[rowIndex+1][colIndex].distanceEst = distance[rowIndex+1][colIndex] + Math.sqrt((Math.pow((rowIndex+1)-(map.length-1), 2)) + (Math.pow((colIndex)-(map.length-1), 2))); //add Euclidean distance heuristic to goal to the current distance from start to node
			}else{
				map[rowIndex+1][colIndex].distanceEst = distance[rowIndex+1][colIndex] + Math.abs(((rowIndex+1)-(map.length-1)) + (colIndex-(map.length-1))); //add Manhattan distance heuristic to the current distance from start to node
			}
			fringe.add(map[rowIndex+1][colIndex]);
		}
		if (colIndex+1 < map.length && map[rowIndex][colIndex+1].isEmpty && map[rowIndex][colIndex+1].prev == null && !visited[rowIndex][colIndex+1]) { //moving right is a non-repeated, viable choice
			map[rowIndex][colIndex+1].prev = curr;
			distance[rowIndex][colIndex+1] = distance[rowIndex][colIndex+1]+1; //distance to child = distance to parent + one additional operation
			if (usesEuclidean) {
				map[rowIndex][colIndex+1].distanceEst = distance[rowIndex][colIndex+1] + Math.sqrt((Math.pow((rowIndex)-(map.length-1), 2)) + (Math.pow((colIndex+1)-(map.length-1), 2))); //add Euclidean distance heuristic to goal to the current distance from start to node
			}else{
				map[rowIndex][colIndex+1].distanceEst = distance[rowIndex][colIndex+1] + Math.abs(((rowIndex)-(map.length-1)) + ((colIndex+1)-(map.length-1))); //add Manhattan distance heuristic to the current distance from start to node
			}
			fringe.add(map[rowIndex][colIndex+1]);
		}
		if (rowIndex-1 >= 0 && map[rowIndex-1][colIndex].isEmpty && map[rowIndex-1][colIndex].prev == null && !visited[rowIndex-1][colIndex]) { //moving up is a non-repeated, viable choice
			map[rowIndex-1][colIndex].prev = curr;
			distance[rowIndex-1][colIndex] = distance[rowIndex-1][colIndex]+1; //distance to child = distance to parent + one additional operation
			if (usesEuclidean) {//Euclidean distance is the heuristic
				map[rowIndex-1][colIndex].distanceEst = distance[rowIndex-1][colIndex] + Math.sqrt((Math.pow((rowIndex-1)-(map.length-1), 2)) + (Math.pow((colIndex)-(map.length-1), 2))); //add Euclidean distance heuristic to goal to the current distance from start to node
			}else{ //use Manhattan distance
				map[rowIndex-1][colIndex].distanceEst = distance[rowIndex-1][colIndex] + Math.abs(((rowIndex-1)-(map.length-1)) + (colIndex-(map.length-1))); //add Manhattan distance heuristic to the current distance from start to node
			}
			fringe.add(map[rowIndex-1][colIndex]);
		}
		if (colIndex-1 >= 0 && map[rowIndex][colIndex-1].isEmpty && map[rowIndex][colIndex-1].prev == null && !visited[rowIndex][colIndex-1]) { //moving left is a non-repeated, viable choice
			map[rowIndex][colIndex-1].prev = curr;
			distance[rowIndex][colIndex-1] = distance[rowIndex][colIndex-1]+1; //distance to child = distance to parent + one additional operation
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
			fringe.add(map[rowIndex][colIndex+1]);
			map[rowIndex][colIndex+1].prev = curr;
		}
		if (rowIndex-1 >= 0 && map[rowIndex-1][colIndex].isEmpty && map[rowIndex-1][colIndex].prev == null && !visited[rowIndex-1][colIndex]) { //moving up is a non-repeated, viable choice
			fringe.add(map[rowIndex-1][colIndex]);
			map[rowIndex-1][colIndex].prev = curr;
		}
		if (colIndex-1 >= 0 && map[rowIndex][colIndex-1].isEmpty && map[rowIndex][colIndex-1].prev == null && !visited[rowIndex][colIndex-1]) { //moving left is a non-repeated, viable choice
			map[rowIndex][colIndex-1].prev = curr;
			fringe.add(map[rowIndex][colIndex-1]);
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
		while (!fringe.isEmpty()) {
			PathNode curr = fringe.getLast();
			fringe.removeLast();
			if (visited[curr.row][curr.col]) {
				System.out.println("Found a node we've seen before: " + curr.row + " " + curr.col);
				continue;
			} 
			visited[curr.row][curr.col] = true; //mark node as visited
			if (curr.equals(map[map.length-1][map.length-1])) { //curr is the goal state
				return curr;
			}else{
				fringe = updateFringe(fringe, map, curr, visited); //updates fringe
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
		while (!fringe.isEmpty()) {
			PathNode curr = fringe.getFirst();
			fringe.remove(); //removes first element from list
			if (visited[curr.row][curr.col]) continue;
			visited[curr.row][curr.col] = true; //mark node as visited
			if (curr.equals(map[map.length-1][map.length-1])) { //curr is the goal state
				return curr;
			}else{
				fringe = updateFringe(fringe, map, curr, visited); //updates fringe
			}
		}
		return null;
	}
	
	public static PathNode AStar(PathNode[][]map, boolean usesEuclidean) {
		if (map == null || map[0] == null || map[0].length == 0) return null; //map isn't constructed in a valid way
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
		map[0][0].prev = null;
		map[0][0].distanceEst = 0;
		while (!fringe.isEmpty()) {
			PathNode curr = fringe.poll();
			if (curr.equals(map[map.length-1][map.length-1])) {
				return curr;
			}else{
				fringe = updateFringeWithHeuristic(fringe, map, curr, visited, usesEuclidean, distance); 
			}
		}
		return null;
	}
	
	public static void printMap(PathNode[][]map) {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				System.out.print("(" + map[i][j].row + "," + map[i][j].col + ") ");
				if (map[i][j].isEmpty) {
					System.out.print("free");
				}else {
					System.out.print("occupied");
				}
				System.out.print("\t");
			}
			System.out.println();
		}
	}
	public static void main(String[] args) {
		PathNode[][] testMap = generateMap(4, 0.5);
		printMap(testMap);
		System.out.println();
		PathNode goal = AStar(testMap, true);
		while (goal!= null) {
			System.out.println("Node: row - " + goal.row + " col - " + goal.col); 
			goal = goal.prev;
		}
	}

}
