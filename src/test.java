
public class test {

	public static void main(String[] args) {
		PathNode[][] testMap = StaticGenAndSearch.generateMap(4, 0.5);
		StaticGenAndSearch.printMap(testMap);
		System.out.println();
		PathNode goal = StaticGenAndSearch.DepthFirstSearch(testMap);
		while (goal!= null) {
			System.out.println("Node: row - " + goal.row + " col - " + goal.col); 
			goal = goal.prev;
		}
		System.out.println("hey!");
	}

}
