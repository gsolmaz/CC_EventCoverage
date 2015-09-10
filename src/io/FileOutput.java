package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import model.ResultBackup;
import model.SimParam;
import model.SimParam.EventCreationType;
import model.SimParam.SinkPositioning;

/**
 * @author Gurkan Solmaz 
 *    
 *    Department of Electrical Engineering and Computer Science
 *    University of Central Florida
 *
 */
public class FileOutput {

	SimParam simParam;
	

	public FileOutput(SimParam simParam ) {
		this.simParam = simParam;
		writeExpInformation();
	}


	private void writeExpInformation() {
		// TODO Auto-generated method stub
		
	}


	public ResultBackup writeSimulationResults(ResultBackup rb) {
		String curDir = System.getProperty("user.dir");
		FileWriter fstream;
		try {			
			new File(curDir + "\\output\\").mkdirs();

			fstream = new FileWriter(curDir + "\\output\\" +"Output-" +  
					getSinkPosStrategyName(simParam.getSinkPosStrategy())+  "-" + 
					getEventCreationTypeName(simParam.getEventCreationType()) + "-" +
					"Spec" + simParam.getNumberOfSpecificAttractionForEvents() +
					".txt", true); // the second parameter "true" stands for appending the new outputs to the same file if the file exists (do not overwriting file !)
		
			BufferedWriter out = new BufferedWriter(fstream);
		
			out.write("Experiment information:" + "Sink pos. strategy: "+ simParam.getSinkPosStrategy() + "," +
					"Event distr. type: " + simParam.getEventCreationType() + "," + "Num of attractions: " + simParam.getNumberOfAttractions() + " Number of sinks: "   + simParam.getNumberOfSinks() + "," );
					out.write("Number of experiments: " + simParam.getNumberOfExperiments() +  "\n");
		
			//  write the results of all the experiments with same number of sinks  (local results)
			
			for(int i=0 ; i<simParam.getNumberOfExperiments();i++){
				out.write(rb.getAvgHandlingTimeResultBuffer().get(i) + "\t" );
				out.write(rb.getHitRatioResultBuffer().get(i) + "\n" );
			}
			rb.clearLocalBuffers();
			
			out.close();
			fstream.close();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rb;
	}

	
	private String getEventCreationTypeName(EventCreationType eventCreationType) {
		if(eventCreationType == EventCreationType.Biased){
			return "BiasedEvents";
		}
		else if(eventCreationType == EventCreationType.Random){
			return "RandomEvents";
		}
		else if(eventCreationType == EventCreationType.Specific){
			return "SpecificEvents";
		}
		return null;

	}


	private String getSinkPosStrategyName(SinkPositioning sinkPosStrategy) {
		if(sinkPosStrategy == SinkPositioning.PCenter){
			return "PCenter";
		}
		else if(sinkPosStrategy == SinkPositioning.PMedian){
			return "PMedian";
		}
		else if(sinkPosStrategy == SinkPositioning.Weighted){
			return "Weighted";
		}
		else if(sinkPosStrategy == SinkPositioning.Random){
			return "Random";
		}
		else if(sinkPosStrategy==SinkPositioning.WeightedUnconnected){
			return "WeighedUnconnected";
		}
		else if(sinkPosStrategy==SinkPositioning.RandomUnconnected){
			return "RandomUnconnected";
		}
		else if(sinkPosStrategy==SinkPositioning.PCenterUnconnected){
			return "PCenterUnconnected";
		}
		else if(sinkPosStrategy==SinkPositioning.PMedianUnconnected){
			return "PMedianUnconnected";
		}
		return null;

	}


	public void writeResultBackupInOrder(ResultBackup rba) {
		String curDir = System.getProperty("user.dir");
		FileWriter fstream;
		try {
			
			fstream = new FileWriter(curDir + "\\output\\" +"ExcelResults-" +  
					getSinkPosStrategyName(simParam.getSinkPosStrategy())+  "-" + 
					getEventCreationTypeName(simParam.getEventCreationType()) + "-" +
					"Spec" + simParam.getNumberOfSpecificAttractionForEvents() +
					".txt", true); // the second parameter "true" stands for appending the new outputs to the same file if the file exists (do not overwriting file !)
		
		
			BufferedWriter out = new BufferedWriter(fstream);
		

			out.write("Experiment information:" + simParam.getSinkPosStrategy() + "," +
				"Event type: " + simParam.getEventCreationType() + "," +  "#ofSpecificAttr: " + simParam.getNumberOfSpecificAttractionForEvents() +  ",");
			out.write("Number of experiments: " + simParam.getNumberOfExperiments() + "," + "# of attractions: " + simParam.getNumberOfAttractions() + ","  
					+ "Mob sink count-> from " + simParam.getStartNumberOfSinks() + "  to " + simParam.getEndNumberOfSinks() + "\n");
		
			for(int i=0;i<(simParam.getEndNumberOfSinks()-simParam.getStartNumberOfSinks())+1;i++){
				out.write(rba.getMeanTimes().get(i)+ "\t");
				out.write(rba.getMeanHitRatios().get(i)+ "\n");
			}
			out.close();
			fstream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




	public ResultBackup addMeanResultsOfExperimentsWithSameNumberOfSinks(
			ResultBackup rb) {
		double tmpTime=0;
		double tmpHit =0;
		for(int i=0;i<simParam.getNumberOfExperiments();i++){
			tmpTime+= rb.getAvgHandlingTimeResultBuffer().get(i);
			tmpHit += rb.getHitRatioResultBuffer().get(i);
		}
		double meanTime = tmpTime / simParam.getNumberOfExperiments();
		double meanHit = tmpHit / simParam.getNumberOfExperiments();
		
		// find mean values
		rb.addGlobalBuffers(meanTime, meanHit);
		return rb;
	}
	
	
}
