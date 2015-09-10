package model;

/**
 * @author Gurkan Solmaz
 * 		   Department of EECS - University of Central Florida
 * 		   Event Coverage - Summer 2013
 * 		   Advisor: Dr. Damla Turgut
 */

public class Event {
	double startTime;
	double endTime;
	
	double activeTime;
	boolean isActive;
	Point location;
	int attractionIndex;
	int priority;
	EventType type;
	
	public enum EventType {
	    PositionUpdateTime, Security
	}
	
	boolean isHandled;
	double handlingTime;
	boolean isOnProcess; // true if sinks made the decision and a sink is trying to handle
	
	
	public Event(double startTime, double endTime, double activeTime,
			boolean isActive, Point location, int priority, int attractionIndex) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.activeTime = activeTime;
		this.isActive = isActive;
		this.location = location;
		this.priority = priority;
		this.attractionIndex = attractionIndex;
		this.isHandled=false;
		this.isOnProcess=false;
		this.type = EventType.Security;
	}
	
	public Event(double startTime) {
		super();
		this.startTime = startTime;
		this.type = EventType.PositionUpdateTime;
	}
	
	public double getStartTime() {
		return startTime;
	}
	public double getEndTime() {
		return endTime;
	}
	public double getActiveTime() {
		return activeTime;
	}
	public boolean isActive() {
		return isActive;
	}
	public Point getLocation() {
		return location;
	}
	public int getPriority() {
		return priority;
	}
	public int getAttractionIndex() {
		return attractionIndex;
	}
	public boolean isHandled() {
		return isHandled;
	}
	public void setHandled(boolean isHandled) {
		this.isHandled = isHandled;
	}
	public boolean isOnProcess() {
		return isOnProcess;
	}
	public void setOnProcess(boolean isOnProcess) {
		this.isOnProcess = isOnProcess;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public double getHandlingTime() {
		return handlingTime;
	}

	public void setHandlingTime(double handlingTime) {
		this.handlingTime = handlingTime;
	}
	
	
	
}
