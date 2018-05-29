// PCMSourceStream Class
// Written by: Craig A. Lindley
// Last Update: 08/02/98

package audiostuff.craigl.pcmplayer;

import javax.media.*;
import javax.media.protocol.*;
import audiostuff.craigl.utils.*;
import audiostuff.craigl.au.*;

public class PCMSourceStream implements PullSourceStream {

    public PCMSourceStream(ContentDescriptor cd, AbstractAudio aa,
						   int channels, int sampleRate) {
		// Save incoming
		this.cd = cd;
		this.aa = aa;
		this.channels = channels;
		this.sampleRate = sampleRate;

		// Initialize class variables
		samplesAvailable = 0;
		endOfMedia = false;
		headerByteOffset = 0;
		sampleOffset = 0;

		// Generate au header
		header = AUWrite.build16BitAuHeader(sampleRate, channels, Integer.MAX_VALUE);
		headerBytesLeft = header.length;

		// Allocate buffer for samples.
		sampleBuffer = new short[AudioConstants.SAMPLEBUFFERSIZE];
    }

    /*************************************************************************
     * Controls interface
     *************************************************************************/

	public Object [] getControls() {

		return new Object[0];
	}

    public Object getControl(String controlType) {

		return null;
    }
    
    /*************************************************************************
     * SourceStream interface
     *************************************************************************/
    
    public ContentDescriptor getContentDescriptor() {

		return cd;
    }

    public long getContentLength() {

		return SourceStream.LENGTH_UNKNOWN;
    }


    public boolean endOfStream() {

		return endOfMedia;
    }

    /*************************************************************************
     * PullSourceStream interface (extends SourceStream)
     *************************************************************************/
    
    public boolean willReadBlock() {

		return false;
    }

    public int read(byte[] buffer, int offset, int nToRead) {

		if (endOfMedia)
			return -1;

		if (nToRead == 0)
			return 0;

		int numberOfBytesProvided = 0;

		// Supply the synthesized header as the initial data
		if (headerBytesLeft != 0) {
			if (headerBytesLeft >= nToRead) {
				// Header can supply all data requested
				headerBytesLeft -= nToRead;
				System.arraycopy(header, headerByteOffset, buffer, offset, nToRead);
				headerByteOffset += nToRead;
				return nToRead;
			}	else	{
				// Header cannot supply all required data
				System.arraycopy(header, headerByteOffset, buffer, offset, headerBytesLeft);
				offset += headerBytesLeft;
				nToRead -= headerBytesLeft;
				numberOfBytesProvided = headerBytesLeft;
				headerBytesLeft = 0;
			}
		}
		// Loop until all of the data requested has been provided or 
		// until the end of the media has been reached.
		while(numberOfBytesProvided < nToRead) {
			// If samples exhaused, pull in some more from previous
			// processing stages.
			if (samplesAvailable == 0) {
				sampleOffset = 0;

				samplesAvailable = 
					aa.previous.getSamples(sampleBuffer, AudioConstants.SAMPLEBUFFERSIZE);
				
				endOfMedia = ((samplesAvailable == 0) || (samplesAvailable == -1));
			}
			if (endOfMedia)
				return numberOfBytesProvided;

			// Convert the 16 bit signed PCM samples into two bytes
			short sample = sampleBuffer[sampleOffset++];
			buffer[offset++] = (byte) (sample >> 8);
			buffer[offset++] = (byte) (sample & 255);
			numberOfBytesProvided += 2;
			samplesAvailable -= 1;
		}
		return numberOfBytesProvided;
    }


	public static void o(String s) {

		System.err.println(s);
	}

	// Class data
	protected ContentDescriptor cd;
	protected AbstractAudio aa;
	protected int channels;
	protected int sampleRate;
	protected int samplesAvailable;
	protected byte [] header;
	protected int headerBytesLeft;
	protected short [] sampleBuffer;
	protected boolean endOfMedia;
	protected int headerByteOffset;
	protected int sampleOffset;
}
