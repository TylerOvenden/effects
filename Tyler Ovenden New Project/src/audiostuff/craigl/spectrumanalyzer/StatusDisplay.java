// Status Display Panel Class for use in the Spectrum Analyzer UI
// Written by: Craig A. Lindley
// Last Update: 03/07/99

package audiostuff.craigl.spectrumanalyzer;

import java.awt.*;

public class StatusDisplay extends Panel {
	/**
	 * This class is a Panel with five display areas. The first two
	 * are static and display the sample rate and a mono/stereo
	 * indicator. The third displays the rate or ratio with which one
	 * component differs in frequency from the next.
	 * The forth panel shows the frequency of the lowest
	 * frequency sample shown currently on the display. The fifth panel
	 * shows the frequency of the highest frequency sample currently
	 * on the display.
	 */
	private static final int NUMBERDIGITS = 7;

	/**
	 * Class constructor
	 *
	 * @param int sampleRate is the sample rate for display 
	 * @param int numberOfChannels is used to determine if the samples are
	 * mono or stereo
	 */
	public StatusDisplay(int sampleRate, int numberOfChannels) {

		setLayout(new GridLayout(1, 5));

		add(new Label("Rate: " + sampleRate + " s/sec"));
		add(new Label("Mode: " + ((numberOfChannels == 2) ? "Stereo":"Mono")));

		rateDisplay = new Label("Rate:");
		add(rateDisplay);
		
		lfDisplay = new Label("LFreq:");
		add(lfDisplay);
		
		hfDisplay = new Label("HFreq:");
		add(hfDisplay);
	}

	/**
	 * This method updates the rate (rate of change) value displayed
	 *
	 * @param double rate is ratio of frequencies of adjacent samples
	 */
	public void setRate(double rate) {

		// Update the display
		rateDisplay.setText("Rate: " + formatDoubleString(NUMBERDIGITS, rate));
	}
	
	/**
	 * This method updates the lowest frequency value displayed
	 *
	 * @param double freq is the frequency of the lowest frequency sample
	 * currently displayed.
	 */
	public void setLowFrequency(double freq) {

		// Update the display
		lfDisplay.setText("LFreq: " + formatDoubleString(NUMBERDIGITS, freq) + " Hz");
	}

	/**
	 * This method updates the highest frequency value displayed
	 *
	 * @param double freq is the frequency of the highest frequency sample
	 * currently displayed.
	 */
	public void setHighFrequency(double freq) {

		// Update the display
		hfDisplay.setText("HFreq: " + formatDoubleString(NUMBERDIGITS, freq) + " Hz");
	}

	/**
	 * Convert a double value into a string and truncate to specified
	 * number of digits.
	 *
	 * @param int digits is the number of digits in the returned string.
	 * Note the decimal point counts as well as a sign if present.
	 * @param double value is the value to format
	 */
	private String formatDoubleString(int digits, double value) {

		String s = "" + value;
		
		// Trim the string to a reasonable precision
		if (s.length() > digits)
			s = s.substring(0, digits);

		return s;
	}
	
	// Private class data
	private Label rateDisplay;
	private Label lfDisplay;
	private Label hfDisplay;
}
