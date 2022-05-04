package ant.colony;

import java.io.Serializable;

public class Edge implements Serializable {
	private static final long serialVersionUID = 1L;

	
	public final double cost;
	public double trail = 1.0;
	

	public Edge(Node source, Node target, double cost) {
		this.cost = source.distance(target) + cost;
	}
	
	@Override
	public String toString() {
		return ""+ String.format("%.2f", trail);
	}
//
//	@Override
//	public int hashCode() {
//		return Integer.hashCode(target);
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (obj != null && obj instanceof Edge) {
//			return ((Edge) obj).target == target;
//		}
//		return false;
//	}
//
//	@Override
//	public String toString() {
//
//		return target + "(" + String.format("%.2f", cost) + ")";
//	}
}
