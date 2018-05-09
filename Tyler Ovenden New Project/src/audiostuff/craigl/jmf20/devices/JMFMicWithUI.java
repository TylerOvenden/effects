// JMF2.0 Sample Acquisition with UI Code
// Written by: Craig A. Lindley
// Last Update: 06/14/99

package craigl.jmf20.devices;

import java.awt.*;
import java.io.*;
import java.util.*;

// The java media packages
import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.format.audio.*;
import javax.media.protocol.*;

import craigl.beans.blinker.*;
import craigl.utils.*;
import craigl.jmf20.media.datasink.intfc.Handler;

public class JMFMicWithUI extends JMFFileBase implements JMFFileCallBackIF, AudioUIIF {

	private static final boolean DEBUG = true;
	private static final boolean DOWNCONVERT = false;

	// Inner class place holder for AbstractAudio device for reading audio files 
	private class BogusDevice extends AbstractAudio {

		public BogusDevice() {
			
			super("Bogus Audio Device", SOURCE);
		}

		public int getSamples(short [] buffer, int length) { 
			System.out.println("Device has not been selected. Please try again.");
			System.exit(1);
			return -1;
		}
	}
	
	/**
	 * Class Constructor
	 *
	 * @param Blinker blink is the blinker to be passed to UI
	 */
	public JMFMicWithUI(Blinker blink, int sampleRate, int channels) {

		// Save incoming suggestions
		this.sampleRate = sampleRate;
		this.channels = channels;
		
		// Register prefix
		registerPackagePrefix(PACKAGEPREFIX, false);

		// Create a bogus AbstractAudio device so this device can be linked
		aa = new BogusDevice();

		// Instantiate UI class
		jfui = new JMFMicUI(blink, this);
	}

	// Return vector of devices that support the format specified. Vector
	// contains CaptureDeviceInfo objects.
	public Vector getDeviceList(int sampleRate, int channels) {

		double rSampleRate = (sampleRate == -1) ? Format.NOT_SPECIFIED:sampleRate;
		int rChannels      = (channels == -1)   ? Format.NOT_SPECIFIED:channels;

		// Define audio format we are interested in
		AudioFormat naf = new AudioFormat(
									AudioFormat.LINEAR,
									rSampleRate,
									16,
									rChannels,
									AudioFormat.LITTLE_ENDIAN,
									AudioFormat.SIGNED);
		
		// Return only those devices which support this format
		return CaptureDeviceManager.getDeviceList(naf);
	}

	// Given the capture devices media locator, create devices for
	// acquiring samples and passing them to the AbstractAudio signal
	// chain.
	public boolean jmfPrepare(MediaLocator sourceMediaLocator) {

		try {
			// Create a data source from media locator
			dataSource = Manager.createDataSource(sourceMediaLocator);

			if (DEBUG)
				System.out.println("DataSource: " + dataSource);

			// Create a processor from the data source
			processor = Manager.createProcessor(dataSource);

			if (DEBUG)
				System.out.println("Processor: " + processor);

			if (processor == null) {
				System.out.println("createProcessor method returned null");
				return false;
			}

			// Advance processor to configure state to get its track controls
			processor.configure();
			
			// Wait until we get there
			waitForState(processor, Processor.Configured);

			// Get the track controls from the processor
			TrackControl [] controls = processor.getTrackControls();

			// Attempt to find any audio track control
			TrackControl tc = null;
			for (int t=0; t < controls.length; t++) {
				TrackControl tc1 = controls[t];
				
				// Get format of track
				Format f = tc1.getFormat();
				
				// Skip if not an audio format track
				if (f instanceof AudioFormat) {
					tc = tc1;
					break;
				}
			}
			if (tc == null) {
				System.out.println("Couldn't find an audio track in media");
				return false;
			}
			// We found an audio track to use
			AudioFormat af = (AudioFormat) tc.getFormat();

			// Extract parameters of audio source needed for configuring
			// data sink
			double sampleRate = af.getSampleRate();
			int channels = af.getChannels();

			// Debug out
			if (DEBUG)
				System.out.println("AudioFormat: " + af);
			
			// Create new format inforcing sample size, byte ordering, number
			// or channels and signed samples. Samples leaving processor must be
			// 16 bit, little endian and signed to fit with the processing
			// architecture presented in this book.
			AudioFormat naf = new AudioFormat(AudioFormat.LINEAR,
								 sampleRate,
								 16,
								 channels,
								 AudioFormat.LITTLE_ENDIAN,
								 AudioFormat.SIGNED);
			// Attempt to set this format on track
			tc.setFormat(naf);

			// Set output descriptor for processor
			processor.setOutputContentDescriptor(
				new ContentDescriptor(ContentDescriptor.RAW));

			// Now wait until processor transitions to realized state
			waitForState(processor, Processor.Realized);

			// Create a MediaLocator for our datasink
			MediaLocator destMediaLocator = new MediaLocator("intfc:");

			// Create a datasink from process data source and the media locator
			datasink = 
				Manager.createDataSink(processor.getDataOutput(), destMediaLocator);

			if (DEBUG)
				System.out.println("DataSink: " + datasink);

			// We found our data sink. It is the real AbstractAudio device
			AbstractAudio ab = (Handler) datasink;

			// Link in the datasink device in place of the bogus device in
			// the AbstractAudio signal chain.
			ab.next = aa.next;
			aa.next.previous = ab;
			aa = ab;

			// Pass sample rate and channels for negotiation to the data
			// sink device.
			((Handler) datasink).setSampleRate((int) af.getSampleRate());
			((Handler) datasink).setChannels(af.getChannels());
			
			// Install callback so data sink can call back into this code
			((Handler) datasink).setCallBack(this);

			// If we get here we are good to go
			if (DEBUG)
				System.out.println("Prepare was successful");

			return true;
		}
		catch(Exception e) {
			System.out.println("Prepare error: " + e.getMessage());
			return false;
		}
	}

	// The next two methods are called from intfc Handler to signal
	// significant events.
	public void signalReset() {

		// Signal UI that reset has occurred
		jfui.setRecordLEDState(false);

		((Handler) datasink).setEOM();
		
		// Stop processor in case it is still running
		processor.stop();
		processor.setMediaTime(new Time(0));
	}

	public void signalPlaybackBegun() {

		// Indicate recording has begun
		jfui.setRecordLEDState(true);

		// Start processor
		processor.start();
	}

	// Called from UI when capture device is selected
	public void captureDeviceSelected() {

		// Ask for media locator for capture device and
		// Prepare for acquisition
		jmfPrepare(jfui.getMediaLocator());
	}
	

	public void stopCapture() {

		signalReset();
	}

	// Return AbstractAudio device for linking
	public AbstractAudio getAA() {

		return aa;
	}

	public void showUI(boolean isVisible) {
        
        jfui.setVisible(isVisible);
    }
	
    public void stopUI() {
        
        aa.setByPass(true);
    }

    public Rectangle getBounds() {

        return jfui.getBounds();
    }
    
    public void setBounds(int x, int y, int w, int h) {

        jfui.setBounds(x, y, w, h);
    }

	// Class data
	public int sampleRate;
	public int channels;
	private AbstractAudio aa;
	private JMFMicUI jfui;
	private DataSource dataSource;
	private Processor processor;
	private DataSink datasink;
}