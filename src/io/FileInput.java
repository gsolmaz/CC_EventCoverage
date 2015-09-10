package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.Point;

/**
 * @author Gurkan Solmaz 
 *    
 *    Department of Electrical Engineering and Computer Science
 *    University of Central Florida
 *
 */
public class FileInput {
	private int numberOfAttractions;
	private double areaDimensionLength;
	private List<Point> attractionPositionList;
	private List<Double> initialAttractionWeightList;

	
	public FileInput() {
		attractionPositionList = new ArrayList<Point>();
		initialAttractionWeightList = new ArrayList<Double>();
		readAttractionsInputFile();
	}



	public void readAttractionsInputFile() {
		

		String curDir = System.getProperty("user.dir");

		File f = new File(curDir+  "\\input\\" + "attractions" + ".txt");
		Scanner s;


			try {
				s = new Scanner(f);
				numberOfAttractions = s.nextInt();
				areaDimensionLength = s.nextDouble();
		
				for(int i=0; i<numberOfAttractions;i++){
					double w = s.nextDouble();
					initialAttractionWeightList.add(w);
					double x = s.nextDouble(); 
					double y = s.nextDouble();
					Point p = new Point(x, y);
					attractionPositionList.add(p);
				}
			
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
	}



	public int getNumberOfAttractions() {
		return numberOfAttractions;
	}



	public double getAreaDimensionLength() {
		return areaDimensionLength;
	}



	public List<Point> getAttractionPositionList() {
		return attractionPositionList;
	}



	public List<Double> getInitialAttractionWeightList() {
		return initialAttractionWeightList;
	}

}
