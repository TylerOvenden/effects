// Demo Front Panel Class
// Written by: Craig A. Lindley
// Last Update: 04/04/99

package frontpanels;

import java.awt.*;

import craigl.utils.AudioConstants;
import craigl.beans.blinker.*;
import craigl.beans.buttons.*;
import craigl.beans.datagen.*;
import craigl.beans.displays.*;
import craigl.beans.leds.*;
import craigl.beans.meters.*;
import craigl.beans.pots.*;
import craigl.uiutils.*;


public class DemoFrontPanel extends BaseUI implements CloseableFrameIF {

	public static final int SMALLKNOB		= 13;
	public static final int LARGEKNOB		= 16;
	
	public static final Color FREQKNOBCOLOR	= 
		new Color((float) 0.64, (float) 0.58, (float) 0.65);
	public static final Color BCKNOBCOLOR	= 
		new Color((float) 0.64, (float) 0.58, (float) 0.65);
	public static final Color QKNOBCOLOR	=
		new Color((float) 0.64, (float) 0.58, (float) 0.65);

	public DemoFrontPanel() {

		super("Demo Instrument Front Panel", null);

		// This UI is interested in window closing events
		registerCloseListener(this);

		setBackground(AudioConstants.PANELCOLOR);

		// Start a blinker
		Blinker blink = new Blinker(250);

		DataGen dg1 = new DataGen(200);
		DataGen dg2 = new DataGen(150);
		
		// Use a grid for the front panel
		setLayout(new GridLayout(3, 1));

		// Meter Panel
		Panel meterPanel = new Panel();
		meterPanel.setLayout(new GridLayout(1, 6));

		leftPeakLED = new RoundLED();
		leftPeakLED.setPanelColor(AudioConstants.PANELCOLOR);
		leftPeakLED.setLEDMode(RoundLED.MODEBLINK);
		leftPeakLED.setLEDState(true);
//		meterPanel.add(leftPeakLED);

		leftAnalogMeter = new AnalogMeter();
		dg1.addAdjustmentListener(leftAnalogMeter);
		leftAnalogMeter.setLabelsString("-30db, , , ,-3db,0db,+3db"); 
		leftAnalogMeter.setWidth(110);
		leftAnalogMeter.setHeight(55);
		leftAnalogMeter.setCaption("LEFT CHANNEL");
		leftAnalogMeter.setColorRange(Color.green,	 0, 33);
		leftAnalogMeter.setColorRange(Color.yellow,	33, 66);
		leftAnalogMeter.setColorRange(Color.red,	66, 100);
		meterPanel.add(leftAnalogMeter);

		leftLEDMeter = new LEDMeter();
		dg1.addAdjustmentListener(leftLEDMeter);
		leftLEDMeter.setPanelColor(AudioConstants.PANELCOLOR);
		leftLEDMeter.setNumberOfSections(10);
		leftLEDMeter.setHasLabels(true);
		leftLEDMeter.setLabelsString("100,90,80,70,60,50,40,30,20,10");
		leftLEDMeter.setColorRange(Color.green, 0, 65);
		leftLEDMeter.setColorRange(Color.yellow, 66, 86);
		leftLEDMeter.setColorRange(Color.red, 87, 100);
		meterPanel.add(leftLEDMeter);

		rightLEDMeter = new LEDMeter();
		dg2.addAdjustmentListener(rightLEDMeter);
		rightLEDMeter.setPanelColor(AudioConstants.PANELCOLOR);
		rightLEDMeter.setNumberOfSections(10);
		rightLEDMeter.setHasLabels(true);
		rightLEDMeter.setLabelsString("100,90,80,70,60,50,40,30,20,10");
		rightLEDMeter.setColorRange(Color.green, 0, 65);
		rightLEDMeter.setColorRange(Color.yellow, 66, 86);
		rightLEDMeter.setColorRange(Color.red, 87, 100);
		meterPanel.add(rightLEDMeter);

		rightAnalogMeter = new AnalogMeter();
		dg2.addAdjustmentListener(rightAnalogMeter);
		rightAnalogMeter.setLabelsString("-30db, , , ,-3db,0db,+3db"); 
		rightAnalogMeter.setWidth(110);
		rightAnalogMeter.setHeight(55);
		rightAnalogMeter.setCaption("RIGHT CHANNEL");
		rightAnalogMeter.setColorRange(Color.green,	 0, 60);
		rightAnalogMeter.setColorRange(Color.yellow,60, 80);
		rightAnalogMeter.setColorRange(Color.red,	80, 100);
		meterPanel.add(rightAnalogMeter);

		rightPeakLED = new RoundLED();
		rightPeakLED.setPanelColor(AudioConstants.PANELCOLOR);
		rightPeakLED.setLEDMode(RoundLED.MODEBLINK);
		rightPeakLED.setLEDState(true);
//		meterPanel.add(rightPeakLED);

		add(meterPanel);

		// Display Panel
		Panel displayPanel = new Panel();
		displayPanel.setLayout(new GridLayout(1, 7));

		sl1 = new SquareLED();
		sl1.setLEDColor(Color.blue);
		sl1.setPanelColor(AudioConstants.PANELCOLOR);
		sl1.setLEDMode(SquareLED.MODEBLINK);
		displayPanel.add(sl1);

		sl2 = new SquareLED();
		sl2.setLEDColor(Color.green);
		sl2.setPanelColor(AudioConstants.PANELCOLOR);
		displayPanel.add(sl2);

		sl3 = new SquareLED();
		sl3.setLEDColor(Color.red);
		sl3.setPanelColor(AudioConstants.PANELCOLOR);
		sl3.setLEDMode(SquareLED.MODEBLINK);
		sl3.setLEDBlinkRate(true);
		displayPanel.add(sl3);

		ld = new IntLEDDisplay(90, 40, 3, 200);
		ld.setPanelColor(AudioConstants.PANELCOLOR);
		ld.setLEDBGColor(Color.black);
		displayPanel.add(ld);

		sl4 = new SquareLED();
		sl4.setLEDColor(Color.red);
		sl4.setPanelColor(AudioConstants.PANELCOLOR);
		displayPanel.add(sl4);

		sl5 = new SquareLED();
		sl5.setLEDColor(Color.green);
		sl5.setPanelColor(AudioConstants.PANELCOLOR);
		sl5.setLEDMode(SquareLED.MODEPULSE);
		displayPanel.add(sl5);

		sl6 = new SquareLED();
		sl6.setLEDColor(Color.blue);
		sl6.setPanelColor(AudioConstants.PANELCOLOR);
		displayPanel.add(sl6);

		add(displayPanel);

		// Pot panel
		Panel potPanel = new Panel();
		potPanel.setLayout(new GridLayout(1, 8));

		rl1 = new RoundLED();
		rl1.setPanelColor(AudioConstants.PANELCOLOR);
		rl1.setRadius(8);
		potPanel.add(rl1);

		sb = new SquareButton();
		sb.setPanelColor(AudioConstants.PANELCOLOR);
		sb.addActionListener(rl1);
		sb.addActionListener(sl1);
		sb.addActionListener(sl3);
		sb.addActionListener(sl4);
		sb.addActionListener(sl6);
		potPanel.add(sb);

		// High freq control
		hfFreq = new Pot();
		hfFreq.setPanelColor(AudioConstants.PANELCOLOR);
		hfFreq.setKnobColor(FREQKNOBCOLOR);
		hfFreq.setCaption("HF Adj");
		hfFreq.setTicLengthPercent(30);
		hfFreq.setKnobUseTics(false);
		hfFreq.setGradUseTics(true);
		hfFreq.setGradLengthPercent(40);
		hfFreq.setCaptionAtBottom(false);
		hfFreq.setNumberOfSections(5);
		hfFreq.setLabelsString("5k, , , , ,18k");
		hfFreq.setLabelPercent(180);
		hfFreq.setTicColor(Color.red);
		hfFreq.setGradColor(Color.red);
		hfFreq.setRadius(LARGEKNOB);
		hfFreq.addAdjustmentListener(ld);
		potPanel.add(hfFreq);

		// Midrangle Freq control
		mfFreq = new Pot();
		mfFreq.setPanelColor(AudioConstants.PANELCOLOR);
		mfFreq.setKnobColor(FREQKNOBCOLOR);
		mfFreq.setCaption("MF Adj");
		mfFreq.setTicLengthPercent(30);
		mfFreq.setKnobUseTics(false);
		mfFreq.setGradUseTics(true);
		mfFreq.setGradLengthPercent(40);
		mfFreq.setCaptionAtBottom(false);
		mfFreq.setNumberOfSections(5);
		mfFreq.setLabelsString("1.5k, , , , ,6k");
		mfFreq.setLabelPercent(180);
		mfFreq.setTicColor(Color.green);
		mfFreq.setGradColor(Color.green);
		mfFreq.setRadius(SMALLKNOB);
		potPanel.add(mfFreq);
	
		// Low freq Freq control
		lfFreq = new Pot();
		lfFreq.setPanelColor(AudioConstants.PANELCOLOR);
		lfFreq.setKnobColor(FREQKNOBCOLOR);
		lfFreq.setCaption("LF Adj");
		lfFreq.setTicLengthPercent(30);
		lfFreq.setKnobUseTics(false);
		lfFreq.setGradUseTics(true);
		lfFreq.setGradLengthPercent(40);
		lfFreq.setCaptionAtBottom(false);
		lfFreq.setNumberOfSections(5);
		lfFreq.setLabelsString("40, , , , ,1.5k");
		lfFreq.setLabelPercent(180);
		lfFreq.setTicColor(Color.blue);
		lfFreq.setGradColor(Color.blue);
		lfFreq.setRadius(LARGEKNOB);
		potPanel.add(lfFreq);

		tsb = new ToggleSwitchButton();
		tsb.setPanelColor(AudioConstants.PANELCOLOR);
		tsb.setButtonColor(Color.lightGray);
		tsb.setSticky(true);
		tsb.setTopCaption("On");
		tsb.addActionListener(sl2);
		tsb.addActionListener(sl5);
		potPanel.add(tsb);

		add(potPanel);

		blink.addPropertyChangeListener(leftPeakLED);
		blink.addPropertyChangeListener(rightPeakLED);
		blink.addPropertyChangeListener(sl1);
		blink.addPropertyChangeListener(sl2);
		blink.addPropertyChangeListener(sl3);
		blink.addPropertyChangeListener(sl4);
		blink.addPropertyChangeListener(sl5);
		blink.addPropertyChangeListener(sl6);
		blink.addPropertyChangeListener(rl1);

	}

	// Called as window is closing
	public void windowClosing() {

		System.exit(1);
	}


	public static void main(String [] args) {

		DemoFrontPanel fp = new DemoFrontPanel();
		fp.pack();
		fp.setVisible(true);
	}

	// Private class data
	private RoundLED leftPeakLED;
	private RoundLED rightPeakLED;
	private AnalogMeter leftAnalogMeter;
	private AnalogMeter rightAnalogMeter;
	private LEDMeter leftLEDMeter;
	private LEDMeter rightLEDMeter;

	private SquareLED sl1, sl2, sl3, sl4, sl5, sl6;
	private IntLEDDisplay ld;
	private RoundLED rl1;

	private SquareButton sb;
	private ToggleSwitchButton tsb;

	private Pot hfFreq;
	private Pot mfFreq;
	private Pot lfFreq;
}
