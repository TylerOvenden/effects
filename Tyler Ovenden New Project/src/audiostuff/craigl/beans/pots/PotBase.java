// Potentiometer Base Class
// Written by: Craig A. Lindley
// Last Update: 08/22/98

package craigl.beans.pots;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;

public abstract class PotBase extends Canvas implements Adjustable {

	// Constants for defaults
	public static final int POTRANGE		= 100;
	
	public static final Color PANELCOLOR	= Color.lightGray;
	public static final Color KNOBCOLOR		= Color.darkGray;
	public static final Color TEXTCOLOR		= Color.white;
	public static final Color TICCOLOR		= Color.white;
	public static final Color GRADCOLOR		= Color.black;
	
	public static final String DEFAULTFONTNAME	= "Dialog";
	public static final int DEFAULTFONTSTYLE	= Font.PLAIN;
	public static final int DEFAULTFONTSIZE		= 9;

	// Class constructor
	// Full strength constructor sets every property of base pot
	public PotBase(String fontName, int fontStyle, int fontSize,
				   String caption, boolean hasLabels, String labelsString,
				   int value, boolean hasHighlight, int numberOfSections, 
				   Color panelColor, Color knobColor, Color textColor,
				   Color ticColor, Color gradColor) {
		super();

		// Process and save incoming
		setFontName(fontName);
		setFontStyle(fontStyle);
		setFontSize(fontSize);
		setCaption(caption);
		setHasLabels(hasLabels);
		setLabelsString(labelsString);
		setValue(value);
		setHighlight(hasHighlight);
		setNumberOfSections(numberOfSections);
		setPanelColor(panelColor);
		setKnobColor(knobColor);
		setTextColor(textColor);
		setTicColor(ticColor);
		setGradColor(gradColor);

		// Calculate pots granularity
		blockIncrement = POTRANGE / 10;
		unitIncrement = 1;
    
		// Enable event processing
		enableEvents(AWTEvent.MOUSE_EVENT_MASK |
					 AWTEvent.MOUSE_MOTION_EVENT_MASK |
					 AWTEvent.KEY_EVENT_MASK);
	}

	// Abstract methods for subclass
	public abstract Dimension getPreferredSize();
	public abstract void paint(Graphics g);

	protected void sizeToFit() {

		// Resize to the preferred size
		Dimension d = getPreferredSize();
		setSize(d);

		Component p = getParent();
		if (p != null) {
			p.invalidate();
			p.validate();
		}
	}
	
	protected int round(double d) {

		return (int) Math.round(d);
	}

	public Color getPanelColor() {

		return panelColor;
	}

	public void setPanelColor(Color panelColor) {

		this.panelColor = panelColor;
		this.brighterPanelColor = panelColor.brighter();
		potImage = null;
		repaint();
	}

	public Color getKnobColor() {

		return knobColor;
	}

	public void setKnobColor(Color knobColor) {

		this.knobColor = knobColor;
		
		// Calculate highlight colors
		this.highlightBrighterColor	= knobColor.brighter();
		this.highlightDarkerColor	= knobColor.darker();

		potImage = null;
		
		// Cause a repaint
		repaint();
	}

	public Color getTextColor() {

		return textColor;
	}

	public void setTextColor(Color textColor) {

		this.textColor = textColor;
		potImage = null;
		repaint();
	}

	public Color getTicColor() {

		return ticColor;
	}

	public void setTicColor(Color ticColor) {

		this.ticColor = ticColor;
		potImage = null;
		repaint();
	}

	public Color getGradColor() {

		return gradColor;
	}

	public void setGradColor(Color gradColor) {

		this.gradColor = gradColor;
		potImage = null;
		repaint();
	}

	public boolean getHighlight() {

		return hasHighlight;
	}

	public void setHighlight(boolean hasHighlight) {

		this.hasHighlight = hasHighlight;
		potImage = null;
		repaint();
	}

	public int getValue() {

		return value;
	}

	public void setValue(int newValue) {

		newValue = Math.min(POTRANGE, newValue);
		newValue = Math.max(0, newValue);
		this.value = newValue;

		repaint();
	}

	// Simulate a pot with an audio taper. Audio taper pots
	// have a 10% / 90% value at 50% rotation. Return an
	// attenuation factor that takes this into consideration.
	// At full scale an attenuation factor of 1.0 is returned.
	// At 50 % full scale an attenuation factor of .1 is returned
	// At 0% full scale an attenuation factor of 0.0 is returned.
	public double getAttenuation() {

		if (value == 0)
			return 0.0;

		else if (value == 100)
			return 1.0;

		else {
			// Calculate log atteuation
			double exp = - (100.0 - (double) value) / (POTRANGE / 2.0);
			return Math.pow(10, exp);
		}
	}
	
	public boolean getHasLabels() {

		return hasLabels;
	}

	public void setHasLabels(boolean hasLabels) {

		this.hasLabels = hasLabels;
		potImage = null;
		repaint();
	}
	
	// Return a CSV string containing the label strings
	public String getLabelsString() {
		String retString = "";
		
		for (int index=0; index < labels.size(); index++) {
			retString += (String) labels.elementAt(index);
			if (index != labels.size() - 1)
				retString += ",";
		}
		return retString;
	}
	
	public void setLabelsString(String s) {

		// Clear any existing labels
		labels.removeAllElements();

		// Prepare to take the string apart
		StringTokenizer st = new StringTokenizer(s, ",");
		
		// Add each part of string as a separate label
		while(st.hasMoreTokens()) 
			labels.addElement(st.nextToken());

		potImage = null;
		repaint();
	}

	public Font getFont() {

		return font;
	}

	public void setFont(Font font) {

		this.font = font;

		// Size the knob to the text/font
		sizeToFit();
		potImage = null;
		repaint();
	}
	
	public String getFontName() {

		return fontName;
	}

	public void setFontName(String fontName) {

		this.fontName = fontName;
		
		font = new Font(fontName, fontStyle, fontSize);
		
		// Size the knob to the text/font
		sizeToFit();
		potImage = null;
		repaint();
	}

	public int getFontStyle() {

		return fontStyle;
	}

	public void setFontStyle(int fontStyle) {

		this.fontStyle = fontStyle;

		font = new Font(fontName, fontStyle, fontSize);

		// Size the knob to the text/font
		sizeToFit();
		potImage = null;
		repaint();
	}
	
	public int getFontSize() {

		return fontSize;
	}

	public void setFontSize(int fontSize) {

		this.fontSize = fontSize;

		font = new Font(fontName, fontStyle, fontSize);

		// Size the knob to the text/font
		sizeToFit();
		potImage = null;
		repaint();
	}
	
	public String getCaption() {

		return caption;
	}

	public void setCaption(String caption) {

		this.caption = new String(caption);

		// Size the knob to the text/font
		sizeToFit();
		potImage = null;
		repaint();
	}

	public int getNumberOfSections() {

		return numberOfSections;
	}

	public void setNumberOfSections(int numberOfSections) {

		this.numberOfSections = numberOfSections;
		blockIncrement = POTRANGE / numberOfSections;
		potImage = null;
		repaint();
	}

	// Methods required for Adjustable interface

	public int getBlockIncrement() {

		return blockIncrement;
	}

	public void setBlockIncrement(int b) {

		this.blockIncrement = b;
	}

	public int getMaximum() {

		return POTRANGE;
	}

	public void setMaximum(int m) {

	}

	public int getMinimum() {

		return 0;
	}

	public void setMinimum(int m) {

	}

	public int getOrientation() {

		return Adjustable.VERTICAL;
	}

	public int getUnitIncrement() {

		return unitIncrement;
	}

	public void setUnitIncrement(int unitIncrement) {

		this.unitIncrement = unitIncrement;
	}

	public int getVisibleAmount() {
		
		return 1;
	}

	public void setVisibleAmount(int a) {
		
	}

/*
	// Could be used to show the knob has input focus
	protected void processFocusEvent(FocusEvent e) {

		// Get the new focus state and repaint
		switch(e.getID()) {
			case FocusEvent.FOCUS_GAINED:
				hasFocus = true;
				repaint();
				break;

			case FocusEvent.FOCUS_LOST:
				hasFocus = false;
				repaint();
				break;
		}
		// Let the superclass continue delivery
		super.processFocusEvent(e);
	}
*/

	// Add an adjustment listener
    public synchronized void addAdjustmentListener(AdjustmentListener l) {
	
		adjustmentListener = AWTEventMulticaster.add(adjustmentListener, l);
	}

	// Remove adjustment listener
	public synchronized void removeAdjustmentListener(AdjustmentListener l){
		
		adjustmentListener = AWTEventMulticaster.remove(adjustmentListener, l);
	}

	// Indicate to all listeners that the pot's adjustment has changed
	public void fireAdjustmentEvent() {

		// Synchronously notify the listeners so that they are 
		// guaranteed to be up-to-date with the Adjustable before
		// it is mutated again.
		AdjustmentEvent e = 
			new AdjustmentEvent(this, 
								AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED,
								AdjustmentEvent.TRACK, value);
		// Send it out if there is a listener registered
		if (adjustmentListener != null)
			adjustmentListener.adjustmentValueChanged(e);
	}

	// Private class data
	protected int blockIncrement;		// Inc of adjustment
	protected int unitIncrement;		// Inc of adjustment

	protected Color panelColor;
	protected Color brighterPanelColor;
	protected Color knobColor;
	protected Color textColor;
	protected Color ticColor;
	protected Color gradColor;
	protected Color highlightBrighterColor;
	protected Color highlightDarkerColor;

	protected boolean hasHighlight;
	protected boolean hasLabels;
	protected transient int value;
	protected int numberOfSections;
	protected String fontName = "Dialog";
	protected int fontStyle = Font.PLAIN;
	protected int fontSize = 8;
	protected Font font;
	protected String caption = "";
	protected Vector labels = new Vector();
    protected AdjustmentListener adjustmentListener = null;

	protected int xCenter;
	protected int yCenter;
	protected boolean mouseDown = false;
	protected boolean mouseInKnob = false;
	protected Point downPt;
	protected boolean hasFocus = false;
	protected Image potImage = null;
}
