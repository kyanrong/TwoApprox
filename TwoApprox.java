import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.lang.Math;

class TwoApprox {
	
	public static void main(String[] args) {
		
		// getting input
		Scanner sc = new Scanner(System.in);
		int T = sc.nextInt();
		for(int i=0; i<T; i++) {
			int n = sc.nextInt();
			ArrayList<Integer> seq = new ArrayList<Integer>();
			for(int j=0; j<n; j++) {
				seq.add(sc.nextInt());
			}
			Permutation p = new Permutation(seq);
			int dist = twoApprox(p);
			System.out.println(dist);
		}
		sc.close();
	}
	
	private static int twoApprox(Permutation p) {
		int dist = 0;
		ArrayList<Integer> seq = p.getSeq();
		
		if(p.getBreakpoints() == 0) {
			return 0;
		}

		if(p.getDecrSeq().size() == 0) {
			System.out.println("flip increasing - " + p.getIncrSeq().get(0));
			p.setSeq(flipIncreasing(seq, p.getIncrSeq().get(0)));	
			System.out.println(p.getSeq());
		}
		
		while(p.getBreakpoints() > 0) {
			int idx_min = seq.indexOf(p.getMin());		
			int idx_max = seq.indexOf(p.getMax());
			int idx_smin = p.getSMinIdx();
			int idx_smax = p.getSMaxIdx();                    // index of s'min and s'max
			
			System.out.println("idx_min: " + idx_min + ", idx_smin: " + idx_smin);
			System.out.println("idx_max: " + idx_max + ", idx_smax: " + idx_smax);
			
			//1st condition guarantees there will be a decreasing strip
			//2nd condition need to check if there exists a decreasing strip after flipping
			//lookaheadOK(p, idx_min, idx_smin
			if((idx_min<idx_smin) || (idx_smin<idx_min && lookaheadOK(p, idx_smin, idx_min))) {
				System.out.println("flip min - " + p.getMin());	
				p.setSeq(flip(seq, idx_min, idx_smin));
			}
			//lookaheadOK(p, idx_max, idx_smax
			else if(idx_smax<idx_max || (idx_max<idx_smax && lookaheadOK(p, idx_max, idx_smax))) {
				System.out.println("flip max - " + p.getMax());
				p.setSeq(flip(seq, idx_max, idx_smax));
			}
			else {
				System.out.println("flip min max - " + p.getMin() + " " + p.getMax());
				p.setSeq(flipMinMax(seq, idx_min, idx_max));
				System.out.println(seq);
			}
			
			if(p.getBreakpoints() == 0) {
				dist++;
				break;
			}
			if(p.getDecrSeq().size() == 0) {
				System.out.println("flip increasing - " + p.getIncrSeq().get(0));
				p.setSeq(flipIncreasing(seq, p.getIncrSeq().get(0)));
			}
			
			dist++;
			System.out.println(seq);
			System.out.println("---------------------------------------------------------");
		}
		
		return dist;
	}
	
	// idx_a < idx_b
	private static boolean lookaheadOK(Permutation p, int idx_a, int idx_b) {
		int start, end;
		
		start = idx_a;
		end = idx_b;
		
		// if there still exists at least 1 decreasing strip after flipping
		if(p.getDecrSeq().size()-1 > 0) {
			System.out.println("here");
			return true;
		}

		ArrayList<ArrayList<Integer>> incr = p.getIncrSeq();
		// if there exists an increasing strip within start and end,
		// which will become a decreasing strip after flipping
		for(int i=0; i<incr.size(); i++) {
			int ele = incr.get(i).get(0);
			if(p.getSeq().indexOf(ele) > start && p.getSeq().indexOf(ele) < end) {
				System.out.println("here 2");
				return true;
			}
		}
			
		if(end+1 < p.getSize()) {
			if(p.getSeq().get(start+1) - p.getSeq().get(end+1) > 0) {
				System.out.println("here 3");
				return true;
			}
		}
			
		System.out.println("here 4");
		return false;
	}
	
	private static ArrayList<Integer> flip(ArrayList<Integer> seq, int idx_m, int idx_sm) {
		int j = 0;
		ArrayList<Integer> temp;
		
		if(idx_m < idx_sm) {
			temp = new ArrayList<Integer>(seq.subList(idx_m+1, idx_sm+1));
			Collections.reverse(temp);
			for(int i=idx_m+1; i<idx_sm+1; i++) {
				seq.set(i, temp.get(j));
				j++;
			}
		}
		else {
			temp = new ArrayList<Integer>(seq.subList(idx_sm+1, idx_m+1));
			Collections.reverse(temp);
			for(int i=idx_sm+1; i<idx_m+1; i++) {
				seq.set(i, temp.get(j));
				j++;
			}
		}
		
		return seq;
	}
	
	private static ArrayList<Integer> flipMinMax(ArrayList<Integer> seq, int idx_min, int idx_max) {
		int j = 0;
		ArrayList<Integer> temp;
		
		if(idx_min < idx_max) {
			temp = new ArrayList<Integer>(seq.subList(idx_min, idx_max+1));
			Collections.reverse(temp);
			for(int i=idx_min; i<idx_max+1; i++) {
				 seq.set(i, temp.get(j));
				 j++;
			 }
		}
		else {
			temp = new ArrayList<Integer>(seq.subList(idx_max, idx_min+1));
			Collections.reverse(temp);
			for(int i=idx_max; i<idx_min+1; i++) {
				 seq.set(i, temp.get(j));
				 j++;
			 }
		}
		return seq;
	}
	
	private static ArrayList<Integer> flipIncreasing(ArrayList<Integer> seq, ArrayList<Integer> strip) {
		int j = seq.indexOf(strip.get(0));
		Collections.reverse(strip);
		for(int i=0; i<strip.size(); i++) {
			seq.set(j, strip.get(i));
			j++;
		}
		return seq;
	}
}

class Permutation {
	
	int size, min, max, breakpoints;
	ArrayList<Integer> seq;
	ArrayList<Integer> identity;
	ArrayList<ArrayList<Integer>> incr;
	ArrayList<ArrayList<Integer>> decr;
	
	@SuppressWarnings("unchecked")
	Permutation(ArrayList<Integer> input) {
		seq = input;
		seq.add(seq.size()+1);
		seq.add(0, 0);
		size = seq.size();
		identity = (ArrayList<Integer>) seq.clone();
		Collections.reverse(identity);
		
		update();
	}
		
	private void update() {
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
	
	private void getBreakpointsAndStrips() {
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
	} 
	
	private void sortStrip(ArrayList<Integer> strip) {
		if(strip.contains(0) || strip.contains(size-1)) {
			return;
		}
		if(strip.get(0) < strip.get(strip.size()-1)) {
			incr.add(strip);
		}
		else {
			decr.add(strip);
		}
	}
	
	// excludes 0 and n+1
	private void getMinAndMax() {
		for(int i=0; i<decr.size(); i++) {
			Object min_temp = Collections.min(decr.get(i));
			Object max_temp = Collections.max(decr.get(i));
			if((Integer)min_temp<min && (Integer)min_temp!=0) min = (Integer) min_temp;
			if((Integer)max_temp>max && (Integer)max_temp!=size-1) max = (Integer) max_temp;
		}
	}
	
	@SuppressWarnings("unused")
	private void printSeq(ArrayList<ArrayList<Integer>> s) {
		System.out.println("size: " + s.size());
		for(int i=0; i<s.size(); i++) {
			System.out.println(s.get(i));
		}
	}
	
	public int getSize() {
		return size;
	}
	
	public int getBreakpoints() {
		return breakpoints;
	}
	
	public ArrayList<Integer> getIdentity() {
		return identity;
	}
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
	public int getSMinIdx() {
		return seq.indexOf(min-1);
	}
	
	public int getSMaxIdx() {
		return seq.indexOf(max+1);
	}
	
	public ArrayList<Integer> getSeq() {
		return seq;
	}
	
	public ArrayList<ArrayList<Integer>> getDecrSeq() {
		return decr;
	}
	
	public ArrayList<ArrayList<Integer>> getIncrSeq() {
		return incr;
	}
	
	public void setSeq(ArrayList<Integer> newseq) {
		seq = newseq;
		update();
	}

}