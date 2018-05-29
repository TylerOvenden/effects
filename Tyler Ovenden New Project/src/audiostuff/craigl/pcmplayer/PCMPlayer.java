// PCMPlayer Class
// Written by: Craig A. Lindley
// Last Update: 11/11/98

package audiostuff.craigl.pcmplayer;

import javax.media.*;
import audiostuff.craigl.utils.*;

/**
 * PCMPlayer class utilizing JMF
 */

public class PCMPlayer extends AbstractAudio {

	public PCMPlayer() {
		
		super("PCMPlayer", SINK);
	}

	public int getSamples(short [] buffer, int length) {
	
		System.out.println("getSamples: Should never get here");
		System.exit(1);
		return 0;
	}
	
	public boolean negotiateParameters() {

		if (!parametersNegotiated) {

			// Get everyone else's idea of format
			MyInt channelsMin = new MyInt(1);
			MyInt channelsMax = new MyInt(2);
			MyInt channelsPreferred = new MyInt(1);

			minMaxChannels(channelsMin, channelsMax, channelsPreferred);
			if (channelsMin.getValue() > channelsMax.getValue()) {
				System.out.println("Couldn't negotiate channels");
				return false;
			}

			MyInt rateMin = new MyInt(8000);
			MyInt rateMax = new MyInt(44100);
			MyInt ratePreferred = new MyInt(22050);

			minMaxSamplingRate(rateMin, rateMax, ratePreferred);
			if (rateMin.getValue() > rateMax.getValue()) {
				System.out.println("Couldn't negotiate rate");
				return false;
			}

			channels = channelsPreferred.getValue();
			sampleRate = ratePreferred.getValue();

			// We know that all modes are valid so set parameters
			setChannelsRecursive(channelsPreferred.getValue());
			setSamplingRateRecursive(ratePreferred.getValue());
		
			parametersNegotiated = true;
		}
		
		// Create a player each time through
		Player player = _PCMPlayer.createPlayer(this, channels, sampleRate);
		if (player == null)
			return false;

		// Wrap the player
		pcmPlayer = new _PCMPlayer(player);

		return true;
	}

	public boolean play() {

		if (negotiateParameters()) {
			pcmPlayer.startPlayer();
			return true;
		}	else
			return false;
	}

	public void stop() {

		if (pcmPlayer != null)
			pcmPlayer.stopPlayer();
	}

	// Private class data
	private _PCMPlayer pcmPlayer = null;
	private boolean parametersNegotiated = false;
	private int channels;
	private int sampleRate;
}

