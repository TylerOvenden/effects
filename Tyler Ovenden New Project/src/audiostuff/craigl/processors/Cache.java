// Audio Sample Cacheing Processor 
// Written by: Craig A. Lindley
// Last Update: 01/08/99

package craigl.processors;

import craigl.utils.*;

public class Cache extends AbstractAudio {

	// Class constructor
	public Cache() {
		
		super("Cache", PROCESSOR);

		// Indicate the cache is uninitialized
		cacheSize = 0;			// Size of cache required
		cacheBufferOffset = 0;	// Offset to where current samples are
	}

	// Process any reset messages received by this stage
	public void reset() {

		o("Cache reset");

		// Reset cache sample offset to the beginning
		cacheBufferOffset = 0;
	}

	// Return samples from the cache
	public int getSamples(short [] buffer, int length) {

		// Determine if cache already contains samples
		if (cacheSize == 0) {
			
			// Allocate a move buffer
			moveBuffer = new short [length];
			
			// Cache uninitialized, determine required cache size
			int len = 0;

			while(len != -1) {
				cacheSize += len;
				len = previous.getSamples(moveBuffer, length);
			}
			o("Required cache size in samples: " + cacheSize);
			
			// Allocate buffer for cache
			cacheBuffer = new short [cacheSize];

			// Now fill the cache buffer samples. NOTE: previous
			// stages must have reset for this to work.

			len = previous.getSamples(moveBuffer, length);
			while(len != -1) {

				System.arraycopy(moveBuffer, 0, cacheBuffer, cacheBufferOffset, len);
				cacheBufferOffset += len;
				len = previous.getSamples(moveBuffer, length);
			}
			// Pt at the beginning of the data in the cache
			cacheBufferOffset = 0;
		}
		// Return samples from the cache
		
		// Calculate data remaining in the cache
		int dataRemaining = cacheSize - cacheBufferOffset;
		
		// Return end of file indication if no more data
		if (dataRemaining == 0)
			return -1;

		int samplesRead;

		if (length <= dataRemaining) {
			// Cache has the required amount of data to return
			System.arraycopy(cacheBuffer, cacheBufferOffset, buffer, 0, length);
			cacheBufferOffset += length;
			return length;
		
		}	else	{
		
			// Cache is short of data
			System.arraycopy(cacheBuffer, cacheBufferOffset, buffer, 0, dataRemaining);
			cacheBufferOffset += dataRemaining;
			return dataRemaining;
		}
	}
	
	// Private class data
	private int cacheSize;
	private int cacheBufferOffset;
	private short [] moveBuffer;
	private short [] cacheBuffer;
}
