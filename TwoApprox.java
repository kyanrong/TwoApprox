import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.lang.Math;

public class TwoApprox {
	
	public static void main(String[] args) {
		
		// getting input
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		ArrayList<Integer> seq = new ArrayList<Integer>();
		for(int i=0; i<n; i++) {
			seq.add(sc.nextInt());
		}
		
		Permutation p = new Permutation(seq);
		int dist = twoApprox(p);
		System.out.println("Reversal distance: " + dist);
	}
	
	private static int twoApprox(Permutation p) {
		int dist = 0;
		ArrayList<Integer> seq = p.getSeq();
		
		if(p.getBreakpoints() == 0) {
			return 0;
		}
		
		if(p.getDecrSeq().size() == 0) {
			p.setSeq(reverseIncreasing(seq, p.getIncrSeq().get(0)));
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
				p.setSeq(reverse(seq, idx_min, idx_smin));		
			}
			//lookaheadOK(p, idx_max, idx_smax
			else if(idx_smax<idx_max || (idx_max<idx_smax && lookaheadOK(p, idx_max, idx_smax))) {
				System.out.println("flip max - " + p.getMax());
				p.setSeq(reverse(seq, idx_max, idx_smax));
			}
			else {
				System.out.println("flip min max - " + p.getMin() + " " + p.getMax());
				p.setSeq(reverseMinMax(seq, idx_min, idx_max));
				System.out.println(seq);
			}
			
			if(p.getBreakpoints() == 0) {
				dist++;
				break;
			}
			if(p.getDecrSeq().size() == 0) {
				System.out.println("flip increasing");
				p.setSeq(reverseIncreasing(seq, p.getIncrSeq().get(0)));
			}
			
			dist++;
			System.out.println(seq);
			System.out.println("---------------------------------------------------------");
		}
		
		return dist;
	}
	
	private static boolean reduceBreakpoints(ArrayList<Integer> seq, int idx_a, int idx_b) {
		int start, end;
		
		if(idx_a < idx_b) {
			start = idx_a;
			end = idx_b;
		}
		else {
			start = idx_b;
			end = idx_a;
		}
		
		if(seq.get(end)==1 && start==0) {
			return true;
		}
		if(seq.get(start)==seq.size() && end==seq.size()-1) {
			return true;
		}
		if(Math.abs(seq.get(start)-seq.get(end+1)) == 1) {
			return true;
		}
		if(Math.abs(seq.get(end)-seq.get(start-1)) == 1) {
			return true;
		}
		
		return false;
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
		else {
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
			
		}
		System.out.println("here 4");
		return false;
	}
	
	// idx_a is the index of smin/max, idx_b is the index of s'min/max
	private static ArrayList<Integer> reverse(ArrayList<Integer> seq, int idx_a, int idx_b) {
		int j = 0;
		ArrayList<Integer> temp;
		
		if(idx_a < idx_b) {
			temp = new ArrayList<Integer>(seq.subList(idx_a, idx_b));
			Collections.reverse(temp);
			for(int i=idx_a; i<idx_b; i++) {
				seq.set(i, temp.get(j));
				j++;
			}
		}
		else {
			temp = new ArrayList<Integer>(seq.subList(idx_b+1, idx_a+1));
			Collections.reverse(temp);
			for(int i=idx_b+1; i<idx_a+1; i++) {
				seq.set(i, temp.get(j));
				j++;
			}
		}
		return seq;
	}
	
	private static ArrayList<Integer> reverseMinMax(ArrayList<Integer> seq, int idx_min, int idx_max) {
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
	
	private static ArrayList<Integer> reverseIncreasing(ArrayList<Integer> seq, ArrayList<Integer> strip) {
		int j = seq.indexOf(strip.get(0));
		Collections.reverse(strip);
		for(int i=0; i<strip.size(); i++) {
			seq.set(j, strip.get(i));
			j++;
		}
		return seq;
	}
}
