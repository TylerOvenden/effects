// JMF File Writer UI Code
// Written by: Craig A. Lindley
// Last Update: 06/09/99

package craigl.jmf20.devices;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import craigl.beans.blinker.*;
import craigl.beans.displays.*;
import craigl.beans.leds.*;
import craigl.utils.*;
import craigl.uiutils.*;


public class JMFFileWriterUI extends BaseUI implements CloseableFrameIF {

	// Class Constructor
	public JMFFileWriterUI(Blinker blink, JMFFileWriterWithUI jfwwui) {
		super("JMF File Writer with UI", jfwwui);

		// Save incoming
		this.jfwwui = jfwwui;

		setBackground(AudioConstants.PANELCOLOR);

		// This UI is interested in window closing events
		registerCloseListener(this);
		
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mp.setLayout(gbl);

		Label l = new Label("Sample Rate:", Label.RIGHT);
		addDefaultComponent(mp, l, gbl, gbc, 0, 0, 3, 1);
        
		l = new Label("Channels:", Label.RIGHT);
		addDefaultComponent(mp, l, gbl, gbc, 0, 1, 3, 1);
        
        rl1 = new ReadoutLabel(Color.red, "samp/sec", 20);
		addDefaultComponent(mp, rl1, gbl, gbc, 3, 0, 4, 1);
        
        rl2 = new ReadoutLabel(Color.red, "channels", 20);
		addDefaultComponent(mp, rl2, gbl, gbc, 3, 1, 4, 1);

		l = new Label("Writing", Label.CENTER);
		addDefaultComponent(mp, l, gbl, gbc, 1, 2, 3, 1);

		writingLED = new RoundLED();
		writingLED.setRadius(7);
		writingLED.setPanelColor(AudioConstants.PANELCOLOR);
		writingLED.setLEDMode(RoundLED.MODEBLINK);
		writingLED.setLEDColor(Color.red);
		writingLED.setLEDState(false);
		blink.addPropertyChangeListener(writingLED);
		addDefaultComponent(mp, writingLED, gbl, gbc, 5, 2, 1, 1);

		fileSelectButton = new Button("Select File");
		fileSelectButton.setBackground(AudioConstants.PANELCOLOR);
		fileSelectButton.setEnabled(true);
		fileSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileSelectButtonClicked();
			}
		});
		addDefaultComponent(mp, fileSelectButton, gbl, gbc, 0, 4, 2, 1);
		
		goButton = new Button("Go");
		goButton.setBackground(AudioConstants.PANELCOLOR);
		goButton.setEnabled(false);
		goButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goButtonClicked();
			}
		});
		addDefaultComponent(mp, goButton, gbl, gbc, 3, 4, 2, 1);

		doneButton = new Button("Done");
		doneButton.setBackground(AudioConstants.PANELCOLOR);
		doneButton.setEnabled(true);
		doneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				windowClosing();
			}
		});
		addDefaultComponent(mp, doneButton, gbl, gbc, 6, 4, 2, 1);
		add(mp);
		pack();
	}

	public void windowClosing() {

		done = true;
	}


	public void fileSelectButtonClicked() {
		
		// Display the file save dialog and block for user input
		FileDialog f = new FileDialog(this, "Save Audio File", FileDialog.SAVE);
		f.show();
		String name = f.getFile();   // Get the user's response
		
		// Make sure cancel was not selected
		if (name != null) {
			String fileName = f.getDirectory() + name;

			// Change component states
			fileSelectButton.setEnabled(false);
			goButton.setEnabled(true);

			// Save the selected filename and path
			jfwwui.setOutputFilename(fileName);
		}
	}
	
	public void goButtonClicked() {

		// Get parameters for audio file
		if (!jfwwui.doNegotiation())
			return;
		
		// Set display values
		rl1.setValue(jfwwui.sampleRate);
		rl2.setValue(jfwwui.channels);

		// Change component states
		writingLED.setLEDState(true);
		goButton.setEnabled(false);

		// Attempt to write the file
		if (!jfwwui.writeOutputFile())
			new MessageBox(this, "Error writing audio file. Please try again");

		// Change component states
		writingLED.setLEDState(false);
		fileSelectButton.setEnabled(true);
		goButton.setEnabled(false);
	}
	
	// This code keeps the UI up until the recording is completed and
	// done is set.
	public void showUI(boolean isVisible) {
        
		// Set visibility
        setVisible(isVisible);

		// Start a thread to stop return
		Thread t = new Thread() {
			public void run() {
				while(!done) {
					// Sleep 
					try {
						sleep(250);
					} catch (InterruptedException ie) {}
				}
			}
		};
		t.start();
		try {
			t.join();
		} catch (InterruptedException ie) {}
	}
	
    public void stopUI() {
        
        done = true;
		dispose();
    }

	// Private class data
	private JMFFileWriterWithUI jfwwui;
    private ReadoutLabel rl1;
    private ReadoutLabel rl2;
	private RoundLED writingLED;
	private Button fileSelectButton;
	private Button goButton;
	private Button doneButton;
	private String fileName;
	private boolean done = false;
}

