// Simulated Compressor Panel Class
// Written by: Craig A. Lindley
// Last Update: 04/04/99

package frontpanels;

import java.awt.*;

import craigl.beans.blinker.*;
import craigl.beans.buttons.*;
import craigl.beans.displays.*;
import craigl.beans.leds.*;
import craigl.beans.leds.LabeledLED;
import craigl.beans.meters.*;
import craigl.beans.pots.*;
import craigl.uiutils.*;


public class CompressorFrontPanel extends BaseUI implements CloseableFrameIF {

	public static final int DEFAULTKNOBSIZE	= 15;
	public static final int LEDDISPLAYWIDTH	= 50;
	public static final int LEDDISPLAYHEIGHT = 25;
	public static final int DEFAULTLEDRADIUS = 7;

	public static final Color DEFAULTPANELCOLOR = Color.lightGray;
	public static final Color DEFAULTTEXTCOLOR = Color.black;
	public static final Color DEFAULTKNOBCOLOR = Color.gray;
	public static final Color DEFAULTLEDCOLOR = Color.green;

	public static final Font DEFAULTFONT = 
		new Font("SansSerif", Font.PLAIN, 9);

	// Private inner class for a dual label panel
	private class DualLabel extends Panel {

		public DualLabel(String topLabel, String bottomLabel) {

			setLayout(new GridLayout(2, 1));
			setBackground(DEFAULTPANELCOLOR);
			setForeground(DEFAULTTEXTCOLOR);
			setFont(DEFAULTFONT);

			add(new Label(topLabel, Label.CENTER));
			add(new Label(bottomLabel, Label.CENTER));
		}
	}

	public CompressorFrontPanel() {

		super("Simulated Compressor Front Panel", null);

		// This UI is interested in window closing events
		registerCloseListener(this);

		setBackground(DEFAULTPANELCOLOR);

		// Start a blinker
		Blinker blink = new Blinker(50);

		// Use a grid bag for the panel
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();

		Panel mp = new Panel();
		mp.setLayout(gbl);

		// Create LEDs for panel
		LabeledLED rl1 = new LabeledLED(
			blink, DEFAULTPANELCOLOR, DEFAULTLEDCOLOR, DEFAULTTEXTCOLOR,
			DEFAULTFONT, DEFAULTLEDRADIUS, "-30", true, true);
		addDefaultComponent(mp, rl1, gbl, gbc, 0, 0, 1, 1);
		
		LabeledLED rl2 = new LabeledLED(
			blink, DEFAULTPANELCOLOR, DEFAULTLEDCOLOR, DEFAULTTEXTCOLOR,
			DEFAULTFONT, DEFAULTLEDRADIUS, "-20", true, true);
		addDefaultComponent(mp, rl2, gbl, gbc, 1, 0, 1, 1);
		
		LabeledLED rl3 = new LabeledLED(
			blink, DEFAULTPANELCOLOR, DEFAULTLEDCOLOR, DEFAULTTEXTCOLOR,
			DEFAULTFONT, DEFAULTLEDRADIUS, "-15", true, true);
		addDefaultComponent(mp, rl3, gbl, gbc, 2, 0, 1, 1);

		LabeledLED rl4 = new LabeledLED(
			blink, DEFAULTPANELCOLOR, DEFAULTLEDCOLOR, DEFAULTTEXTCOLOR,
			DEFAULTFONT, DEFAULTLEDRADIUS, "-10", true, true);
		addDefaultComponent(mp, rl4, gbl, gbc, 3, 0, 1, 1);
		
		LabeledLED rl5 = new LabeledLED(
			blink, DEFAULTPANELCOLOR, DEFAULTLEDCOLOR, DEFAULTTEXTCOLOR,
			DEFAULTFONT, DEFAULTLEDRADIUS, "-6", true, true);
		addDefaultComponent(mp, rl5, gbl, gbc, 4, 0, 1, 1);
		
		LabeledLED rl6 = new LabeledLED(
			blink, DEFAULTPANELCOLOR, DEFAULTLEDCOLOR, DEFAULTTEXTCOLOR,
			DEFAULTFONT, DEFAULTLEDRADIUS, "-4", true, true);
		addDefaultComponent(mp, rl6, gbl, gbc, 5, 0, 1, 1);
		
		LabeledLED rl7 = new LabeledLED(
			blink, DEFAULTPANELCOLOR, DEFAULTLEDCOLOR, DEFAULTTEXTCOLOR,
			DEFAULTFONT, DEFAULTLEDRADIUS, "-2", true, true);
		addDefaultComponent(mp, rl7, gbl, gbc, 6, 0, 1, 1);
		
		LabeledLED rl8 = new LabeledLED(
			blink, DEFAULTPANELCOLOR, DEFAULTLEDCOLOR, DEFAULTTEXTCOLOR,
			DEFAULTFONT, DEFAULTLEDRADIUS, "-1", true, true);
		addDefaultComponent(mp, rl8, gbl, gbc, 7, 0, 1, 1);
		
		LabeledLED rl9 = new LabeledLED(
			blink, DEFAULTPANELCOLOR, DEFAULTLEDCOLOR, DEFAULTTEXTCOLOR,
			DEFAULTFONT, DEFAULTLEDRADIUS, "0", true, true);
		addDefaultComponent(mp, rl9, gbl, gbc, 8, 0, 1, 1);

		DualLabel dl1 = new DualLabel("dB", "G.R.");
		addDefaultComponent(mp, dl1, gbl, gbc, 9, 0, 1, 1);

		Label l = new Label("Simulated Compressor", Label.CENTER);
		l.setBackground(DEFAULTPANELCOLOR);
		l.setForeground(DEFAULTTEXTCOLOR);
		addDefaultComponent(mp, l, gbl, gbc, 11, 0, 9, 1);

		LabeledLED rl10 = new LabeledLED(
			blink, DEFAULTPANELCOLOR, DEFAULTLEDCOLOR, DEFAULTTEXTCOLOR,
			DEFAULTFONT, DEFAULTLEDRADIUS, "-16", true, true);
		addDefaultComponent(mp, rl10, gbl, gbc, 21, 0, 1, 1);
		
		LabeledLED rl11 = new LabeledLED(
			blink, DEFAULTPANELCOLOR, DEFAULTLEDCOLOR, DEFAULTTEXTCOLOR,
			DEFAULTFONT, DEFAULTLEDRADIUS, "-10", true, true);
		addDefaultComponent(mp, rl11, gbl, gbc, 22, 0, 1, 1);
		
		LabeledLED rl12 = new LabeledLED(
			blink, DEFAULTPANELCOLOR, DEFAULTLEDCOLOR, DEFAULTTEXTCOLOR,
			DEFAULTFONT, DEFAULTLEDRADIUS, "-4", true, true);
		addDefaultComponent(mp, rl12, gbl, gbc, 23, 0, 1, 1);
		
		LabeledLED rl13 = new LabeledLED(
			blink, DEFAULTPANELCOLOR, DEFAULTLEDCOLOR, DEFAULTTEXTCOLOR,
			DEFAULTFONT, DEFAULTLEDRADIUS, "-2", true, true);
		addDefaultComponent(mp, rl13, gbl, gbc, 24, 0, 1, 1);
		
		LabeledLED rl14 = new LabeledLED(
			blink, DEFAULTPANELCOLOR, DEFAULTLEDCOLOR, DEFAULTTEXTCOLOR,
			DEFAULTFONT, DEFAULTLEDRADIUS, "-1", true, true);
		addDefaultComponent(mp, rl14, gbl, gbc, 25, 0, 1, 1);

		DualLabel dl2 = new DualLabel("dB", "G.R.");
		addDefaultComponent(mp, dl2, gbl, gbc, 26, 0, 1, 1);

		IntLEDDisplay ild1 = new IntLEDDisplay(LEDDISPLAYWIDTH, LEDDISPLAYHEIGHT, 3, 50);
		ild1.setPanelColor(DEFAULTPANELCOLOR);
		ild1.setTextColor(DEFAULTTEXTCOLOR);
		addDefaultComponent(mp, ild1, gbl, gbc, 0, 1, 3, 1);

		IntLEDDisplay ild2 = new IntLEDDisplay(LEDDISPLAYWIDTH, LEDDISPLAYHEIGHT, 3, 50);
		ild2.setPanelColor(DEFAULTPANELCOLOR);
		ild2.setTextColor(DEFAULTTEXTCOLOR);
		addDefaultComponent(mp, ild2, gbl, gbc, 3, 1, 3, 1);

		IntLEDDisplay ild3 = new IntLEDDisplay(LEDDISPLAYWIDTH, LEDDISPLAYHEIGHT, 3, 50);
		ild3.setPanelColor(DEFAULTPANELCOLOR);
		ild3.setTextColor(DEFAULTTEXTCOLOR);
		addDefaultComponent(mp, ild3, gbl, gbc, 6, 1, 3, 1);

		IntLEDDisplay ild4 = new IntLEDDisplay(LEDDISPLAYWIDTH, LEDDISPLAYHEIGHT, 3, 50);
		ild4.setPanelColor(DEFAULTPANELCOLOR);
		ild4.setTextColor(DEFAULTTEXTCOLOR);
		addDefaultComponent(mp, ild4, gbl, gbc, 12, 1, 3, 1);

		IntLEDDisplay ild5 = new IntLEDDisplay(LEDDISPLAYWIDTH, LEDDISPLAYHEIGHT, 3, 50);
		ild5.setPanelColor(DEFAULTPANELCOLOR);
		ild5.setTextColor(DEFAULTTEXTCOLOR);
		addDefaultComponent(mp, ild5, gbl, gbc, 15, 1, 3, 1);

		IntLEDDisplay ild6 = new IntLEDDisplay(LEDDISPLAYWIDTH, LEDDISPLAYHEIGHT, 3, 50);
		ild6.setPanelColor(DEFAULTPANELCOLOR);
		ild6.setTextColor(DEFAULTTEXTCOLOR);
		addDefaultComponent(mp, ild6, gbl, gbc, 21, 1, 3, 1);

		RoundLED rl15 = aLED(blink);
		addDefaultComponent(mp, rl15, gbl, gbc, 10, 2, 1, 1);
		
		RoundLED rl16 = aLED(blink);
		addDefaultComponent(mp, rl16, gbl, gbc, 19, 2, 1, 1);
		
		RoundLED rl17 = aLED(blink);
		addDefaultComponent(mp, rl17, gbl, gbc, 25, 2, 1, 1);
		
		Pot p1 = addPot("Threshold");
		p1.addAdjustmentListener(ild1);
		addDefaultComponent(mp, p1, gbl, gbc, 0, 3, 3, 2);

		Pot p2 = addPot("Ratio");
		p2.addAdjustmentListener(ild2);
		addDefaultComponent(mp, p2, gbl, gbc, 3, 3, 3, 2);

		Pot p3 = addPot("Attack");
		p3.addAdjustmentListener(ild3);
		addDefaultComponent(mp, p3, gbl, gbc, 6, 3, 3, 2);

		Pot p4 = addPot("Release");
		p4.addAdjustmentListener(ild4);
		addDefaultComponent(mp, p4, gbl, gbc, 12, 3, 3, 2);

		Pot p5 = addPot("Gain");
		p5.addAdjustmentListener(ild5);
		addDefaultComponent(mp, p5, gbl, gbc, 15, 3, 3, 2);

		Pot p6 = addPot("Limit");
		p6.addAdjustmentListener(ild6);
		addDefaultComponent(mp, p6, gbl, gbc, 21, 3, 3, 2);

		ToggleSwitchButton tsb1 = addToggle("Auto");
		tsb1.addActionListener(rl15);
		addDefaultComponent(mp, tsb1, gbl, gbc, 10, 3, 1, 2);

		ToggleSwitchButton tsb2 = addToggle("Auto");
		tsb2.addActionListener(rl16);
		addDefaultComponent(mp, tsb2, gbl, gbc, 19, 3, 1, 2);

		ToggleSwitchButton tsb3 = addToggle("Bypass");
		tsb3.addActionListener(rl17);
		addDefaultComponent(mp, tsb3, gbl, gbc, 25, 3, 1, 2);

		LabeledLED rl18 = new LabeledLED(
			blink, DEFAULTPANELCOLOR, DEFAULTLEDCOLOR, DEFAULTTEXTCOLOR,
			DEFAULTFONT, DEFAULTLEDRADIUS, "ON", true, true);
		addDefaultComponent(mp, rl18, gbl, gbc, 0, 5, 1, 1);

		add("Center", mp);
	}

	private ToggleSwitchButton addToggle(String bottomCaption) {

		ToggleSwitchButton tsb = new ToggleSwitchButton();
		tsb.setPanelColor(DEFAULTPANELCOLOR);
		tsb.setTextColor(DEFAULTTEXTCOLOR);
		tsb.setButtonColor(Color.lightGray);
		tsb.setSticky(true);
		tsb.setBottomCaption(bottomCaption);
		return tsb;
	}
	
	private Pot addPot(String label) {
		
		Pot p = new Pot();
		p.setPanelColor(DEFAULTPANELCOLOR);
		p.setKnobColor(DEFAULTKNOBCOLOR);
		p.setTextColor(DEFAULTTEXTCOLOR);
		p.setCaption(label);
		p.setKnobUseTics(true);
		p.setGradUseTics(true);
		p.setGradLengthPercent(40);
		p.setCaptionAtBottom(true);
		p.setNumberOfSections(10);
		p.setLabelPercent(180);
		p.setLabelsString(" , , , , , , , , , , ");
		p.setTicColor(Color.white);
		p.setGradColor(DEFAULTTEXTCOLOR);
		p.setRadius(DEFAULTKNOBSIZE);

		return p;
	}
	
	public RoundLED aLED(Blinker blinker) {
		
		RoundLED rl = new RoundLED();
		rl.setRadius(7);
		rl.setPanelColor(DEFAULTPANELCOLOR);
		rl.setLEDMode(RoundLED.MODESOLID);
		rl.setLEDState(false);
		blinker.addPropertyChangeListener(rl);

		return rl;
	}
	
	// Called as window is closing
	public void windowClosing() {

		System.exit(1);
	}

	public static void main(String [] args) {

		CompressorFrontPanel cp = new CompressorFrontPanel();
		cp.pack();
		cp.setVisible(true);

	}
}


