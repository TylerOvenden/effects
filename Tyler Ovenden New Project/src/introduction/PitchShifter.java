package introduction;

public class PitchShifter {

	boolean initializationComplete;
	short[] localBuffer;
	short[] delayBuffer;
	int readIndexALow;
	int readIndexAHigh;
	int readIndexBLow;
	int readIndexBHigh;
	boolean sweepUp;
	int sweep;
	int blendA;
	int blendB;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public int getSamples(short[] buffer, int length) {
		PitchShifter previous = null;
		if(getByPass() || !initializationComplete)
			return previous.getSamples(buffer,length);
	
		int len =  previous.getSamples(localBuffer, length);
		double delaySampleA, delaySampleB;
		
		for(int i = 0; i < len; i++){
			long inputSample = localBuffer[i];
			
			long dsALow = delayBuffer[readIndexALow];
			long dsAHigh = delayBuffer[readIndexAHigh];
			long dsBLow = delayBuffer[readIndexBLow];
			long dsBHigh = delayBuffer[readIndexBHigh];
			
			if(sweepUp) {
				delaySampleA = (dsAHigh * sweep)+(dsALow *(1.0-sweep));
				delaySampleB = (dsBHigh * sweep)+(dsBLow *(1.0-sweep));
			}	
			else {
				delaySampleA = (dsAHigh*(1.0-sweep) )+(dsALow* sweep );
				delaySampleB = (dsBHigh*(1.0-sweep) )+(dsBLow* sweep );
			}	
			double outputSample = (delaySampleA * blendA) +(delaySampleB *blendB);
			}
		}
	

	private boolean getByPass() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
