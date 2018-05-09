// JMF File Writer With UI Code
// Written by: Craig A. Lindley
// Last Update: 06/09/99

package craigl.jmf20.devices;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

// The java media packages
import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.format.audio.*;
import javax.media.protocol.*;

import craigl.au.*;
import craigl.beans.blinker.*;
import craigl.utils.*;
import craigl.uiutils.*;

public class JMFFileWriterWithUI extends AbstractAudio {

	private static final boolean DEBUG = true;

	// Parameters for negotiation
	public static final int MINCHANNELS   = 1;
	public static final int MAXCHANNELS   = 2;
	public static final int DEFAULTCHANNELS = MINCHANNELS;

	public static final int MINSAMPLERATE = 11025;
	public static final int MAXSAMPLERATE = 44100;
	public static final int DEFAULTSAMPLERATE = MINSAMPLERATE;

	// Inner classes for listening to states
    public class StateListener implements ControllerListener {

		public void controllerUpdate(ControllerEvent ce) {
			
			if (ce instanceof EndOfMediaEvent) {

				signal();
			
			}	else if (ce instanceof ControllerClosedEvent) {
				
				System.out.println("ControllerClosedEvent");

			}	else if (ce instanceof ControllerErrorEvent) {
				
				// Error occurred
				failure = true;
				String failMessage = ((ControllerErrorEvent) ce).getMessage();
				System.out.println(failMessage);

			}	else if (ce instanceof ControllerEvent) {
					synchronized (getStateLock()) {
						getStateLock().notifyAll();
					}
			}
		}
    }


    public Integer getStateLock() {
		
		return stateLock;
    }

	// Wait for processor to arrive at the specified state
	public synchronized boolean waitForState(Processor p, int state) {

		p.addControllerListener(new StateListener());
		failure = false;

		if (state == Processor.Configured)
			p.configure();
		else if (state == Processor.Realized)
			p.realize();

		while (p.getState() < state && !failure) {
			synchronized (getStateLock()) {
				try	{
					getStateLock().wait();
				}
				catch (InterruptedException ie) {
					return false;
				}
			}
		}
		return failure? false:true;
	}

	// Class Constructor
	public JMFFileWriterWithUI(Blinker blink) {
		super("JMF File Writer with UI", SINK);

		// Register protocol package prefix so out data source can be found
		JMFFileBase.registerProtocolPrefix(JMFFileBase.PACKAGEPREFIX, false);
		
		// Register our demultiplexer
		JMFFileBase.registerDemultiplexer(JMFFileBase.DEMULTIPLEXER, false);

		initComplete = false;

		// Instantiate UI for the file writer
		jmffw = new JMFFileWriterUI(blink, this);
	}

	// This method is needed for every AbstractAudio device
	public int getSamples(short [] buffer, int length) {
	
		System.out.println("getSamples: Should never get here");
		System.exit(1);
		return 0;
	}
	
	// Negotiate number of channels and sample rate with previous
	// AbstractAudio devices.
	public boolean doNegotiation() {

		if (initComplete)
			return true;
		
		// Propose using MINCHANNELS as the number of channels 
		MyInt channelsMin = new MyInt(MINCHANNELS);
		MyInt channelsMax = new MyInt(MAXCHANNELS);
		MyInt channelsPreferred = new MyInt(DEFAULTCHANNELS);

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
		// All negotiation has been completed. Extract parameters.
		sampleRate = ratePreferred.getValue();
		channels = channelsPreferred.getValue();
		
		// Inform all previous stages
		setSamplingRateRecursive(sampleRate);
		setChannelsRecursive(channels);

		// Success
		initComplete = true;

		return true;
	}

	// Called when end of media is signalled
	public synchronized void signal() {

		notify();
	}

	// Set the output filename
	public void setOutputFilename(String fileName) {

		// Do some parameter extraction to prepare transcoding
		bitsPerSample = 16;
		encodingName = "LINEAR";

		if (fileName.endsWith(".mp3")) {
			encodingName = "mpeglayer3";
			bitsPerSample = 0;
		}	else if (fileName.endsWith(".au"))
			bitsPerSample = 8;

		// Create output media locator
		outputMediaLocator = new MediaLocator("file://" + fileName);
	}

	// Write the output file from the audio samples provided by the
	// AbstractAudio device.
	public synchronized boolean writeOutputFile() {
		
		// If not output locator bail
		if (outputMediaLocator == null)
			return false;

		try {
			// If a processor is running close it down
			if (processor != null) {
				processor.close();
				processor = null;
			}

			// Create a media locator for our datasource
			MediaLocator sourceMediaLocator = new MediaLocator("intfc:");

			// Create a data source from media locator
			PullDataSource dataSource = 
				(PullDataSource) Manager.createDataSource(sourceMediaLocator);

			// Set some parameters into the data source
			((craigl.jmf20.media.protocol.intfc.DataSource) dataSource).setAA(this);
			((craigl.jmf20.media.protocol.intfc.DataSource) dataSource).setSampleRate(sampleRate);
			((craigl.jmf20.media.protocol.intfc.DataSource) dataSource).setChannels(channels);

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
			// Create audio format for transcoding
			AudioFormat af = new AudioFormat(
									encodingName,
									sampleRate,
									bitsPerSample,
									channels,
									Format.NOT_SPECIFIED,
									Format.NOT_SPECIFIED);
			// Set the format on the track
			tc.setFormat(af);

			// Set processor output content descriptor
			processor.setOutputContentDescriptor(
			    new ContentDescriptor(ContentDescriptor.RAW));

			// Advance processor to configure state
			processor.realize();
			
			// Wait until we get there
			waitForState(processor, Processor.Realized);

			// Create a data sink for writing the file
			DataSink dataSink = Manager.createDataSink(processor.getDataOutput(),
											           outputMediaLocator);
			if (DEBUG)
				System.out.println("DataSink: " + dataSink);
			
			// Write the file
			dataSink.open();
			dataSink.start();
			processor.start();
			
			// Indicate success
			return true;
		}
		catch(Exception e) {
			System.out.println("Error writing output file: " + e.getMessage());
			return false;
		}
	}

	public void showUI(boolean isVisible) {

		jmffw.showUI(isVisible);
	}

	// Private class data
	private int bitsPerSample;
	public int sampleRate;
	public int channels;
	private String encodingName;
	private boolean initComplete;
	private MediaLocator outputMediaLocator;
	private Processor processor;
	private boolean failure = false;
	private Integer stateLock = new Integer(1);
	private JMFFileWriterUI jmffw;
}

