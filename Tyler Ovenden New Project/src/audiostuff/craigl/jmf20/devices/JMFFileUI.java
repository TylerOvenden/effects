// JMF2.0 File Reader With UI Code
// Written by: Craig A. Lindley
// Last Update: 06/02/99

package audiostuff.craigl.jmf20.devices;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import audiostuff.craigl.utils.*;
import audiostuff.craigl.uiutils.*;
import audiostuff.craigl.beans.blinker.*;
import audiostuff.craigl.beans.displays.*;
import audiostuff.craigl.beans.leds.*;

public class JMFFileUI extends CloseableFrame implements CloseableFrameIF {

	/**
	 * JMFFileUI class constructor
	 */
	public JMFFileUI(Blinker blink, JMFFileWithUI jfwui) {
		super("JMFFile with UI");

		// Save incoming
		this.jfwui = jfwui;

		// Register this UI as being interested in window close events
		registerCloseListener(this);

		// Build the simply UI
		setBackground(AudioConstants.PANELCOLOR);
		setLayout(new GridLayout(2, 1));

		Panel p1 = new Panel();
		p1.setLayout(new GridLayout(1, 3));

		Label l = new Label("Reading", Label.CENTER);
		p1.add(l);

		readingLED = new RoundLED();
		readingLED.setRadius(7);
		readingLED.setPanelColor(AudioConstants.PANELCOLOR);
		readingLED.setLEDMode(RoundLED.MODEBLINK);
		readingLED.setLEDColor(Color.green);
		readingLED.setLEDState(false);
		blink.addPropertyChangeListener(readingLED);
		p1.add(readingLED);

		l = new Label("Indicator", Label.CENTER);
		p1.add(l);
		add(p1);

		Panel p2 = new Panel();
		p2.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		fileSelectButton = new Button("Select File");
		fileSelectButton.setBackground(AudioConstants.PANELCOLOR);
		fileSelectButton.setEnabled(true);
		fileSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileSelectButtonClicked();
			}
		});
		p2.add(fileSelectButton);
		add(p2);
		pack();
	}

	public void fileSelectButtonClicked() {
		
		boolean done = false;

		while (!done) {

			// Display the file save dialog and block for user input
			FileDialog f = new FileDialog(this, "Load Audio File", FileDialog.LOAD);
			f.show();
			String name = f.getFile();   // Get the user's response
		
			// Make sure cancel was not selected
			if (name == null)
				continue;

			// We got a file to work with
			String fileName = f.getDirectory() + name;

			// Could a valid media locator be formed from file name?
			if (!jfwui.createMediaLocator(fileName))
				continue;

			// Got a valid media locator. Do prepare to see if we are there
			if (!jfwui.jmfPrepare())
				continue;

			// Success, terminate loop
			done = true;
		}
		// Change component states
		readingLED.setLEDState(true);
		fileSelectButton.setEnabled(false);
	}
	
	public void signalReadBegun() {		
		
		// Change component states
		readingLED.setLEDState(true);
	}
	
	public void signalReadComplete() {		
		
		// Change component states
		readingLED.setLEDState(false);
	}
	
	public void showUI(boolean isVisible) {
        
		// Set visibility
        setVisible(isVisible);
	}
	
    public void stopUI() {
        
        dispose();
    }


	public void windowClosing() {

		stopUI();
	}

	// Private class data
	private RoundLED readingLED;
	private Button fileSelectButton;
	private JMFFileWithUI jfwui;
}


