 package introduction;

public class PitchShifter {

	boolean initializationComplete;
	short[] localBuffer;
	short[] delayBuffer;
	short[] fadeA;
	short[] fadeB;
	short[] fadeOut;
	short[] fadeIn;
	int readIndexALow;
	int readIndexAHigh;
	int readIndexBLow;
	int readIndexBHigh;
	boolean sweepUp;
	double sweep;
	double blendA;
	double blendB;
	int feedbackLevel;
	int writeIndex; 
	double dryLevel;
	double wetLevel;
	int crossFadeCount;
	int numberOfChannels;
	double step;
	int delayBufferSize;
	int activeCount;
	int numberOfCrossFadeSamples;
	int activeSampleCount;
	boolean channelA;
	private static final double twelvethRootOfTwo = Math.pow(2, 1.0/12.0);
	int numberOfDelaySamples;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public int getSamples(short[] buffer, int length) {
		PitchShifter previous = null;
		//don't perform processing until initialization is complete and bypass not active
		if(getByPass() || !initializationComplete)
			return previous.getSamples(buffer,length);
		//read number of samples from previous stage
		int len =  previous.getSamples(localBuffer, length);
		double delaySampleA, delaySampleB;
		
		for(int i = 0; i < len; i++){
		
			long inputSample = localBuffer[i];
			//take 4 samples at a time for the interpolation and blending
			long dsALow = delayBuffer[readIndexALow];
			long dsAHigh = delayBuffer[readIndexAHigh];
			long dsBLow = delayBuffer[readIndexBLow];
			long dsBHigh = delayBuffer[readIndexBHigh];
			
			//do the linear interpolation
			if(sweepUp) {
				delaySampleA = (dsAHigh * sweep)+(dsALow *(1.0-sweep));
				delaySampleB = (dsBHigh * sweep)+(dsBLow *(1.0-sweep));
			}	
			else {
				delaySampleA = (dsAHigh*(1.0-sweep) )+(dsALow* sweep );
				delaySampleB = (dsBHigh*(1.0-sweep) )+(dsBLow* sweep );
			}	
			//combine delay channels A & B with appropriate 
			double outputSample = (delaySampleA * blendA) +(delaySampleB *blendB);
			
			
			delayBuffer[writeIndex] = (short)(inputSample + ((outputSample * feedbackLevel)/100));
		
			writeIndex = (writeIndex + 1)% delayBufferSize;
			
			outputSample = ((inputSample * dryLevel)/100)+ ((outputSample * wetLevel)/100);
			
			if(outputSample > 32767)
				outputSample = 32767;
			if(outputSample < -32768)
				outputSample = -32768;
			
			//store output sample in outgoing sample
			buffer[i] = (short) outputSample;
			
			//update cross fade blending 
			if(crossFadeCount != 0) {
				crossFadeCount--;
				blendA = fadeA[crossFadeCount];
				blendB = fadeB[crossFadeCount];
			}
			
			
			//update sweep value for each pass if processing mono
			// and every other pass if processing stereo 
			if((numberOfChannels ==1) || (i+1) % 2 ==0 )
				sweep += step;
		
			if(sweepUp) {
				//upward frequency change
				//advance indices to reduce delay
				readIndexALow = readIndexAHigh;
				readIndexAHigh = (readIndexAHigh +1)%delayBufferSize; 
				readIndexBLow = readIndexBHigh;
				readIndexBHigh = (readIndexBHigh +1)%delayBufferSize; 
			
				if(sweep<1.0) {
					//no overflow
					continue;
				}
				//
				sweep = 0.0;
				readIndexALow = readIndexAHigh;
				readIndexAHigh = (readIndexAHigh +1)%delayBufferSize; 
				readIndexBLow = readIndexBHigh;
				readIndexBHigh = (readIndexBHigh +1)%delayBufferSize; 
				
				if(activeCount ==0) {
					crossFadeCount = numberOfCrossFadeSamples;
					activeCount = activeSampleCount;
			
					if(channelA) {
					channelA = !channelA;
					readIndexBHigh = (writeIndex + AudioConstants.SAMPLEBUFFERSIZE) % delayBufferSize;
					fadeA = fadeOut;
					fadeB = fadeIn;
					}	else {
					channelA = !channelA;
					readIndexAHigh = (writeIndex + AudioConstants.SAMPLEBUFFERSIZE) % delayBufferSize;
					fadeA = fadeIn;
					fadeB = fadeOut;
						
					}
				} else {
					//downward frequency change
					
					if(sweep <1.0) {
						readIndexALow = readIndexAHigh;
						readIndexAHigh = (readIndexAHigh +1)%delayBufferSize; 
						readIndexBLow = readIndexBHigh;
						readIndexBHigh = (readIndexBHigh +1)%delayBufferSize; 
						
						continue;
					}
					//octave exceeded don't bump indices so the delay is increased
					sweep = 0.0;
					
					if(activeCount-1 == 0) {
						crossFadeCount = numberOfCrossFadeSamples;
						activeCount = activeSampleCount;
						if(channelA) {
							channelA = !channelA;
							readIndexBHigh = (writeIndex + AudioConstants.SAMPLEBUFFERSIZE) % delayBufferSize;
							fadeA = fadeOut;
							fadeB = fadeIn;
							
						}
						else {
							channelA = !channelA;
							readIndexAHigh = (writeIndex + AudioConstants.SAMPLEBUFFERSIZE) % delayBufferSize;
							fadeA = fadeIn;
							fadeB = fadeOut;
								
						}
						
					}
					
				}
			}
			 
		}
		return len;
	}

	public void setPitchShift(int pitchShift) {
		//values are in half steps (semitones) in the range -12 to 12 corresponding -/+ octave for a range of 2 octaves
		//determine what direction the sweep is going
		sweepUp = (pitchShift >= 0);
		setIndices();
		double newStep = 1.0;
		if(pitchShift == 0) 
			step = 0;
		else {
			for(int i = 0; i< Math.abs(pitchShift);i++) {
				if(pitchShift >0)
					newStep *= twelvethRootOfTwo;
				else 
					newStep /= twelvethRootOfTwo;
			}
			step = Math.abs(newStep -1.0);
		}
		sweep = 0.0;
		crossFadeCount = 0;
		activeSampleCount = numberOfDelaySamples - (int)(numberOfCrossFadeSamples *(newStep -1.0)-2);
		}
	
		
	

	private void setIndices() {
		// TODO Auto-generated method stub
		
	}

	private boolean getByPass() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
