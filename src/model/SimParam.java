package model;

/**
 * @author Gurkan Solmaz
 * 		   Department of EECS - University of Central Florida
 * 		   Event Coverage - Summer 2013
 * 		   Advisor: Dr. Damla Turgut
 */

public class SimParam {
	
	private void setInputs() {

		this.isVisualizerOn = false;
		this.isKeyboardInputOn = false;		
	
		this.numberOfExperiments=100;
		this.startNumberOfSinks=7;
		this.endNumberOfSinks = 7;     // up to 5 sinks (1,2,3,4,5)
		this.sinkSpeed = 1;
		
		this.mobileSinkPositionUpdateTime =30*60; // update in half an hour
		this.isConnectivityContraintOn=true;

		this.sinkPosStrategy = SinkPositioning.PMedian;
		this.eventCreationType= EventCreationType.Random; // "Random", "Biased", "Specific" 
		this.numberOfEvents = 100; // 600 events   simTime/numbeOfEvents =  360000/600=600 (one event in ten minutes
		this.eventMinActiveTime = 200;
		this.eventMaxActiveTime = 600;
		
		this.sinkCommunicationRange = 100;
		this.attractionMaxDistance = 250;
		this.attractionMinDistance= 50;
	
		this.numberOfSpecificAttractionForEvents = 5;
		this.graphNodeDegree = 4; // 4
		//this.graphDensity = 0.7;
		this.adaptiveWeightChangeRate = 0.01; 	// adaptive lambda
		this.edgeWeightChangeRate = 0.20;
		this.maxEdgeWeightDifference = 4; // how many times more ? 1 time (*2), 2 times (*3) maximum 
		this.isAttractionsReadFromFile = false;
		
		if(!this.isAttractionsReadFromFile){ // otherwise, these are stated in the input file
			this.numberOfAttractions = 15; // 20  - for experiments
			this.terrainDimLength = 500;  //500  - used for experiments
		}
		
		this.edgeCreationType = "Degree"; 			// Degree or Density 
	
		this.simulationTime = 36000; // 10 hours 
	}


	// Simulation settings
	double sinkCommunicationRange;
	double attractionMaxDistance;
	double attractionMinDistance;
	int startNumberOfSinks;
	int endNumberOfSinks;
	int numberOfSinks; // current number of sinks

	double sinkSpeed;
	SinkPositioning sinkPosStrategy;
	double mobileSinkPositionUpdateTime;

	EventCreationType eventCreationType;
	int numberOfEvents;
	double eventMinActiveTime;
	double eventMaxActiveTime;
	int numberOfSpecificAttractionForEvents;


	int numberOfAttractions;
	boolean isAttractionsReadFromFile;
	int graphNodeDegree;
	double adaptiveWeightChangeRate; // lambda
	String edgeCreationType;	// node degree or graph density
	double edgeWeightChangeRate; // weight change rate after each event
	double maxEdgeWeightDifference;
	
	double terrainDimLength; 
	double simulationTime; 
	int numberOfExperiments;

	
	// simulation user inputs
	//double samplingTime; 				// no need , DES
	boolean isVisualizerOn; 			// input for simulation visualizer
	boolean isKeyboardInputOn;			// keyboard input
	boolean isConnectivityContraintOn;
	
	
	public enum SinkPositioning {
	    PCenter, PMedian, Random, Weighted, WeightedUnconnected, RandomUnconnected, PCenterUnconnected, PMedianUnconnected
	}
	public enum EventCreationType {
	    Random, Biased, Specific
	}

	public SimParam() {
		super();
		setInputs();
	}

	public double getSinkCommunicationRange() {
		return sinkCommunicationRange;
	}

	public double getSinkSpeed() {
		return sinkSpeed;
	}
	public SinkPositioning getSinkPosStrategy() {
		return sinkPosStrategy;
	}


	public int getNumberOfEvents() {
		return numberOfEvents;
	}

	public double getEventMinActiveTime() {
		return eventMinActiveTime;
	}
	public double getEventMaxActiveTime() {
		return eventMaxActiveTime;
	}
	public int getNumberOfAttractions() {
		return numberOfAttractions;
	}

	public EventCreationType getEventCreationType() {
		return eventCreationType;
	}

	public int getNumberOfSpecificAttractionForEvents() {
		return numberOfSpecificAttractionForEvents;
	}

	public int getGraphNodeDegree() {
		return graphNodeDegree;
	}
	public double getAdaptiveWeightChangeRate() {
		return adaptiveWeightChangeRate;
	}
	public String getEdgeCreationType() {
		return edgeCreationType;
	}
	public double getTerrainDimLength() {
		return terrainDimLength;
	}
	public double getSimulationTime() {
		return simulationTime;
	}

	public boolean isVisualizerOn() {
		return isVisualizerOn;
	}
	public boolean isKeyboardInputOn() {
		return isKeyboardInputOn;
	}

	public double getEdgeWeightChangeRate() {
		return edgeWeightChangeRate;
	}

	public double getMaxEdgeWeightDifference() {
		return maxEdgeWeightDifference;
	}

	public double getMobileSinkPositionUpdateTime() {
		return mobileSinkPositionUpdateTime;
	}

	public int getNumberOfExperiments() {
		return numberOfExperiments;
	}

	public boolean isAttractionsReadFromFile() {
		return isAttractionsReadFromFile;
	}

	public void setNumberOfAttractions(int numberOfAttractions) {
		this.numberOfAttractions = numberOfAttractions;
	}

	public int getStartNumberOfSinks() {
		return startNumberOfSinks;
	}

	public void setStartNumberOfSinks(int startNumberOfSinks) {
		this.startNumberOfSinks = startNumberOfSinks;
	}

	public int getEndNumberOfSinks() {
		return endNumberOfSinks;
	}

	public void setEndNumberOfSinks(int endNumberOfSinks) {
		this.endNumberOfSinks = endNumberOfSinks;
	}

	public int getNumberOfSinks() {
		return numberOfSinks;
	}

	public void setNumberOfSinks(int numberOfSinks) {
		this.numberOfSinks = numberOfSinks;
	}

	public boolean isConnectivityContraintOn() {
		return isConnectivityContraintOn;
	}

	public void setTerrainDimLength(double terrainDimLength) {
		this.terrainDimLength = terrainDimLength;
	}

	public double getAttractionMaxDistance() {
		return attractionMaxDistance;
	}

	public double getAttractionMinDistance() {
		return attractionMinDistance;
	}

	public void setAttractionMinDistance(double attractionMinDistance) {
		this.attractionMinDistance = attractionMinDistance;
	}




	
	
}
