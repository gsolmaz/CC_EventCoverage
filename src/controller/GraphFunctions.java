package controller;

import java.util.ArrayList;
import java.util.List;

public class GraphFunctions {
	// commonly used simple graph functions
	public static boolean isGraphConnected(double[][] graphMatrix, boolean isDirected){
		// the method works for both directed and undirected graphs
		// returns true if the graph is connected, false otherwise
		List<Integer> connectedVertexList=new ArrayList<Integer>();
		
		if(!isDirected){
			// call the recursive exploration function
			connectedVertexList= findAndIncludeNeighbours(graphMatrix,0,connectedVertexList);

			if(connectedVertexList.size()==graphMatrix.length){
				return true;
			}
			else return false;
		}
		else{ // directed graph, look for all starting vertex
			for(int i=0;i<graphMatrix.length;i++){
				connectedVertexList= findAndIncludeNeighbours(graphMatrix,i,connectedVertexList);
				if(connectedVertexList.size()<graphMatrix.length){
					return false;
				}
			}
			return true;
		}
		
		
	}

	private static List<Integer> findAndIncludeNeighbours(double[][] graphMatrix, int startingVertex,
			List<Integer> connectedVertexList) {
		// add the starting vertex to the list initially
		connectedVertexList.add(startingVertex);
		for(int i=0;i<graphMatrix.length;i++){
			if(i==startingVertex) continue; // check if it is the same vertex
			if(graphMatrix[startingVertex][i]>0){ 
				// there is an edge between starting vertex and i
				// add vertex i to the list if it is not inside the list already and continue exploring
				if(!connectedVertexList.contains(i)){
					// recursion
					connectedVertexList = findAndIncludeNeighbours(graphMatrix, i, connectedVertexList);
				}
			}
		}
		return connectedVertexList;
	}
}
