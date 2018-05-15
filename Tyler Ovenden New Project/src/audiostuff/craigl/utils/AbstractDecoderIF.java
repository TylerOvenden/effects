package audiostuff.craigl.utils;

public interface AbstractDecoderIF {

	
	public String getName();
	public int getSamples(short [] buffer, int length);
}
