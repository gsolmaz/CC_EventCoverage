/**
 * 
 */
package model;

import java.util.List;

/**
 * @author Gurkan Solmaz
 * 		   Department of EECS - University of Central Florida
 * 		   Event Coverage - Summer 2013
 * 		   Advisor: Dr. Damla Turgut
 */

public class ThemePark {
	DirectedGraph attractionGraph;
	DirectedGraph distanceGraph;
	//DirectedGraph connectionGraph;
	List<MobileSink> mobileSinkList;
	List<Attraction> attractionList;
	List<Event> eventList;
	List<List<Integer>> allPossibleConnectedSinkLocationCombinations;
	
	
	public void setAttractionGraph(DirectedGraph attractionGraph) {
		this.attractionGraph = attractionGraph;
	}
	public void setMobileSinkList(List<MobileSink> mobileSinkList) {
		this.mobileSinkList = mobileSinkList;
	}
	public void setAttractionList(List<Attraction> attractionList) {
		this.attractionList = attractionList;
	}
	public void setEventList(List<Event> eventList) {
		this.eventList = eventList;
	}
	public DirectedGraph getAttractionGraph() {
		return attractionGraph;
	}
	public List<MobileSink> getMobileSinkList() {
		return mobileSinkList;
	}
	public List<Attraction> getAttractionList() {
		return attractionList;
	}
	public List<Event> getEventList() {
		return eventList;
	}

	public List<List<Integer>> getAllPossibleConnectedSinkLocationCombinations() {
		return allPossibleConnectedSinkLocationCombinations;
	}
	public void setAllPossibleConnectedSinkLocationCombinations(
			List<List<Integer>> allPossibleConnectedSinkLocationCombinations) {
		this.allPossibleConnectedSinkLocationCombinations = allPossibleConnectedSinkLocationCombinations;
	}
	public DirectedGraph getDistanceGraph() {
		return distanceGraph;
	}
	public void setDistanceGraph(DirectedGraph distanceGraph) {
		this.distanceGraph = distanceGraph;
	}

	
	
	
}
