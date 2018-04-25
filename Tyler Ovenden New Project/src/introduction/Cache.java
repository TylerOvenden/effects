package introduction;

public class Cache {
	int cacheSize;
	short[] moveBuffer;
	short[] cacheBuffer;
	int cacheBufferOffset;
 	Double previous;
	
	public int getSamples(short [] buffer, int length) {
		if(cacheSize == 0) {
			moveBuffer = new short [length];
			
			int len = 0;
			
			while(len != -1) {
				cacheSize += len;
				len = previous.getSamples(moveBuffer, length);
				 
			}
			o("required cache size in samples: " + cacheSize);
	
			cacheBuffer = new short [cacheSize];
			
			len = previous.getSamples(moveBuffer, length);
			while(len != -1) {
				System.arraycopy(moveBuffer, 0, cacheBuffer, cacheBufferOffset, len);
				cacheBufferOffset += len;
				len = previous.getSamples(moveBuffer, length);
			}
			cacheBufferOffset = 0;
		}
		int dataRemaining = cacheSize - cacheBufferOffset;
		
		if(dataRemaining == 0)
			return -1;
		
		int samplesRead;
		
		if(length <= dataRemaining) {
			System.arraycopy(cacheBuffer, cacheBufferOffset, buffer, 0, length);
			cacheBufferOffset += length;
			return length;}
		
		else {
			System.arraycopy(cacheBuffer, cacheBufferOffset, buffer, 0, dataRemaining);
			cacheBufferOffset += dataRemaining;
			return dataRemaining;
		}
	}

	private void o(String string) {
		// TODO Auto-generated method stub
		
	}
}
