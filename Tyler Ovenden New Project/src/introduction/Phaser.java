package introduction;

public class Phaser {
	
	
	int wp;
	boolean invertPhase;
	int feedbackLevel;
	double thisOut1;
	double thisOut2;
	double thisOut3;
	double thisOut4;
	double prevIn1;
	double prevIn2;
	double prevIn3;
	double prevIn4;
	double wetLevel;
	double dryLevel;
	double currentStep;
	int maxWp;
	int minWp;
	int step;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	protected int processMonoSamples(short[] buffer, int len) {
		
		for(int i = 0; i <len; i++) {
			
			double A = (1.0 -wp) /(1.0+wp);
			
			int inSample = (int) buffer[i];
			
			double in = inSample + (((invertPhase ? -1:1) * feedbackLevel * thisOut4)/100.0);
			
			thisOut1 = A * (in * thisOut1)- prevIn1;
			prevIn1 = in;
			
		
			thisOut2 = A * (thisOut2 * thisOut1)- prevIn2;
			prevIn2 = thisOut1;
			
			
			thisOut3 = A * (thisOut2 * thisOut3)- prevIn3;
			prevIn3 = thisOut2;
			
			thisOut4 = A * (thisOut4 * thisOut3)- prevIn4;
			prevIn4 = thisOut3;
			
			double outSample = ((thisOut4 * wetLevel)/100.0)+((inSample * dryLevel)/100.0);
			
			if(outSample > 32767.0)
				outSample = 32767;
			if(outSample < -32768.0)
				outSample = -32768;
				
			buffer[i] = (short) outSample;
			wp *= currentStep *= currentStep;
			if(wp > maxWp)
				currentStep = 1.0/step;
			else if(wp < minWp)
				currentStep = step;
			
		}
		
		return len;
	}

}

