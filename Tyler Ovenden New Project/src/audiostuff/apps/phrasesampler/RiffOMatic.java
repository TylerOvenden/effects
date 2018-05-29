// RiffOMatic Phrase Sampler Application 
// Written by: Craig A. Lindley
// Last Update: 02/13/99

package audiostuff.apps.phrasesampler;

import java.awt.Color;
import audiostuff.craigl.utils.*;
import audiostuff.craigl.processors.PitchShifterWithUI;
import audiostuff.craigl.winrecorder.WinRecorder;
import audiostuff.craigl.winplayer.WinPlayer;

/*
Half speed playback at normal pitch is accomplished by using the
pitch shifter to shift the program material up one octave and then
doubling the number of samples. The PitchShifterWithUI class is used
to shift the pitch and the inner class, SampleDouble, is used to double
the number of samples.
*/

public class RiffOMatic extends AbstractAudio {

	// Sample at 11025 samples/second
	private static final int DEFAULTSAMPLERATE = 11025;
	
	// Inner class for doubling (and interpolating) samples.
	// First sample is normal and the second
	// sample is the average of the first and second sample.
	private static class SampleDoubler extends AbstractAudio {

		public SampleDoubler() {

			super("SampleDoubler", PROCESSOR);

			// Allocate local sample buffer
			localBuffer = new short[AudioConstants.SAMPLEBUFFERSIZE];

			availableSamples = 0;
			sampleIndex = 0;
		}

		// Method needed for AbstractAudio interface
		public int getSamples(short [] buffer, int length) {

			// Don't perform processing if stage is bypassed
			if (getByPass())
				return previous.getSamples(buffer, length);

			// Fill output buffer with length number of samples
			for (int i=0; i < length; i++) {
				// See if samples are available from previous stage
				if (availableSamples == 0) {
					sampleIndex = 0;
					availableSamples = 
						previous.getSamples(localBuffer, AudioConstants.SAMPLEBUFFERSIZE);
				}
				// Was EOF found ?
				if (availableSamples == -1) {
					// EOF was detected, reset variables
					availableSamples = 0;
					sampleIndex = 0;

					// Return EOF indication
					return -1;
				}
				// Process even and odd samples differently
				if ((i % 2) == 0) { 
					// Even index so move sample from source to destination
					buffer[i] = localBuffer[sampleIndex];
				}	else	{
					// Odd index so create new sample by interpolation
					
					// Keep second index in range.
					int sample1Index = sampleIndex + 1;
					if (sample1Index == AudioConstants.SAMPLEBUFFERSIZE)
						sample1Index--;

					buffer[i] = (short)(((int) localBuffer[sampleIndex] + 
										 (int) localBuffer[sample1Index]) / 2);
					// Update counts and indices
					sampleIndex++;
					availableSamples--;
				}
			}
			return length;
		}
		// Private class data
		private short [] localBuffer;
		private int availableSamples;
		private int sampleIndex;
	}
	
	// Class constructor
	public RiffOMatic() {

		super("RiffOMatic", PROCESSOR);

		// Create a WinRecorder for sampling
		recorder = new WinRecorder(DEFAULTSAMPLERATE,
								   WinRecorder.DEFAULTCHANNELS,
								   WinRecorder.DEFAULTDEVICEID, 
								   null);
		// Create the UI for this processor
		romui = new RiffOMaticUI(this);

		// No looping yet
		loopingMode = false;
	}

	// Process a change of the sample duration pot
	public void sampleDurationChanged(int valueInSeconds) {

		synchronized(lock) {

			// Calculate sample buffer size
			sampleBufferSize = valueInSeconds * DEFAULTSAMPLERATE;

			// Calculate number of buffers of samples required
			bufferCount = sampleBufferSize / AudioConstants.SAMPLEBUFFERSIZE;
			if ((sampleBufferSize % AudioConstants.SAMPLEBUFFERSIZE) != 0)
				bufferCount++;

			// New buffer size
			sampleBufferSize = bufferCount * AudioConstants.SAMPLEBUFFERSIZE;

			// Amount the meter should move after each buffer is filled
			meterGranularity = 100.0 / bufferCount;

			// Allocate buffer
			sampleBuffer = new short[sampleBufferSize];

			// Initialize indices
			startPlayIndex	 = 0;
			stopPlayIndex	 = sampleBufferSize - 1;
			newStopPlayIndex = stopPlayIndex;
		}
	}
		
	// Record button was clicked
	public void recordButtonPressed() {

		// Create thread for acquisition
		new Thread() {
			public void run() {
				synchronized(lock) {
					// Open sound card for recording
					if (recorder.initRecorder()) {
						// Open was successful
												
						// Fill the buffers necessary for sampling duration
						for (int i=0; i < bufferCount; i++) {
							// Update status meter
							romui.setMeterValue((int)((i + 1) * meterGranularity));

							// Get the samples into the buffer
							recorder.getSamplesWithOffset(
								sampleBuffer, i * AudioConstants.SAMPLEBUFFERSIZE);
						}
						// Close the sound card
						recorder.closeRecorder();

						// Buffer full, update indices
						startPlayIndex	 = 0;
						stopPlayIndex	 = sampleBufferSize - 1;
						newStopPlayIndex = stopPlayIndex;
					}
				}
			}
		}.start();	// Start the acquisition thread
	}
	
	// Do playback with appropriate looping
	public void doPlayBack() {

		synchronized(lock) {
			new Thread() {
				public void run() {
					// See if there is something to play
					if (newStopPlayIndex == startPlayIndex) {
						return;

					// Swap indices if necessary
					}	else if (newStopPlayIndex < startPlayIndex) {
						int tmp = startPlayIndex;
						startPlayIndex = newStopPlayIndex;
						newStopPlayIndex = tmp;
					}
					
					boolean firstTime = true;
					while (loopingMode || firstTime) {
						// Only play once unless looping
						firstTime = false;

						// Start playback at specified index
						currentPlayIndex = startPlayIndex;
						stopPlayIndex    = newStopPlayIndex;

						// Start the player
						winplayer.play();
						winplayer.stop();
					}
				}
			}.start();
		}
	}
	
	// Process the loop begin button click
	public void loopBeginButtonPressed() {

		// Record current index for start position
		startPlayIndex = currentPlayIndex;

		// Calculate position in buffer
		int percent = (currentPlayIndex * 100) / sampleBufferSize;

		// Set loop begin color
		romui.setStatusIndicatorColor(Color.red, percent, percent);
	}

	// Process the loop end button click
	public void loopEndButtonPressed() {

		// Record current index for end position
		newStopPlayIndex = currentPlayIndex;

		// Calculate position in buffer
		int percent = (currentPlayIndex * 100) / sampleBufferSize;

		// Set loop begin color
		romui.setStatusIndicatorColor(Color.red, percent, percent);
	}

	public void loopResetButtonPressed() {

		// Reset indices to start and end of buffer
		startPlayIndex	 = 0;
		newStopPlayIndex = sampleBufferSize - 1;
		
		// Reset the color of indicator
		romui.setStatusIndicatorColor(RiffOMaticUI.LETTERCOLOR, 0, 100);
	}

	// Loop mode button has been toggled
	public void loopModeButtonPressed(boolean state) {

		loopingMode = state;
	}

	// Do full speed playback
	public void fullSpeedPlayButtonPressed() {

		pswui.setByPass(true);
		sd.setByPass(true);
		doPlayBack();
	}

	// Do half speed playback
	public void halfSpeedPlayButtonPressed() {

		pswui.setPitchShift(12);
		pswui.setByPass(false);
		sd.setByPass(false);
		doPlayBack();
	}

	// Get samples from the sampled and stored data
	public int getSamples(short [] buffer, int length) {

		int count = stopPlayIndex - currentPlayIndex + 1;

		if (count == 0)
			return -1;

		if (count >= length) {
			System.arraycopy(sampleBuffer, currentPlayIndex, 
							 buffer, 0, length);
			currentPlayIndex += length;
			return length;

		}	else	{
			System.arraycopy(sampleBuffer, currentPlayIndex, 
							 buffer, 0, count);
			currentPlayIndex += count;
			return count;
		}
	}
	
	// We know this is first device in chain so no propagation
	// is necessary.
	public void minMaxSamplingRate(MyInt min, MyInt max, MyInt preferred) {
		
		// Use the sample rate passed in
		max.setValue(DEFAULTSAMPLERATE);
		min.setValue(DEFAULTSAMPLERATE);
		preferred.setValue(DEFAULTSAMPLERATE);
	}

	// We know this is first device in chain so no propagation
	// is necessary.
	public void minMaxChannels(MyInt min, MyInt max, MyInt preferred) {

		min.setValue(WinRecorder.DEFAULTCHANNELS);
		max.setValue(WinRecorder.DEFAULTCHANNELS);
		preferred.setValue(WinRecorder.DEFAULTCHANNELS);
	}

	// RiffOMatic application entry point
	public static void main(String [] args) {

		// Instantiate data structure for linking abstract audio devices
		LinkedListVector ll = new LinkedListVector();

		// Create the sampler source and link it in
		ll.addElement(new RiffOMatic());

		// Create the pitch shifter processor
		pswui = new PitchShifterWithUI();
		
		// Configure the pitch shifter
		pswui.setDryLevel(0);
		pswui.setWetLevel(100);
		pswui.setFeedbackLevel(0);
		pswui.setByPass(true);
		
		// Link in the pitch shifter
		ll.addElement(pswui);

		// Create sample doubler
		sd = new SampleDoubler();
		sd.setByPass(true);
		
		// Link in the sample doubler
		ll.addElement(sd);

		// Create a player
		winplayer = new WinPlayer();
		
		// Link in the player
		ll.addElement(winplayer);
	}

	// Private class data
	private static WinRecorder recorder;
	private static RiffOMaticUI romui;
	private static WinPlayer winplayer;
	private static PitchShifterWithUI pswui;
	private static SampleDoubler sd;
	
	private Object lock = new Object();
	private boolean loopingMode;
	private int startPlayIndex;
	private int currentPlayIndex;
	private int stopPlayIndex;
	private int newStopPlayIndex;
	private int sampleBufferSize;
	private short [] sampleBuffer;
	private int bufferCount;
	private double meterGranularity;
}

