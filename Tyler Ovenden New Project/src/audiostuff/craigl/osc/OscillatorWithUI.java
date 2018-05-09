// Oscillator with UI Code
// Written by: Craig A. Lindley
// Last Update: 04/22/99

package craigl.osc;

import java.awt.*;
import java.awt.event.*;
import craigl.beans.displays.*;
import craigl.beans.leds.*;
import craigl.beans.pots.*;
import craigl.utils.*;
import craigl.uiutils.*;

public class OscillatorWithUI extends BaseUI
	implements CloseableFrameIF, NegotiationCompleteIF {

	// Multi valued pot ranges
	private static final int FREQMIN = 1;
	private static final int RANGE0	 = 20;
	private static final int RANGE1  = 200;
	private static final int RANGE2  = 2000;
	private static final int RANGE3  = 20000;
	private static final int FREQMAX = RANGE3;

	public OscillatorWithUI() {
		super("Oscillator With UI", null);

		// This UI is interested in window closing events
		registerCloseListener(this);

		// Instantiate a mono oscillator
		osc = new Oscillator(this);
		aa = (AbstractAudio) osc;

		// Calculate pot range granularities
		range0Granularity = RANGE0 / 100.0;
		range1Granularity = RANGE1 / 100.0;
		range2Granularity = RANGE2 / 100.0;
		range3Granularity = RANGE3 / 100.0;

		// Set initial pot mode 
		freqPotMode = 2;

		// Create the UI
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mp.setLayout(gbl);

		sampleRateGroup = createSampleRateGroup();
		addDefaultComponent(mp, sampleRateGroup, gbl, gbc, 0, 0, 5, 4);

		Panel cp = new Panel();
		addDefaultComponent(mp, cp, gbl, gbc, 5, 0, 5, 4);

		Box b = createOscTypeGroup();
		addDefaultComponent(mp, b, gbl, gbc, 10, 0, 5, 5);

		b = createRangeGroup();
		addDefaultComponent(mp, b, gbl, gbc, 0, 5, 5, 4);

		freqDisplay = new ReadoutLabel("Hz");
		addDefaultComponent(mp, freqDisplay, gbl, gbc, 6, 5, 3, 1);

		// Create the frequency pot
		// Freq pot has 20 sections
		freqPot = createPot(19, "Frequency", "min, , , , , , , , , ,max");
		freqPot.setTextColor(Color.black);
		freqPot.setGradColor(Color.black);

		freqPot.setNumberOfSections(20);
		freqPot.setLabelsString("min, , , , , , , , , , , , , , , ,  , , , ,max");
		addDefaultComponent(mp, freqPot, gbl, gbc, 6, 6, 3, 3);

		// Set midrange value
		freqChanged(50);

		// Add listener to this pot
		freqPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				freqChanged(e.getValue());
			}
		});
		// Create the amplitude slide pot
		amplitudePot = new SlidePot(100, 16, "Amplitude", 100);
        amplitudePot.setKnobColor(AudioConstants.KNOBCOLOR);
        amplitudePot.setPanelColor(AudioConstants.PANELCOLOR);
		amplitudePot.setTextColor(Color.black);
		amplitudePot.setGradColor(Color.black);
		amplitudePot.setNumberOfSections(10);
		amplitudePot.setLabelPercent(200);
		amplitudePot.setLabelsString("0db, , , , ,-20, , , , ,Inf");
		addDefaultComponent(mp, amplitudePot, gbl, gbc, 11, 5, 3, 5);

		// Add listener to this pot
		amplitudePot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				amplitudeChanged(amplitudePot);
			}
		});

		add(mp);
		pack();
	}

	// Called when the freq pot is manipulated. potValue is
	// always 0..100. Actual freq is dependent upon range
	// selected.
	public void freqChanged(int newPotValue) {

		// Determine direction and magnitude of change
		int delta = newPotValue - oldPotValue;
		
		oldPotValue = newPotValue;
		
		// Calculate current value based on freq mode and potValue
		double newValue = 0;
		switch(freqPotMode) {

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
		currentFreq += newValue;
		
		// Range check values
		if (currentFreq < FREQMIN)
			currentFreq = FREQMIN;

		else if (currentFreq > FREQMAX)
			currentFreq = FREQMAX;
		
		// Update the display
		freqDisplay.setValue((int) currentFreq);

		// Set osc frequency
		osc.setFrequency((int) currentFreq);
	}

	// Called when the amplitude pot is manipulated. Note: amplitude
	// pot is modelled with a pseudo audio taper.
	public void amplitudeChanged(PotBase p) {

		osc.setAmplitudeAdj(p.getAttenuation());
	}
	
	// Called when negotation has been completed. After this point
	// sampleRate and number of channels cannot be changed.
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

	private Box createChannelsGroup() {
		
		Panel panel = new Panel();
		Box box = new Box(panel, "Mode");
		CheckboxGroup group = new CheckboxGroup();

		panel.setLayout(new GridLayout(2, 0));
		
		Checkbox cb1 = new Checkbox("Mono", group, true);
		cb1.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					osc.setNumberOfChannels(1);
			}
		});
		panel.add(cb1);

		Checkbox cb2 = new Checkbox("Stereo", group, false);
		cb2.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					osc.setNumberOfChannels(2);
			}
		});
		panel.add(cb2);
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

	
	private Box createRangeGroup() {
		
		Panel panel = new Panel();
		Box box = new Box(panel, "Freq Range");
		CheckboxGroup group = new CheckboxGroup();

		panel.setLayout(new GridLayout(4, 0));
		
		Checkbox cb1 = new Checkbox("1 Hz", group, false);
		cb1.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					freqPotMode = 0;
			}
		});
		panel.add(cb1);

		Checkbox cb2 = new Checkbox("10 Hz",  group, false);
		cb2.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					freqPotMode = 1;
			}
		});
		panel.add(cb2);

		Checkbox cb3 = new Checkbox("100 Hz", group, true);
		cb3.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					freqPotMode = 2;
			}
		});
		panel.add(cb3);
		
		Checkbox cb4 = new Checkbox("1000 Hz", group, false);
		cb4.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					freqPotMode = 3;
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
	private Oscillator osc;
	private ReadoutLabel freqDisplay;
	private Pot freqPot;
	private int freqPotMode;
	private double range0Granularity;
	private double range1Granularity;
	private double range2Granularity;
	private double range3Granularity;
	private int oldPotValue;
	private double currentFreq;
	private SlidePot amplitudePot;
}
