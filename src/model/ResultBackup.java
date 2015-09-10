/**
 * 
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gurkan
 *
 */
public class ResultBackup {
	
	List<Double> avgHandlingTimeResultBuffer, hitRatioResultBuffer;
	List<Double>  meanTimes, meanHitRatios;
	public ResultBackup() {
		avgHandlingTimeResultBuffer = new ArrayList<Double>();
		hitRatioResultBuffer = new ArrayList<Double>();
		meanTimes = new ArrayList<Double>();
		meanHitRatios = new ArrayList<Double>();
	}

	public void clearLocalBuffers(){
		avgHandlingTimeResultBuffer.clear();
		avgHandlingTimeResultBuffer = new ArrayList<Double>();
		hitRatioResultBuffer.clear(); hitRatioResultBuffer = new ArrayList<Double>();
	}
	
	public void addLocalBuffers(double time, double ratio){
		avgHandlingTimeResultBuffer.add(time);
		hitRatioResultBuffer.add(ratio);

	}
	
	public void addGlobalBuffers(double meanTime, double meanRatio){
		meanTimes.add(meanTime);
		meanHitRatios.add(meanRatio);
	}

	public List<Double> getAvgHandlingTimeResultBuffer() {
		return avgHandlingTimeResultBuffer;
	}

	public List<Double> getHitRatioResultBuffer() {
		return hitRatioResultBuffer;
	}
	public List<Double> getMeanTimes() {
		return meanTimes;
	}

	public List<Double> getMeanHitRatios() {
		return meanHitRatios;
	}
}
