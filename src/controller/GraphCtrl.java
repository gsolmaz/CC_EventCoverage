/**
 * 
 */
package controller;


import io.FileInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import model.Attraction;
import model.DirectedGraph;
import model.Point;
import model.SimParam;
import model.ThemePark;
import cc.AdjMatrixGraph;
import cc.CC;
import cc.Queue;
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
public class GraphCtrl {

	SimParam simParam;
	ThemePark themePark;
	FileInput fileInput;
	List<List<Integer>> connectionCombinationList;
	
	public GraphCtrl(SimParam simParam, FileInput fileInput) {
		this.simParam=simParam;
		this.fileInput=fileInput;
		themePark = new ThemePark();
		connectionCombinationList = new ArrayList<List<Integer>>();
		createInitialGraphs();
		themePark.setAllPossibleConnectedSinkLocationCombinations(getConnectionCombinationList());
	
		assignInitialProbabilitiesToAttractions();
	}
	


	public List<List<Integer>> getConnectionCombinationList() {
		return connectionCombinationList;
	}



	public void setConnectionCombinationList(
			List<List<Integer>> connectionCombinationList) {
		this.connectionCombinationList = connectionCombinationList;
	}



	private void assignInitialProbabilitiesToAttractions() {
		
		if(simParam.isAttractionsReadFromFile()){
			double sum = 0;
			for(int i=0 ;i<fileInput.getNumberOfAttractions();i++){
				sum+=fileInput.getInitialAttractionWeightList().get(i);
			}
			for(int i=0 ;i<fileInput.getNumberOfAttractions();i++){
				double prob = fileInput.getInitialAttractionWeightList().get(i)/sum;
				// set the initial event probability
				themePark.getAttractionList().get(i).setEventProbability(prob);
			}
		}
		else{ // do not read from file, assign random probabilities to attractions
			List<Double> randomValueList = new ArrayList<Double>();
			double totalValue =0;
			for(int i=0;i<simParam.getNumberOfAttractions();i++){
				Random rand = new Random(); 
				double r = rand.nextDouble();
				randomValueList.add(r);
				totalValue += r;
			}
			// now assign the probabilities according to the random values of each attraction
			for(int i=0;i<simParam.getNumberOfAttractions();i++){
				double prob = randomValueList.get(i) / totalValue;
				// set the initial event probability
				themePark.getAttractionList().get(i).setEventProbability(prob);
			}
		}
	}

	private void createInitialGraphs() {
		if(simParam.getEdgeCreationType().equalsIgnoreCase("degree")){		
			while(true){
				DirectedGraph initialAttractionGraph=null;
				// create initial attractions with 0 event probability
				List<Attraction> attractionList  = null;
				if(!simParam.isAttractionsReadFromFile()){
					attractionList= createAttractionList();
				}
				else{
					attractionList= createAttractionListFromFile();
				}
				themePark.setAttractionList(attractionList);
				
				// create connection graph
				DirectedGraph connGraph=createConnectionGraph(attractionList);
				connectionCombinationList=findConnectedSubsets(connGraph);
				System.out.println("Number of candidate connected subsets: " + connectionCombinationList.size());
				
				if(connectionCombinationList.size()<5 ){ //&& MathFunctions.combination(simParam.getNumberOfAttractions(),simParam.getNumberOfSinks()) > 10 ){ //System.out.println("Trying again...");
					continue; // this is possible, try again, do not halt the program
				}
				
				//findConnectedComponents(connGraph);

				
			//	setAllPossibleConnectedCombinationsOfSinks(connGraph);
			
			
				//themePark.setConnectionGraph(connGraph);
				
				//List<Integer> vertices = findConnectionGraphVertexSet(connGraph);
				//themePark.setConnectionGraphVertexSet(vertices);
				
				// create attraction graph by edges with distances
				initialAttractionGraph = createUnweightedEdgesByNodeDegree(attractionList);
				if(!checkIfGraphConnected(initialAttractionGraph)){ // the graph is not connected, create a new one
					//System.out.println("Graph is not connected, try again !!");
					//System.out.println("Graph is not connected, trying again...");
					continue;
				}
				themePark.setDistanceGraph(initialAttractionGraph);
				DirectedGraph weightedGraph = generateEdgeWeights(initialAttractionGraph);
				themePark.setAttractionGraph(weightedGraph);	
				break;
			}	
		}
	}

	/*private List<Integer> findConnectionGraphVertexSet(DirectedGraph connGraph) {
		double[][] gMatrix = connGraph.getGraphMatrix();
		List<Integer> returnList= new ArrayList<Integer>();
		for(int i= 0;i<simParam.getNumberOfAttractions(); i++){ 
			// for each vertex
			for(int j=0;j<simParam.getNumberOfAttractions();j++){
				// look for neighbor vertices
				if(gMatrix[i][j]>0){
					returnList.add(i); 
					break; // go look for other vertices
				}
			}
		}
		return returnList;
			
	}*/



	private List<List<Integer>> findConnectedSubsets(DirectedGraph connGraph) {
		int[] array = new int[connGraph.getNumberOfVertices()];
		for(int i=0;i<connGraph.getNumberOfVertices();i++){
			array[i] = i;
		}
		
	    List<List<Integer>> subsetList = findSubsets(array);
	    List<List<Integer>> connectedSubsetList = new ArrayList<List<Integer>>();
	    for(int i=0; i<subsetList.size();i++){
			// check if the subset has exactly p elements or not
			List<Integer> subset =subsetList.get(i);
			if(subset.size()!= simParam.getNumberOfSinks()) continue;
			// the subset has exactly p elements, check if it is connected or not (it is connected if the number of connected components is 1)
			
			if(!simParam.isConnectivityContraintOn()){
				connectedSubsetList.add(subset);  // add all possible candidate with size p if it does not have to be connected
			}
			else if(simParam.isConnectivityContraintOn()){
				// if it should be connected, check if the subset consists of only 1 connected component (in other words, connected) or not
				DirectedGraph d = createSubsetGraph(subset,connGraph.getGraphMatrix());
				int numConComp = findNumberOfConnectedComponents(d);
				if(numConComp==1){ // this subset is connected
					connectedSubsetList.add(subset);
				}
			}
					}
		return connectedSubsetList;
		
	}
	
	
	private DirectedGraph createSubsetGraph(List<Integer> subset, double[][] graphMatrix ){
		int numVertices = subset.size();
		int numEdges =0;
		double[][] newMatrix = new double[numVertices][numVertices];
		for(int i=0;i<subset.size();i++){
			for(int j=0;j<subset.size();j++){
				newMatrix[i][j] = graphMatrix[subset.get(i)][subset.get(j)];
				if(newMatrix[i][j]>0){
					numEdges++;
				}
			}
		}
		return new DirectedGraph(newMatrix, numVertices,  numEdges);
	}

	private List<List<Integer>> findSubsets(int array[])
	{
		List<List<Integer>> returnList = new ArrayList<List<Integer>>();
	  int numOfSubsets = 1 << array.length; 

	  for(int i = 0; i < numOfSubsets; i++)
		 {
		    int pos = array.length - 1;
		   int bitmask = i;
	
		 //  System.out.print("{");
		   List<Integer> subset = new ArrayList<Integer>();
		   while(bitmask > 0)
		   {
		    if((bitmask & 1) == 1)
		    	subset.add(array[pos]);
		     //System.out.print(array[pos]+",");
		    	bitmask >>= 1;
		    	pos--;
		   }
		   //System.out.print("}");
		   returnList.add(subset);
		 }
	  return returnList;
	}

	private boolean checkIfGraphConnected(DirectedGraph initialAttractionGraph) {
		Graph g = createDijkstraGraph(initialAttractionGraph.getGraphMatrix());
/*		for(int i=0;i<simParam.getNumberOfAttractions();i++){
			for(int j=0;j<simParam.getNumberOfAttractions();j++){
				String substring = initialAttractionGraph.getGraphMatrix()[i][j] +"";
				substring = substring.substring(0,1);
				System.out.print(substring + " " );
			}
			System.out.println();
		}*/
		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(g);
		// find shortest path among all possible paths and return the min path (time)
		
		for(int i=0;i<initialAttractionGraph.getNumberOfVertices();i++){
			dijkstra.execute(g.getVertexes().get(i)); // execute for source

			for(int j=0;j<initialAttractionGraph.getNumberOfVertices();j++){
				if(i==j) continue;
				LinkedList<Vertex> path = dijkstra.getPath(g.getVertexes().get(j)); // find the path to the target (event)
				double tmp = findValueOfPath(path, initialAttractionGraph.getGraphMatrix());
				if(tmp==-1){ // there exists no path between an attraction to another
					return false;
				}
			}
		}
			
	
		return true;
		
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


	private List<Attraction> createAttractionListFromFile() {
		List<Attraction> returnList = new ArrayList<Attraction>();
		for(int i=0; i<fileInput.getNumberOfAttractions();i++){
			Point p = fileInput.getAttractionPositionList().get(i);
			Attraction a = new Attraction(i, p, fileInput.getInitialAttractionWeightList().get(i), false);	
			returnList.add(a);
		}
		return returnList;
	}



//
//	private void setAllPossibleConnectedCombinationsOfSinks(
//			DirectedGraph connGraph) {
//		double[][] gMatrix = connGraph.getGraphMatrix();
//	
//		List<Integer> tmpList = new ArrayList<Integer>();
//		exploreAllCombinations( tmpList, gMatrix);
//
//	}



/*	private void exploreAllCombinations(List<Integer> currentList, double[][] gMatrix) {
		// explore from the current state (tmpList)
		// take the last one to explore
		
		int currentNode = currentList.get(currentList.size()-1);
		
		for(int j=0;j<gMatrix.length;j++){
			if(currentNode==j || !(gMatrix[currentNode][j]>0)) continue; // either the same index or the distance is more than connection range, don`t add this to the current list
			if(currentList.contains(j)) continue; // the current list already contains this vertex
			// add vertex to the current list
			currentList.add(j);
			if(currentList.size() == simParam.getNumberOfSinks()){ // a combination is found now add to the main list, if it does not already exist
				// sort first to make comparison 
				List<Integer> sortedList = new ArrayList<Integer>();
				sortedList.addAll(currentList);
				Collections.sort(sortedList);
				if(!connectionCombinationList.contains(sortedList)){
					// you can add the combination to the main list 
					connectionCombinationList.add(sortedList);
					// search for other possibilities
					currentList.remove(simParam.getNumberOfSinks()-1);
					exploreAllCombinations(currentList, gMatrix);
				}
				else{ // the combination is already contained, try the new one
					currentList.remove(simParam.getNumberOfSinks()-1);
				}
			}
			else{ // go and explore the other sinks recursively
				exploreAllCombinations(currentList, gMatrix);
			}
		}
	}
*/
	
	private void findConnectedComponents(DirectedGraph d){
		
        AdjMatrixGraph G = new AdjMatrixGraph(d.getNumberOfVertices(), d.getNumberOfEdges()   , d.getGraphMatrix());
        CC cc = new CC(G);
        // number of connected components
        int M = cc.count();
        
        // compute list of vertices in each connected component
        Queue<Integer>[] components = (Queue<Integer>[]) new Queue[M];
        for (int i = 0; i < M; i++) {
            components[i] = new Queue<Integer>();
        }
        for (int v = 0; v < G.V(); v++) {
            components[cc.id(v)].enqueue(v);
        }
        
        for (int i = 0; i < M; i++) {
            for (int v : components[i]) {
                System.out.print(v + " ");
            }
            System.out.println();
        }

	}
	
	private int findNumberOfConnectedComponents(DirectedGraph d){
		
        AdjMatrixGraph G = new AdjMatrixGraph(d.getNumberOfVertices(), d.getNumberOfEdges()   , d.getGraphMatrix());
        CC cc = new CC(G);
        // number of connected components
        int M = cc.count();
        
        return M;

	}
	
	

	private void exploreAllCombinations(List<Integer> currentList, double[][] gMatrix) {
		// explore from the current state (tmpList)
		
		if(currentList.size() == simParam.getNumberOfSinks()){ // base case, halting condition
			// sort first to make comparison 
			List<Integer> sortedList = new ArrayList<Integer>();
			sortedList.addAll(currentList);
			Collections.sort(sortedList);
			if(!connectionCombinationList.contains(sortedList)){
				// you can add the combination to the main list 
				connectionCombinationList.add(sortedList);
				return;
			}
			else{ // the combination is already contained, there is no way out
				return;
			}
		}
		// the size of the current list is less than the number of sinks
		for(int i=0;i<gMatrix.length;i++){
			if(currentList.contains(i)) continue; // current list already contains this vertex, do not consider it, go for other vertices
			if(currentList.size()==0){ // this is the initial vertex of the list, just add it to the list
				currentList.add(i);
				exploreAllCombinations(currentList, gMatrix); // go ahead for new vertices
				return; // we are done with this vertex
			}
			// the current list does not contain this element, try if it is in communication range
			boolean isInRange=false;
			for(int j=0;j<currentList.size();j++){
				if(gMatrix[currentList.get(j)][i]>0){ // the new vertex i is in com. range of vertex currentList.get(j) which was already in the list
					isInRange = true;
					break; // we can break because we found that it is in range
				}
			}
			if(isInRange){ // add the new vertex i and go ahead
				 currentList.add(i); 
				 exploreAllCombinations(currentList, gMatrix);
				 return; // we are done
			}
			
			// else do nothing, this vertex is not in the range and cannot be added, try the other possible vertices
			return;
		}
		
	}


	private DirectedGraph generateEdgeWeights(
			DirectedGraph initialAttractionGraph) {
			int n = simParam.getNumberOfAttractions();
			double[][] newMatrix = new double[n][n];
			double[][] gMatrix = initialAttractionGraph.getGraphMatrix();
			for(int i=0;i<n; i++){
				for(int j=0;j<n;j++){
					if(i==j || gMatrix[i][j] == 0) continue;
					Random r =new Random();
					double d = r.nextDouble();
					d = simParam.getMaxEdgeWeightDifference() * d;
					newMatrix[i][j] = gMatrix[i][j] + (gMatrix[i][j] * d); // at least the same result stays, at most 3 times increase 
				}
			}
		DirectedGraph returnGraph = new DirectedGraph(newMatrix, n);
		return returnGraph;
	}

	private DirectedGraph createUnweightedEdgesByNodeDegree(List<Attraction> attractionList) {
		int numberOfVertices = attractionList.size();
		double[][] graphMatrix = new double[numberOfVertices][numberOfVertices];
		
		int[] currentDegrees = new int[numberOfVertices];
		for(int i=0;i<numberOfVertices;i++){
			currentDegrees[i]=0;
		}
		
		for(int i=0;i<attractionList.size();i++){
			if(currentDegrees[i]==simParam.getGraphNodeDegree()) continue;
			Attraction a = attractionList.get(i);
			// find the nearest neighbors
			List<Integer> currentNeighborIndexList= new ArrayList<Integer>();
			
			while(true){
				double currentLowestDistance=9999999;
				int tmpNeighborIndex= -1;
				for(int k=0;k<attractionList.size();k++){
						// check if same attraction
						if(k==i) continue; 
						// check if the edge is already added for neighbor
						if(graphMatrix[i][k]!= 0) continue;
					
						Attraction b = attractionList.get(k);
						double distance = MathFunctions.findDistanceBetweenTwoPoints(a.getLocation(),b.getLocation());
						if(distance < currentLowestDistance){
							tmpNeighborIndex = k;
							currentLowestDistance = distance;
						}
						else if(k==attractionList.size()-1 && tmpNeighborIndex==-1){
							System.out.println("HERE IS THE ERROR");
						}
				}
				if(tmpNeighborIndex == -1){
					System.out.println("ERROR IN NODE DEGREES!!!"); System.exit(0); 
					break;
				}
				graphMatrix[i][tmpNeighborIndex] = currentLowestDistance;
				currentDegrees[i]++; 
			//	graphMatrix[tmpNeighborIndex][i] = currentLowestDistance;
			//	currentDegrees[tmpNeighborIndex]++;
			//	for(int x=0;x<numberOfVertices;x++){
			//	System.out.print(currentDegrees[x]+" ");
			//	} System.out.println();
				if(currentDegrees[i]==simParam.getGraphNodeDegree()){ 
					break;
				}
			}	
		}
		return new DirectedGraph(graphMatrix, numberOfVertices);
	}
	
/*	private DirectedGraph createUnweightedEdgesByNodeDegree(List<Attraction> attractionList) {
		int numberOfVertices = attractionList.size();
		double[][] graphMatrix = new double[numberOfVertices][numberOfVertices];
		
		int[] currentDegrees = new int[numberOfVertices];
		for(int i=0;i<numberOfVertices;i++){
			currentDegrees[i]=0;
		}
		
		for(int i=0;i<attractionList.size();i++){
			if(currentDegrees[i]==simParam.getGraphNodeDegree()) continue;
			Attraction a = attractionList.get(i);
			// find the nearest neighbors
			List<Integer> currentNeighborIndexList= new ArrayList<Integer>();
			
			while(true){
				double currentLowestDistance=9999999;
				int tmpNeighborIndex= -1;
				for(int k=0;k<attractionList.size();k++){
						// check if same attraction
						if(k==i) continue; 
						// check if the edge is already added for neighbor
						if(graphMatrix[i][k]!= 0) continue;
						if(currentDegrees[k]==simParam.getGraphNodeDegree()) continue;
					
						Attraction b = attractionList.get(k);
						double distance = MathFunctions.findDistanceBetweenTwoPoints(a.getLocation(),b.getLocation());
						if(distance < currentLowestDistance){
							tmpNeighborIndex = k;
							currentLowestDistance = distance;
						}
						else if(k==attractionList.size()-1 && tmpNeighborIndex==-1){
							System.out.println("HERE IS THE ERROR");
						}
				}
				if(tmpNeighborIndex == -1){
					//System.out.println("ERROR IN NODE DEGREES!!!"); System.exit(0); 
					break;
				}
				graphMatrix[i][tmpNeighborIndex] = currentLowestDistance;
				currentDegrees[i]++; 
				graphMatrix[tmpNeighborIndex][i] = currentLowestDistance;
				currentDegrees[tmpNeighborIndex]++;
				for(int x=0;x<numberOfVertices;x++){
				System.out.print(currentDegrees[x]+" ");
				} System.out.println(); 
				if(currentDegrees[i]==simParam.getGraphNodeDegree()){ 
					break;
				}
			}	
		}
		return new DirectedGraph(graphMatrix, numberOfVertices);
	}*/

	private DirectedGraph createConnectionGraph(List<Attraction> attractionList) {
		// create the baseline graph for connections of mobile sinks from attraction to attraction
		int numberOfVertices = attractionList.size();
		int numberOfEdges= 0;
		double[][] graphMatrix = new double[numberOfVertices][numberOfVertices];
		for(int i=0;i<attractionList.size();i++){
			Attraction a = attractionList.get(i);
			for(int j=0;j<attractionList.size();j++){
				if(i==j) continue;
				Attraction b = attractionList.get(j);
				double distance = MathFunctions.findDistanceBetweenTwoPoints(a.getLocation(), b.getLocation());
				if(distance<=simParam.getSinkCommunicationRange()){
					numberOfEdges++;
					graphMatrix[i][j] = distance;
					graphMatrix[j][i] = distance;
				}
			}
		}
		return new DirectedGraph(graphMatrix, numberOfVertices, numberOfEdges);
		
	}

	private List<Attraction> createAttractionList() {
		List<Attraction> returnList = new ArrayList<Attraction>();
		for(int i=0; i<simParam.getNumberOfAttractions();i++){
			Attraction a = createAttraction(i, returnList);
			if(a==null){ // try to create the attraction again
				i--;
			}
			else{ // attraction is created, we can add this attraction to the list
				returnList.add(a);
			}
		}
		return returnList;	
	}

	private Attraction createAttraction(int index, List<Attraction> currentList) {
		Random r= new Random();
		double x = r.nextDouble() * simParam.getTerrainDimLength(); 
		double y= r.nextDouble() * simParam.getTerrainDimLength(); ;	
		Point p = new Point(x,y);
		
		if(index !=0){ // if index is 0, create the attraction anyways, if index is not zero, check if the attraction is connected or not
			boolean flagConnected= false;
			for(int i=0;i<currentList.size();i++){
				if(MathFunctions.findDistanceBetweenTwoPoints(p, currentList.get(i).getLocation()) <= simParam.getAttractionMaxDistance() && MathFunctions.findDistanceBetweenTwoPoints(p, currentList.get(i).getLocation())>=simParam.getAttractionMinDistance()){
					//  create the attraction, it`s connected to the current attractions
					flagConnected=true;
					break;
				}
			
				// else continue and try for the other attractions
			}
			if(flagConnected == false ){ // do not create this attraction
				return null; 
			}
		}
		
		Attraction a = new Attraction(index, p, 0, false);
		
		return a;
	}

	public ThemePark getThemePark() {
		return themePark;
	}


}
