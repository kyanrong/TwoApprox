// A0083834Y

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

class TwoApprox { 
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
    
    public static void main(String[] args) throws NumberFormatException, IOException {
        //long start = System.currentTimeMillis();

        int T = Integer.parseInt(new StringTokenizer(br.readLine()).nextToken());
        for(int i=0; i<T; i++) {
        	br.readLine();
        	StringTokenizer st = new StringTokenizer(br.readLine());
			ArrayList<Integer> seq = new ArrayList<Integer>();
			while(st.hasMoreTokens()) {
				seq.add(Integer.parseInt(st.nextToken()));
			}
			Permutation p = new Permutation(seq);
			int dist = twoApprox(p);
			pw.write(dist+"\n");
		}
        
        //long end = System.currentTimeMillis();
        //pw.write("time(secs): " + (end-start)/1000.0);

        pw.close();
        br.close();
    }

	private static int twoApprox(Permutation p) {
		int dist = 0;
		
		if(p.getBreakpoints() == 0) {
			return 0;
		}
		
		if(p.getDecrSeq().size() == 0) {
			//System.out.println("flip increasing - " + p.getIncrSeq().get(0));
			p.setSeq(flipIncreasing(p.getSeq(), p.getIncrSeq().get(0)));	
			dist++;
			//System.out.println(p.getSeq());
		}
		
		while(p.getBreakpoints() > 0) {
			int idx_min = p.getMinIdx();
			int idx_max = p.getMaxIdx();
			int idx_smin = p.getSMinIdx();
			int idx_smax = p.getSMaxIdx();                    // index of s'min and s'max
			
			//System.out.println("idx_min: " + idx_min + ", idx_smin: " + idx_smin);
			//System.out.println("idx_max: " + idx_max + ", idx_smax: " + idx_smax);
			
			// first condition guarantees there will be a decreasing strip
			if((idx_min<idx_smin) || lookaheadOK(p.getSeq(), "min", idx_smin, idx_min)) {
				//System.out.println("flip min - " + p.getMin());	
				p.setSeq(flip(p.getSeq(), "min", idx_min, idx_smin));
				dist++;
				//System.out.println(p.getSeq());
				//System.out.println("---------------------------------------------------------");
			}
			
			else if(idx_smax<idx_max || lookaheadOK(p.getSeq(), "max", idx_max, idx_smax)) {
				//System.out.println("flip max - " + p.getMax());
				p.setSeq(flip(p.getSeq(), "max", idx_max, idx_smax));
				dist++;
				//System.out.println(p.getSeq());
				//System.out.println("---------------------------------------------------------");
			}
			
			else {
				//System.out.println("flip min max - " + p.getMin() + " " + p.getMax());
				p.setSeq(flipMinMax(p.getSeq(), idx_min, idx_max));
				dist++;
				//System.out.println(p.getSeq());
				//System.out.println("---------------------------------------------------------");
				if(p.getBreakpoints()>0) {
					//System.out.println("flip increasing - " + p.getIncrSeq().get(0));
					p.setSeq(flipIncreasing(p.getSeq(), p.getIncrSeq().get(0)));
					dist++;
					//System.out.println(p.getSeq());
					//System.out.println("---------------------------------------------------------");
				}
			}
		}
		
		return dist;
	}

	private static ArrayList<Integer> flipMinMax(ArrayList<Integer> seq, int idx_min, int idx_max) {
		if(idx_min < idx_max) {
			Collections.reverse(seq.subList(idx_min, idx_max+1));
		}
		else {
			Collections.reverse(seq.subList(idx_max, idx_min+1));
		}
		return seq;
	}

	private static ArrayList<Integer> flip(ArrayList<Integer> seq, String type, int idx_m, int idx_sm) {
		if(type.equals("min")) {
			if(idx_m < idx_sm) {
				Collections.reverse(seq.subList(idx_m+1, idx_sm+1));
			}
			else {
				Collections.reverse(seq.subList(idx_sm+1, idx_m+1));
			}
		}
		else {
			if(idx_m < idx_sm) {
				Collections.reverse(seq.subList(idx_m, idx_sm));
			}
			else {
				Collections.reverse(seq.subList(idx_sm, idx_m));
			}
		}
		
		return seq;
	}

	//idx_a < idx_b
	private static boolean lookaheadOK(ArrayList<Integer> seq, String type, int idx_a, int idx_b) {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		temp.addAll(seq);

		if(type.equals("min")) {
			Collections.reverse(temp.subList(idx_a+1, idx_b+1));
		}
		
		else {
			Collections.reverse(temp.subList(idx_a, idx_b));
		}
		
		if(hasDecreasing(temp)) {
			return true;
		}
		return false;
	}

	private static ArrayList<Integer> flipIncreasing(ArrayList<Integer> seq, Pair strip) {
		Collections.reverse(seq.subList(strip.getStart(), strip.getEnd()));
		return seq;
	}
	
	private static boolean hasDecreasing(ArrayList<Integer> seq) {
		int[] diff = new int[seq.size()-1];
		for(int i=0; i<diff.length; i++) {
			diff[i] = seq.get(i+1) - seq.get(i);
		}
		int start = 0;
		for(int j=0; j<diff.length; j++) {
			// if is a continuous strip
			if(Math.abs(diff[j])==1 && diff[j]==diff[start]) {
				continue;
			}
			if(start > 0) {
				if(diff[start]!=1) {
					return true;
				}
			}
			start = j+1;
		}
		return false;
	}
}

class Permutation {
	
	int size, min, max, breakpoints;
	int min_idx, max_idx;
	ArrayList<Integer> seq;
	ArrayList<Pair> incr;
	ArrayList<Pair> decr;
	
	Permutation(ArrayList<Integer> input) {
		seq = input;
		seq.add(seq.size()+1);
		seq.add(0, 0);
		size = seq.size();
		
		update();
	}
	
	private void update() {
		incr = new ArrayList<Pair>();
		decr = new ArrayList<Pair>();
		min = size;
		max = 0;
		breakpoints = 0;
		min_idx = max_idx = -1;
		
		preprocess();
		
		/*System.out.println("---------------------------------------------------------");
		System.out.println("breakpoints: " + breakpoints); 
		System.out.print("increasing, "); printSeq(incr); 
		System.out.print("decreasing, "); printSeq(decr); 
		System.out.println("min: " + min + ", max: " + max); 
		System.out.println("min idx: " + min_idx + ", max_idx: " + max_idx);*/		
	}
	
	private void preprocess() {
		int[] diff = new int[size-1];
		for(int i=0; i<diff.length; i++) {
			diff[i] = seq.get(i+1) - seq.get(i);
		}
		int start = 0;
		for(int j=0; j<diff.length; j++) {
			// if is a continuous strip
			if(Math.abs(diff[j])==1 && diff[j]==diff[start]) {
				continue;
			}
			if(start > 0) {
				if(diff[start]==1) {
					incr.add(new Pair(start, j+1));
				}
				else {
					decr.add(new Pair(start, j+1));
					findMinMax(start, j+1);
				}
				breakpoints++;
			}
			start = j+1;
		}
	}
	
	private void findMinMax(int start, int end) {
		int min_temp = seq.get(end-1);
		int max_temp = seq.get(start);
		if(min_temp<min && min_temp!=0) {
			min = min_temp;
			min_idx = end-1;
		}
		if(max_temp>max && max!=size-1) {
			max = max_temp;
			max_idx = start;
		}
	}
	
	public int getSize() {
		return size;
	}
	
	public int getBreakpoints() {
		return breakpoints;
	}
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
	public int getMinIdx() {
		return min_idx;
	}
	
	public int getMaxIdx() {
		return max_idx;
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
	
	public ArrayList<Pair> getDecrSeq() {
		return decr;
	}
	
	public ArrayList<Pair> getIncrSeq() {
		return incr;
	}
	
	public void setSeq(ArrayList<Integer> newseq) {
		seq = newseq;
		update();
	}

}

// start and end indexes of a strip
class Pair {
	
	private int start;
	private int end;			// end index is exclusive
	
	public Pair(int s, int e) {
		start = s;
		end = e;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
}