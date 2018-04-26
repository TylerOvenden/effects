package introduction;

public class Delay {

	int delayInMs;
	int writeIndex;
	int sampleRate;
	short[] localBuffer;
	int numberOfChannels;
	boolean intializationComplete;
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
		
		for(int i = 0;i < len;i++)
	}

	private boolean getByPass() {
		// TODO Auto-generated method stub
		return false;
	}
}
