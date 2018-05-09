// PCM Player With UI Code
// Written by: Craig A. Lindley
// Last Update: 08/23/98

package craigl.pcmplayer;

import java.awt.*;
import java.awt.event.*;
import craigl.utils.*;
import craigl.uiutils.*;
import craigl.beans.blinker.*;
import craigl.beans.leds.*;


public class PCMPlayerWithUI extends CloseableFrame
	implements CloseableFrameIF {

	public PCMPlayerWithUI(Blinker blink) {
		super("PCMPlayer with UI");

		setBackground(AudioConstants.PANELCOLOR);

		// This UI is interested in window closing events
		registerCloseListener(this);

		// Instantiate a player for the UI to use
		pcmp = new PCMPlayer();
		
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mp.setLayout(gbl);

		Label l = new Label("Playing", Label.CENTER);
		BaseUI.addDefaultComponent(mp, l, gbl, gbc, 0, 1, 3, 1);

		playingLED = new RoundLED();
		playingLED.setRadius(7);
		playingLED.setPanelColor(AudioConstants.PANELCOLOR);
		playingLED.setLEDMode(RoundLED.MODEBLINK);
		playingLED.setLEDColor(Color.red);
		playingLED.setLEDState(false);
		blink.addPropertyChangeListener(playingLED);
		BaseUI.addDefaultComponent(mp, playingLED, gbl, gbc, 3, 1, 1, 1);

		l = new Label("Indicator", Label.CENTER);
		BaseUI.addDefaultComponent(mp, l, gbl, gbc, 4, 1, 3, 1);

		playButton = new Button("  Play  ");
		playButton.setBackground(AudioConstants.PANELCOLOR);
		playButton.setEnabled(true);
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playButtonClicked();
			}
		});
		BaseUI.addDefaultComponent(mp, playButton, gbl, gbc, 1, 3, 1, 1);
		
		stopButton = new Button("  Stop  ");
		stopButton.setBackground(AudioConstants.PANELCOLOR);
		stopButton.setEnabled(false);
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopButtonClicked();
			}
		});
		BaseUI.addDefaultComponent(mp, stopButton, gbl, gbc, 3, 3, 1, 1);

		doneButton = new Button("  Done  ");
		doneButton.setBackground(AudioConstants.PANELCOLOR);
		doneButton.setEnabled(true);
		doneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				windowClosing();
			}
		});
		BaseUI.addDefaultComponent(mp, doneButton, gbl, gbc, 5, 3, 1, 1);
		add(mp);
		pack();
	}

	public void windowClosing() {
		
		done = true;	// Exit play loop next pass
		pcmp.stop();	// Stop the player
		pcmp.doReset();	// Reset all processing stages
	}

	public synchronized void playButtonClicked() {

		// Change component states
		playingLED.setLEDState(true);
		playButton.setEnabled(false);
		stopButton.setEnabled(true);

		start = true;
	}
	
	public synchronized void stopButtonClicked() {

		// Change component states
		playingLED.setLEDState(false);
		playButton.setEnabled(true);
		stopButton.setEnabled(false);

		pcmp.stop();	// Stop the player
		pcmp.doReset();	// Reset all processing stages
	}

	public void playCompleted() {

		// Change component states
		playingLED.setLEDState(false);
		playButton.setEnabled(true);
		stopButton.setEnabled(false);

		// Reset all processing stages in preparation for playing
		pcmp.doReset();
	}
	
	public void showUI(boolean isVisible) {
        
		// Set visibility
        setVisible(isVisible);

		if (isVisible) {
			Thread t = new Thread() {
				public void run() {
					while(!done) {
						if (start) {
							start = false;
							pcmp.play();
							playCompleted();
						}
						try {
							sleep(250);
						}
						catch (InterruptedException ie) {}
					}
				}
			};
			t.start();
			try {
				t.join();
			}
			catch (InterruptedException ie) {}
		}
    }
	
    public void stopUI() {
        
        dispose();
    }

	public AbstractAudio getAA() {

		return pcmp;
	}
	
	// Private class data
	private boolean done  = false;
	private boolean start = false;
	private RoundLED playingLED;
	private Button playButton;
	private Button stopButton;
	private Button doneButton;
	private PCMPlayer pcmp;
}
