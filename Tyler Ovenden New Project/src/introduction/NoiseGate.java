package introduction;

public class NoiseGate {
	
	boolean intializationComplete;
	int thresholdValue;
	int releaseCount;
	int calcReleaseCount;
	boolean attackExpired;
	boolean limiting;
	int atRatio;
	int attackCount;
	int calcAttackCount;
	int btRatio;
	int gain;
	
	public int getSamples(short[] buffer, int length) {
		NoiseGate previous = null;
		int len = previous.getSamples(buffer,length);
		
		if(getByPass() || !intializationComplete)
			return len;
		double sample = 0;
		for(int i=0; i<len;i++) {
			
		 sample = (double)buffer[i];
		 
		 	if(Math.abs(sample)>= thresholdValue) {
				releaseCount++;
				releaseCount %= (calcReleaseCount + 1);
				if(attackExpired) {
					if(!limiting)
						sample *= atRatio;
					else 
						sample = (sample <0) ? -thresholdValue : thresholdValue;
				}
				else {
					attackCount--;
					if(attackCount <= 0) {
						attackExpired = true;
						releaseCount = calcReleaseCount;
						
					}
				}
			}	
			 else {
				attackCount++;
				attackCount = calcAttackCount;
			}
			
		 		sample *= btRatio;
			
		}
		 	sample *= gain;
			
		 	if(sample > 32767.0)
				sample = 32767;
			else if (sample < -32768.0) 
				sample = -32768.0;
		}
//	return len;
	}


	private boolean getByPass() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
