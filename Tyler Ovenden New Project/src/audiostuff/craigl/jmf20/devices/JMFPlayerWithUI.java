// JMF Media Player With UI Code
// Written by: Craig A. Lindley
// Last Update: 06/05/99

package audiostuff.craigl.jmf20.devices;

import java.io.*;

// The java media packages
import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.protocol.*;

import audiostuff.craigl.beans.blinker.*;
import audiostuff.craigl.utils.*;
import audiostuff.craigl.jmf20.media.protocol.intfc.*;

/**
 * JMFPlayerWithUI class
 *
 * A player device built using JMF2.0
 *
 * @author      Craig A. Lindley
 * @date        06/05/99
 */
public class JMFPlayerWithUI extends AbstractAudio {

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

	/**
	 * JMFPlayerWithUI Class Constructor
	 */
	public JMFPlayerWithUI(Blinker blink) {
		super("JMFPlayerWithUI", SINK);

		// Register protocol package prefix so out data source can be found
		JMFFileBase.registerProtocolPrefix(JMFFileBase.PACKAGEPREFIX, false);
		
		// Register our demultiplexer
		JMFFileBase.registerDemultiplexer(JMFFileBase.DEMULTIPLEXER, false);

		initComplete = false;

		// Instantiate UI
		jpui = new JMFPlayerUI(blink, this);
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
	
		// Try to
		try {
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

			// Create a realized player
			player = Manager.createRealizedPlayer(dataSource);
			
			// Setup a controller listener for the player to listen for
			// end of media
			player.addControllerListener(new StateListener());

			if (DEBUG)
				System.out.println("player: " + player);
			
			// Signal initialization has been completed
			initComplete = true;
			return true;
		}
		catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
			return false;
		}
	}

	// Called to begin playing
	public synchronized void play() {

		if (doNegotiation()) {
			// Set player to the beginning of the media
			player.setMediaTime(new Time(0));
			
			// Start the player
			player.start();
			
			// Wait for end of media indication
			try {
				wait();
			}
			catch(InterruptedException iee) {}
		}
	}

	// Called to stop playing the PCM samples
	public synchronized void stop() {

		if (player != null) {
			player.stop();
			notify();
		}
	}
	
	// Close the player
	public synchronized void close() {

		if (player != null) {
			player.close();
			notify();
		}
	}

	public synchronized void signal() {

		notify();
	}

	public void showUI(boolean isVisible) {

		jpui.showUI(isVisible);
	}


	// Private class data
	private boolean initComplete;
	private JMFPlayerUI jpui;
	private int sampleRate;
	private int channels;
	private Player player;
	private boolean failure = false;
	private Integer stateLock = new Integer(1);
}

