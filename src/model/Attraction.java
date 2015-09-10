package model;
/**
 * @author Gurkan Solmaz
 * 		   Department of EECS - University of Central Florida
 * 		   Event Coverage - Summer 2013
 * 		   Advisor: Dr. Damla Turgut
 */

public class Attraction {
	int index;
	Point location;
	double eventProbability;
	boolean isOccupied;
	
	public Attraction(int index, Point location, double eventProbability,
			boolean isOccupied) {
		super();
		this.index = index;
		this.location = location;
		this.eventProbability = eventProbability;
		this.isOccupied = isOccupied;
	}

	public int getIndex() {
		return index;
	}

	public Point getLocation() {
		return location;
	}

	public double getEventProbability() {
		return eventProbability;
	}

	public boolean isOccupied() {
		return isOccupied;
	}

	public void setEventProbability(double eventProbability) {
		this.eventProbability = eventProbability;
	}
	
	
	
}
