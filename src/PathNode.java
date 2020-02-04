
public class PathNode {
	int row;
	int col;
	int distance;
	boolean isEmpty; //true if Node is safe to move to
	PathNode prev;
	
	public PathNode(int row, int col, boolean isEmpty){
		this.row = row;
		this.col = col;
		this.distance = 0;
		this.isEmpty = isEmpty;
		this.prev = null;
	}
}
