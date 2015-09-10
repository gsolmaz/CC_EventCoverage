package controller;

import java.util.Random;

import model.Point;

public class MathFunctions {
		// commonly used mathematical operations
		
		public static double pickRandomDoubleValueBetweenTwoNumbers(double min, double max){
			Random random = new Random();
			return min + ((max-min)* random.nextDouble());
		}
		
		public static int pickRandomIntegerValueBetweenTwoNumbers(int min, int max){
			Random random = new Random();
			return (int) (min + ((max-min)* random.nextDouble()));
		}
		
		public static double findDistanceBetweenTwoPoints(Point p1, Point p2){
			double returnValue = (p1.getX() - p2.getX()) *(p1.getX() - p2.getX());
			returnValue += (p1.getY() - p2.getY()) *(p1.getY() - p2.getY());
			return Math.sqrt(returnValue);
		}
		
		public static double poissonNextTime(double rateParameter){
			Random r = new Random();
			 return -Math.log(1.0 - r.nextDouble()) / rateParameter;
		}
		
		public static int combination(int n, int k){
			  return factorial(n) / (factorial (k) * factorial (n-k));
		}

		public static int factorial(int k) {
			int result = 1;
			for(int i=1;i<=k;i++){
				result = result*i;
			}
			return result;
		}

}
