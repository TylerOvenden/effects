// Interface Data Source Class
// Constructs a data source for converting samples from the AbstractAudio
// signal chain to JMF2.0 format for use with JMF2.0 devices.
// Written by: Craig A. Lindley
// Last Update: 06/07/99

package craigl.jmf20.media.protocol.intfc;

import craigl.utils.*;

// The java media packages
import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

public class DataSource extends PullDataSource {

	// Inner class source stream
	private class JMFSourceStream implements PullSourceStream {

		public JMFSourceStream(ContentDescriptor cd, AbstractAudio aa) {

			// Save incoming
			this.cd = cd;
			this.aa = aa;

			endOfMedia = false;
		
			// Allocate buffer for samples.
			sampleBuffer = new short[AudioConstants.SAMPLEBUFFERSIZE];
		}

		public Object [] getControls() {

			return new Object[0];
		}

		public Object getControl(String controlType) {

			return null;
		}
    
	    public ContentDescriptor getContentDescriptor() {

			return cd;
		}

		public long getContentLength() {

			return SourceStream.LENGTH_UNKNOWN;
		}

		public boolean endOfStream() {

			return endOfMedia;
		}

	    public boolean willReadBlock() {

			return false;
		}

		public int read(byte [] buffer, int offset, int nToRead) {

			if (endOfMedia)
				return -1;
			
			// Read samples from the AbstractAudio chain
			int samplesRead = 
				aa.previous.getSamples(sampleBuffer, AudioConstants.SAMPLEBUFFERSIZE);

			// Have we reached EOF ?
			if (samplesRead == -1) {
				endOfMedia = true;
				return -1;
			}

			// We have samples to return via the buffer
			int bufferIndex = 0;
			for (int i=0; i < samplesRead; i++) {
				short s = sampleBuffer[i];
				buffer[bufferIndex++] = (byte)(s & 255);
				buffer[bufferIndex++] = (byte)(s >> 8);
			}
			return samplesRead * 2;
		}

		// Class data
		private ContentDescriptor cd;
		private AbstractAudio aa;
		public boolean endOfMedia;
		private short [] sampleBuffer;
	}
	
	
	/**
	 * DataSource Class Constructor
	 */
	public DataSource() {

		cd = new ContentDescriptor("audio_raw");
	}

	public PullSourceStream [] getStreams() {

		if (pullStreams == null) {
			pullStreams = new PullSourceStream[1];

			if (sourceStream == null)
				sourceStream = new JMFSourceStream(cd, aa);

			pullStreams[0] = sourceStream;
		}
		return pullStreams;
    }

	public void connect() {

	}

	public void disconnect() {

	}

	public String getContentType() {

		return "audio_raw";
	}

	public MediaLocator getLocator() {

		return ml;
	}

	public void initCheck() {

	}

	public void setLocator(MediaLocator ml) {

		this.ml = ml;
	}

	public void start() {

		sourceStream.endOfMedia = false;
	}

	public void stop() {

		sourceStream.endOfMedia = true;
	}

	public Object getControl(String controlType) {

		return null;
	}

	public Object [] getControls() {

		return new Object[0];
	}

	public Time getDuration() {

		return Duration.DURATION_UNKNOWN;
	}

	public void setAA(AbstractAudio aa) {

		this.aa = aa;
	}
	
	public void setSampleRate(int sampleRate) {

		this.sampleRate = sampleRate;
	}
	
	public void setChannels(int channels) {

		this.channels = channels;
	}
	
	private void p(String s) {

		System.out.println(s);
	}

	// Class data
	private MediaLocator ml;
	private AbstractAudio aa;
	private JMFSourceStream sourceStream = null;
	private PullSourceStream [] pullStreams = null;
	private ContentDescriptor cd;
	public  int sampleRate;
	public  int channels;
}

