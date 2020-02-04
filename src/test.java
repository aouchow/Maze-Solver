
public class test {
	public static void testJava(int[]x) {
		x[0] = 45;
	}
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
		int []x = new int [4];
		testJava(x);
		System.out.println(x[0]);
	}

}
