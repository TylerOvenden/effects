// Audio Constants
// Written by: Craig A. Lindley
// Last Update: 01/09/99

// Various constants used throughout the code. A change to
// these and a recompile will ripple through the code.

package craigl.utils;

import java.awt.Color;

public class AudioConstants {

	// Size of buffer used throughout the code
	public static final int SAMPLEBUFFERSIZE = 7500;

	public static final Color PANELCOLOR = 
		new Color((float) 0.87, (float) 0.72, (float) 0.53);
	
	public static final Color TEXTCOLOR	= Color.white;

	public static final Color KNOBCOLOR	= 
		new Color((float) 0.64, (float) 0.58, (float) 0.65);

	public static final int KNOBSIZE = 16;

	private AudioConstants() {}

}
