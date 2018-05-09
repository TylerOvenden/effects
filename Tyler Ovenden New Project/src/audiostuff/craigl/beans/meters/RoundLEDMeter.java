// RoundLEDMeter Class
// Written by: Craig A. Lindley
// Last Update: 04/01/99

package craigl.beans.meters;

import java.awt.*;
import java.util.*;

import craigl.beans.blinker.Blinker;
import craigl.beans.leds.*;

public class RoundLEDMeter extends Meter {

	private static final int DEFAULTNUMBEROFSECTIONS = 10;
	private static final String DEFAULTLABELSTRING = 
					"0,-10,-20,-30,-40,-50,-60,-70,-80,-90";

	/**
	 * RoundLEDMeter class constructor with all agruments
	 *
	 * @param int radius is the radius of the LEDs that will be used in the
	 * meter.
	 * @param boolean isVertical true if meter is vertical false if meter
	 * should be horizontal.
	 * @param int meterMode is not currently used
	 * @param String fontName is the name of the font for labelling
	 * @param String fontStyle is the name of the font style for labelling
	 * @param int fontSize is the size of the font for labelling
	 * @param String caption is the caption to label the meter with
	 * @param boolean topLeftCaption if true causes the caption to be placed
	 * at the top of vertical meters and at the left of horizontal meters.
	 * False causes the caption to be place at the bottom or the right of
	 * the meter.
	 * @param boolean hasLabels is true if the meter has labels and it
	 * is desired they are displayed.
	 * @param String labelsString is the string of comma separated label
	 * strings used to label the meter. There should be one entry for each
	 * section of the meter.
	 * @param boolean labelsOnTopLeft is true if the labels should be on the
	 * left of the vertical meter LEDs or on top of a horizontal meter LEDs.
	 * If false the labels will be on the right or on the bottom of the LEDs.
	 * @param int value is the value the meter should initially display
	 * @param int numberOfSections is the number of meter sections. NOTE:
	 * this value can only be set in the constructor. It cannot be changed
	 * at runtime.
	 * @param Color panelColor is the color of the panel surrounding the
	 * meter.
	 * @param Color textColor is the color used for the labelling text
	 * @param Blinker blinker is the blinker used to drive the LED state
	 * machine. A blinker is passed in so applications using this meter
	 * can have synchronized LEDs.
	 */
	public RoundLEDMeter(int radius, boolean isVertical, int meterMode,
					String fontName, int fontStyle, int fontSize,
					String caption, boolean topLeftCaption, boolean hasLabels,
					String labelsString, boolean labelsOnTopLeft,
					int value, int numberOfSections, 
					Color panelColor, Color textColor, Blinker blinker) {

		super(0, 0, meterMode, fontName, fontStyle, fontSize,
			  caption, hasLabels, labelsString, value, false,
			  numberOfSections, panelColor, Color.black, textColor);

		// Create the meter

		// Create a caption label
		Label l = new Label(getCaption(), Label.CENTER);
		l.setForeground(textColor);
		l.setBackground(panelColor);
		l.setFont(font);

		// Allocate array for LED storage
		ledArray = new LabeledLED[numberOfSections];
		
		if (isVertical) {
			// Layout is vertical
			setLayout(new GridLayout(numberOfSections + 1, 1));
			
			// Conditionally place the label
			if (topLeftCaption)
				add(l);

			// Create the Labeled LEDs
			for (int i=0; i < numberOfSections; i++) {
				String label;
				if (hasLabels) 
					label = (String) labels.elementAt(i);
				else
					label = "";
				
				ledArray[i] = new LabeledLED(blinker,
											 panelColor, Color.green, textColor,
											 font, radius, label,
											 false, !labelsOnTopLeft);
				add(ledArray[i]);
			}
			// Conditionally place the label
			if (!topLeftCaption)
				add(l);

		}	else	{
			
			// Layout is horizontal. Plus one for caption at end
			setLayout(new GridLayout(1, numberOfSections + 1));

			// Conditionally place the label
			if (topLeftCaption)
				add(l);

			// Create the Labeled LEDs
			for (int i=0; i < numberOfSections; i++) {
				String label;
				if (hasLabels) 
					label = (String) labels.elementAt(i);
				else
					label = "";
				
				ledArray[i] = new LabeledLED(blinker,
											 panelColor, Color.green, textColor,
											 font, radius, label,
											 true, !labelsOnTopLeft);
				add(ledArray[i]);
			}
			// Conditionally place the label
			if (!topLeftCaption)
				add(l);
		}
		validate();
	}

	// Constructor with reasonable defaults
	public RoundLEDMeter(int radius, boolean isVertical, 
						 int numberOfSections, String labelsString,
						 boolean labelsOnTopLeft,
						 String caption, boolean topLeftCaption, int value,
						 Blinker blinker) {

		this(radius, isVertical, MODEPEAK, 
			 DEFAULTFONTNAME, DEFAULTFONTSTYLE, DEFAULTFONTSIZE,
			 caption, topLeftCaption, true, labelsString, labelsOnTopLeft,
			 value, numberOfSections,
			 PANELCOLOR, TEXTCOLOR, blinker);
	}

	// Constructor with reasonable defaults
	public RoundLEDMeter(int radius, boolean isVertical, 
						 String caption, int value, Blinker blinker) {

		this(radius, isVertical, MODEPEAK, 
			 DEFAULTFONTNAME, DEFAULTFONTSTYLE, DEFAULTFONTSIZE,
			 caption, true, true, DEFAULTLABELSTRING, true,
			 value, DEFAULTNUMBEROFSECTIONS,
			 PANELCOLOR, TEXTCOLOR, blinker);
	}

	// Zero argument constructor for testing
	public RoundLEDMeter() {
		
		this(7, false, "dB", 50, new Blinker(100));
	}

	/**
	 * Paint the component and the meter's value
	 *
	 * @param Graphics g is the graphics context the painting will occur on
	 */
	public void paint(Graphics g) {

		// First allow the component to paint
		super.paint(g);

		// Next update meter's value
		
		// Determine the number of LED's to light given the value
		// and meterGranularity
		int ledsToLight = round(meterGranularity * value);

		for (int led = numberOfSections - 1; led >= 0 ; led--) {
			
			boolean state = (ledsToLight-- > 0);
			
			ledArray[led].setLEDState(state);
		}
	}

	/**
	 * Set the meter's color over a range of percent values
	 *
	 * @param Color color is the color to set the specified range of
	 * meter values to.
	 * @param int minPercentValue specifies the start of the color range
	 * @param int maxPercentValue specifies the end of the color range
	 */
	public void setColorRange(Color color, 
							  int minPercentValue, int maxPercentValue) {

		int minLEDIndex = round(meterGranularity * minPercentValue);
		int maxLEDIndex = round(meterGranularity * maxPercentValue);

		for (int index=Math.max(minLEDIndex - 1, 0); index < maxLEDIndex; index++) {
			int ledOffset = (numberOfSections - 1) - index;
			ledArray[ledOffset].setLEDColor(color);
		}
	}
	
	/**
	 * Overloaded method to prevent the number of meter sections from being
	 * changed at runtime. In essience, this method can be called once
	 * during initialization (by the base class) but never again.
	 *
	 * @param int numberOfSections the initial number of sections value
	 */
	public void setNumberOfSections(int numberOfSections) {

		if (getNumberOfSections() == -1)
			super.setNumberOfSections(numberOfSections);
		else
			System.out.println("Number of sections cannot be changed at runtime");
	}

	/**
	 * Overloaded method to prevent the meter label string from being
	 * changed at runtime. In essience, this method can be called once
	 * during initialization (by the base class) but never again.
	 *
	 * @param String labelString is the string of labels to set
	 */
	public void setLabelsString(String labelString) {

		if (getLabelsString().length() == 0)
			super.setLabelsString(labelString);
		else
			System.out.println("Label string cannot be changed at runtime");
	}

	// Test program for RoundLEDMeter
	public static void main(String [] args) {
		
		Frame f = new Frame("RoundLEDMeter Test");

		// Create a blinker for the LEDs
		Blinker blinker = new Blinker(100);

		RoundLEDMeter lm = new RoundLEDMeter(
								7, true,
								15, "15,14,13,12,11,10,9,8,7,6,5,4,3,2,1",
								false, "Test Display", false, 50, blinker);
		f.add(lm);
		f.pack();
		f.setVisible(true);
		
		// Set the color ranges on the meter
		lm.setColorRange(Color.green,   0, 60);
		lm.setColorRange(Color.yellow, 61, 80);
		lm.setColorRange(Color.red,    81,100);
		
		// Generate 1000 random data values and display them
		for (int index=0; index < 1000; index++) {

			int value = (int)(Math.random() * 100);
			lm.setValue(value);
			try {
				Thread.sleep(250);
			}
			catch(Exception ignor) {}
		}
	}
	// Private class data
	private LabeledLED [] ledArray;
 }

