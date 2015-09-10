package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Attraction;
import model.Event;
import model.Point;
import model.SimParam;
import model.SimParam.EventCreationType;
import model.ThemePark;

/**
 * @author Gurkan Solmaz
 * 		   Department of EECS - University of Central Florida
 * 		   Event Coverage - Summer 2013
 * 		   Advisor: Dr. Damla Turgut
 */
public class EventCtrl {
	List<Event> eventList;
	SimParam simParam;
	ThemePark themePark;
	public EventCtrl(SimParam simParam, ThemePark themePark) {
		super();
		this.simParam = simParam;
		this.themePark = themePark;
		createEvents();
	//	createMobSinkPositionUpdateTimeEvents();
	}
	
	

	private void createEvents() {
		eventList = new ArrayList<Event>();
		Random rand = new Random();
		double numberOfEvents=simParam.getNumberOfEvents();
		double simTime = simParam.getSimulationTime(); 
		
		double poissonRateParameter = numberOfEvents/simTime;
		
		int numberOfAttractions = simParam.getNumberOfAttractions();

		double minActTime = simParam.getEventMinActiveTime();
		double maxActTime  = simParam.getEventMaxActiveTime();
		
		// determine the start times and active times of the events
		List<Double> eventStartTimes = new ArrayList<Double>();
		List<Double> eventActiveTimes = new ArrayList<Double>();
		double currentTime=0;
		for(int i=0;i<numberOfEvents;i++){
		double waitingTime = MathFunctions.poissonNextTime(poissonRateParameter);
		currentTime += waitingTime;
			eventStartTimes.add(currentTime);
			
			double a= rand.nextDouble();
			double tmpActTime = ( a* (maxActTime - minActTime) )+ minActTime ;
			eventActiveTimes.add(tmpActTime);
		}
		//Collections.sort(eventStartTimes);  // no need for a sort, we followed poisson process
		
		
		EventCreationType ect = simParam.getEventCreationType();
		
		//determine the specific attraction if it is chosen as the simulation setting
		List<Integer> specificAttractionIndexList = null;
		if(ect==EventCreationType.Specific){
			specificAttractionIndexList = determineSpecificAttractions(numberOfAttractions);
		}

		// determine the attractions of the events
		List<Integer> eventAttractionIndexList = new ArrayList<Integer>();
		
		for(int i=0;i<numberOfEvents;i++){
			if(ect==EventCreationType.Random){
				int tmpIndex = rand.nextInt(simParam.getNumberOfAttractions());
				eventAttractionIndexList.add(tmpIndex);
			}
			else if(ect==EventCreationType.Biased){
				double tmp = rand.nextDouble();
				for(int j=0;j<simParam.getNumberOfAttractions();j++)
				{	Attraction a = themePark.getAttractionList().get(j);
					if(tmp<=a.getEventProbability()){
						eventAttractionIndexList.add(j);
						continue;
					}
					else{
						tmp -= a.getEventProbability();
					}
				}		
			}
			else if(ect==EventCreationType.Specific){
				int tmpIndex = rand.nextInt(simParam.getNumberOfSpecificAttractionForEvents());
				tmpIndex = specificAttractionIndexList.get(tmpIndex);
				eventAttractionIndexList.add(tmpIndex);
			}
		}
		
		double updateTime = simParam.getMobileSinkPositionUpdateTime();
		
		double currentUpdateTime=0;
		
		for(int i=0;i<numberOfEvents;i++){
			if(eventStartTimes.get(i) >= currentUpdateTime){
				// create update time event
				Event e = new Event(currentUpdateTime);
				eventList.add(e);
				// set new update time
				currentUpdateTime += updateTime;
				// try again with the same event, if it's less than the new event time create it, or do the same
				i--; continue;
			}
			double tmpEndTime = eventStartTimes.get(i) + eventActiveTimes.get(i);
			Point tmpLocation = themePark.getAttractionList().get(eventAttractionIndexList.get(i)).getLocation();
			Event e = new Event(eventStartTimes.get(i),tmpEndTime , eventActiveTimes.get(i), false, tmpLocation, 1, eventAttractionIndexList.get(i));
			eventList.add(e);
		}
				
	}



	private List<Integer> determineSpecificAttractions(int numberOfAttractions) {
		Random rand = new Random();
		List<Integer> specificAttractionIndexList = new ArrayList<Integer>();
		for(int i=0;i<simParam.getNumberOfSpecificAttractionForEvents();i++){
			int tmpIndex = rand.nextInt(numberOfAttractions);
			if(specificAttractionIndexList.contains(tmpIndex)){
				// try your chance again to pick one attraction randomly
				i--; continue;
			}
			else{ // choose this attraction as one of the specific attractions
				specificAttractionIndexList.add(tmpIndex);
			}
		}
		return specificAttractionIndexList;
	}


	public List<Event> getEventList() {
		return eventList;
	}
	
	
	
	
}
