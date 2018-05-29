// JMF Raw Media Demultiplexer
// Written by: Craig A. Lindley
// Last Update: 06/05/99


package audiostuff.craigl.jmf20.media.parser.audio;

import java.io.IOException;

import javax.media.BadHeaderException;
import javax.media.Buffer;
import javax.media.Demultiplexer;
import javax.media.Duration;
import javax.media.Format;
import javax.media.IncompatibleSourceException;
import javax.media.Time;
import javax.media.Track;
import javax.media.TrackListener;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;
import javax.media.protocol.SourceStream;

import audiostuff.craigl.utils.AudioConstants;

public class RawParser implements Demultiplexer {

	private static ContentDescriptor[] supportedFormat =
		
		new ContentDescriptor[] {
			new ContentDescriptor("audio_raw")
	};

	public ContentDescriptor [] getSupportedInputContentDescriptors() {
		
		return supportedFormat;
	}

	// Inner class for the RawTrack
	private class RawTrack implements Track {

		public RawTrack(Format f, PullSourceStream stream) {

			// Save incoming
			this.f = f;
			this.stream = stream;

			enabled = true;

			// Allocate a buffer for storing the samples
			byteBufferSize = AudioConstants.SAMPLEBUFFERSIZE * 2;
			byteBuffer = new byte[byteBufferSize];
		}

        //  Obtain the format information associated with this Track. 
		public Format getFormat() {

			return f;
		}

		// Return the number of buffers that this demultiplexer requires
		// for this Track. 
		public int getNumberOfBuffers() {

			return 0;
		}

		// Return the start time of this Track
		public Time getStartTime() {

			return new Time(0);
		}

		public Time getDuration() {

			return Duration.DURATION_UNKNOWN;
		}

		// Return true if the track is enabled, false otherwise.
		public boolean isEnabled() {
			
			return enabled;
		}

		// Return the Time that corresponds to the frame number passed
		// as parameter 
		public Time mapFrameToTime(int frameNumber) {

			return new Time(0);
		}

		// Return the frame number that corresponds to the Time passed
		// as parameter 
		public int mapTimeToFrame(Time t) {

			return 0;
		}
		
		// Read the next frame for this track. 
		public void readFrame(Buffer buffer) {

			if (buffer == null)
				return;

			if (!enabled) {
				buffer.setDiscard(true);
				return;
			}

			// Mark the format of the buffer
			buffer.setFormat(f);
			buffer.setSequenceNumber(sequenceNumber++);
			buffer.setTimeStamp(Buffer.TIME_UNKNOWN);

			// Read a buffer full of data from the data source
			try {
				int bytesRead = stream.read(byteBuffer, 0, byteBufferSize);
				if (bytesRead == -1) {
					buffer.setLength(0);
					buffer.setEOM(true);
				}	else	{
					buffer.setData(byteBuffer);
					buffer.setLength(bytesRead);
					buffer.setEOM(false);
				}
			}
			catch(IOException e) {
				System.out.println("IOException: " + e.getMessage());
			}
		}

		// Read the next key frame. 
		public void readKeyFrame(Buffer buffer) {
			
			readFrame(buffer);
		}

		// Enables or disables this track, depending on the value of
		// the parameter.
		public void setEnabled(boolean t) {

			enabled = t;
		}

		public void setTrackListener(TrackListener listener) {

			this.listener = listener;
		}

		// Return true if the readFrame() call will block. 
		public boolean willReadFrameBlock() {

			return false;
		}

		// Class data
		private Format f;
		private boolean enabled;
		private TrackListener listener;
		private PullSourceStream stream;
		private int byteBufferSize;
		private byte [] byteBuffer;
		private int sequenceNumber = 0;
	}  


	public RawParser() {
		
		tracks = new Track[1];
	}

	public void setSource(DataSource source)
		throws IOException, IncompatibleSourceException {

		if (!(source instanceof PullDataSource)) {
			throw new IncompatibleSourceException("DataSource not supported: " + source);
		}	else
			streams = ((PullDataSource) source).getStreams();

		if ( streams == null) 
			throw new IOException("Got a null stream from the DataSource");

		if (streams.length == 0) 
			throw new IOException("Got a empty stream array from the DataSource");

		// All is well save some info
		this.source  = source;
		this.streams = streams;

		// Get the sample rate and number of channels from data source 
		sampleRate = ((audiostuff.craigl.jmf20.media.protocol.intfc.DataSource) source).sampleRate;
		channels   = ((audiostuff.craigl.jmf20.media.protocol.intfc.DataSource) source).channels;

		positionable = false;
		seekable = false;
	}

	public boolean isPositionable() {

		return positionable;
	}

	public boolean isRandomAccess() {
		
		return seekable;
	}

	/**
	 * Opens the plug-in software or hardware component and acquires
	 * necessary resources. If all the needed resources could not be
	 * acquired, it throws a ResourceUnavailableException. Data should not
	 * be passed into the plug-in without first calling this method.
	 */
	public void open() {
		// throws ResourceUnavailableException;
	}

	/**
	 * Closes the plug-in component and releases resources. No more data
	 * will be accepted by the plug-in after a call to this method. The
	 * plug-in can be reinstated after being closed by calling open.
	 */
	public void close() {

		if (source != null) {
			try	{
				source.stop();
				source.disconnect();
			} catch (IOException e) {
				// Internal error?
			}
			source = null;
		}
	}

	/**
	 * This get called when the player/processor is started.
	 */
	public void start() throws IOException {

		if (source != null)
			source.start();
	}

	/**
	 * This get called when the player/processor is stopped.
	 */
	public void stop() {

		if (source != null) {
			try {
				source.stop();
			} catch (IOException e) {
				// Internal errors?
			}
		}
	}

	/**
	 * Resets the state of the plug-in. Typically at end of media
	 * or when media is repositioned.
	 */
	public void reset() {

	}

	public Track [] getTracks() throws IOException, BadHeaderException {

		if (tracks[0] != null)
			return tracks;

		// Determine the format of the single track
		AudioFormat format = new AudioFormat(
							AudioFormat.LINEAR,
							sampleRate,
							16,
							channels,
							AudioFormat.LITTLE_ENDIAN,
							AudioFormat.SIGNED,
							16 * channels,
							Format.NOT_SPECIFIED,
							Format.byteArray);

		stream = (PullSourceStream) streams[0];

		tracks[0] = new RawTrack(format, stream);
		return tracks;
	}

	public Object[] getControls() {

		return new Object[0];
	}

	public Object getControl(String controlType) {

		return null;
	}

	// Track contains 1 audio track
	public String getTrackLayout() {

		return "A";
	}

	public Time setPosition(Time where, int rounding) {
	
		return getMediaTime();
	}

	public Time getMediaTime() {

		return new Time(0);
	}

	public Time getDuration() {

		return Duration.DURATION_UNKNOWN;
	}

	/**
	 * Returns a descriptive name for the plug-in.
	 * This is a user readable string.
	 */
	public String getName() {

		return "Parser for raw samples";
	}

	// Class data
	private Track [] tracks;
	private PullSourceStream stream = null;
	private DataSource source;
	private SourceStream[] streams;
	private boolean seekable = false;
	private boolean positionable = false;
	private int sampleRate;
	private int channels;
}

