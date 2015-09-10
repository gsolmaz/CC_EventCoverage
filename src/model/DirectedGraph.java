/**
 * 
 */
package model;


/**
 * @author Gurkan Solmaz
 * 		   Department of EECS - University of Central Florida
 * 		   Event Coverage - Summer 2013
 * 		   Advisor: Dr. Damla Turgut
 */

public class DirectedGraph {
	double[][] graphMatrix; // matrix with weight values of edges	
	double[][] edgeDistanceMatrix; // matrix with Euclidean distance edge weights
	int numberOfVertices;
	int numberOfEdges;
	public DirectedGraph(double[][] graphMatrix, int numberOfVertices) {
		super();
		this.graphMatrix = graphMatrix;
		this.numberOfVertices = numberOfVertices;
		this.edgeDistanceMatrix=graphMatrix;
	}
	
	public DirectedGraph(double[][] graphMatrix, int numberOfVertices, int numberOfEdges) {
		super();
		this.graphMatrix = graphMatrix;
		this.numberOfVertices = numberOfVertices;
		this.edgeDistanceMatrix=graphMatrix;
		this.numberOfEdges = numberOfEdges;
	}
	public double[][] getGraphMatrix() {
		return graphMatrix;
	}
	public int getNumberOfVertices() {
		return numberOfVertices;
	}
	public void setGraphMatrix(double[][] gMatrix) {
		this.graphMatrix = new double[numberOfVertices][numberOfVertices];
		this.graphMatrix = gMatrix;
	}

	public int getNumberOfEdges() {
		return numberOfEdges;
	}

}
