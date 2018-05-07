package introduction;

public class Distortion {
	
	public static final int THRESHOLD = 23;
	int gain;
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public int getSamples(short[] buffer, int length) {
		Distortion previous = null;
		int len = previous.getSamples(buffer, length);
		
		for(int i = 0; i < len; i++) {
			int sample = buffer[i];
			if(sample > THRESHOLD) 
				sample = THRESHOLD;
			if(sample < -THRESHOLD)
				sample = -THRESHOLD;
			buffer[i] = (short)(sample * gain);
				
		}
		
		
		
		return len;
	}
	
	
}
