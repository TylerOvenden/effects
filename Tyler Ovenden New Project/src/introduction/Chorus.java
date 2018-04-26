package introduction;

public class Chorus {
	
	double sweepValue;
	int halfDepthSamples;
	int readIndex;
	int delayBufferSize;
	int[] delayBuffer;
	int dryLevel;
	int wetLevel;
	int feedbackLevel;
	boolean invertPhase;
	int writeIndex;
	boolean isSinLFO;
	int sampleNumber;
	int sampleRate;
	int halfDepthInSamples;
	int radiansPerSample;
	int step;
	boolean initializationComplete;
	short[] localBuffer;
	int len;

	int delayInMs;

	int numberOfChannels;
	int delayOffset = (delayInMs * sampleRate * numberOfChannels)/1000;
	protected int processMonoSamples(short[] localBuffer, short[] buffer, int len) {
		for(int i = 0; i<len; i++) {
			int inputSample = (int) localBuffer[i];
			
			double sampleOffset1 = sweepValue = halfDepthSamples;
			double sampleOffset2 = sampleOffset1 - 1;
			
			double delta = Math.abs((int) sampleOffset1 - sampleOffset2);
			int actualIndex1 = readIndex + (int) sampleOffset1;
			int actualIndex2 = readIndex++  + (int) sampleOffset2;
			boolean underflow1 = (actualIndex1 < 0);
			boolean underflow2 = (actualIndex2 < 0);
			
			if(underflow1)
				actualIndex1 += delayBufferSize;
			else 
				actualIndex1 &= delayBufferSize;
			
			if(underflow2)
				actualIndex2 += delayBufferSize;
			else 
				actualIndex2 &= delayBufferSize;
			
			
			int delaySample1 = (int)delayBuffer[actualIndex1];
			int delaySample2 = (int)delayBuffer[actualIndex2];
			int delaySample = (int)(delaySample2 * delta + delaySample1 * ( 1.0 - delta));
			
			
			int outputSample = ((inputSample * dryLevel) /100) + ((delaySample * wetLevel));
			
			if(outputSample > 32767)
				outputSample = 32767;
			else if(outputSample < -32768)
				outputSample = -32768;
		
			buffer[i] = (short)outputSample;
			
			inputSample += (delaySample * feedbackLevel * ( invertPhase ? -1:+1))/100;
		
			delayBuffer[writeIndex++] = inputSample;
			
			readIndex %= delayBufferSize;
			writeIndex %= delayBufferSize;
			
			if(isSinLFO) {
				sampleNumber %= sampleRate;
				sweepValue = halfDepthInSamples * Math.sin(radiansPerSample * sampleNumber++);
			}
			else {
				sweepValue += step;
				
				if((sweepValue >= halfDepthInSamples) || (sweepValue  <= -halfDepthInSamples))
					step *= -1;
			}
			
		}
		return len;
	}
	
	public int getSamples(short[] buffer, int length) {
		Chorus previous = null;
		if(getByPass() || !initializationComplete)
			return previous.getSamples(localBuffer, length);
		
		if(len == -1)
			return -1;
		
		if(numberOfChannels == 1)
			return processMonoSamples(localBuffer, buffer, len);
		return processStereoSamples(localBuffer, buffer, len);
	}

	private int processStereoSamples(short[] localBuffer2, short[] buffer, int len2) {
		// TODO Auto-generated method stub
		return 0;
	}

	private boolean getByPass() {
		// TODO Auto-generated method stub
		return false;
	}
	

}
