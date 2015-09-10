package model;

/**
 * @author Gurkan Solmaz
 * 		   Department of EECS - University of Central Florida
 * 		   Event Coverage - Summer 2013
 * 		   Advisor: Dr. Damla Turgut
 */

public class MobileSink {
	double maxSpeed;
	Point location;
	//int destinationAttraction; // null if the sink doesn`t move
	int currentAttraction; // null if the sink does not belong to any attraction now
	
	public MobileSink(double maxSpeed) {
		super();
		this.maxSpeed = maxSpeed;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public Point getLocation() {
		return location;
	}

	public int getCurrentAttraction() {
		return currentAttraction;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public void setCurrentAttraction(int currentAttraction) {
		this.currentAttraction = currentAttraction;
	}

}
