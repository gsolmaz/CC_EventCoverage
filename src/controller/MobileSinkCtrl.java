/**
 * 
 */
package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Attraction;
import model.MobileSink;
import model.SimParam;
import model.SimParam.SinkPositioning;
import model.ThemePark;

/**
 * @author Gurkan Solmaz
 * 		   Department of EECS - University of Central Florida
 * 		   Event Coverage - Summer 2013
 * 		   Advisor: Dr. Damla Turgut
 */
public class MobileSinkCtrl {
	SimParam simParam;
	List<MobileSink> mobSinkList;
	boolean isSimulationStarted;
	
	public MobileSinkCtrl(SimParam simParam) {
		super();
		this.simParam = simParam;
		this.mobSinkList = new ArrayList<MobileSink>();
		this.isSimulationStarted = false;
		createMobileSinks();
	}
	public MobileSinkCtrl(SimParam simParam, boolean isSimulationStarted) {
		super();
		this.simParam = simParam;
		this.isSimulationStarted = isSimulationStarted;

	}


	private void createMobileSinks() {
		for(int i=0;i<simParam.getNumberOfSinks();i++){
			MobileSink ms = new MobileSink(simParam.getSinkSpeed());
			//ms.setDestinationAttraction(-1);
			mobSinkList.add(ms);
		}
	}

	public List<MobileSink> getMobSinkList() {
		return mobSinkList;
	}

	public void setMobSinkList(List<MobileSink> mobSinkList) {
		this.mobSinkList = mobSinkList;
	}
	public List<MobileSink> placeMobileSinks(ThemePark tp) {
		List<Integer> bestCandidate = new ArrayList<Integer>();

		if(simParam.isConnectivityContraintOn()){
			// for each candidate connected nodes
			if(simParam.getSinkPosStrategy() == SinkPositioning.PCenter ||simParam.getSinkPosStrategy() == SinkPositioning.PMedian ){
				bestCandidate = pCenterMedianAlgorithm(tp);
			}
			else if(simParam.getSinkPosStrategy() == SinkPositioning.Weighted){
				bestCandidate = weightedPositioningAlgorithm(tp);
			}
			else if(simParam.getSinkPosStrategy() == SinkPositioning.Random){
				bestCandidate = randomPositioningAlgorithm(tp);
			}
		}
		else{ // without connectivity constraint
			if(simParam.getSinkPosStrategy()== SinkPositioning.WeightedUnconnected){
				bestCandidate = weightedUnconnectedAlgorithm(tp);
			}
			else if(simParam.getSinkPosStrategy() == SinkPositioning.RandomUnconnected){
				bestCandidate = randomUnconnectedAlgorithm(tp);
			}
			else if(simParam.getSinkPosStrategy() == SinkPositioning.PCenterUnconnected ||simParam.getSinkPosStrategy() == SinkPositioning.PMedianUnconnected){
				bestCandidate = pCenterMedianAlgorithm(tp); // exactly the same algorithm for PCenter and Pmedian, but the candidate list is different (larger), including all possible subsets with size p
			}

		}
		List<MobileSink> returnList = updateMobSinkLocations(bestCandidate,tp);
		return returnList;
		
	}

	
	
	private List<Integer> randomUnconnectedAlgorithm(ThemePark tp) {
		List<Integer> returnList= new ArrayList<Integer>();
		List<Attraction> tmpAttractionList = new ArrayList<Attraction>();
		tmpAttractionList.addAll(tp.getAttractionList());
		Random r= new Random();
		for(int i=0;i<simParam.getNumberOfSinks();i++){ // for each sink, select the one randomly and add it to the return list
			int removedAttractionIndexFromTmpList = r.nextInt(tmpAttractionList.size());
			int selectedAttractionIndex= tmpAttractionList.get(removedAttractionIndexFromTmpList).getIndex();
			
			tmpAttractionList.remove(removedAttractionIndexFromTmpList); // remove this as it is already selected
			returnList.add(selectedAttractionIndex); 
		}
		return returnList;
	}
	
	
	private List<Integer> weightedUnconnectedAlgorithm(ThemePark tp) {		
		
		List<Integer> returnList= new ArrayList<Integer>();
		List<Attraction> tmpAttractionList = new ArrayList<Attraction>();
		tmpAttractionList.addAll(tp.getAttractionList());

		for(int i=0;i<simParam.getNumberOfSinks();i++){ // for each sink, select the best one wrt. event probabilities among remaining attractions
			double bestEventProbability=0; 
			int selectedAttractionIndex = -1;
			int removedAttractionIndexFromTmpList = -1;
			for(int j=0; j<tmpAttractionList.size();j++){ // find the best attraction
				if(tmpAttractionList.get(j).getEventProbability() > bestEventProbability){
					bestEventProbability = tmpAttractionList.get(j).getEventProbability();
					selectedAttractionIndex =tmpAttractionList.get(j).getIndex();
					removedAttractionIndexFromTmpList = j;
				}
			}// found the best possible attraction
			tmpAttractionList.remove(removedAttractionIndexFromTmpList); // remove this as it is already selected
			returnList.add(selectedAttractionIndex); 
		}
		return returnList;
	}
	
	
	private List<MobileSink> updateMobSinkLocations(
			List<Integer> bestCandidate, ThemePark tp) {
		List<MobileSink> returnList = new ArrayList<MobileSink>();
		for(int i=0;i<tp.getMobileSinkList().size();i++){
			MobileSink tmpSink = tp.getMobileSinkList().get(i);
			tmpSink.setCurrentAttraction(bestCandidate.get(i));
			tmpSink.setLocation(tp.getAttractionList().get(tmpSink.getCurrentAttraction()).getLocation());
			returnList.add(tmpSink);
		}
		
		return returnList;
	}
	private List<Integer> randomPositioningAlgorithm(ThemePark tp) {
		int numberOfCandidates = tp.getAllPossibleConnectedSinkLocationCombinations().size();
		Random r = new Random();
		int candidateIndex =r.nextInt(numberOfCandidates); // guess a value between 0 and last candidate index
		return tp.getAllPossibleConnectedSinkLocationCombinations().get(candidateIndex);
	}
	private List<Integer> weightedPositioningAlgorithm(ThemePark tp) {
		double bestWeightSum=0;
		int bestCandidateIndex=-1;

		for(int i=0; i<tp.getAllPossibleConnectedSinkLocationCombinations().size();i++ ){// for each candidate
			// calculate the total weight for the candidate
			List<Integer> candidate = tp.getAllPossibleConnectedSinkLocationCombinations().get(i);
			double candidateWeightSum=0;
			for(int j=0;j<candidate.size();j++){
				// get the weight of the each corresponding attraction
				int c = candidate.get(j);
				Attraction a = tp.getAttractionList().get(c);
				candidateWeightSum+=a.getEventProbability();
			}
			// check if it is the best candidate so far or not
			if(candidateWeightSum>=bestWeightSum){
				bestWeightSum = candidateWeightSum;
				bestCandidateIndex = i;
			}
		}
		return tp.getAllPossibleConnectedSinkLocationCombinations().get(bestCandidateIndex);
	}
	private List<Integer> pCenterMedianAlgorithm(ThemePark tp) {
		double maxValueForPCenter = 9999999;
		int bestCandidateIndexForPCenter=-1;
		int bestCandidateIndexForPMedian=-1;

		double sumValueForPMedian = 9999999;
		for(int i=0; i<tp.getAllPossibleConnectedSinkLocationCombinations().size();i++ ){// for each candidate
			List<Integer> candidate = tp.getAllPossibleConnectedSinkLocationCombinations().get(i);
			double candidateShortestPathMax =0 ;
			double candidateShortestPathSum =0;
			for(int j=0;j<tp.getAttractionList().size();j++){ // for each attraction a
				
				Attraction a = tp.getAttractionList().get(j);
				// find the shortest path to a from each of the vertices in the candidate
				ShortestPathFinder spf = new ShortestPathFinder("Shortest Path");
				double valueOfShortestPath;
				if(candidate.contains(j)){ // one of the p-centers is already on this attraction
					valueOfShortestPath =0;
				}
				else{ // find the shortest path
					valueOfShortestPath = spf.findBestSink(tp.getAttractionGraph(), j, candidate); // graph, attraction index, indices of mob sinks in candidate
					valueOfShortestPath = valueOfShortestPath * a.getEventProbability(); // add attraction probabilities (weights) to this formula
				}
			//	System.out.println(valueOfShortestPath);
				if(valueOfShortestPath>=candidateShortestPathMax){
					candidateShortestPathMax = valueOfShortestPath; // update the maximum shortest path value for p-center
				}
				candidateShortestPathSum += valueOfShortestPath; // for p-median
			}
			
			if(maxValueForPCenter>=candidateShortestPathMax){ // minimize the maximum distance, means best candidate for p-center
				bestCandidateIndexForPCenter = i;
				maxValueForPCenter = candidateShortestPathMax;
			}
			if(sumValueForPMedian>=candidateShortestPathSum){ // minimize the sum of the shortest path values for p-median
				bestCandidateIndexForPMedian = i;
				sumValueForPMedian = candidateShortestPathSum;
			}	
		}
		if(simParam.getSinkPosStrategy() == SinkPositioning.PCenter ||simParam.getSinkPosStrategy() == SinkPositioning.PCenterUnconnected ){
			return tp.getAllPossibleConnectedSinkLocationCombinations().get(bestCandidateIndexForPCenter);
		}
		else if(simParam.getSinkPosStrategy() == SinkPositioning.PMedian ||simParam.getSinkPosStrategy() == SinkPositioning.PMedianUnconnected){
			return tp.getAllPossibleConnectedSinkLocationCombinations().get(bestCandidateIndexForPMedian);
		}
		else return null;
	}
	
	
	
}
