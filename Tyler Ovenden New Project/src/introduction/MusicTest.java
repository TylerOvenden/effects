package introduction;

public class MusicTest {
/** hard distortion: goes straight to peak after a certain level
 * for (int i = 0; i < duration; i++)
{
    double sample = samples[i] * gain_pre;
    if (sample > clip)
        sample = clip;
    else
    if (sample < -clip)
        sample = -clip;
    samples[i] = sample * gain_post;
}
 * regular/soft distortion: expoentential change, gradual, creates a curve rather than flat spikes
 * 
 * double max = 1.0 / (1.0 - exp(-gain_pre));
for (int i = 0; i < duration; i++)
{
    double sample = samples[i] * gain_pre;
    double z = (sample < 0.0) ? (-1.0 + exp(sample)) :
                                (1.0 - exp(-sample));   
    samples[i] = z * max * gain_post;
}

	flangereffect: 
	need to calculate the index of this delay:
	
	delayPos = (Math.sin(sPos)*(depth) + constantDelay;
	sPosn += deltaSound;
	

	
   	





 * 
 * 
 */
	
}
