// DataGen Random Data Source
// Written by: Craig A. Lindley
// Last Update: 03/10/99

package audiostuff.craigl.beans.datagen;

import java.io.*;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;

/**
 * DataGen is a random source of data used for testing things
 * like meters and such. The values generated
 * are between 0 and 100. The time interval between data samples
 * is controlled by the interval set. Since DataGen implements
 * Adjustable many methods are implemented that are currently unused.
 */
public class DataGen implements Runnable, Adjustable {

    private static final int DEFAULTINTERVAL = 50;
	
	/**
	 * Class Constructor
	 *
	 * @param int milliSeconds is the period between new data
	 * samples
	 */
	public DataGen(int milliSeconds) {

		// Save incoming
		interval = milliSeconds;

        reset();
    }

    /**
	 * Class constructor which uses the default interval
	 */
    public DataGen() {

		this(DEFAULTINTERVAL);
	}
    
	/**
	 * Create a new thread to run this data source
	 */
	private void reset() {
        runner = new Thread(this);
        runner.start();
    }

    /**
	 * Return the data generator interval
	 *
	 * @return int containing the current interval in milliseconds
	 */
    public int getInterval() {
        return interval;
    }

    /**
	 * Set the current data generator interval
	 *
	 * @param int milliseconds is the new interval
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

				// Generate and send some random data
				fireAdjustmentEvent();

            } catch (InterruptedException ignore) {}
        }
    }
    
	// Method necessary to implement Adjustable

	/**
	 * Gets the orientation of the adjustable object.
	 */
	public int getOrientation() { return 0; }

	/**
	 * Sets the minimum value of the adjustable object.
	 *
	 * @param min the minimum value
	 */
	public void setMinimum(int min) {}

	/**
	 * Gets the minimum value of the adjustable object.
	 */
	public int getMinimum() { return 0; }

	/**
	 * Sets the maximum value of the adjustable object.
	 * @param max the maximum value
	 */
	public void setMaximum(int max) {}

	/**
	 * Gets the maximum value of the adjustable object.
	 */
	public int getMaximum() { return 100; }

	/**
	 * Sets the unit value increment for the adjustable object.
	 * @param u the unit increment
	 */
	public void setUnitIncrement(int u) {}

	/**
	 * Gets the unit value increment for the adjustable object.
	 */
	public int getUnitIncrement() { return 1; }

	/**
	 * Sets the block value increment for the adjustable object.
	 * @param b the block increment
	 */
	public void setBlockIncrement(int b) {}

	/**
	 * Gets the block value increment for the adjustable object.
	 */
	public int getBlockIncrement() { return 1; }

	/**
	 * Sets the length of the proportionl indicator of the
	 * adjustable object.
	 * @param v the length of the indicator
	 */
	public void setVisibleAmount(int v) {} 

	/**
	 * Gets the length of the propertional indicator.
	 */
	public int getVisibleAmount() { return 1; }

	/**
	 * Sets the current value of the adjustable object. This
	 * value must be within the range defined by the minimum and
	 * maximum values for this object.
	 * @param v the current value 
	 */
	public void setValue(int v) { value = v; }

	/**
	 * Gets the current value of the adjustable object.
	 */
	public int getValue() { return value; }

	/**
	 * Add a listener to recieve adjustment events when the value of
	 * the adjustable object changes.
	 *
	 * @param l the listener to receive events
	 */    
	public synchronized void addAdjustmentListener(AdjustmentListener l) {
	
		adjustmentListener = AWTEventMulticaster.add(adjustmentListener, l);
	}

	/**
	 * Removes an adjustment listener.
	 *
	 * @param l the listener being removed
	 */ 
	public synchronized void removeAdjustmentListener(AdjustmentListener l){
		
		adjustmentListener = AWTEventMulticaster.remove(adjustmentListener, l);
	}

	/**
	 * Fire an adjustment event containing random data
	 */
	public void fireAdjustmentEvent() {

		// Generate a random data value between 0 and 100
		value = (int) (Math.random() * 100);
		
		// Synchronously notify the listeners so that they are 
		// guaranteed to be up-to-date with the Adjustable before
		// it is mutated again.
		AdjustmentEvent e = 
			new AdjustmentEvent(this, 
								AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED,
								AdjustmentEvent.TRACK, value);
		// Send it out if there is a listener
		if (adjustmentListener != null)
			adjustmentListener.adjustmentValueChanged(e);
	}

    // Private class data
    private int interval;
	private int value;
	private transient Thread runner = null;
    private AdjustmentListener adjustmentListener = null;
}
