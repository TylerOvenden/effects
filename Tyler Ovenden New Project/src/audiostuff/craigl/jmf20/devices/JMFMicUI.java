// JMF2.0 Sample Acquisition UI Code
// Written by: Craig A. Lindley
// Last Update: 11/07/99

package craigl.jmf20.devices;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import craigl.utils.*;
import craigl.uiutils.*;
import craigl.beans.blinker.*;
import craigl.beans.displays.*;
import craigl.beans.leds.*;

// The java media packages
import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.format.audio.*;
import javax.media.protocol.*;

public class JMFMicUI extends CloseableFrame implements CloseableFrameIF {

	private static final int LISTBOXHEIGHT = 4;

	/**
	 * JMFMicUI class constructor
	 */
	public JMFMicUI(Blinker blink, JMFMicWithUI jfwui) {
		super("JMFMic with UI");

		// Save incoming
		this.jfwui = jfwui;
		sampleRate = jfwui.sampleRate;
		channels = jfwui.channels;

		// Register this UI as being interested in window close events
		registerCloseListener(this);

		setBackground(AudioConstants.PANELCOLOR);

		// Now build the simple UI
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mp.setLayout(gbl);

		// Create the sample rate radio button group
		Box b = createSampleRateGroup();
		BaseUI.addDefaultComponent(mp, b, gbl, gbc, 0, 0, 5, 5);

		// Create the channels selection radio button group
		b = createChannelsGroup();
		BaseUI.addDefaultComponent(mp, b, gbl, gbc, 6, 0, 5, 5);

		// Create a list box for displaying the devices
		listBox = new java.awt.List(LISTBOXHEIGHT);
		
		// Fill the list box
		fillListBoxWithDevices(sampleRate, channels);
		BaseUI.addDefaultComponent(mp, listBox, gbl, gbc, 0, 6, 10, 5);

		// Create the round recording LED
		recordingLED = new RoundLED();
		recordingLED.setRadius(7);
		recordingLED.setPanelColor(AudioConstants.PANELCOLOR);
		recordingLED.setLEDMode(RoundLED.MODESOLID);
		recordingLED.setLEDColor(Color.green);
		recordingLED.setLEDState(true);
		blink.addPropertyChangeListener(recordingLED);
		BaseUI.addDefaultComponent(mp, recordingLED, gbl, gbc, 5, 12, 1, 1);

		// Create the select device button
		selectButton = new Button("Select Device");
		selectButton.setBackground(AudioConstants.PANELCOLOR);
		selectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectButtonClicked();
			}
		});
		BaseUI.addDefaultComponent(mp, selectButton, gbl, gbc, 0, 12, 3, 1);

		// Create the stop button
		stopButton = new Button("Stop");
		stopButton.setBackground(AudioConstants.PANELCOLOR);
		stopButton.setEnabled(false);
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopButtonClicked();
			}
		});
		BaseUI.addDefaultComponent(mp, stopButton, gbl, gbc, 7, 12, 3, 1);
		add(mp);

		pack();
	}

	// Method called when select device button is clicked
	private void selectButtonClicked() {

		selectButton.setEnabled(false);
		listBox.setEnabled(false);

		// Prepare the device for acquisition with specified number
		// of channels. 
		jfwui.captureDeviceSelected();

		stopButton.setEnabled(true);
	}

	// Method called when stop button is clicked
	private void stopButtonClicked() {

		jfwui.stopCapture();
	}


	// Determine which list box item (device) was selected and return the
	// media locator associated with it.
	public MediaLocator getMediaLocator() {

		// Which device is selected
		int index = listBox.getSelectedIndex();

		// Get the CaptureDeviceInfo for the selected device
		CaptureDeviceInfo cdi = 
			(CaptureDeviceInfo) captureDevices.elementAt(index);
		
		// Get the media locator associated with the capture device
		MediaLocator ml = cdi.getLocator();
		
		// Get the protocol string for the device
		String protocolString = ml.getProtocol();

		// Build a new protocol string for the device we require
		protocolString += "://" + sampleRate + "/16/" + channels + "/" +
			              "little/signed";
		// Build and return a new media locator
		return new MediaLocator(protocolString);
	}

	// Fill the device list box with devices that match the specifications.
	private void fillListBoxWithDevices(int sampleRate, int channels) {

		// Remove all previous entries
		listBox.removeAll();
		
		// Get the collection of capture devices on the system with the
		// specified attributes.
		captureDevices = jfwui.getDeviceList(sampleRate, channels);

		// Fill the list with content
	    for (int i = 0; i < captureDevices.size(); i++) {
			// Get a device
			CaptureDeviceInfo cdi = (CaptureDeviceInfo) captureDevices.elementAt(i);

			// Get its name
			String name = cdi.getName();

			// Add it in
			listBox.add(name);
	    }
	    // Select first device in list
		listBox.select(0);
	}


	// Create the sample rate control group and check the proper item. 
	private Box createSampleRateGroup() {
		
		Panel panel = new Panel();
		Box box = new Box(panel, "Sampling");
		CheckboxGroup group = new CheckboxGroup();

		panel.setLayout(new GridLayout(3, 0));
		boolean enableCheck = (sampleRate == 44100);
		Checkbox cb1 = new Checkbox("44.1K rate",   group, enableCheck);
		panel.add(cb1);
		cb1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					sampleRate = 44100;

					// Reload list box
					fillListBoxWithDevices(sampleRate, channels);
				}
			}
		});

		enableCheck = (sampleRate == 22050);
		Checkbox cb2 = new Checkbox("22.05K rate",  group, enableCheck);
		panel.add(cb2);
		cb2.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					sampleRate = 22050;

					// Reload list box
					fillListBoxWithDevices(sampleRate, channels);
				}
			}
		});

		enableCheck = (sampleRate == 11025);
		Checkbox cb3 = new Checkbox("11.025K rate", group, enableCheck);
		panel.add(cb3);
		cb3.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					sampleRate = 11025;

					// Reload list box
					fillListBoxWithDevices(sampleRate, channels);
				}
			}
		});
		
		return box;
	}
	
	
	// Create the channel mode control group and check the proper item. 
	private Box createChannelsGroup() {
		
		Panel panel = new Panel();
		Box box = new Box(panel, "Mode");
		CheckboxGroup group = new CheckboxGroup();

		panel.setLayout(new GridLayout(2, 0));
		
		boolean enableCheck = (channels == 1);
		Checkbox cb1 = new Checkbox("Mono", group, enableCheck);
		panel.add(cb1);
		cb1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					channels = 1;

					// Reload list box
					fillListBoxWithDevices(sampleRate, channels);
				}
			}
		});

		enableCheck = (channels == 2);
		Checkbox cb2 = new Checkbox("Stereo", group, enableCheck);
		panel.add(cb2);
		cb2.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					channels = 2;

					// Reload list box
					fillListBoxWithDevices(sampleRate, channels);
				}
			}
		});
		
		return box;
	}
	
	// Control the recording LED
	public void setRecordLEDState(boolean recording) {

		recordingLED.setLEDColor(recording ? Color.red:Color.green);
	}
	
	public void showUI(boolean isVisible) {
        
		// Set visibility
        setVisible(isVisible);
	}
	
    public void stopUI() {
        
		jfwui.stopCapture();
        dispose();
    }


	public void windowClosing() {

		stopUI();
	}

	// Private class data
	private int sampleRate;
	private int channels;
	private java.awt.List listBox;
	private Vector captureDevices;
	private RoundLED recordingLED;
	private Button selectButton;
	private Button stopButton;
	private JMFMicWithUI jfwui;
}
