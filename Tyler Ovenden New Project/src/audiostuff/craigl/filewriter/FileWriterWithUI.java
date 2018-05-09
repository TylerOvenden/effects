// File Writer With UI Code
// Written by: Craig A. Lindley
// Last Update: 08/29/99

package audiostuff.craigl.filewriter;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import audiostuff.craigl.au.*;
import audiostuff.craigl.beans.blinker.*;
import audiostuff.craigl.beans.displays.*;
import audiostuff.craigl.beans.leds.*;
import audiostuff.craigl.utils.*;
import audiostuff.craigl.uiutils.*;
import audiostuff.craigl.wave.*;


public class FileWriterWithUI extends CloseableFrame
	implements CloseableFrameIF {

	public static final int AUTYPE	 = 0;
	public static final int WAVETYPE = 1;
	
	// Inner class definitions

	// Place holder for real AbstractAudio device for writing audio files 
	class BogusDevice extends AbstractAudio {

		public BogusDevice() {
			
			super("Audio Device", SINK);
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

	// Class Constructor
	public FileWriterWithUI(Blinker blink) {
		super("File Writer with UI");

		// Create a bogus device to allow linking
		aa = new BogusDevice();

		setBackground(AudioConstants.PANELCOLOR);

		// This UI is interested in window closing events
		registerCloseListener(this);

		// Default file type is AU file or type 0
		fileType = AUTYPE;
		
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mp.setLayout(gbl);

		Label l = new Label("Sample Rate:", Label.RIGHT);
		BaseUI.addDefaultComponent(mp, l, gbl, gbc, 0, 0, 3, 1);
        
		l = new Label("Channels:", Label.RIGHT);
		BaseUI.addDefaultComponent(mp, l, gbl, gbc, 0, 1, 3, 1);
        
        rl1 = new ReadoutLabel(Color.red, "samp/sec", 20);
		BaseUI.addDefaultComponent(mp, rl1, gbl, gbc, 3, 0, 4, 1);
        
        rl2 = new ReadoutLabel(Color.red, "channels", 20);
		BaseUI.addDefaultComponent(mp, rl2, gbl, gbc, 3, 1, 4, 1);

        // File type selection group
		Box b = createFileTypeGroup();
		BaseUI.addDefaultComponent(mp, b, gbl, gbc, 0, 2, 4, 4);

		l = new Label("Writing", Label.CENTER);
		BaseUI.addDefaultComponent(mp, l, gbl, gbc, 0, 7, 3, 1);

		writingLED = new RoundLED();
		writingLED.setRadius(7);
		writingLED.setPanelColor(AudioConstants.PANELCOLOR);
		writingLED.setLEDMode(RoundLED.MODEBLINK);
		writingLED.setLEDColor(Color.red);
		writingLED.setLEDState(false);
		blink.addPropertyChangeListener(writingLED);
		BaseUI.addDefaultComponent(mp, writingLED, gbl, gbc, 3, 7, 1, 1);

		fileSelectButton = new Button("Select File");
		fileSelectButton.setBackground(AudioConstants.PANELCOLOR);
		fileSelectButton.setEnabled(true);
		fileSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileSelectButtonClicked();
			}
		});
		BaseUI.addDefaultComponent(mp, fileSelectButton, gbl, gbc, 4, 3, 2, 1);
		
		goButton = new Button("Go");
		goButton.setBackground(AudioConstants.PANELCOLOR);
		goButton.setEnabled(false);
		goButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goButtonClicked();
			}
		});
		BaseUI.addDefaultComponent(mp, goButton, gbl, gbc, 4, 5, 2, 1);

		doneButton = new Button("Done");
		doneButton.setBackground(AudioConstants.PANELCOLOR);
		doneButton.setEnabled(true);
		doneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				windowClosing();
			}
		});
		BaseUI.addDefaultComponent(mp, doneButton, gbl, gbc, 4, 7, 2, 1);
		add(mp);
		pack();
	}

	public void windowClosing() {

		done = true;
	}

	public boolean negotiateParameters() {

		if (!parametersNegotiated) {

			// Get everyone else's idea of format
			MyInt channelsMin = new MyInt(1);
			MyInt channelsMax = new MyInt(2);
			MyInt channelsPreferred = new MyInt(1);

			aa.minMaxChannels(channelsMin, channelsMax, channelsPreferred);
			if (channelsMin.getValue() > channelsMax.getValue()) {
				System.out.println("Couldn't negotiate channels");
				return false;
			}

			MyInt rateMin = new MyInt(8000);
			MyInt rateMax = new MyInt(44100);
			MyInt ratePreferred = new MyInt(22050);

			aa.minMaxSamplingRate(rateMin, rateMax, ratePreferred);
			if (rateMin.getValue() > rateMax.getValue()) {
				System.out.println("Couldn't negotiate rate");
				return false;
			}

			numberOfChannels = channelsPreferred.getValue();
			sampleRate = ratePreferred.getValue();

			// We know that all modes are valid so set parameters
			aa.setChannelsRecursive(channelsPreferred.getValue());
			aa.setSamplingRateRecursive(ratePreferred.getValue());

			parametersNegotiated = true;
		}
		return true;
	}

	public synchronized void fileSelectButtonClicked() {
		
		// Display the file save dialog and block for user input
		FileDialog f = new FileDialog(this, "Save Audio File", FileDialog.SAVE);
		
		// Install file filter
		f.setFilenameFilter(new FileFilter());
		f.show();
		String name = f.getFile();   // Get the user's response
		
		// Make sure cancel was not selected
		if (name != null) {
			this.fileName = f.getDirectory() + name;

			// Change component states
			fileSelectButton.setEnabled(false);
			goButton.setEnabled(true);
		}
	}
	
	public synchronized void goButtonClicked() {

		// Change component states
		writingLED.setLEDState(true);
		goButton.setEnabled(false);

		// Get parameters for audio file
		if (!negotiateParameters())
			return;
		
		// Set display values
		rl1.setValue(sampleRate);
		rl2.setValue(numberOfChannels);

		// Spawn a separate thread to do the file writing otherwise the UI
		// will be starved and the buttons sometimes won't work.
		Thread t = new Thread() {
			public void run() {

				// Successfully nogotiated parameters
				FileWriterIF fwif = null;

				if (fileType == AUTYPE) {
					// Instantiate AUWrite and write the file
					fwif = (FileWriterIF) new AUWrite(fileName, sampleRate, numberOfChannels);
				}	else	{
					// Instantiate WaveWrite and write the file
					fwif = (FileWriterIF) new WaveWrite(fileName, sampleRate, numberOfChannels);
				}
				// Write the file 
				fwif.writeFile(aa);

				// Reset the previous processors in preparation for
				// writing another file
				aa.doReset();
		
				// Writing done change component states
				writingLED.setLEDState(false);
				fileSelectButton.setEnabled(true);
				goButton.setEnabled(false);
			}
		};
		t.start();
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

	public AbstractAudio getAA() {

		return aa;

	}
	
	// Create the file type group radio buttons
	private Box createFileTypeGroup() {
		
		Panel panel = new Panel();
		Box box = new Box(panel, "Type");
		CheckboxGroup group = new CheckboxGroup();

		panel.setLayout(new GridLayout(2, 0));
		
		Checkbox cb1 = new Checkbox("AU File", group, true);
		cb1.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					fileType = AUTYPE;
			}
		});
		panel.add(cb1);

		Checkbox cb2 = new Checkbox("Wave File", group, false);
		cb2.addItemListener(new ItemListener () {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					fileType = WAVETYPE;
			}
		});
		panel.add(cb2);
		return box;
	}

	// Private class data
    private AbstractAudio aa;
    private ReadoutLabel rl1;
    private ReadoutLabel rl2;
	private RoundLED writingLED;
	private Button fileSelectButton;
	private Button goButton;
	private Button doneButton;
	private int fileType;
	private String fileName;
	private int sampleRate;
	private int numberOfChannels;
	private boolean parametersNegotiated;
	private boolean done = false;
}

