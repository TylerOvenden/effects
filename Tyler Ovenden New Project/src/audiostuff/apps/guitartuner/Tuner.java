// Guitar Tuner Application 
// Written by: Craig A. Lindley
// Last Update: 04/01/99

package audiostuff.apps.guitartuner;

import java.awt.*;
import craigl.beans.leds.LabeledLED;
import craigl.filters.*;
import craigl.scope.*;
import craigl.utils.*;
import craigl.winrecorder.WinRecorder;

public class Tuner extends AbstractAudio {

	/**
	 * This application is a tuner for guitars and basses. A fast Fourier
	 * transform is used to determine the frequency of the vibrating open
	 * strings. A meter and LEDs exist in the user interface to indicate
	 * the note being tuned and how close to the reference the note is.
	 * This application is "tuned" for guitar but works for basses because
	 * of the harmonics they generate when a string is plucked.
	 */
	// Sample at 11025 samples/second
	public static final int DEFAULTSAMPLERATE	= 11025;
	
	private static final int A440				= 440;
	private static final double A440BY32		= A440/32.0;
	private static final int CENTSPEROCTIVE		= 1200;
	private static final int CENTSTOLERANCE		= 3;
	private static final int CENTSHALFHALFSTEP	= 50;
	private static final int CENTSMETERMAX		= CENTSHALFHALFSTEP;
	
	private static final double THRESHOLD		= 150.0;

	// Note tables for instruments. These are the MIDI note numbers.
	private int [] SixStringGuitarTable	= {40, 45, 50, 55, 59, 64};
	private int [] FourStringBassTable	= {28, 33, 38, 43};
	private int [] FiveStringBassTable	= {23, 28, 33, 38, 43};

	// Tuner Class constructor
	public Tuner() {

		super("Tuner", PROCESSOR);

		// Get count of notes to calculate
		int noteCount = SixStringGuitarTable.length;

		// Create the note table that contains note frequencies
		noteTable = new double[noteCount];

		// Initialize the note table with the note frequencies
		for (int i=0; i < noteCount; i++) {
			int noteNumber = SixStringGuitarTable[i];
			noteTable[i] = noteNumberToFreq(noteNumber);
		}
		// Create the UI for this processor
		tui = new TunerUI();

        // Configure the LED tables
        configureLEDTables(tui);

		// Determine cutoff frequency for filters.
		int cutoffFrequency = (int) (noteTable[noteTable.length - 1] * 1.1);	

		// Design the low pass filter. Cutoff is 2 * 12 db/octave when
		// two filter sections are used.
		IIRLowpassFilterDesign lpfd = 
			new IIRLowpassFilterDesign(cutoffFrequency, DEFAULTSAMPLERATE, 1);
		lpfd.doFilterDesign();

		// Implement the filter design
		lowPassShelf = new IIRLowpassFilter(lpfd);

		// Instantiate class which will do the frequency determination
		cfwfft = new ComputeFrequencyWithFFT(DEFAULTSAMPLERATE);
	}

    /**
     * Initialize LED Tables
     *
     * @param TunerUI tui is a reference to the TunerUI so that this
     * method can have access to the UI LEDs.
     */
    public void configureLEDTables(TunerUI tui) {

        // Create the flat, in tune and sharp LED table
        LabeledLED [] indicatorLEDTable = {tui.flatLED, tui.inTuneLED, tui.sharpLED};

        // Create the note LED array. These are the LEDs for the individual
		// strings starting at the big E through little E.
		LabeledLED [] noteLEDTable = {tui.beLED, tui.aLED, tui.dLED,
									  tui.gLED,  tui.bLED, tui.leLED};

        // Update class variables
		this.indicatorLEDTable = indicatorLEDTable;
        this.noteLEDTable = noteLEDTable;
    }

	/**
	 * Set all LEDs in a set off
	 *
	 * @param LabeledLED [] ledArray is the set of LEDs of interest
	 */
    public void setLEDsOff(LabeledLED [] ledArray) {

		// Turn all LEDs off
		for (int i=0; i < ledArray.length; i++)
			ledArray[i].setLEDState(false);
    }
    
	/**
	 * Set the state of an LED in a set of LEDs
	 *
	 * @param LabeledLED [] ledArray is the set of LEDs of interest
	 * @param int index is the index of the LED in the set to access
	 * @param boolean state is true if the LED should be lit and false
	 * if the LED should be extinguished. If an LED is turned on all of
	 * the remaining LEDs in the set are turned off.
	 */
    public void setLEDState(LabeledLED [] ledArray, int index, boolean state) {

        if (state == false) {
            // LED is going off so simply turn it off
            ledArray[index].setLEDState(false);
        
        }   else    {
            // LED is going on. First turn all leds off
            setLEDsOff(ledArray);

            // Then turn selected one one
            ledArray[index].setLEDState(true);
        }
    }
    /**
	 * Given a MIDI note number, calculate its frequency
	 *
	 * @param int noteNumber is the MIDI note number
	 *
	 * @return double containing the frequency of the note
	 */
	public double noteNumberToFreq(int noteNumber) {

		return A440BY32 * Math.pow(2.0, ((noteNumber - 9.0) / 12.0));
	}

	/**
	 * Find the note that is closest to the specified frequency
	 *
	 * @param double freq is the frequency of the note to find
	 *
	 * @return int which is the index of the note closest to the specified
	 * frequency or -1 if the note is not within the table.
	 */
	public int findClosestNoteIndex(double freq) {

		// Is the freq lower than the lowest note in the table?
		if (noteTable[0] >= freq) {
			// Yes it is. Is it within range of the table's note?
			if (calculateFrequencyDifference(0, freq) >= -CENTSHALFHALFSTEP)
				return 0;
			else
				return -1;
		}

		// Is the freq greater than the highest note in the table?
		else if (noteTable[noteTable.length - 1] <= freq) {
			// Yes it is. Is it within range of the table's note?
			if (calculateFrequencyDifference(0, freq) <= CENTSHALFHALFSTEP)
				return noteTable.length - 1;
			else
				return -1;
		}		
		// Search for note in table
		for (int i=0; i < noteTable.length; i++) {
			if ((noteTable[i] <= freq) && (freq <= noteTable[i+1])) {
				// In the correct range, calculate deltas
				double d1 = Math.abs(noteTable[i]   - freq);
				double d2 = Math.abs(noteTable[i+1] - freq);
				if (d1 >= d2)
					return i + 1;
				else
					return i;
			}
		}
		return -1;
	 }

	/**
	 * Calculate how close in cents a specified frequency is to a
	 * specified note. NOTE: an octive is 1200 cents, a halfstep is
	 * 100 cents, one half a half step (CENTSHALFHALFSTEP) is 50 cents.
	 *
	 * @param int noteIndex is the index of the note to compare against
	 * @param double freq is the frequency to compare against
	 *
	 * @return value in cents of difference
	 */
	public int calculateFrequencyDifference(int noteIndex, double freq) {

		double noteFreq = noteTable[noteIndex];

		return (int)(-CENTSPEROCTIVE * Math.log(noteFreq/freq) / Math.log(2.0));
	}
	
	/**
	 * An infinite loop that samples the input, calculates the frequency
	 * and updates the meter and the LEDs in the tuners UI.
	 */
	public void doTuner() {

		double [] freqs = new double[2];

		// Buffer size for approximately 1/2 second of samples
		int sampleBufferSize = AudioConstants.SAMPLEBUFFERSIZE;

		// Allocate the sample buffers
		short [] sampleBuffer    = new short[sampleBufferSize];
		double [] sampleBufferD  = new double [sampleBufferSize];
		double [] sampleBufferD1 = new double [sampleBufferSize];

		// Calculate the highest note that should be considered in
		// frequency calculations.
		double maxFreq = noteTable[noteTable.length - 1];

		// Do forever
		while(true) {
			
			// Must calculate the same frequency twice before registering
			// a reading.
			for (int i=0; i < 2; i++) {
				// Load up a buffer of samples by requesting from previous
				// processing stage.
				previous.getSamples(sampleBuffer, sampleBufferSize);
				
				// Run the lowpass filters on the data
				lowPassShelf.doFilterNoSum(sampleBuffer, sampleBufferD, sampleBufferSize);
				lowPassShelf.doFilterNoSum(sampleBufferD, sampleBufferD1, sampleBufferSize);

				// Calculate and store the frequency
				freqs[i] = cfwfft.computeFrequency(maxFreq, sampleBufferD1);
			}
			// Frequencies close enough to use?
			if (Math.abs(freqs[0] - freqs[1]) > 5)
				continue;

			// Take the average of the detected frequencies
			double freq = (freqs[0] + freqs[1]) / 2.0;

			if (freq == 0.0) {
				// Nothing is being picked up, turn off interface.
				
				// Set meter to 0
				tui.tuneMeter.setValue(0);

				// Turn all LEDs off as well
				setLEDsOff(indicatorLEDTable);
				setLEDsOff(noteLEDTable);
				continue;
			}

			System.out.println("freq: " + freq);

			// Blink the update LED in the UI
			tui.updateLED.setLEDState(true);
			
			try {
				Thread.currentThread().sleep(200);
			}
			catch(Exception e) {}
			tui.updateLED.setLEDState(false);
			
			// Find the note corresponding to the frequency
			int index = findClosestNoteIndex(freq);
			
			// Is the note close enough to register?
			if (index == -1)
				continue;
			
			// Found a corresponding note. Calculate its difference from
			// reference.
			int cents = calculateFrequencyDifference(index, freq);

			// Update the UI components with the results of the calculations
			
			// First the meter
			if ((cents >= -CENTSMETERMAX) && (cents <= CENTSMETERMAX)) {
				// Frequency difference is within range of meter
				tui.tuneMeter.setValue(cents + CENTSMETERMAX);
			}	else	{
				// Frequency difference is out of range of meter
				tui.tuneMeter.setValue(0);
			}

			// Then the LED status indicators
			if ((cents >= -CENTSTOLERANCE) && (cents <= CENTSTOLERANCE)) {
				// Frequency is within tolerance so lite the in tune LED
				setLEDState(indicatorLEDTable, 1, true);
			
			}	else if (cents < 0) {
				// Frequencey is out of range and is negative so lite the
				// flat LED
				setLEDState(indicatorLEDTable, 0, true);
			}	else	{
				// Frequencey is out of range and is positive so lite the
				// sharp LED
				setLEDState(indicatorLEDTable, 2, true);
			}
			// Finally the LED note indicators
			setLEDState(noteLEDTable, index, true);
		}
	}

	// Method necessary for AbstractAudio device
	public int getSamples(short [] buffer, int length) {

		return 0;
	}

	// Tuner application entry point
	public static void main(String [] args) {

        // Instantiate data structure for linking abstract audio devices
		LinkedListVector ll = new LinkedListVector();

		// Create a WinRecorder for gathering samples
		WinRecorder recorder = new WinRecorder(DEFAULTSAMPLERATE,
								   WinRecorder.DEFAULTCHANNELS,
								   WinRecorder.DEFAULTDEVICEID, 
								   null);
		
		if (recorder.initRecorder()) {
			// Open was successful, so continue
			ll.addElement(recorder);

			// Create the tuner device
			Tuner tuner = new Tuner();
			ll.addElement(tuner);

			// Run the tuner code 
			tuner.doTuner();
		}
	}

	// Private class data
	private static TunerUI tui;
	private static double [] noteTable;
	private IIRLowpassFilter lowPassShelf;
	
    private LabeledLED [] indicatorLEDTable;
    private LabeledLED [] noteLEDTable;
	private ComputeFrequencyWithFFT cfwfft;
}

