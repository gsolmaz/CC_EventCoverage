/**
 * 
 */
package controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.DirectedGraph;
import dijkstra.DijkstraAlgorithm;
import dijkstra.Edge;
import dijkstra.Graph;
import dijkstra.Vertex;

/**
 * @author Gurkan Solmaz
 * 		   Department of EECS - University of Central Florida
 * 		   Event Coverage - Summer 2013
 * 		   Advisor: Dr. Damla Turgut
 */

public class ShortestPathFinder {
	String pathFinderType;
	
	
	public ShortestPathFinder(String pathFinderType) {
		super();
		this.pathFinderType = pathFinderType;
	}
	
	public double findBestSink(DirectedGraph directedGraph, int attractionIndex, List<Integer> mobSinkAttractionIndices){
		double shortestPath = 0;

		if(pathFinderType.equalsIgnoreCase("Shortest Path")){
			shortestPath = findDijsktraShortestPath(directedGraph.getGraphMatrix(), attractionIndex,mobSinkAttractionIndices);
		}
		else if(pathFinderType.equalsIgnoreCase("Closest Sink")){
//			shortestPath = findClosestSinkShortestPath(distanceGraph.getGraphMatrix(), directedGraph.getGraphMatrix());

		}
		else if(pathFinderType.equalsIgnoreCase("Random Sink")){
	//		shortestPath = findRandomShortestPath(distanceGraph.getGraphMatrix(), directedGraph.getGraphMatrix(), numberOfQueues);
		}
	
	
		// if the type is not according to dynamic edge weights, find the time according to exact weights but using the distance path
		return shortestPath;
		
	}

	/*private double findRandomShortestPath(double[][] distGraphMatrix,
			double[][] dynamicGraphMatrix, int numberOfQueues) {
		Graph g = createDijkstraGraph(distGraphMatrix);
		
		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(g);
		// find shortest path from a random mobile sink
		double returnValue;
		int numberOfMobileSinks = distGraphMatrix.length - (numberOfQueues + 1);
		
		Random r = new Random();
		int selectedSink = r.nextInt(numberOfMobileSinks);
		selectedSink += numberOfQueues;
		
		dijkstra.execute(g.getVertexes().get(selectedSink)); // execute for source 
		LinkedList<Vertex> path = dijkstra.getPath(g.getVertexes().get(distGraphMatrix.length -1)); // find the path to the target (event)
		returnValue = findValueOfPath(path, dynamicGraphMatrix);
		this.lastTravelDistance = findValueOfPath(path, distGraphMatrix);

		return returnValue;

	}*/

	


/*	private double findClosestSinkShortestPath(double[][] distGraphMatrix,
			double[][] dynamicGraphMatrix, int numberOfQueues, int closestSinkIndex) {
		// find shortest path from the closest sink
		Graph g = createDijkstraGraph(distGraphMatrix);
		
		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(g);

		double returnValue;
		
		int selectedSink = closestSinkIndex + numberOfQueues;
		
		dijkstra.execute(g.getVertexes().get(selectedSink)); // execute for source 
		LinkedList<Vertex> path = dijkstra.getPath(g.getVertexes().get(distGraphMatrix.length -1)); // find the path to the target (event)
		if(path == null){
			System.out.println("Could not find a path to travel");
		}
		returnValue = findValueOfPath(path, dynamicGraphMatrix);
		if(returnValue == 0){
			System.out.println("Error in calculating return value");
		}
		this.lastTravelDistance = findValueOfPath(path, distGraphMatrix);

		return returnValue;
		
		
	}*/

	private double findDijsktraShortestPath(double[][] dynamicGraphMatrix, int targetIndex, List<Integer> candidateIndices) {
		Graph g = createDijkstraGraph(dynamicGraphMatrix);
		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(g);
		// find shortest path among all possible paths and return the min path (time)
		double minValue = 10000;
		int selectedSinkIndex=-1;
		for(int i=0;i<candidateIndices.size();i++){// for each sink
		
			dijkstra.execute(g.getVertexes().get(candidateIndices.get(i))); // execute for source
			LinkedList<Vertex> path = dijkstra.getPath(g.getVertexes().get(targetIndex)); // find the path to the target (event)
			double tmp = findValueOfPath(path, dynamicGraphMatrix);
			if(tmp!=-1){
				if(tmp<minValue){
					minValue = tmp;
					selectedSinkIndex=candidateIndices.get(i);
				}
			}
		}
		return minValue; // return the value of the shortest path
	}
	private double findValueOfPath(LinkedList<Vertex> path, double[][] graphMatrix) {
		double returnValue =0;
		// find the total distance of a path
		if(path == null){
			return -1;
		}
		
		for(int i=0;i<path.size()-1; i++){
			Vertex from = path.get(i);
			Vertex to = path.get(i+1);
			returnValue += graphMatrix[Integer.parseInt(from.getId())][Integer.parseInt(to.getId())];		
		}
		
		return returnValue;
	}

	private Graph createDijkstraGraph(double[][] graphMatrix) {
		
		List<Vertex> vertexes = new ArrayList<Vertex>();
		List<Edge> edges = new ArrayList<Edge>();
		int edgeIndex=0;
		for(int i=0;i<graphMatrix.length; i++){
			Vertex source = new Vertex(i+"", i+"");
			vertexes.add(source);
			for(int j=0;j<graphMatrix.length; j++){
				Vertex dest = new Vertex(j+"", j+"");
				if(i!=j && graphMatrix[i][j]!=0){ // ==> if there exists an edge
					Edge e = new Edge(edgeIndex+"", source, dest, graphMatrix[i][j]);
					edgeIndex++;
					edges.add(e);
				}
			}
		}
		Graph g = new Graph(vertexes, edges);
		return g;
	}


}
