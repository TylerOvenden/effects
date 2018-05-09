// Java Application for testing sources, monitors, processors and sinks 
// Written by: Craig A. Lindley
// Last Update: 06/09/99

package craigl.test;

import java.awt.*;
import craigl.beans.blinker.*;
import craigl.utils.*;
import craigl.filereader.*;
import craigl.filewriter.*;
import craigl.jmf20.devices.*;
import craigl.winplayer.*;
import craigl.winrecorder.*;
import craigl.pcmplayer.*;
import craigl.processors.*;
import craigl.osc.*;
import craigl.scope.*;
import craigl.spectrumanalyzer.*;

public class AudioTest {

	private static final String USAGE = 
		"\nAudioTest cmd line arguments are as follows:\n" +
		"\tSource input device: -i (osc | sosc | file | jmffile | mic sr chs | jmfmic sr chs)\n" +
		"\tMonitor device: [-m (scope | spectrumanalyzer)]\n" +
		"\tProcessor device(s): [-p (aadj cache chorus compexp delay distortion\n" +
		"\t\t\t\teq peq pan phaser pshift reverb)+]\n" +
		"\tOutput device: -o (file | jmffile | player | jmfplayer | winplayer)\n";

	private static final String USAGE2 = 
		"Devices are:\n" +
		"osc - mono oscillator source\n"+
		"sosc - stereo oscillator source\n" +
		"file - input from file or output to file\n" +
		"jmffile - input from file or output to file using JMF2.0\n" +
		"mic sr chs - mic/line input at sample rate sr and chs channels\n" +
		"jmfmic sr chs - mic/line input at sample rate sr and chs channels using JMF2.0\n" +
		"scope - sample scope monitor\n" +
		"spectrumanalyzer - spectrum analyzer monitor\n" +
		"aadj - amplitude adjust processor (level control)\n" +
		"cache - sample cacheing processor\n" +
		"chorus - chorus/flanger effect processor\n" +
		"compexp - compressor/expander/limiter/noise gate processor\n" +
		"delay - digital delay processor\n" +
		"distortion - distortion processor\n" +
		"eq - graphic equalizer processor\n" +
		"peq - parametric equalizer processor\n" +
		"pan - panner processor\n" +
		"phaser - phaser effect processor\n" +
		"pshift - pitch shifter effect processor\n" +
		"reverb - reverb effect processor\n" +
		"player - sample player based on JMF1.0\n" +
		"jmfplayer - sample player based on JMF2.0\n" +
		"winplayer - sample player for Windows";

	public static void main(String [] args) {

		// First arg better be -i or we're done
		if ((args.length == 0) || !args[0].equalsIgnoreCase("-i")) {
			System.out.println("First switch must be -i");
			System.out.println(USAGE);
			System.out.println(USAGE2);
			System.exit(1);
		}
		
		// Start a blinker for the LEDs
		Blinker blinker = new Blinker(250);
		
		// Instantiate data structure for linking abstract audio devices
		LinkedListVector ll = new LinkedListVector();
 		
		// Process all of the cmd line arguments
		for (int i=0; i < args.length; i++) {

			String arg = args[i++];
			
			if (arg.equalsIgnoreCase("-i")) {

				arg = args[i];
				
				if (arg.equalsIgnoreCase("osc")) {

					OscillatorWithUI osc = new OscillatorWithUI();
					ll.addElement(osc.getAA());
					osc.showUI(true);
				
				}	else if (arg.equalsIgnoreCase("sosc")) {

					StereoOscillatorWithUI sosc = new StereoOscillatorWithUI();
					ll.addElement(sosc.getAA());
					sosc.showUI(true);
				
				}	else if (arg.equalsIgnoreCase("file")) {

					FileReaderWithUI frwui = new FileReaderWithUI(blinker);
					ll.addElement(frwui.getAA());
					frwui.showUI(true);

				}	else if (arg.equalsIgnoreCase("jmffile")) {

					JMFFileWithUI jmffui = new JMFFileWithUI(blinker);
					ll.addElement(jmffui.getAA());
					jmffui.showUI(true);

				}	else if (arg.equalsIgnoreCase("mic")) {

					// Parse out the command line parameters for sample
					// rate and number of channels
					String sampleRateString = args[++i];
					String channelsString   = args[++i];

					int sampleRate = WinRecorder.DEFAULTSAMPLERATE;
					int channels   = WinRecorder.DEFAULTCHANNELS;

					// Attempt the numeric string conversions
					try {
						sampleRate = new Integer(sampleRateString).intValue();
						channels   = new Integer(channelsString).intValue();
					}
					catch (NumberFormatException nfe) {
						// Conversion failed, indicate error and terminate
						System.err.println("mic parameters wrong. Should be -mic rate channels");
						System.exit(-1);
					}
					// Got converted numbers make sure they are in range
					if ((sampleRate != 44100) &&
					    (sampleRate != 22050) &&
						(sampleRate != 11025)) {
						
						System.err.println("mic sample rate wrong. Using: " +
										    new Integer(WinRecorder.DEFAULTSAMPLERATE));
						sampleRate = WinRecorder.DEFAULTSAMPLERATE;
					}

					if ((channels != 1) &&
					    (channels != 2)) {
						System.err.println("mic channels wrong. Using: " +
										    new Integer(WinRecorder.DEFAULTCHANNELS));
						channels = WinRecorder.DEFAULTCHANNELS;
					}
					
					// Instantiate recorder with the proper arguments
					WinRecorderWithUI wrwui = 
						new WinRecorderWithUI(blinker, sampleRate, channels);
					ll.addElement(wrwui.getAA());
					wrwui.showUI(true);

				}	else if (arg.equalsIgnoreCase("jmfmic")) {

					// Parse out the command line parameters for sample
					// rate and number of channels
					String sampleRateString = args[++i];
					String channelsString   = args[++i];

					int sampleRate = WinRecorder.DEFAULTSAMPLERATE;
					int channels   = WinRecorder.DEFAULTCHANNELS;

					// Attempt the numeric string conversions
					try {
						sampleRate = new Integer(sampleRateString).intValue();
						channels   = new Integer(channelsString).intValue();
					}
					catch (NumberFormatException nfe) {
						// Conversion failed, indicate error and terminate
						System.err.println("mic parameters wrong. Should be -mic rate channels");
						System.exit(-1);
					}
					// Got converted numbers make sure they are in range
					if ((sampleRate != 44100) &&
					    (sampleRate != 22050) &&
						(sampleRate != 11025)) {
						
						System.err.println("mic sample rate wrong. Using: " +
										    new Integer(WinRecorder.DEFAULTSAMPLERATE));
						sampleRate = WinRecorder.DEFAULTSAMPLERATE;
					}

					if ((channels != 1) &&
					    (channels != 2)) {
						System.err.println("mic channels wrong. Using: " +
										    new Integer(WinRecorder.DEFAULTCHANNELS));
						channels = WinRecorder.DEFAULTCHANNELS;
					}
					
					// Instantiate recorder with the proper arguments
					JMFMicWithUI jmfmic = 
						new JMFMicWithUI(blinker, sampleRate, channels);
					ll.addElement(jmfmic.getAA());
					jmfmic.showUI(true);

				}	else	{
					
					System.err.println("Unknown input device");
					System.out.println(USAGE);
					System.out.println(USAGE2);
					System.exit(1);
				}


			}	else if (arg.equalsIgnoreCase("-m")) {	// Any monitor devices?
			
				arg = args[i];

				while (!(arg.equalsIgnoreCase("-p") || arg.equalsIgnoreCase("-o"))) {

					if (arg.equalsIgnoreCase("scope")) {
						Scope scope = new Scope("");
						ll.addElement(scope);
						scope.showUI(true);
					
					}	else if (arg.equalsIgnoreCase("spectrumanalyzer")) {
						
						SpectrumAnalyzer sa = new SpectrumAnalyzer("");
						ll.addElement(sa);
						sa.showUI(true);
				
					}	else	{
					
						System.err.println("Unknown monitor device");
						System.out.println(USAGE);
						System.out.println(USAGE2);
						System.exit(1);
					}
					// Get next processor if any
					arg = args[++i];
				}
				// Backup index so for loop gets correct cmd line arg
				i--;
			
			}	else if (arg.equalsIgnoreCase("-p")) {	// Any processor devices?

				arg = args[i];

				while (!(arg.equalsIgnoreCase("-o") || arg.equalsIgnoreCase("-m"))) {

					if (arg.equalsIgnoreCase("aadj")) {

						AmplitudeAdjustWithUI aa = new AmplitudeAdjustWithUI(blinker);
						ll.addElement(aa);
						aa.showUI(true);

					}	else if (arg.equalsIgnoreCase("cache")) {

						Cache cache = new Cache();
						ll.addElement(cache);

					}	else if (arg.equalsIgnoreCase("chorus")) {

						ChorusWithUI chorus = new ChorusWithUI(blinker);
						ll.addElement(chorus);
						chorus.showUI(true);

					}	else if (arg.equalsIgnoreCase("compexp")) {

						CompExpWithUI ceui = new CompExpWithUI(blinker);
						ll.addElement(ceui);
						ceui.showUI(true);

					}	else if (arg.equalsIgnoreCase("delay")) {

						DelayWithUI delay = new DelayWithUI(blinker);
						ll.addElement(delay);
						delay.showUI(true);

					}	else if (arg.equalsIgnoreCase("distortion")) {

						DistortionWithUI dist = new DistortionWithUI(blinker);
						ll.addElement(dist);
						dist.showUI(true);

					}	else if (arg.equalsIgnoreCase("eq")) {

						GraphicEQWithUI gequi = new GraphicEQWithUI(blinker);
						ll.addElement(gequi);
						gequi.showUI(true);

					}	else if (arg.equalsIgnoreCase("peq")) {

						ParametricEQWithUI pequi = new ParametricEQWithUI(blinker);
						ll.addElement(pequi);
						pequi.showUI(true);

					}	else if (arg.equalsIgnoreCase("pan")) {

						PannerWithUI pwui = new PannerWithUI(blinker);
						ll.addElement(pwui);
						pwui.showUI(true);

					}	else if (arg.equalsIgnoreCase("phaser")) {

						PhaserWithUI del = new PhaserWithUI(blinker);
						ll.addElement(del);
						del.showUI(true);


					}	else if (arg.equalsIgnoreCase("pshift")) {

						PitchShifterWithUI pwui = new PitchShifterWithUI(blinker);
						ll.addElement(pwui);
						pwui.showUI(true);

					}	else if (arg.equalsIgnoreCase("reverb")) {

						ReverbWithUI rui = new ReverbWithUI(blinker);
						ll.addElement(rui);
						rui.showUI(true);

					}	else	{

						System.err.println("Unknown processor device");
						System.out.println(USAGE);
						System.out.println(USAGE2);
						System.exit(1);
					}
					
					// Get next processor if any
					arg = args[++i];
				}
				// Backup index so for loop gets correct cmd line arg
				i--;

			}	else if (arg.equalsIgnoreCase("-o")) {

				arg = args[i];

				if (arg.equalsIgnoreCase("file")) {

					FileWriterWithUI fwwui = new FileWriterWithUI(blinker);
					ll.addElement(fwwui.getAA());
					fwwui.showUI(true);

				}	else if (arg.equalsIgnoreCase("jmffile")) {

					JMFFileWriterWithUI jmffw = new JMFFileWriterWithUI(blinker);
					ll.addElement(jmffw);
					jmffw.showUI(true);

				}	else if (arg.equalsIgnoreCase("player")) {

					PCMPlayerWithUI pcmp = new PCMPlayerWithUI(blinker);
					ll.addElement(pcmp.getAA());
					pcmp.showUI(true);

				}	else if (arg.equalsIgnoreCase("jmfplayer")) {

					JMFPlayerWithUI jmfp = new JMFPlayerWithUI(blinker);
					ll.addElement(jmfp);
					jmfp.showUI(true);

				}	else if (arg.equalsIgnoreCase("winplayer")) {

					WinPlayerWithUI wpui = new WinPlayerWithUI(blinker);
					ll.addElement(wpui.getAA());
					wpui.showUI(true);

				}	else	{
						
					System.err.println("Unknown sink device");
					System.out.println(USAGE);
					System.out.println(USAGE2);
					System.exit(1);
				}
			
			}	else	{
					
				System.err.println("Unknown cmd line switch: \"" + arg + "\"");
				System.out.println(USAGE);
				System.out.println(USAGE2);
				System.exit(1);

			}
		}
		// Free list
		ll.removeAllElements();
		System.exit(0);
	}
}

