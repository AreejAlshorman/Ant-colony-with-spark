package ant.colony;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Ant implements Serializable {
	private static final long serialVersionUID = 1L;

	public Set<Integer> visited = new HashSet<>();
	public List<Integer> trail = new LinkedList<>();

	public int id;

	public Ant(int id, Node node) {
		this.id = id;
		visitNode(node);
	}

	public Ant(Ant ant) {
		this.id = ant.id;
		this.visited.addAll(ant.visited);
		this.trail.addAll(ant.trail);
	}

	public void visitNode(Node node) {
		visited.add(node.id);
		trail.add(node.id);
	}

	public void visitNode(int nodeId) {
		visited.add(nodeId);
		trail.add(nodeId);
	}

	public boolean isVisited(Node node) {
		return visited.contains(node.id);
	}

	public boolean isVisited(int nodeId) {
		return visited.contains(nodeId);
	}

}
