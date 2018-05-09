// File Reader With UI Code
// Written by: Craig A. Lindley
// Last Update: 05/02/99

package craigl.filereader;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import craigl.utils.*;
import craigl.uiutils.*;
import craigl.beans.blinker.*;
import craigl.beans.displays.*;
import craigl.beans.leds.*;
import craigl.au.*;
import craigl.wave.*;

public class FileReaderWithUI extends Frame
		implements ReadCompleteIF {

	// Define the inner classes

	// Place holder for real AbstractAudio device for reading audio files 
	class BogusDevice extends AbstractAudio {

		public BogusDevice() {
			
			super("Audio Device", SOURCE);
		}

		public int getSamples(short [] buffer, int length) { return -1; }
	}

	// Filter all files except au and wave. NOTE: this code does not
	// currently seem to work in Java 1.2.
	class FileFilter implements FilenameFilter {

		public boolean accept(File  dir, String  name) {

			int dotIndex = name.indexOf('.');
			if (dotIndex != -1) {
				String ext = name.substring(dotIndex + 1);
				return ((ext.equals("au")) | (ext.equals("wav")));
			}	else	{
				return false;
			}
		}
	}

	/**
	 * FileReaderWithUI class constructor
	 */
	public FileReaderWithUI(Blinker blink) {
		super("File Reader with UI");

		// Create a bogus AbstractAudio device so this device can be linked
		aa = new BogusDevice();

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

	public synchronized void fileSelectButtonClicked() {
		
		// Display the file save dialog and block for user input
		FileDialog f = new FileDialog(this, "Load Audio File", FileDialog.LOAD);
		
		// Attempt to install a filename filter for AU and WAV files. This
		// doesn't currently seem to work in Java.
		f.setFilenameFilter(new FileFilter());
		f.show();
		String name = f.getFile();   // Get the user's response
		
		// Make sure cancel was not selected
		if (name == null)
			return;
		
		// We got a file to work with
		String fileName = f.getDirectory() + name;

		// See if it is a type we can work with
		boolean isAUFile   = AURead.isAUFile(fileName);
		boolean isWaveFile = WaveRead.isWaveFile(fileName);
		if (!isAUFile && !isWaveFile) {
			MessageBox mb = new MessageBox(this, "User Advisory",
				"Selected file is not an AU or WAV file");
			mb.show();
			return;
		}
		// Is a file type we understand. Instantiate reader for it.
		AbstractAudio ab;
		if (isAUFile)
			ab = new AURead(fileName, this);
		else
			ab = new WaveRead(fileName, this);

		// Link in the new device in place of the bogus device
		ab.next = aa.next;
		aa.next.previous = ab;
		aa = ab;

		// Change component states
		readingLED.setLEDState(true);
		fileSelectButton.setEnabled(false);
	}
	
	public void signalReadComplete() {		
		
		// Change component states
		readingLED.setLEDState(false);
		fileSelectButton.setEnabled(true);
	}
	
	public void showUI(boolean isVisible) {
        
		// Set visibility
        setVisible(isVisible);
	}
	
    public void stopUI() {
        
        dispose();
    }

	public AbstractAudio getAA() {

		return aa;

	}
	// Private class data
    private AbstractAudio aa;
	private RoundLED readingLED;
	private Button fileSelectButton;
}


