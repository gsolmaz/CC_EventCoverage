/**
 * 
 */
package controller;

import io.FileInput;
import io.FileOutput;

import java.util.Calendar;

import model.ResultBackup;
import model.SimParam;
import model.ThemePark;

/**
 * @author Gurkan Solmaz
 * 		   Department of EECS - University of Central Florida
 * 		   Event Coverage - Summer 2013
 * 		   Advisor: Dr. Damla Turgut
 */

public class MainCtrl {
	public static void main(String[] args) throws InterruptedException {
		SimParam simParam =  new SimParam();
		FileInput fileInput;
		if(simParam.isAttractionsReadFromFile()){
			fileInput = new FileInput();
			simParam.setNumberOfAttractions(fileInput.getNumberOfAttractions());
			simParam.setTerrainDimLength(fileInput.getAreaDimensionLength());
		}
		else{
			fileInput=null;
		}
		FileOutput fileOutput = new FileOutput(simParam);
		ResultBackup rb = new ResultBackup();
		int numSinks = simParam.getEndNumberOfSinks();
		for(int i=simParam.getStartNumberOfSinks();i<numSinks+1;i++){
			simParam.setNumberOfSinks(i);
			   Calendar beginTime = Calendar.getInstance();
			for(int j =0;j<simParam.getNumberOfExperiments();j++){
			    
				GraphCtrl graphCtrl = new GraphCtrl(simParam,fileInput);
				ThemePark tp = graphCtrl.getThemePark();
				
				MobileSinkCtrl mobSinkCtrl = new MobileSinkCtrl(simParam);
				tp.setMobileSinkList(mobSinkCtrl.getMobSinkList());
				
				EventCtrl eventCtrl = new EventCtrl(simParam, tp);
				tp.setEventList(eventCtrl.getEventList());
			
				SimulationCtrl simCtrl = new SimulationCtrl(tp, simParam);
				rb = simCtrl.startSimulation(rb);
			}

			rb = fileOutput.addMeanResultsOfExperimentsWithSameNumberOfSinks(rb);
			rb = fileOutput.writeSimulationResults(rb);
		    Calendar endTime2 = Calendar.getInstance();
		    double fileOutputTime= ((double)endTime2.getTimeInMillis() - (double)beginTime.getTimeInMillis())/1000;
		    System.out.println( "Number of sinks: " + i  + " - Execution time: " + fileOutputTime  + " seconds.");

		}
		fileOutput.writeResultBackupInOrder(rb);
		
		System.exit(0);	
	}


}
