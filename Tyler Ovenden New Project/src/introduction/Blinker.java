// Blinker Data Source
// Written by: Craig A. Lindley
// Last Update: 02/31/99

package introduction;

import java.io.*;
import java.beans.*;

/**
 * Blinker is an invisible bean that fires a property
 * change event at a regular specified interval.
 */
public class Blinker implements Runnable {

    private static final int DEFAULTINTERVAL = 100;
	
	/**
	 * Class Constructor
	 *
	 * @param int milliSeconds is the period of the blink
	 */
	public Blinker(int milliSeconds) {

		// Save incoming period interval
		interval = milliSeconds;

        reset();
    }

    /**
	 * Class constructor which uses the default interval
	 */
	public Blinker() {

		this(DEFAULTINTERVAL);
	}
    
	/**
	 * Create a new thread to run this blinker
	 */
	private void reset() {
        runner = new Thread(this);
        runner.start();
    }

    /**
	 * Return the blink interval
	 *
	 * @return int containing the current interval in milliseconds
	 */
	public int getInterval() {
        return interval;
    }

    /**
	 * Set the current blinker interval
	 *
	 * @param int milliseconds is the new blinker interval
	 */
	public void setInterval(int milliSeconds) {
        
		interval = milliSeconds;
        if (runner != null) 
            runner.interrupt();
    }

    /**
	 * Run method runs a infinite loop
	 */
	public void run() {

		while(true) {
            try {
                // Sleep for the interval
				Thread.sleep(interval);
				
				// Toggle pulse stream
				pulse = !pulse;

				// Fire change event
				listeners.firePropertyChange("blink", null, 
					pulse ? Boolean.TRUE:Boolean.FALSE);
            } catch (InterruptedException ignore) {}
        }
    }
    
    /**
	 * Register a property change listener
	 *
	 * @param PropertyChangeListener l is the listener to register
	 */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        listeners.addPropertyChangeListener(l);
    }
    
    /**
	 * Remove a property change listener
	 *
	 * @param PropertyChangeListener l is the listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener l) {
        listeners.removePropertyChangeListener(l);
    }

    // Private class data
    private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private int interval;
    private boolean pulse = false;
	private transient Thread runner = null;
}
