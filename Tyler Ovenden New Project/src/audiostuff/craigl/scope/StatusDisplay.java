// Status Display Panel Class for use in the Scope UI
// Written by: Craig A. Lindley
// Last Update: 02/28/99

package craigl.scope;

import java.awt.*;

public class StatusDisplay extends Panel {
	/**
	 * This class is a Panel with three display areas. The first two
	 * are static and display the sample rate and a mono/stereo
	 * indicator. The third panel is a display of the the time represented
	 * by the number of samples being displayed.
	 */

	/**
	 * Class constructor
	 *
	 * @param int sampleRate is the sample rate for display 
	 * @param int numberOfChannels is used to determine if the samples are
	 * mono or stereo
	 */
	public StatusDisplay(int sampleRate, int numberOfChannels) {

		setLayout(new GridLayout(1, 3));

		add(new Label("Rate: " + sampleRate + " s/sec"));
		add(new Label("Mode: " + ((numberOfChannels == 2) ? "Stereo":"Mono")));

		timeDisplay = new Label("Time:");
		add(timeDisplay);
	}

	/**
	 * This method updates the time value displayed
	 *
	 * @param double time that the samples shown on the scope represent
	 */
	public void setTimeDisplay(double time) {

		// Convert time to string
		String s = "" + time;
		
		// Does the string contain scientific notation?
		int eIndex = s.indexOf("E");
		String eString = (eIndex != -1) ? s.substring(eIndex):"";

		// Trim the string to a reasonable precision
		if (s.length() > 6)
			s = s.substring(0, 6);

		// Add exponent string if there is one
		s += eString;
		
		// Update the time on the display
		timeDisplay.setText("Time: " + s + " sec");
	}
	// Private class data
	private Label timeDisplay;
}
