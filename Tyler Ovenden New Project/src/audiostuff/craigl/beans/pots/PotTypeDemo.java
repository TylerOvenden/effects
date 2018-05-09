// Pot Demonstration Program
// Written by: Craig A. Lindley
// Last Update: 03/21/99

package craigl.beans.pots;

import java.awt.*;
import java.awt.event.*;
import craigl.beans.displays.IntLEDDisplay;

public class PotTypeDemo extends Frame {

	public static final Color PANELCOLOR = new Color(44,  148, 103);
	public static final Color LEDCOLOR   = Color.red;
	public static final Color LEDBGCOLOR = Color.black;
	public static final Color POTCOLOR1  = new Color(155, 128,   0);
	public static final Color POTCOLOR2  = new Color(128, 128,  64);

	public PotTypeDemo() {

		super("Pot Demonstration Program");

		// Create Main Demo Panel
		Panel potDemoPanel = new Panel();
		potDemoPanel.setLayout(new GridLayout(2, 1));
		potDemoPanel.setBackground(PANELCOLOR);

		// Create the display panel
		Panel displayPanel = new Panel();
		displayPanel.setLayout(new GridLayout(1, 5));

		final IntLEDDisplay display1 = new IntLEDDisplay(90, 40, 3, 0);
		display1.setPanelColor(PANELCOLOR);
		display1.setLEDColor(LEDCOLOR);
		display1.setLEDBGColor(LEDBGCOLOR);
		display1.setTextColor(Color.green);
		display1.setCaption("Pot 1 Value");
		displayPanel.add(display1);

		final IntLEDDisplay display2 = new IntLEDDisplay(90, 40, 3, 0);
		display2.setPanelColor(PANELCOLOR);
		display2.setLEDColor(LEDCOLOR);
		display2.setLEDBGColor(LEDBGCOLOR);
		display2.setTextColor(Color.green);
		display2.setCaption("Pot 2 Value");
		displayPanel.add(display2);

		final IntLEDDisplay display3 = new IntLEDDisplay(90, 40, 3, 0);
		display3.setPanelColor(PANELCOLOR);
		display3.setLEDColor(LEDCOLOR);
		display3.setLEDBGColor(LEDBGCOLOR);
		display3.setTextColor(Color.green);
		display3.setCaption("Pot 3 Value");
		displayPanel.add(display3);

		final IntLEDDisplay display4 = new IntLEDDisplay(90, 40, 3, 0);
		display4.setPanelColor(PANELCOLOR);
		display4.setLEDColor(LEDCOLOR);
		display4.setLEDBGColor(LEDBGCOLOR);
		display4.setTextColor(Color.green);
		display4.setCaption("Pot 4 Value");
		displayPanel.add(display4);

		final IntLEDDisplay display5 = new IntLEDDisplay(90, 40, 3, 0);
		display5.setPanelColor(PANELCOLOR);
		display5.setLEDColor(LEDCOLOR);
		display5.setLEDBGColor(LEDBGCOLOR);
		display5.setTextColor(Color.green);
		display5.setCaption("Pot 5 Value");
		displayPanel.add(display5);

		potDemoPanel.add(displayPanel);

		// Create the pot control panel
		Panel controlPanel = new Panel();
		controlPanel.setLayout(new GridLayout(1, 5));

		// Create first round pot
		final Pot pot1 = new Pot();
		pot1.setPanelColor(PANELCOLOR);
		pot1.setKnobColor(POTCOLOR1);
		pot1.setCaption("Normal Pot");
		pot1.setKnobUseTics(false);
		pot1.setGradUseTics(false);
		pot1.setGradLengthPercent(25);
		pot1.setTicStartPercent(70);
		pot1.setTicLengthPercent(30);
		pot1.setGradGapPercent(10);
		pot1.setCaptionAtBottom(true);
		pot1.setNumberOfSections(12);
		pot1.setLabelsString(
			"a,b,c,d,e,f,g,h,i,j,k,l,m");
		pot1.setLabelPercent(180);

		pot1.setTicColor(Color.red);
		pot1.setGradColor(Color.red);
		pot1.setTextColor(Color.black);
		pot1.setRadius(16);
		pot1.addAdjustmentListener(display1);
		int value = pot1.getValue();
		display1.setValue(value);
		controlPanel.add(pot1);

		// Create second round pot
		final Pot pot2 = new Pot();
		pot2.setPanelColor(PANELCOLOR);
		pot2.setKnobColor(POTCOLOR2);
		pot2.setCaption("Audio Taper Pot");
		pot2.setKnobUseTics(true);
		pot2.setGradUseTics(true);
		pot2.setGradLengthPercent(25);
		pot2.setTicStartPercent(70);
		pot2.setTicLengthPercent(30);
		pot2.setGradGapPercent(10);
		pot2.setCaptionAtBottom(false);
		pot2.setNumberOfSections(15);
		pot2.setLabelsString(
			"min, , , , , , , , , , , , , , ,max");
		pot2.setLabelPercent(180);
		pot2.setTicColor(Color.red);
		pot2.setGradColor(Color.red);
		pot2.setTextColor(Color.black);
		pot2.setRadius(30);
		pot2.setValue(0);
		// Add listener to this pot
		pot2.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				amplitudeChanged(pot2, display2);
			}
		});
		controlPanel.add(pot2);

		// Create the first slide pot
		final SlidePot pot3 = new SlidePot(85, 13, "Level", 0);
        pot3.setKnobColor(POTCOLOR1);
		pot3.setGradColor(Color.red);
        pot3.setPanelColor(PANELCOLOR);
		pot3.setTextColor(Color.black);
		pot3.setNumberOfSections(10);
		pot3.setLabelPercent(170);
		pot3.setLabelsString("100, , , , ,50, , , , ,0");
		pot3.addAdjustmentListener(display3);
		controlPanel.add(pot3);

		// Create special boost/cut pot
		final BoostCutSlidePot pot4 = new BoostCutSlidePot(85, 13, "Gain", -12, +12);
        pot4.setKnobColor(POTCOLOR2);
		pot4.setGradColor(Color.red);
        pot4.setPanelColor(PANELCOLOR);
		pot4.setTextColor(Color.black);
		pot4.setNumberOfSections(12);
		pot4.setLabelPercent(170);
		pot4.setLabelsString("+12, , , , , ,0dB, , , , , ,-12");
		// Add listener to this pot
		pot4.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				gainChanged(pot4, display4);
			}
		});
		controlPanel.add(pot4);

		// Create a slide pot with altered range
		final IntValuedSlidePot pot5 = new IntValuedSlidePot(85, 13, "Range", +750, -750);
        pot5.setKnobColor(POTCOLOR2);
		pot5.setGradColor(Color.red);
        pot5.setPanelColor(PANELCOLOR);
		pot5.setTextColor(Color.black);
		pot5.setNumberOfSections(12);
		pot5.setLabelPercent(170);
		pot5.setLabelsString("+750, , , , , ,0, , , , , ,-750");
		pot5.setValue(50);
		// Add listener to this pot
		pot5.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				intValueChanged(pot5, display5);
			}
		});
		controlPanel.add(pot5);


		// Add the control panel to the demo panel
		potDemoPanel.add(controlPanel);

		// Add demo panel to Frame
		add(potDemoPanel);

		pack();
	}

	// Called when the amplitude pot is manipulated. Note: amplitude
	// pot is modelled with a pseudo audio taper.
	public void amplitudeChanged(PotBase p, IntLEDDisplay display) {

		display.setValue((int)( 100 * p.getAttenuation()));
	}

	// Called when the gain pot is manipulated. 
	public void gainChanged(BoostCutSlidePot p, IntLEDDisplay display) {

		double gain = p.getGain();
		display.setValue((int) (p.getGain() * 100));
	}

	// Called when the int valued pot is manipulated. 
	public void intValueChanged(IntValuedSlidePot p, IntLEDDisplay display) {

		int value = p.getIntValue();
		display.setValue(value);
	}

	// Test application entry point	
	public static void main(String [] args) {

		PotTypeDemo btd = new PotTypeDemo();
		btd.show();
	}
}