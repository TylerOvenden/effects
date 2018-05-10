// JMF CallBack Interface
// Written by: Craig A. Lindley
// Last Update: 06/02/99

// Used to signal when a read of an audio file has been completed

package audiostuff.craigl.jmf20.devices;

public interface JMFFileCallBackIF {

	public void signalReset();
	public void signalPlaybackBegun();
}

