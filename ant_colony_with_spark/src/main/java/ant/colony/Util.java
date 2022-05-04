package ant.colony;


public class Util {
	
	public static  double slope(Node n1, Node n2) {
		double dx = Math.abs(n1.x - n2.x);
		double dy = Math.abs(n1.y - n2.y);
		return dy / dx;
	}

}
