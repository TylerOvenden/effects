package introduction;

public class Distortion {
	
	int threshold;
	int gain;
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public int getSamples(short[] buffer, int length) {
		Distortion previous = null;
		int len = previous.getSamples(buffer, length);
		
		for(int i = 0; i < len; i++) {
			int sample = buffer[i];
			if(sample > threshold) 
				sample = threshold;
			if(sample < -threshold)
				sample = -threshold;
			buffer[i] = (short)(sample * gain);
				
		}
		
		
		
		return len;
	}
	
	
}
