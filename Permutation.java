import java.util.ArrayList;
import java.util.Collections;


public class Permutation {
	
	static int size, min, max, breakpoints;
	static ArrayList<Integer> seq;
	static ArrayList<Integer> identity;
	static ArrayList<ArrayList<Integer>> incr;
	static ArrayList<ArrayList<Integer>> decr;
	
	Permutation(ArrayList<Integer> input) {
		seq = input;
		size = seq.size();
		identity = (ArrayList<Integer>) seq.clone();
		Collections.reverse(identity);
		
		update();
	}
		
	private static void update() {
		incr = new ArrayList<ArrayList<Integer>>();
		decr = new ArrayList<ArrayList<Integer>>();
		min = size;
		max = 0;
		breakpoints = 0;
		
		getBreakpointsAndStrips();
		getMinAndMax();
		
		/*System.out.println("---------------------------------------------------------");
		System.out.println("breakpoints: " + breakpoints); 
		System.out.print("increasing, "); printSeq(incr); 
		System.out.print("decreasing, "); printSeq(decr); 
		System.out.println("min: " + min + ", max: " + max);*/
		
	}
	
	private static void getBreakpointsAndStrips() {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		temp.add(seq.get(0));
		
		for(int i=0; i<size-1; i++) {
			if(Math.abs(seq.get(i)-seq.get(i+1)) != 1) {
				breakpoints++;
				sortStrip(temp);
				temp = new ArrayList<Integer>();
				temp.add(seq.get(i+1));
			}
			else {
				temp.add(seq.get(i+1));
			}
		}
		
		sortStrip(temp);
		
		if(Math.abs(seq.get(0)-0) != 1) { breakpoints++;}
		if(Math.abs(seq.get(size-1)-(size+1)) != 1) { breakpoints++;}
	} 
	
	private static void sortStrip(ArrayList<Integer> strip) {
		
		if(strip.size()==1 && strip.get(0)==1 && seq.indexOf(strip.get(0))==0) {
			incr.add(strip);
		}
		else if(strip.size()==1 && strip.get(0)==size && seq.indexOf(strip.get(0))==size-1) {
			incr.add(strip);
		}
		else if(strip.get(0) < strip.get(strip.size()-1)) {
			incr.add(strip);
		}
		else {
			decr.add(strip);
		}
		
	}
	
	private static void getMinAndMax() {
		for(int i=0; i<decr.size(); i++) {
			Object min_temp = Collections.min(decr.get(i));
			Object max_temp = Collections.max(decr.get(i));
			if((Integer)min_temp < min) min = (Integer) min_temp;
			if((Integer)max_temp > max) max = (Integer) max_temp;
		}
	}
	
	private static void printSeq(ArrayList<ArrayList<Integer>> s) {
		System.out.println("size: " + s.size());
		for(int i=0; i<s.size(); i++) {
			for(int j=0; j<s.get(i).size(); j++) {
				System.out.print(s.get(i).get(j) + " ");
			}
			System.out.println();
		}
	}
	
	public static int getSize() {
		return size;
	}
	
	public static int getBreakpoints() {
		return breakpoints;
	}
	
	public static ArrayList<Integer> getIdentity() {
		return identity;
	}
	
	public static int getMin() {
		return min;
	}
	
	public static int getMax() {
		return max;
	}
	
	public static int getSMinIdx() {
		if(min == 1) {
			return -1;
		}
		else {
			return seq.indexOf(min-1);
		}
	}
	
	public static int getSMaxIdx() {
		if(max == size) {
			return size-1;
		}
		else {
			return seq.indexOf(max+1);
		}
	}
	
	public static ArrayList<Integer> getSeq() {
		return seq;
	}
	
	public static ArrayList<ArrayList<Integer>> getDecrSeq() {
		return decr;
	}
	
	public static ArrayList<ArrayList<Integer>> getIncrSeq() {
		return incr;
	}
	
	public static void setSeq(ArrayList<Integer> newseq) {
		update();
	}

}