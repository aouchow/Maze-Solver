
public class PathNode implements Comparable<PathNode>{
	int row;
	int col;
	double distanceEst;
	boolean isEmpty; //true if Node is safe to move to
	boolean isOnFire;
	PathNode prev;
	
	public PathNode(int row, int col, boolean isEmpty, boolean isOnFire){
		this.row = row;
		this.col = col;
		this.distanceEst = 0;
		this.isEmpty = isEmpty;
		this.isOnFire = isOnFire;
		this.prev = null;
	}

	@Override
	public int compareTo(PathNode o) { //allows comparisons of nodes based on estimated distance
		return Double.compare(this.distanceEst, o.distanceEst);
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {

	    return super.clone();
	}
}
