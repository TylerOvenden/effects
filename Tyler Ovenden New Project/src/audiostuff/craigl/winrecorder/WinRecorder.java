// WinRecorder.java
// Written by: Craig A. Lindley
// Last Update: 01/18/99

package audiostuff.craigl.winrecorder;

import audiostuff.craigl.utils.*;

public class WinRecorder extends AbstractAudio {

    public static final int DEFAULTSAMPLERATE = 22050;
    public static final int DEFAULTCHANNELS   = 1;
    public static final int DEFAULTDEVICEID   = 0;
	
	// Native method calls
    protected native boolean nativeHasSoundCard();
    protected native boolean nativeInitRecorder(int sampleRate, int numberOfChannels, int deviceID);
    protected native void nativeResetRecorder();
    protected native boolean nativeCloseRecorder();
    protected native int nativeGetSamples(short [] buffer, int offset);
	
    // Load the WinRecorder interface DLL
    static {
	    // Load the interface DLL
		System.loadLibrary("craigl/winrecorder/winrecorderdll");
    }

	public WinRecorder(int sampleRate, int numberOfChannels,
					   int deviceID, RecorderIF recordIF) {
		super("WinRecorder", SOURCE);

		// Save incoming
		this.sampleRate = sampleRate;
		this.numberOfChannels = numberOfChannels;
		this.deviceID = deviceID;
		this.recordIF = recordIF;

		running = false;
	}

	// Zero argument constructor
	public WinRecorder() {
		
		this(DEFAULTSAMPLERATE, DEFAULTCHANNELS, DEFAULTDEVICEID, null);
	}
	
	// Check for presence of sound card in system
	public boolean hasSoundCard() {

		return nativeHasSoundCard();
	}

	public boolean initRecorder() {

		return nativeInitRecorder(sampleRate, numberOfChannels, deviceID);
	}
	
	public boolean closeRecorder() {

		signalled = false;
		nativeResetRecorder();
		return nativeCloseRecorder();
	}

	public void minMaxSamplingRate(MyInt min, MyInt max, MyInt preferred) {
		
		// Use the sample rate passed in
		max.setValue(sampleRate);
		min.setValue(sampleRate);
		preferred.setValue(sampleRate);
	}

	public void minMaxChannels(MyInt min, MyInt max, MyInt preferred) {

		// Use the number of channels passed in
		min.setValue(numberOfChannels);
		max.setValue(numberOfChannels);
		preferred.setValue(numberOfChannels);
	}

	// NOTE: length is ignored here. Buffers of size
	// AudioConstants.SAMPLEBUFFERSIZE or smaller are always returned.
	public int getSamples(short [] buffer, int length) {
		
		// If recorder has been stopped, return end of file
		if (running && !signalled) {
			running = false;
			return -1;
		}

		if (!signalled && (recordIF != null)) {
			// Signal that recording to begun
			recordIF.beginRecording();
			signalled = true;
			running = true;
		}
		return nativeGetSamples(buffer, 0);
	}

	// NOTE: Buffers of size AudioConstants.SAMPLEBUFFERSIZE or smaller
	// are always returned.
	public int getSamplesWithOffset(short [] buffer, int offset) {

		return nativeGetSamples(buffer, offset);
	}

	// Private class data
	private int sampleRate;
	private int numberOfChannels;
	private int deviceID;
	private RecorderIF recordIF;
	private boolean signalled = false;
	private boolean running = false;
}

