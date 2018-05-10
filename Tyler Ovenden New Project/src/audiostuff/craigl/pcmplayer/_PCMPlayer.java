// _PCMPlayer Class
// Written by: Craig A. Lindley
// Last Update: 08/02/98

package audiostuff.craigl.pcmplayer;

import javax.media.*;
import java.io.*;
import audiostuff.craigl.utils.*;

public class _PCMPlayer extends ControllerAdapter {

    Player player = null;

	public _PCMPlayer(Player player) {

		this.player = player;

		if (player != null) 
			player.addControllerListener(this);
	}

	public synchronized void startPlayer() {

		if (player != null) {
			player.realize();

			// Wait for end of media indication
			try {
				wait();
			}
			catch(InterruptedException iee) {}
		}
	}

	// Used in conjunction with wait above and end of media event
	// to postpone return from startPlayer until media finished.
	private synchronized void signal() {

		notify();
	}
	
	public synchronized void stopPlayer() {

		if (player != null) { 
			player.close();
			notify();
		}
	}

	public static Player createPlayer(AbstractAudio aa, int channels, int sampleRate) {

		// Create a PCM data source from the parameters
	    PCMDataSource pcmds = new PCMDataSource(aa, channels, sampleRate);

	    // Create an instance of a player for this data source
		try {
			return Manager.createPlayer(pcmds);
	    }
		catch(NoPlayerException e) {
			o("Error:" + e.getMessage());
			return null;
		}
		catch(IOException e) {
			o("Error:" + e.getMessage());
			return null;
		}
   }
	
	// Handle controller/player events by overriding methods in base class
	
	// Check for insufficient data rate to player
	public void dataStarved(DataStarvedEvent e) {
	
		o("DataStarvedEvent");
	}
	
	// RealizeCompleteEvent occurs after a realize() call. 
	public void realizeComplete(RealizeCompleteEvent e) {

		o("RealizeCompleteEvent");

		// Ask the player to prefetch data and prepare to start.
		player.prefetch();
	}

	// PrefetchCompleteEvent is generated when the player has finished
	// prefetching enough data to fill its internal buffers and is
	// ready to start playing.
	public void prefetchComplete(PrefetchCompleteEvent e) {

		o("PrefetchCompleteEvent");
		
		if (player.getTargetState() != Controller.Started)
			player.start();
	}

	// EndOfMediaEvent occurs when the media file has played till the
	// end. The player is now in the stopped state.
	public void endOfMedia(EndOfMediaEvent e) {

		o("EndOfMediaEvent");
		// To loop
		// player.setMediaTime(new Time(0));
		// player.prefetch();
		
		// Send notification that media ended
		signal();
	}

	// If at any point the Player encountered an error - possibly in
	// the data stream and it could not recover from the error, it
	// generates a ControllerErrorEvent
	public void controllerError(ControllerErrorEvent e) {

		o("ControllerErrorEvent");

	    player.removeControllerListener(this);
	    player.close();
		player = null;
	} 

	// Occurs when a player is closed.
	public void controllerClosed(ControllerClosedEvent e) {

		o("ControllerClosedEvent");

		System.gc();
		System.runFinalization();
	}

	// DurationUpdateEvent occurs when the player's duration changes
	// or is updated for the first time
	public void durationUpdate(DurationUpdateEvent e) {

		o("DurationUpdateEvent");

		Time t = e.getDuration();
		o("Time: " + t);
	}

	public static void o(String s) {

		System.err.println(s);
	}
}


	    



