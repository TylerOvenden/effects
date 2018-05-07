package introduction;

public class Delay {

	int delayInMs;
	int writeIndex;
	int sampleRate;
	short[] localBuffer;
	int numberOfChannels;
	int readIndex;
	int wetLevel;
	int dryLevel;
	int delayBufferSize;
	short[] delayBuffer;
	boolean intializationComplete;
	public static final int PEAK = 32767;
	public static final int BOTTOM = -32768;
	int delayOffset = (delayInMs * sampleRate * numberOfChannels)/1000;
	private Delay previous;

	
	delayBufferSize = AudioConstants.SAMPLEBUFFERSIZE + delayOffset;
	
	delayBuffer = new short[delayBufferSize];
	writeIndex = 0;
	
	readIndex = AudioConstants.SAMPLEBUFFERSIZE;
	
	

	public int getSamples(short [] buffer, int length) {
		if(getByPass() || !intializationComplete)
			return previous.getSamples(buffer,length);
		int len = previous.getSamples(localBuffer, length);
		
		for(int i = 0;i < len;i++) {
			int inputSample = (int) localBuffer[i];
			int delaySample = (int) delayBuffer[readIndex++];
			int outputSample = ((inputSample * dryLevel)/100)+ ((delaySample * wetLevel)/100);
			
			if(outputSample >PEAK)
				outputSample = PEAK;
			if(outputSample<BOTTOM)
				outputSample = BOTTOM;
			
			delayBuffer[writeIndex++] = (short) inputSample;
			
			readIndex %= delayBufferSize;
			writeIndex %= delayBufferSize;
			
		}
		return len;
	}

	private boolean getByPass() {
		// TODO Auto-generated method stub
		return false;
	}
}
