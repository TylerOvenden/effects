// PCMDataSource Class
// Written by: Craig A. Lindley
// Last Update: 07/08/98

package audiostuff.craigl.pcmplayer;

import java.io.*;
import javax.media.*;
import javax.media.protocol.*;
import audiostuff.craigl.utils.*;

public class PCMDataSource extends PullDataSource {

    
    public PCMDataSource(AbstractAudio aa, int channels, int sampleRate) {

		this.aa = aa;
		this.channels = channels;
		this.sampleRate = sampleRate;
		this.cd = new ContentDescriptor(contentDescriptorString);
	}

    /*************************************************************************
     * Controls Methods
     *************************************************************************/

	public Object[] getControls() {

		return new Object[0];
    }

	public Object getControl(String controlType) {

		return null;
    }

    /*************************************************************************
     * Duration interface
     *************************************************************************/

	public Time getDuration() {

		return Duration.DURATION_UNKNOWN;
    }
    
    /*************************************************************************
     * DataSource Methods
     *************************************************************************/
    
	public String getContentType() {

		return contentDescriptorString;
    }

    /**
     * Connect to the source.
     */
	public void connect() throws IOException {

		if (sourceStream == null)
			sourceStream = 
				new PCMSourceStream(cd, aa, channels, sampleRate);
    }

    /**
     * Disconnect from the source
     */
	public void disconnect() {

		// Nothing to do
    }

	public void start() {

		if (sourceStream == null)
			sourceStream = new PCMSourceStream(cd, aa, channels, sampleRate);
    }

    public void stop() {

		// Nothing to do
    }

    /*************************************************************************
     * PullDataSource Methods
     *************************************************************************/
    
    public PullSourceStream[] getStreams() {

		if (pullStreams == null) {
			pullStreams = new PullSourceStream[1];

			if (sourceStream == null)
				sourceStream = new PCMSourceStream(cd, aa, channels, sampleRate);

			pullStreams[0] = sourceStream;
		}
		return pullStreams;
    }
	
	// Class data
    protected AbstractAudio aa;
	protected int channels;
	protected int sampleRate;
	protected ContentDescriptor cd;
	protected static final String contentDescriptorString = "audio.basic";
	protected PCMSourceStream sourceStream = null;
    protected PullSourceStream[] pullStreams = null;
}
