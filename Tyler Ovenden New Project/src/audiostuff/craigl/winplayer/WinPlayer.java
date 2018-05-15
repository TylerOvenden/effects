// WinPlayer.java

package audiostuff.craigl.winplayer;

import audiostuff.craigl.utils.*;

/**
 * WinPlayer class
 *
 * A low latency platform dependent PCM player for the Win32 environment
 *
 * @author      Craig A. Lindley
 * @date        01/05/99
 */
public class WinPlayer extends AbstractAudio {

	public static final int MINSAMPLERATE = 8000;
	public static final int MAXSAMPLERATE = 44100;
	public static final int DEFAULTSAMPLERATE = 11025;

    // Native method calls
    protected static native boolean nativeSelectDevice(int channels, int rate);
    protected static native void nativePlay(WinPlayer obj);
    protected static native void nativeReset();
    protected static native void nativeClose();
	protected static native void nativeStoreSamples(short [] buffer, int length);
	
    // Load the WinPlayer interface DLL
    static {
	    // Load the interface DLL
		System.loadLibrary("craigl/winplayer/winplayerdll");
    }

	public WinPlayer() {
		super("WinPlayer", SINK);

		initComplete = false;
		resetMode = true;

		// Allocate buffer for samples
		buffer = new short[AudioConstants.SAMPLEBUFFERSIZE];
	}

	// This method is called from the native code whenever samples
	// are required for playback.
	private int requestSamples(int length) {

		if (resetMode)
			return -1;

		// Pull samples from previous stages
		int samplesRead = previous.getSamples(buffer, length);

		if (samplesRead == -1) {
			resetMode = true;
			nativeReset();
			return -1;
		}

		// Store the samples into the native code for playing
		nativeStoreSamples(buffer, samplesRead);

		// Return total samples read
		return samplesRead;
	}
	
	// This method is needed for every AbstractAudio device
	public int getSamples(short [] buffer, int length) {
	
		System.out.println("getSamples: Should never get here");
		System.exit(1);
		return 0;
	}
	
	// Negotiate number of channels and sample rate with previous
	// AbstractAudio devices.
	public boolean selectDevice() {

		if (initComplete)
			return true;
		
		// Propose using a single channel 
		MyInt channelsMin = new MyInt(1);
		MyInt channelsMax = new MyInt(2);
		MyInt channelsPreferred = new MyInt(1);

		// Negotiate number of channels
		minMaxChannels(channelsMin, channelsMax, channelsPreferred);
		if (channelsMin.getValue() > channelsMax.getValue()) {
			System.out.println("Couldn't negotiate channels");
			return false;
		}

		// Propose using DEFAULTSAMPLERATE as the sample rate
		MyInt rateMin = new MyInt(MINSAMPLERATE);
		MyInt rateMax = new MyInt(MAXSAMPLERATE);
		MyInt ratePreferred = new MyInt(DEFAULTSAMPLERATE);

		// Negotiate sample rate
		minMaxSamplingRate(rateMin, rateMax, ratePreferred);
		if (rateMin.getValue() > rateMax.getValue()) {
			System.out.println("Couldn't negotiate rate");
			return false;
		}
		// All negotiation has been completed
		// See if the native device supports the negotiated parameters
		if (nativeSelectDevice(channelsPreferred.getValue(),
							   ratePreferred.getValue())) {

			// Found a match for requested configuration, set all
			// previous stages.
			setChannelsRecursive(channelsPreferred.getValue());
			setSamplingRateRecursive(ratePreferred.getValue());
	
			// Signal initialization has been completed
			initComplete = true;
			return true;
		}
		return false;
	}

	// Called to begin playing the PCM samples
	public synchronized void play() {

		if (selectDevice()) {
			resetMode = false;
			nativePlay(this);
		}
	}

	// Called to stop playing the PCM samples
	public void stop() {

		resetMode = true;
		nativeReset();
	}
	
	// Close the PCM player and release the underlying audio wave device
	public void close() {

		nativeClose();
	}
	
	// Private class data
	private static short [] buffer;
	private static boolean initComplete;
	private static boolean resetMode;
}

