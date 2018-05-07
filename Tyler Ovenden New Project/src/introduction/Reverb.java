package introduction;

public class Reverb {
	public static final double COMB1DELAYMSDEF = 29.7;
	public static final double COMB2DELAYMSDEF = 37.1;
	public static final double COMB3DELAYMSDEF = 41.1;
	public static final double COMB4DELAYMSDEF = 43.7;
	public static final double ALLPASS1DELAYMSDEF = 5.0;
	public static final double ALLPASS2DELAYMSDEF = 1.7;
	public static final double ALLPASS1SUSTAINMSDEF = 96.8;
	public static final double ALLPASS2SUSTAINMSDEF = 32.9;
	double gain;
	int delayInMs;
	int sustainTimeInMs;
	

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void calcGain() {
		//calculate gain for this filter such that a recirculating sample will 
		//reduce in level 60db in the specified sustain time
		gain = Math.pow(0.001, delayInMs/ sustainTimeInMs);
	}

	
	
}
