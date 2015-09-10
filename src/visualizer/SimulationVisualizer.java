package visualizer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JFrame;

import model.Attraction;
import model.Event.EventType;
import model.MobileSink;
import model.Point;
import model.SimParam;
import model.ThemePark;

/**
 * @author Gurkan Solmaz
 * 		   Department of EECS - University of Central Florida
 * 		   Disaster Mobility - Spring 2013
 * 		   Advisor: Dr. Damla Turgut
 */
public class SimulationVisualizer extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5687055481193363161L;
	/**
	 * 
	 */
	private ThemePark tp;
	private List<MobileSink> mobileSinkList;
	SimParam simParam;
	private double currentTime; 
	
	private Image dbImage;
	private Graphics dbGraphics;
	//private Graphics tpmGraphics;
	
	private boolean resume;
	private boolean finish;
	
	public class ActionListener extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			int keyCode = e.getKeyCode();
			if(keyCode == KeyEvent.VK_UP){
				resume = true;
			}
			else if(keyCode == KeyEvent.VK_RIGHT){
				finish = true;
			}
		}
		public void keyReleased(KeyEvent e){
			
		}
	}
	
	public SimulationVisualizer(ThemePark tp, SimParam simParam ){
		addKeyListener(new ActionListener());
		setTitle("Theme Park Simulation-Disaster");
		setSize(700, 700);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		resume=false;
		finish=false;
		this.tp = tp;
		this.currentTime = 0.0;
		this.simParam = simParam;

	}
	
	public void paint(Graphics g){
		
		dbImage = createImage(getWidth(), getHeight());
		dbGraphics = dbImage.getGraphics();
		paintComponent(dbGraphics);
		g.drawImage(dbImage, 0,0, this);

	}
	
	public void paintComponent(Graphics g){

		g = drawThemePark(g);

		repaint();
	}

	
	  
    private Graphics drawMobileSinks(Graphics g,int margin, double ratio) {
    	// draw nodes
    	if(mobileSinkList==null) return g;
		for(int i=0; i<mobileSinkList.size(); i++){
			MobileSink m = mobileSinkList.get(i);
			g.setColor(Color.orange);
			// draw a triangle to represent a mobile sink
			double scale = 16;
			
			int[] xArray = new int[3];
			int[] yArray = new int[3];
			// below point
			Point p = m.getLocation();
			
			// upper point
			xArray[0] =  margin+ (int)Math.floor(p.getX()*ratio); 		yArray[0] =(int)Math.floor(p.getY()*ratio);
			// lower point
			xArray[1] =  margin+ (int)Math.floor(p.getX()*ratio); 		yArray[1] =(int)Math.floor(p.getY()*ratio-scale);		
			// mid-far point 
			xArray[2] =  margin+ (int)Math.floor(p.getX()*ratio+scale); yArray[2] =(int)Math.floor(p.getY()*ratio-(scale/2));
			
			g.drawPolygon(xArray, yArray, 3);		
			g.fillPolygon(xArray, yArray, 3);	
			
		}
		return g;
	}

	private Graphics drawThemePark(Graphics g) {
    	// find projection proportions
    	double minBound = Math.min(getHeight(), getWidth());
    	int margin = (int)Math.floor((getWidth() - getHeight())/2);
    	double maxWidth = simParam.getTerrainDimLength();
    	double ratio = minBound/maxWidth; // projection ratio
    	
    	// draw the background
    	g.setColor(Color.BLACK);
    	g.drawRect(0,0, getWidth(), getHeight());
    	g.setColor(Color.WHITE);
    	g.drawRect(margin, 0,  getWidth() - 2*margin, getHeight());
    	g.fillRect(margin, 0,  getWidth() -2* margin, getHeight());
       	Font font = new Font ("Monospaced", Font.BOLD , 20); // Put Font.BOLD if you want to make them bold
    		g.setFont(font);
    		g.setColor(Color.RED);
    		g.drawString("Sim time:" + currentTime, getWidth()-200, getHeight()-20);
    		
    		// set a standard font for texts in the visualizer 
        	font = new Font ("Monospaced", Font.PLAIN , 14); // Put Font.BOLD if you want to make them bold
    		g.setFont(font);
    	 
    	g= drawObjects(g, margin, ratio);
   
		return g;
}
    private Graphics drawObjects(Graphics g,int margin, double ratio){
		g = drawAttractions(g,margin, ratio);
		g = drawEdges(g, margin, ratio);
		g = drawMobileSinks(g,margin,ratio);
		g= 	drawMobileSinkConnections(g,margin,ratio);
		g = drawEvents(g,margin,ratio);
		return g;
    }
    
    private Graphics drawMobileSinkConnections(Graphics g,int margin, double ratio) {
    	// draw nodes
		g.setColor(Color.blue);
	
    	if(mobileSinkList==null) return g;
		for(int i=0; i<mobileSinkList.size(); i++){
			MobileSink m1 = mobileSinkList.get(i);
			Point p1 = m1.getLocation();

			for(int j=0;j<mobileSinkList.size();j++){
				MobileSink m2 = mobileSinkList.get(j);
				Point p2=m2.getLocation();
				if(findDistanceBetweenTwoPoints(p1,p2)<simParam.getSinkCommunicationRange()){			
					double x1= p1.getX();
					double y1= p1.getY();
					double x2= p2.getX();
					double y2= p2.getY();
					g.drawLine((int)Math.floor(x1*ratio)+4, (int)Math.floor(y1*ratio)+4, (int)Math.floor(x2*ratio), (int)Math.floor(y2*ratio));	
				}
			}
		
    	}
			return g;
	}
    
    private Graphics drawAttractions(Graphics g, int margin, double ratio) {
		// draw nodes
		g.setColor(Color.gray);

		for(int i=0; i<tp.getAttractionList().size(); i++){
			g.setColor(Color.gray);

			Attraction a = tp.getAttractionList().get(i);
			Point p = a.getLocation();
			int radius=15;
			g.drawOval(margin+ (int)Math.floor(p.getX()*ratio),(int)Math.floor(p.getY()*ratio), radius, radius);
			g.fillOval(margin+ (int)Math.floor(p.getX()*ratio),(int)Math.floor(p.getY()*ratio), radius, radius);
			g.setColor(Color.RED);

			g.drawString(a.getIndex()+"", margin + radius/4+ (int)Math.floor(p.getX()*ratio) ,(int)Math.floor(p.getY()*ratio) + (radius*7)/8);
			g.drawString((a.getEventProbability()+"").substring(0,5), margin + radius/4+ (int)Math.floor(p.getX()*ratio) ,(int)Math.floor(p.getY()*ratio) - (radius*7)/8);
		}
		return g;
	}
    

	private Graphics drawEdges(Graphics g, int margin, double ratio) {
		// draw road		
		g.setColor(Color.black);
		for(int i=0; i<tp.getAttractionGraph().getGraphMatrix().length; i++){ // from each attraction
			for(int j=0; j<tp.getAttractionGraph().getGraphMatrix().length; j++){ // to each attraction
				double edgeWeight = tp.getAttractionGraph().getGraphMatrix()[i][j];
				if(edgeWeight==0){ // no edge between the vertices i and j
					continue;
				}
				double x1= tp.getAttractionList().get(i).getLocation().getX();
				double y1= tp.getAttractionList().get(i).getLocation().getY();
				double x2= tp.getAttractionList().get(j).getLocation().getX();
				double y2= tp.getAttractionList().get(j).getLocation().getY();
				g.setColor(Color.BLUE);
				if(i>j){
					g.setColor(Color.black);		
					g.drawLine((int)Math.floor(x1*ratio), (int)Math.floor(y1*ratio)+5, (int)Math.floor(x2*ratio), (int)Math.floor(y2*ratio)+5);	
					g.setColor(Color.BLUE);
					g.drawString((edgeWeight+"").substring(0,5), (int)Math.floor(ratio* (x1+x2)/2), 8+ (int)Math.floor(ratio* (y1+y2)/2));
				}
				else{
					g.setColor(Color.black);		
					g.drawLine((int)Math.floor(x1*ratio), (int)Math.floor(y1*ratio)-5, (int)Math.floor(x2*ratio), (int)Math.floor(y2*ratio)-5);	
					g.setColor(Color.BLUE);
					g.drawString((edgeWeight+"").substring(0,5), (int)Math.floor(ratio* (x1+x2)/2), (int)Math.floor(ratio* (y1+y2)/2) - 8);
				}

			}
		}
		return g;
	}

	private Graphics drawEvents(Graphics g, int margin, double ratio) {
		// draw nodes
		g.setColor(Color.red);
		for(int i=0; i<tp.getEventList().size(); i++){
			if(tp.getEventList().get(i).getType()!=EventType.Security) continue;
			if(tp.getEventList().get(i).getStartTime()<=currentTime && tp.getEventList().get(i).getEndTime()>=currentTime){
				Point p = tp.getEventList().get(i).getLocation();					
				int radius= 12;
				g.setColor(Color.RED);
				g.drawOval(margin+ (int)Math.floor(p.getX()*ratio),(int)Math.floor(p.getY()*ratio), radius, radius);
				g.fillOval(margin+ (int)Math.floor(p.getX()*ratio),(int)Math.floor(p.getY()*ratio), radius, radius);
				
			}
		}	
		return g;			
	}
	


	
	public boolean isResume() {
		return resume;
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	public void setMobileSinkList(List<MobileSink> mobileSinkList) {
		this.mobileSinkList = mobileSinkList;
	}

	public void setResume(boolean resume) {
		this.resume = resume;
	}

	public void setCurrentTime(double currentTime) {
		this.currentTime = currentTime;
	}

	
	public static double findDistanceBetweenTwoPoints(Point p1, Point p2){
		double returnValue = (p1.getX() - p2.getX()) *(p1.getX() - p2.getX());
		returnValue += (p1.getY() - p2.getY()) *(p1.getY() - p2.getY());
		return Math.sqrt(returnValue);
	}
	
	
}
