// Stereo Oscillator with UI Code
// Written by: Craig A. Lindley
// Last Update: 03/21/99

package craigl.osc;

import java.awt.*;
import java.awt.event.*;
import craigl.beans.displays.*;
import craigl.beans.leds.*;
import craigl.beans.pots.*;
import craigl.utils.*;
import craigl.uiutils.*;

public class StereoOscillatorWithUI extends BaseUI
	implements CloseableFrameIF, NegotiationCompleteIF {

	// Multi valued pot ranges
	private static final int FREQMIN = 1;
	private static final int RANGE0	 = 20;
	private static final int RANGE1  = 200;
	private static final int RANGE2  = 2000;
	private static final int RANGE3  = 20000;
	private static final int FREQMAX = RANGE3;

	public StereoOscillatorWithUI() {
		super("Stereo Oscillator With UI", null);

		// This UI is interested in window closing events
		registerCloseListener(this);

		// Instantiate a stereo oscillator
		osc = new StereoOscillator(this);
		aa = (AbstractAudio) osc;

		// Calculate pot range granularities
		range0Granularity = RANGE0 / 100.0;
		range1Granularity = RANGE1 / 100.0;
		range2Granularity = RANGE2 / 100.0;
		range3Granularity = RANGE3 / 100.0;

		// Set initial pot mode 
		leftFreqPotMode  = 2;
		rightFreqPotMode = 2;

		// Create the UI
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mp.setLayout(gbl);

		sampleRateGroup = createSampleRateGroup();
		addDefaultComponent(mp, sampleRateGroup, gbl, gbc, 0, 0, 5, 5);

		Box b = createLeftRangeGroup();
		addDefaultComponent(mp, b, gbl, gbc, 5, 0, 5, 5);

		Label l = new Label("Left Channel", Label.CENTER);
		addDefaultComponent(mp, l, gbl, gbc, 11, 0, 3, 1);

		leftFreqDisplay = new ReadoutLabel("Hz");
		addDefaultComponent(mp, leftFreqDisplay, gbl, gbc, 11, 1, 3, 1);
		
		// Create the left frequency pot
		// Freq pot has 20 sections
		leftFreqPot = createPot(19, "Frequency", "min, , , , , , , , , ,max");
		leftFreqPot.setTextColor(Color.black);
		leftFreqPot.setGradColor(Color.black);

		leftFreqPot.setNumberOfSections(20);
		leftFreqPot.setLabelsString("min, , , , , , , , , , , , , , , ,  , , , ,max");
		addDefaultComponent(mp, leftFreqPot, gbl, gbc, 11, 2, 3, 3);

		// Set midrange value
		leftFreqChanged(50);

		// Add listener to this pot
		leftFreqPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				leftFreqChanged(e.getValue());
			}
		});
		// Create the amplitude slide pot
		leftAmplitudePot = new SlidePot(100, 16, "Amplitude", 100);
        leftAmplitudePot.setKnobColor(AudioConstants.KNOBCOLOR);
        leftAmplitudePot.setPanelColor(AudioConstants.PANELCOLOR);
		leftAmplitudePot.setTextColor(Color.black);
		leftAmplitudePot.setGradColor(Color.black);
		leftAmplitudePot.setNumberOfSections(10);
		leftAmplitudePot.setLabelPercent(200);
		leftAmplitudePot.setLabelsString("0db, , , , ,-20, , , , ,Inf");
		addDefaultComponent(mp, leftAmplitudePot, gbl, gbc, 15, 0, 3, 5);

		// Add listener to this pot
		leftAmplitudePot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				leftAmplitudeChanged(leftAmplitudePot);
			}
		});
		
		// Now the bottom half of UI
		
		b = createOscTypeGroup();
		addDefaultComponent(mp, b, gbl, gbc, 0, 5, 5, 5);

		b = createRightRangeGroup();
		addDefaultComponent(mp, b, gbl, gbc, 5, 5, 5, 5);

		l = new Label("Right Channel", Label.CENTER);
		addDefaultComponent(mp, l, gbl, gbc, 11, 5, 3, 1);
		
		rightFreqDisplay = new ReadoutLabel("Hz");
		addDefaultComponent(mp, rightFreqDisplay, gbl, gbc, 11, 6, 3, 1);

		// Create the right frequency pot
		// Freq pot has 20 sections
		rightFreqPot = createPot(19, "Frequency", "min, , , , , , , , , ,max");
		rightFreqPot.setTextColor(Color.black);
		rightFreqPot.setGradColor(Color.black);

		rightFreqPot.setNumberOfSections(20);
		rightFreqPot.setLabelsString("min, , , , , , , , , , , , , , , ,  , , , ,max");
		addDefaultComponent(mp, rightFreqPot, gbl, gbc, 11, 7, 3, 3);

		// Set midrange value
		rightFreqChanged(50);

		// Add listener to this pot
		rightFreqPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				rightFreqChanged(e.getValue());
			}
		});
		// Create the amplitude slide pot
		rightAmplitudePot = new SlidePot(100, 16, "Amplitude", 100);
        rightAmplitudePot.setKnobColor(AudioConstants.KNOBCOLOR);
        rightAmplitudePot.setPanelColor(AudioConstants.PANELCOLOR);
		rightAmplitudePot.setTextColor(Color.black);
		rightAmplitudePot.setGradColor(Color.black);
		rightAmplitudePot.setNumberOfSections(10);
		rightAmplitudePot.setLabelPercent(200);
		rightAmplitudePot.setLabelsString("0db, , , , ,-20, , , , ,Inf");
		addDefaultComponent(mp, rightAmplitudePot, gbl, gbc, 15, 5, 3, 5);

		// Add listener to this pot
		rightAmplitudePot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				rightAmplitudeChanged(rightAmplitudePot);
			}
		});

		add(mp);
		pack();
	}

	// Called when the left freq pot is manipulated. potValue is
	// always 0..100. Actual freq is dependent upon range
	// selected.
	public void leftFreqChanged(int newPotValue) {

		// Determine direction and magnitude of change
		int delta = newPotValue - leftOldPotValue;
		
		leftOldPotValue = newPotValue;
		
		// Calculate current value based on freq mode and potValue
		double newValue = 0;
		switch(leftFreqPotMode) {

			case 0:	// Finest range
				newValue = range0Granularity * delta;
				break;
		
			case 1:
				newValue = range1Granularity * delta;
				break;
		
			case 2:
				newValue = range2Granularity * delta;
				break;

			case 3:	// Coursest range
				newValue = range3Granularity * delta;
				break;
		}
		// Update currentFreq
		leftCurrentFreq += newValue;
		
		// Range check values
		if (leftCurrentFreq < FREQMIN)
			leftCurrentFreq = FREQMIN;

		else if (leftCurrentFreq > FREQMAX)
			leftCurrentFreq = FREQMAX;
		
		// Update the display
		leftFreqDisplay.setValue((int) leftCurrentFreq);

		// Set osc frequency
		osc.setLeftFrequency((int) leftCurrentFreq);
	}

	// Called when the right freq pot is manipulated. potValue is
	// always 0..100. Actual freq is dependent upon range
	// selected.
	public void rightFreqChanged(int newPotValue) {

		// Determine direction and magnitude of change
		int delta = newPotValue - rightOldPotValue;
		
		rightOldPotValue = newPotValue;
		
		// Calculate current value based on freq mode and potValue
		double newValue = 0;
		switch(rightFreqPotMode) {

			case 0:	// Finest range
				newValue = range0Granularity * delta;
				break;
		
			case 1:
				newValue = range1Granularity * delta;
				break;
		
			case 2:
				newValue = range2Granularity * delta;
				break;

			case 3:	// Coursest range
				newValue = range3Granularity * delta;
				break;
		}
		// Update currentFreq
		rightCurrentFreq += newValue;
		
		// Range check values
		if (rightCurrentFreq < FREQMIN)
			rightCurrentFreq = FREQMIN;

		else if (rightCurrentFreq > FREQMAX)
			rightCurrentFreq = FREQMAX;
		
		// Update the display
		rightFreqDisplay.setValue((int) rightCurrentFreq);

		// Set osc frequency
		osc.setRightFrequency((int) rightCurrentFreq);
	}

	// Called when the left amplitude pot is manipulated. Note: amplitude
	// pot is modelled with a pseudo audio taper.
	public void leftAmplitudeChanged(PotBase p) {

		osc.setLeftAmplitudeAdj(p.getAttenuation());
	}
	
	// Called when the right amplitude pot is manipulated. Note: amplitude
	// pot is modelled with a pseudo audio taper.
	public void rightAmplitudeChanged(PotBase p) {

		osc.setRightAmplitudeAdj(p.getAttenuation());
	}
	
	// Called when negotation has been completed. After this point
	// sampleRate cannot be changed.
	public void signalNegotiationComplete() {

		// Disable all of the buttons in the group boxes
		sampleRateGroup.setEnabled(false);
	}
	
	public void windowClosing() {
		
		// Nothing extra to do
	}
	
	private Box createSampleRateGroup() {
		
		Panel panel = new Panel();
		Box box = new Box(panel, "Sampling");
		CheckboxGroup group = new CheckboxGroup();

		panel.setLayout(new GridLayout(3, 0));
		
		Checkbox cb1 = new Checkbox("44.1K rate",   group, false);
		cb1.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					osc.setSampleRate(44100);
			}
		});
		panel.add(cb1);

		Checkbox cb2 = new Checkbox("22.05K rate",  group, true);
		cb2.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					osc.setSampleRate(22050);
			}
		});
		panel.add(cb2);

		Checkbox cb3 = new Checkbox("11.025K rate", group, false);
		cb3.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					osc.setSampleRate(11025);
			}
		});
		panel.add(cb3);
		return box;
	}

	private Box createOscTypeGroup() {
		
		Panel panel = new Panel();
		Box box = new Box(panel, "Osc Type");
		CheckboxGroup group = new CheckboxGroup();

		panel.setLayout(new GridLayout(4, 0));
		
		Checkbox cb1 = new Checkbox("Noise",   group, false);
		cb1.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					osc.setOscType(Oscillator.NOISE);
			}
		});
		panel.add(cb1);

		Checkbox cb2 = new Checkbox("Sine",  group, true);
		cb2.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					osc.setOscType(Oscillator.SINEWAVE);
			}
		});
		panel.add(cb2);

		Checkbox cb3 = new Checkbox("Triangle", group, false);
		cb3.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					osc.setOscType(Oscillator.TRIANGLEWAVE);
			}
		});
		panel.add(cb3);
		
		Checkbox cb4 = new Checkbox("Square", group, false);
		cb4.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					osc.setOscType(Oscillator.SQUAREWAVE);
			}
		});
		panel.add(cb4);
		return box;
	}

	private Box createLeftRangeGroup() {
		
		Panel panel = new Panel();
		Box box = new Box(panel, "Left Range");
		CheckboxGroup group = new CheckboxGroup();

		panel.setLayout(new GridLayout(4, 0));
		
		Checkbox cb1 = new Checkbox("1 Hz", group, false);
		cb1.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					leftFreqPotMode = 0;
			}
		});
		panel.add(cb1);

		Checkbox cb2 = new Checkbox("10 Hz",  group, false);
		cb2.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					leftFreqPotMode = 1;
			}
		});
		panel.add(cb2);

		Checkbox cb3 = new Checkbox("100 Hz", group, true);
		cb3.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					leftFreqPotMode = 2;
			}
		});
		panel.add(cb3);
		
		Checkbox cb4 = new Checkbox("1000 Hz", group, false);
		cb4.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					leftFreqPotMode = 3;
			}
		});
		panel.add(cb4);
		return box;
	}

	private Box createRightRangeGroup() {
		
		Panel panel = new Panel();
		Box box = new Box(panel, "Right Range");
		CheckboxGroup group = new CheckboxGroup();

		panel.setLayout(new GridLayout(4, 0));
		
		Checkbox cb1 = new Checkbox("1 Hz", group, false);
		cb1.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					rightFreqPotMode = 0;
			}
		});
		panel.add(cb1);

		Checkbox cb2 = new Checkbox("10 Hz",  group, false);
		cb2.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					rightFreqPotMode = 1;
			}
		});
		panel.add(cb2);

		Checkbox cb3 = new Checkbox("100 Hz", group, true);
		cb3.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					rightFreqPotMode = 2;
			}
		});
		panel.add(cb3);
		
		Checkbox cb4 = new Checkbox("1000 Hz", group, false);
		cb4.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					rightFreqPotMode = 3;
			}
		});
		panel.add(cb4);
		return box;
	}

	public void showUI(boolean isVisible) {
        
        setVisible(isVisible);
    }
	
    public void stopUI() {
        
        dispose();
    }

	public AbstractAudio getAA() {

		return osc;
	}

	// Private class data
	private Box sampleRateGroup;
	private StereoOscillator osc;
	private ReadoutLabel leftFreqDisplay;
	private ReadoutLabel rightFreqDisplay;
	private Pot leftFreqPot;
	private Pot rightFreqPot;
	private int leftFreqPotMode;
	private int rightFreqPotMode;
	private double range0Granularity;
	private double range1Granularity;
	private double range2Granularity;
	private double range3Granularity;
	private int leftOldPotValue;
	private int rightOldPotValue;
	private double leftCurrentFreq;
	private double rightCurrentFreq;
	private SlidePot leftAmplitudePot;
	private SlidePot rightAmplitudePot;
}
