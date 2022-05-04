package ant.colony;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.google.gson.Gson;

/**
 * Hello world!
 *
 */
public class AntColony {

	public static final int START_NODE = 7;
	public static final int GOAL_NODE = 5;

	private static double Q = 500;
	private static int maxIterations = 1000;
	private static double randomFactor = 0.01;
	private static double evaporation = 0.5;
	private static double alpha = 1;
	private static double beta = 5;

	private static JavaRDD<Node> graph;

	public static void main(String[] args) throws FileNotFoundException {

		Random random = new Random();

		System.setProperty("hadoop.home.dir", "C:\\hadoop");
		
		
		try (JavaSparkContext spark = new JavaSparkContext(
				new SparkConf().setAppName("Ant Colony").setMaster("local[2]"))) {

			// spark.setCheckpointDir("C:\\Users\\USER\\spark-workspace\\ant_colony_with_spark\\checkpoint");

			JavaRDD<String> points = spark.textFile("points.txt");

			final double contribution = Q / points.count();

			graph = points.map(p -> {
				Node node = Node.parseNode(p);
				if (node.id == START_NODE) {
					node.haveAnt = true;
				}
				return node;
			});

			try (PrintWriter out = new PrintWriter("out/points_0.txt")) {
				Gson gson = new Gson();
				graph.collect().forEach(n -> {
					out.println(gson.toJson(n));
				});
			}

			for (int i = 0; i < maxIterations; i++) {

				JavaRDD<String> lines = spark.textFile("out/points_" + i + ".txt");

				graph = lines.map(l -> Node.parseGsonNode(l));

				List<Node> ants =  graph.filter(n -> n.haveAnt).collect();

				ants.forEach(a -> {

					Double minCost = null;

					for (Edge edge : a.edges.values()) {
						if (minCost == null || edge.cost < minCost) {
							minCost = edge.cost;
						}
					}

					double cost = minCost != null ? minCost : 0;

					// System.out.println(a + ": minCost = " + cost);

					graph = graph.map(n -> {

						if (!n.edges.containsKey(a.id) && Util.slope(a, n) <= .1) {

							n.edges.put(a.id, new Edge(a, n, cost));

							// n.haveAnt = n.id != GOAL_NODE;
							n.haveAnt = true;
						}
						return n;
					});
				});

				graph = graph.map(n -> {
					if (n.haveAnt && n.edges.size() > 0) {
						int t = random.nextInt(n.edges.size());
						if (random.nextDouble() >= randomFactor) {
							double pheromone = 0.0;
							List<Double> numerators = new ArrayList<>();

							for (Edge Edge : n.edges.values()) {
//								double numerator = 1/Edge.cost;
								double numerator = Math.pow(Edge.trail, alpha) * Math.pow(1.0 / Edge.cost, beta);
								numerators.add(numerator);
								pheromone += numerator;
							}

							double probabilities[] = new double[numerators.size()];
							for (int j = 0; j < numerators.size(); j++) {
								probabilities[j] = numerators.get(j) / pheromone;
							}

							double r = random.nextDouble();
							double total = 0;
							for (int j = 0; j < probabilities.length; j++) {
								total += probabilities[j];
								if (total >= r) {
									t = j;
									break;
								}
							}
						}

						int j = 0;
						for (Edge edge : n.edges.values()) {
							edge.trail *= evaporation;
							if (j == t) {
								edge.trail += contribution;
							}
							j++;
						}
					}
					return n;
				});
				try (PrintWriter out = new PrintWriter("out/points_" + (i + 1) + ".txt")) {
					Gson gson = new Gson();
					graph.collect().forEach(n -> {
						out.println(gson.toJson(n));
					});
				}

				System.out.println("i = " + i);
			}
			print(graph);

			StringBuilder sb = new StringBuilder();
			Set<Integer> visited = new HashSet<>();

			Node node = graph.filter(n -> n.id == GOAL_NODE).first();
			if (node.edges.size() == 0) {
				System.out.println("path not found!");
				return;
			}
			
			int count = 0;
			int nodeId = GOAL_NODE;
			boolean finished = false;
			while (!finished) {
				finished = nodeId == START_NODE;
				visited.add(nodeId);
				++count;
				int nId = nodeId;
				node = graph.filter(n -> n.id == nId).first();
				sb.append(node).append(" <- ");
				if (!finished) {
					nodeId = getBestEdge(node.edges, visited);
				}
			}

			System.out.println(sb.toString());
			System.out.println(count);

		}
	}

	private static int getBestEdge(Map<Integer, Edge> edges, Set<Integer> visited) {
		Edge best = null;
		Integer bestId = null;
		for (int id : edges.keySet()) {
			if (!visited.contains(id)) {
				Edge edge = edges.get(id);
				if (best == null || edge.trail > best.trail) {
					best = edge;
					bestId = id;
				}
			}
		}
		assert bestId != null;
		return bestId;
	}

	static void print(JavaRDD<Node> graph) {
		List<Node> nodes = graph.collect();

		nodes.forEach(n -> {
			System.out.println(n);
		});

	}

}
