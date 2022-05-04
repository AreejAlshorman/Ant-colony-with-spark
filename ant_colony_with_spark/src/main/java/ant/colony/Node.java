package ant.colony;

import java.awt.Point;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Node extends Point implements Serializable {
	private static final long serialVersionUID = 1L;

	public int id;

	// public Set<Ant> ants = new HashSet<>();
	public Map<Integer, Edge> edges = new HashMap<>();

	public boolean haveAnt;

	public Node() {

	}

	// public Node(Node node) {
	// super(node);
	// this.id = node.id;
	// ants.addAll(node.ants);
	// edges.addAll(node.edges);
	// }
	//
	// public Node(int id) {
	// this.id = id;
	// }
	//
	public static Node parseNode(String line) {
		Node node = new Node();
		String values[] = line.split(",");
		node.id = Integer.parseInt(values[0]);
		node.x = Integer.parseInt(values[1]);
		node.y = Integer.parseInt(values[2]);
		return node;
	}
	//
	// @Override
	// public boolean equals(Object obj) {
	// if (obj != null && obj instanceof Node) {
	// return ((Node) obj).id == id;
	// }
	// return false;
	// }

	@Override
	public int hashCode() {
		return Integer.hashCode(id);
	}

	@Override
	public String toString() {
		// return id + ": " + edges.keySet() + " / " + haveAnt;
		return "(" + x + ", " + y + ")";
	}

	public static Node parseGsonNode(String l) {
		Gson gson = new Gson();

		return gson.fromJson(l, Node.class);
	}

}
