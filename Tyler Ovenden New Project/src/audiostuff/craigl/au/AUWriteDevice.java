// AU File Writer Device Class
// Written by: Craig A. Lindley
// Last Update: 05/02/99

package audiostuff.craigl.au;

import java.io.*;
import audiostuff.craigl.utils.*;

public class AUWriteDevice extends AbstractAudio { 

	public AUWriteDevice(String fileName) {
		super("AUWriteDevice", SINK);

		// Save incoming
		this.fileName = fileName;
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

			numberOfChannels = channelsPreferred.getValue();
			sampleRate = ratePreferred.getValue();

			// We know that all modes are valid so set parameters
			setChannelsRecursive(channelsPreferred.getValue());
			setSamplingRateRecursive(ratePreferred.getValue());

			parametersNegotiated = true;
		}
		return true;
	}

	// Use an AUWrite object to write the AU file
	public boolean writeFile() {

		// Get parameters for audio file
		if (!negotiateParameters())
			return false;
		
		// Successfully nogotiated parameters
		// Instantiate AUWrite and write the file
		AUWrite auw = new AUWrite(fileName, sampleRate, numberOfChannels);
		return auw.writeFile(this);
	}

	// Private class data
	private String fileName;
	private boolean parametersNegotiated;
	private int sampleRate;
	private int numberOfChannels;
}

