// JMF2.0 File Reader With UI Code
// Written by: Craig A. Lindley
// Last Update: 06/03/99

package audiostuff.craigl.jmf20.devices;

import java.awt.*;
import java.io.*;
import java.util.*;

// The java media packages
import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.format.audio.*;
import javax.media.protocol.*;

import audiostuff.craigl.beans.blinker.*;
import audiostuff.craigl.utils.*;
import audiostuff.craigl.jmf20.media.datasink.intfc.Handler;

public class JMFFileWithUI extends JMFFileBase implements JMFFileCallBackIF, AudioUIIF {

	private static final boolean DEBUG = false;
	private static final boolean DOWNCONVERT = false;

	// Inner class place holder for AbstractAudio device for reading audio files 
	private class BogusDevice extends AbstractAudio {

		public BogusDevice() {
			
			super("Bogus Audio Device", SOURCE);
		}

		public int getSamples(short [] buffer, int length) { return -1; }
	}
	
	/**
	 * Class Constructor
	 *
	 * @param Blinker blink is the blinker to be passed to UI
	 */
	public JMFFileWithUI(Blinker blink) {

		// Register prefix
		registerPackagePrefix(PACKAGEPREFIX, false);

		// Create a bogus AbstractAudio device so this device can be linked
		aa = new BogusDevice();

		// Instantiate UI class
		jfui = new JMFFileUI(blink, this);
	}

	public boolean createMediaLocator(String fileName) {

		// Prepend datasource tag to filename
		fileName = "file://" + fileName;

		if (DEBUG)
			System.out.println("createMediaLocator file specifier: " + fileName);

		// Attempt to create a media locator for the specified file
		sourceMediaLocator = new MediaLocator(fileName);
		
		return (sourceMediaLocator != null);
	}

	public boolean jmfPrepare() {

		try {
			if (processor != null) {
				processor.close();
				processor = null;
			}

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

			// Advance processor to configure state
			processor.configure();
			
			// Wait until we get there
			waitForState(processor, Processor.Configured);

			// Get the track controls from the processor
			TrackControl [] controls = processor.getTrackControls();

			// Attempt to find an audio track control
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

			// Extract parameters of audio source
			double sampleRate = af.getSampleRate();
			int channels   = af.getChannels();

			// Debug out
			if (DEBUG)
				System.out.println("Orig AudioFormat: " + af);
			
			// This should work but it doesn't yet
			if (DOWNCONVERT) {
				if (sampleRate == 44100)
					sampleRate = 22050;
			}

			// Create new format inforcing sample size, byte ordering
			// and signed samples. Samples leaving processor must be
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

			// Create a datasink from the media locator
			DataSink datasink = 
				Manager.createDataSink(processor.getDataOutput(), destMediaLocator);

			if (DEBUG)
				System.out.println("DataSink: " + datasink);

			// We found our data sink. It is the real AbstractAudio device
			AbstractAudio ab = (Handler) datasink;

			// Link in the new device in place of the bogus device
			ab.next = aa.next;
			aa.next.previous = ab;
			aa = ab;

			// Pass sample rate and channels for negotiation
			((Handler) datasink).setSampleRate((int) af.getSampleRate());
			((Handler) datasink).setChannels(af.getChannels());
			
			// Install callback
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
		jfui.signalReadComplete();
		
		// Stop processor in case it is still running
		processor.stop();
		
		// Reset to beginning of media
		processor.setMediaTime(new Time(0));
	}

	public void signalPlaybackBegun() {

		jfui.signalReadBegun();
		
		// Start/Restart processor
		processor.start();
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
	private AbstractAudio aa;
	private JMFFileUI jfui;
	private DataSource dataSource;
	private Processor processor;
	private MediaLocator sourceMediaLocator; 
}