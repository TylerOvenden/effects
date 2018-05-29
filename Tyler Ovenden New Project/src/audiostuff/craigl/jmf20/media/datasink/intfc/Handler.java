// Connector Data Sink Class
// Constructs a JMF2.0 data sink which is also an AbstractAudio
// device thereby allowing an JMF2.0 input device to interact with
// other AbstractAudio devices. 

// Written by: Craig A. Lindley
// Last Update: 06/09/99

package audiostuff.craigl.jmf20.media.datasink.intfc;

import audiostuff.craigl.jmf20.devices.*;
import audiostuff.craigl.utils.*;
import java.io.*;

// The java media packages
import javax.media.*;
import javax.media.datasink.DataSinkListener;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.util.*;

public class Handler extends AbstractAudio implements DataSink {


	public Handler() {

		super("JMF2.0 AbstractAudio/Data Sink Device", SOURCE);

		playbackStartSignalled = false;
		EOMMode = false;
	}

	public void close() {

	}

	public String getContentType() {

		return "audio_raw";
	}

	public MediaLocator getOutputLocator() {

		return outputLocator;
	}

	public void open() {

		playbackStartSignalled = false;
		EOMMode = false;
	}

	public void setOutputLocator(MediaLocator outputLocator) {

		this.outputLocator = outputLocator;
	}

	public void start() {

		playbackStartSignalled = false;
		EOMMode = false;
	}

	public void stop() {

	}

	// Methods required by MediaHandler class
	public void setSource(DataSource datasource) {

		// Save incoming
		this.datasource = datasource;

		// Datasource is known to be a PushBufferDataSource
		// Cast will fail if not
		PushBufferDataSource pbds = (PushBufferDataSource) datasource;

		// Extract single push buffer stream
		pbs = (pbds.getStreams())[0];

		// Get the format
		Format f = pbs.getFormat();

		// Create empty buffer so source will allocate memory and fill it
		bufferObj = new Buffer();

		// Set format on the buffer
		bufferObj.setFormat(f);
	}

	// Methods required by Controls class. This data sink has no controls
	public Object getControl(String cn) {

		System.out.println("getControl");
		return null;
	}

	public Object [] getControls() {

		System.out.println("getControls");
		return new Object[0];
	}


	// Methods required by AbstractAudio class
	
	// Called by next processing stage to retrieve samples 
	public int getSamples(short [] buffer, int length) {

		int samplesRead = 0;
		int i = 0;

		if (!playbackStartSignalled) {
			cbif.signalPlaybackBegun();
			playbackStartSignalled = true;
		}

		if (EOMMode)
			return -1;

		// While there are still samples needing to be provided
		while(samplesRead < length) {
			// Are there any bytes left in last read?
			if (bytesRemaining <= 0) {
				try	{
					// Read another buffer full of data
					pbs.read(bufferObj);
					
					// Reset index and get new buffer
					dataIndex = 0;
					data = (byte []) bufferObj.getData();
				}
				catch(IOException e) {
					System.out.println("IOException");
					System.exit(1);
				}
				// Read the number of bytes read in the buffer
				bytesRemaining = bufferObj.getLength();
			}
			// See if we've reached end of media
			if (bufferObj.isEOM() && (bytesRemaining == 0))
				return -1;

			// We've got bytes to return as samples
			buffer[i++] = (short)((((int) data[dataIndex++]) & 255) + 
								  (((int) data[dataIndex++]) << 8));
			bytesRemaining -= 2;
			samplesRead++;
		}
		return length;
	}

	// Set the sample rate for when negotiation occurs
	public void minMaxSamplingRate(MyInt min, MyInt max, MyInt preferred) {
		
		max.setValue(sampleRate);
		min.setValue(sampleRate);
		preferred.setValue(sampleRate);
	}

	// Set the number of channels for when negotiation occurs
	public void minMaxChannels(MyInt min, MyInt max, MyInt preferred) {

		min.setValue(numberOfChannels);
		max.setValue(numberOfChannels);
		preferred.setValue(numberOfChannels);
	}

	// Called to set EOM mode. Handler will then return EOF indication
	// when asked for samples
	public void setEOM() {

		EOMMode = true;
	}
	
	// Called from app level to set sample rate for negotiation
	public void setSampleRate(int sampleRate) {

		this.sampleRate = sampleRate;
	}

	// Called from app level to set number of channels for negotiation
	public void setChannels(int numberOfChannels) {

		this.numberOfChannels = numberOfChannels;
	}

	// Register callback object
	public void setCallBack(JMFFileCallBackIF cbif) {

		this.cbif = cbif;
	}

	// If reset called on signal chain propagate to calling class
	public void reset() {
		
		// Signal that reset has been detected
		cbif.signalReset();

		// Setup for replay
		playbackStartSignalled = false;
		EOMMode = false;
		bytesRemaining = 0;
		dataIndex = 0;
	}

	// Class data
	private MediaLocator outputLocator;
	private DataSource datasource;
	private PushBufferStream pbs;
	private int sampleRate;
	private int numberOfChannels;
	private Buffer bufferObj;
	private int bytesRemaining = 0;
	private int dataIndex = 0;
	private byte [] data = null;
	private JMFFileCallBackIF cbif;
	private boolean playbackStartSignalled;
	private boolean EOMMode = false;
	@Override
	public void addDataSinkListener(DataSinkListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeDataSinkListener(DataSinkListener arg0) {
		// TODO Auto-generated method stub
		
	}
}

