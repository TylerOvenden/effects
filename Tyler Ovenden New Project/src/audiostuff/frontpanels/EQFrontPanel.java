// EQ Front Panel Class
// Written by: Craig A. Lindley
// Last Update: 04/04/99

package audiostuff.frontpanels;

import java.awt.*;

import audiostuff.craigl.beans.pots.*;
import audiostuff.craigl.uiutils.*;
import audiostuff.craigl.utils.AudioConstants;


public class EQFrontPanel extends BaseUI implements CloseableFrameIF {

	public static final int SMALLKNOB = 13;
	public static final int LARGEKNOB = 16;
	
	public static final Color FREQKNOBCOLOR	= 
		new Color((float) 0.64, (float) 0.58, (float) 0.65);
	public static final Color BCKNOBCOLOR	= 
		new Color((float) 0.64, (float) 0.58, (float) 0.65);
	public static final Color QKNOBCOLOR	=
		new Color((float) 0.64, (float) 0.58, (float) 0.65);

	public EQFrontPanel() {

		super("EQ Front Panel", null);

		// This UI is interested in window closing events
		registerCloseListener(this);

		setBackground(AudioConstants.PANELCOLOR);

		// Use a grid for the EQ strip
		setLayout(new GridLayout(7, 1));

		// Begin HF Controls
		// High freq Freq control
		hfFreq = new Pot();
		hfFreq.setPanelColor(AudioConstants.PANELCOLOR);
		hfFreq.setKnobColor(FREQKNOBCOLOR);
		hfFreq.setCaption("HF Shelf Freq Adj");
		hfFreq.setKnobUseTics(true);
		hfFreq.setGradUseTics(true);
		hfFreq.setGradLengthPercent(40);
		hfFreq.setCaptionAtBottom(false);
		hfFreq.setNumberOfSections(5);
		hfFreq.setLabelsString("5k, , , , ,18k");
		hfFreq.setLabelPercent(180);

		hfFreq.setTicColor(Color.red);
		hfFreq.setGradColor(Color.red);
		hfFreq.setRadius(SMALLKNOB);
		add(hfFreq);

		// High freq boost/cut control
		hfBC = new Pot();
		hfBC.setPanelColor(AudioConstants.PANELCOLOR);
		hfBC.setKnobColor(BCKNOBCOLOR);
		hfBC.setCaption("High Freq Eq");
		hfBC.setKnobUseTics(false);
		hfBC.setGradUseTics(false);
		hfBC.setGradLengthPercent(25);
		hfBC.setTicStartPercent(70);
		hfBC.setTicLengthPercent(30);
		hfBC.setGradGapPercent(10);
		hfBC.setCaptionAtBottom(true);
		hfBC.setNumberOfSections(12);
		hfBC.setLabelsString(
			"-12db, , , , , ,0, , , , , ,+12db");
		hfBC.setLabelPercent(180);

		hfBC.setTicColor(Color.red);
		hfBC.setGradColor(Color.red);
		hfBC.setRadius(LARGEKNOB);
		add(hfBC);
		// End HF Controls

		// Begin MF Controls
		// Midrangle Freq control
		mfFreq = new Pot();
		mfFreq.setPanelColor(AudioConstants.PANELCOLOR);
		mfFreq.setKnobColor(FREQKNOBCOLOR);
		mfFreq.setCaption("MF Peak Freq Adj");
		mfFreq.setKnobUseTics(true);
		mfFreq.setGradUseTics(true);
		mfFreq.setGradLengthPercent(40);
		mfFreq.setCaptionAtBottom(false);
		mfFreq.setNumberOfSections(5);
		mfFreq.setLabelsString("1.5k, , , , ,6k");
		mfFreq.setLabelPercent(180);

		mfFreq.setTicColor(Color.green);
		mfFreq.setGradColor(Color.green);
		mfFreq.setRadius(SMALLKNOB);
		add(mfFreq);
	
		// Midrangle Q control
		mfq = new Pot();
		mfq.setPanelColor(AudioConstants.PANELCOLOR);
		mfq.setKnobColor(QKNOBCOLOR);
		mfq.setCaption("Q");
		mfq.setKnobUseTics(true);
		mfq.setGradUseTics(true);
		mfq.setGradLengthPercent(40);
		mfq.setCaptionAtBottom(false);
		mfq.setNumberOfSections(15);
		mfq.setLabelsString("1, , , , , , , , , , , , , , ,16");
		mfq.setLabelPercent(180);

		mfq.setTicColor(Color.green);
		mfq.setGradColor(Color.green);
		mfq.setRadius(SMALLKNOB);
		add(mfq);

		// Mid freq boost/cut control
		mfBC = new Pot();
		mfBC.setPanelColor(AudioConstants.PANELCOLOR);
		mfBC.setKnobColor(BCKNOBCOLOR);
		mfBC.setCaption("Midrange Eq");
		mfBC.setKnobUseTics(false);
		mfBC.setGradUseTics(false);
		mfBC.setGradLengthPercent(25);
		mfBC.setTicStartPercent(70);
		mfBC.setTicLengthPercent(30);
		mfBC.setGradGapPercent(10);
		mfBC.setCaptionAtBottom(true);
		mfBC.setNumberOfSections(12);
		mfBC.setLabelsString(
			"-12db, , , , , ,0, , , , , ,+12db");
		mfBC.setLabelPercent(180);

		mfBC.setTicColor(Color.green);
		mfBC.setGradColor(Color.green);
		mfBC.setRadius(LARGEKNOB);
		add(mfBC);
		// End MF Controls

		// Begin LF Controls
		
		// Low freq Freq control
		lfFreq = new Pot();
		lfFreq.setPanelColor(AudioConstants.PANELCOLOR);
		lfFreq.setKnobColor(FREQKNOBCOLOR);
		lfFreq.setCaption("LF Shelf Freq Adj");
		lfFreq.setKnobUseTics(true);
		lfFreq.setGradUseTics(true);
		lfFreq.setGradLengthPercent(40);
		lfFreq.setCaptionAtBottom(false);
		lfFreq.setNumberOfSections(5);
		lfFreq.setLabelsString("40, , , , ,1.5k");
		lfFreq.setLabelPercent(180);

		lfFreq.setTicColor(Color.blue);
		lfFreq.setGradColor(Color.blue);
		lfFreq.setRadius(SMALLKNOB);
		add(lfFreq);

		// Low freq boost/cut control
		lfBC = new Pot();
		lfBC.setPanelColor(AudioConstants.PANELCOLOR);
		lfBC.setKnobColor(BCKNOBCOLOR);
		lfBC.setCaption("Low Freq Eq");
		lfBC.setKnobUseTics(false);
		lfBC.setGradUseTics(false);
		lfBC.setGradLengthPercent(25);
		lfBC.setTicStartPercent(70);
		lfBC.setTicLengthPercent(30);
		lfBC.setGradGapPercent(10);
		lfBC.setCaptionAtBottom(true);
		lfBC.setNumberOfSections(12);
		lfBC.setLabelsString(
			"-12db, , , , , ,0, , , , , ,+12db");
		lfBC.setLabelPercent(180);

		lfBC.setTicColor(Color.blue);
		lfBC.setGradColor(Color.blue);
		lfBC.setRadius(LARGEKNOB);
		add(lfBC);
		// End LF Controls
	}

	// Called as window is closing
	public void windowClosing() {

		System.exit(1);
	}


	public static void main(String [] args) {

		EQFrontPanel eq = new EQFrontPanel();
		eq.pack();
		eq.setVisible(true);
	}

	// Private class data
	Pot hfFreq;
	Pot hfBC;
	Pot mfFreq;
	Pot mfq;
	Pot mfBC;
	Pot lfFreq;
	Pot lfBC;

}
