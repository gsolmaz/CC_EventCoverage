package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import visualizer.SimulationVisualizer;

import model.Attraction;
import model.Event;
import model.Event.EventType;
import model.MobileSink;
import model.ResultBackup;
import model.SimParam;
import model.ThemePark;

/**
 * @author Gurkan Solmaz
 * 		   Department of EECS - University of Central Florida
 * 		   Event Coverage - Summer 2013
 * 		   Advisor: Dr. Damla Turgut
 */
public class SimulationCtrl {
	// implements a Discrete Time Simulation

	ThemePark tp;
	SimParam simParam;
	ShortestPathFinder spf;
	public SimulationCtrl(ThemePark tp, SimParam simParam) {
		this.tp = tp;
		this.simParam= simParam;
		spf = new ShortestPathFinder("Shortest Path");
	}
	public ResultBackup startSimulation(ResultBackup rb) throws InterruptedException {
		SimulationVisualizer sv =null;
		List<Integer> currentMobSinkPositionList= new ArrayList<Integer>();
	
		// draw the environment if the visualizer is on
		if(simParam.isVisualizerOn()){
			sv = new SimulationVisualizer(tp,simParam);
		}
		
		int counter=0;

		
		while(true){	
			if(!simParam.isKeyboardInputOn() || sv.isFinish() || sv.isResume()){
				if(counter== tp.getEventList().size()) break;
				Event e = tp.getEventList().get(counter);
		
				if(e.getType() == EventType.PositionUpdateTime){
				//if it is a mobile sink position update time event
					// update mobile sinks vertices according to new values
					tp.setMobileSinkList(updateMobileSinkPositions());
					currentMobSinkPositionList = setCurrentMobSinkPositionList(tp.getMobileSinkList());
					// update (change) the edge values according to edgeWeightChange parameter 
					updateEdgeWeights();
				}
				else{
				// if it is a security event
					
					// find the best sink to handle event and record the time & hit information
						// you should find the best sink (smallest shortest path) from all sinks to the event
					e= handleEvent(e,currentMobSinkPositionList);
					tp.getEventList().set(counter, e);
					
					// update (change) attraction weights according to lambda after the event
					updateAttractionProbabilities(e.getAttractionIndex());
				}
				if(simParam.isVisualizerOn()){
					sv.setCurrentTime(e.getStartTime());

					sv.setMobileSinkList(tp.getMobileSinkList());
					sv.setResume(false); 
				}
				counter++;
			}
			if(simParam.isVisualizerOn()){
				Thread.sleep(1);
			}
		}
		if(simParam.isVisualizerOn()){
			sv.dispose();
		}
		// output the results using the stored information in event list 
		return computeResults(tp.getEventList(), rb);
	}
	private ResultBackup computeResults(List<Event> eventList, ResultBackup rb) {
		int numberOfHits =0;
		int numberOfEvents = simParam.getNumberOfEvents();
		double sumOfHandlingTimes=0;
		for(int i=0;i<eventList.size();i++){
			Event e = eventList.get(i);
			if(e.getHandlingTime()==-1){ // couldn`t find the path
				numberOfEvents--;
				continue;
			}
			if(e.getType()==EventType.Security){
				if(e.isHandled()){
					numberOfHits++;
				}

				sumOfHandlingTimes = sumOfHandlingTimes + e.getHandlingTime();
			//	System.out.println("Event handling time: " + e.getHandlingTime());
			}		
		}
		double hitRatio = ((double)numberOfHits)/numberOfEvents;
		double avgHandlingTime =sumOfHandlingTimes / numberOfEvents;
		rb.addLocalBuffers(avgHandlingTime, hitRatio);
		return rb;
	
	}
	private void updateAttractionProbabilities(int eventIndex) {
		List<Attraction> attractionList = tp.getAttractionList();
		List<Double> newEventProbabilities = new ArrayList<Double>();
		boolean boundaryFlag = false;
		
		// attraction with boundary values
		List<Integer> criticalAttractionList = new ArrayList<Integer>();
		for(int i=0;i<attractionList.size();i++){
			if(attractionList.get(i).getEventProbability() < simParam.getAdaptiveWeightChangeRate()){
				criticalAttractionList.add(i);
			}
		}
		int numberOfCriticalAttractions = criticalAttractionList.size();
		if(criticalAttractionList.contains(eventIndex)){
			numberOfCriticalAttractions--;
		}
		
		for(int i=0;i<attractionList.size();i++){
			double previousProb= attractionList.get(i).getEventProbability();
			if(i == eventIndex){
				previousProb = previousProb + simParam.getAdaptiveWeightChangeRate();
				if(previousProb > 1){
					boundaryFlag = true; break;
				}
				newEventProbabilities.add(previousProb);
			}
			else{
				if(!criticalAttractionList.contains(i)){
					previousProb = previousProb - simParam.getAdaptiveWeightChangeRate()/((simParam.getNumberOfAttractions()-1)- numberOfCriticalAttractions);

				}
				if(previousProb < 0){
					boundaryFlag = true; break;
				}
				newEventProbabilities.add(previousProb);
			}
		}
		if(boundaryFlag==false){
			for(int i=0;i<attractionList.size();i++){
			//	System.out.println("1: "+tp.getAttractionList().get(i).getEventProbability());
				tp.getAttractionList().get(i).setEventProbability(newEventProbabilities.get(i));
			//	System.out.println("2: " + tp.getAttractionList().get(i).getEventProbability());

			}
		}
		else{ // boundary flag is true
			System.out.println("Boundary Alert: Attraction probabilities were out of bound! ");
		}
/*		double sum = 0;
		for(int i=0;i<attractionList.size();i++){
			sum+=tp.getAttractionList().get(i).getEventProbability();
		}
		System.out.println("Sum of attraction probabilities:" + sum);*/
	}
	private void updateEdgeWeights() {
 		double[][] gMatrix = tp.getAttractionGraph().getGraphMatrix();
 		double[][] newMatrix = new double[simParam.getNumberOfAttractions()][simParam.getNumberOfAttractions()];
		double[][] dMatrix = tp.getDistanceGraph().getGraphMatrix();

		double changeRate = simParam.getEdgeWeightChangeRate();
		Random r = new Random();
		for(int i=0;i<gMatrix.length;i++){
			for(int j=0;j<gMatrix.length;j++){
				if(gMatrix[i][j]==0) continue; // check if there is no edge 
				double d = r.nextDouble();
				d = d * 2;  // get something between 0 and 2
				double weight =  gMatrix[i][j];
				weight = weight - (gMatrix[i][j]* changeRate);
				weight = weight +  (gMatrix[i][j] * changeRate* d); // at worst case, d will be 0 and
				// at the best case, d will be 2 and the weight will increase
				
				// check if it is out of boundaries
				if(weight<dMatrix[i][j]){ // lower bound is the distance itself, weight cannot be less than distance
					weight= dMatrix[i][j];
				}
				else if(weight> (simParam.getMaxEdgeWeightDifference()+1) * dMatrix[i][j]){ // upper bound
					weight = (simParam.getMaxEdgeWeightDifference()+1) * dMatrix[i][j];
				}
				
				newMatrix[i][j]= weight;
			}
		}
		
		tp.getAttractionGraph().setGraphMatrix(newMatrix);

	}
	private Event handleEvent(Event e, List<Integer> currentMobSinkPositionList) {
		// TODO Auto-generated method stub
	
		ShortestPathFinder spf = new ShortestPathFinder("Shortest Path");
		double valueOfShortestPath;
		if(currentMobSinkPositionList.contains(e.getAttractionIndex())){ // one of the p-centers is already on this attraction
			valueOfShortestPath =0;
		}
		else{ // find the shortest path
			valueOfShortestPath = spf.findBestSink(tp.getAttractionGraph(), e.getAttractionIndex(), currentMobSinkPositionList); // graph, attraction index, indices of mob sinks in candidate
		}
		
		// find the event handling time
		if(valueOfShortestPath==10000){ // couldn`t find a shortest path
			e.setHandlingTime(-1);
			//System.out.println("Warning: Attraction graph has unconnected components ! ");
			return e;
		}
		double time = valueOfShortestPath / simParam.getSinkSpeed();
		
		// find if it is a hit or miss
		if(time<e.getActiveTime()){
			e.setHandled(true);
		}
		else{
			e.setHandled(false);
		}
		e.setHandlingTime(time);
		return e;
	}

	private List<Integer> setCurrentMobSinkPositionList(
			List<MobileSink> mobileSinkList) {
		List<Integer> returnList = new ArrayList<Integer>();
		for(int i=0;i<mobileSinkList.size();i++){
			returnList.add(mobileSinkList.get(i).getCurrentAttraction());
		}
		return returnList;
	}
	private List<MobileSink> updateMobileSinkPositions() {
		// get all combinations in the connected subgraph
		MobileSinkCtrl mobSinkCtrl = new MobileSinkCtrl(simParam, true );
		
		
		return mobSinkCtrl.placeMobileSinks(tp);
	}
	
/*	private void startSimulation() { // old simulation
		
		while(true)	{		
			
			
			
			
			// check the current state of the sinks, if events are handled, release the sinks
			//checkMobSinkStates();
			
			
			// get the current event	
			//List<Event> currentEvents= checkEvents(currentSimulationTime);
			
			// decide the best sinks to handle the events (shortest path)
			
			// place the sinks according to p-center, p-median, random or weighted
			
			// move the sinks
			
			// draw the environment if the visualizer is on
			
		}	
	}*/


/*	private List<Event> checkEvents(double currentSimulationTime) {
		List<Event> returnList = new ArrayList<Event>();
		for(int i=0;i<simParam.getNumberOfEvents();i++){
			Event e = tp.getEventList().get(i);
			if(e.getStartTime()<= currentSimulationTime && e.getEndTime()>currentSimulationTime
					&& !e.isHandled() && !e.isOnProcess()){
				// event is active and not processed yet
				returnList.add(e);
			}
		}
		return returnList;
	}*/
/*	private void checkMobSinkStates() {
		for(int i=0;i<simParam.getNumberOfSinks();i++){
			MobileSink ms = tp.getMobileSinkList().get(i);
			if(ms.getDestinationAttraction()== -1){
				continue;
			}
			else if(ms.getLocation().equals(tp.getMobileSinkList().get(
					ms.getDestinationAttraction()).getLocation())){
				// the sink arrived at the destination
				ms.setCurrentAttraction(ms.getDestinationAttraction());
				ms.setDestinationAttraction(-1);
				tp.getMobileSinkList().set(i, ms);
			}
			
		}
	}*/
	
	
}
