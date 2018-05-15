// WinRecorder UI Code
// Written by: Craig A. Lindley
// Last Update: 08/26/98

package audiostuff.craigl.winrecorder;

import java.awt.*;
import java.awt.event.*;
import audiostuff.craigl.utils.*;
import audiostuff.craigl.uiutils.*;
import audiostuff.craigl.beans.blinker.*;
import audiostuff.craigl.beans.leds.*;

public class WinRecorderWithUI extends CloseableFrame
	implements AudioUIIF, RecorderIF, CloseableFrameIF {

	public WinRecorderWithUI(Blinker blink, int sampleRate, int channels) {
		super("Windows Recorder With UI");

		// Save incoming parameters
		this.sampleRate = sampleRate;
		this.channels   = channels;
		
		// This UI is interested in window closing events
		registerCloseListener(this);

		// Instantiate recorder for callback but don't initialize it
		wr = new WinRecorder(sampleRate, channels, 0, this);

		setBackground(AudioConstants.PANELCOLOR);

		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mp.setLayout(gbl);

		Box b = createSampleRateGroup();

		BaseUI.addDefaultComponent(mp, b, gbl, gbc, 0, 0, 5, 5);

		b = createChannelsGroup();
		BaseUI.addDefaultComponent(mp, b, gbl, gbc, 6, 0, 5, 5);

		Label l = new Label("Recording", Label.CENTER);
		BaseUI.addDefaultComponent(mp, l, gbl, gbc, 1, 6, 3, 1);

		recordingLED = new RoundLED();
		recordingLED.setRadius(7);
		recordingLED.setPanelColor(AudioConstants.PANELCOLOR);
		recordingLED.setLEDMode(RoundLED.MODESOLID);
		recordingLED.setLEDColor(Color.green);
		recordingLED.setLEDState(true);
		blink.addPropertyChangeListener(recordingLED);
		BaseUI.addDefaultComponent(mp, recordingLED, gbl, gbc, 5, 6, 1, 1);

		stopButton = new Button("Stop");
		stopButton.setBackground(AudioConstants.PANELCOLOR);
		stopButton.setEnabled(false);
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopButtonClicked();
			}
		});
		BaseUI.addDefaultComponent(mp, stopButton, gbl, gbc, 7, 6, 3, 1);
		add(mp);

		pack();
	}

	// Part of RecorderIF
	public void beginRecording() {

		// Initialize recorder
		wr.initRecorder();

		// Change component states
		recordingLED.setLEDColor(Color.red);
		stopButton.setEnabled(true);
	}
	
	public void stopButtonClicked() {

		// Stop the recorder
		wr.closeRecorder();

		// Change component states
		recordingLED.setLEDColor(Color.green);
		stopButton.setEnabled(false);
	}

	public void windowClosing() {
		// Stop the recorder
		wr.closeRecorder();
	}
	
	// Create the sampling rate control group and check the proper
	// rate. Make all of the check boxes read only.
	private Box createSampleRateGroup() {
		
		Panel panel = new Panel();
		Box box = new Box(panel, "Sampling");
		CheckboxGroup group = new CheckboxGroup();

		panel.setLayout(new GridLayout(3, 0));
		boolean enableCheck = (sampleRate == 44100);
		
		Checkbox cb1 = new Checkbox("44.1K rate",   group, enableCheck);
		cb1.setEnabled(false);
		panel.add(cb1);

		enableCheck = (sampleRate == 22050);
		Checkbox cb2 = new Checkbox("22.05K rate",  group, enableCheck);
		cb2.setEnabled(false);
		panel.add(cb2);

		enableCheck = (sampleRate == 11025);
		Checkbox cb3 = new Checkbox("11.025K rate", group, enableCheck);
		cb3.setEnabled(false);
		panel.add(cb3);
		
		return box;
	}

	// Create the channel mode control group and check the proper
	// item. Make all of the check boxes read only.
	private Box createChannelsGroup() {
		
		Panel panel = new Panel();
		Box box = new Box(panel, "Mode");
		CheckboxGroup group = new CheckboxGroup();

		panel.setLayout(new GridLayout(2, 0));
		
		boolean enableCheck = (channels == 1);
		Checkbox cb1 = new Checkbox("Mono", group, enableCheck);
		cb1.setEnabled(false);
		panel.add(cb1);

		enableCheck = (channels == 2);
		Checkbox cb2 = new Checkbox("Stereo", group, enableCheck);
		cb2.setEnabled(false);
		panel.add(cb2);
		
		return box;
	}
	
	public void showUI(boolean isVisible) {
        
        setVisible(isVisible);
    }
	
    public void stopUI() {
        
        dispose();
    }

	public AbstractAudio getAA() {

		return wr;
	}
	
	// Private class data
	private int sampleRate;
	private int channels;
	private RoundLED recordingLED;
	private Button stopButton;
	private WinRecorder wr;
}
